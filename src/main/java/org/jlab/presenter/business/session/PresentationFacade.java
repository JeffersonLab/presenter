package org.jlab.presenter.business.session;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jlog.Body.ContentType;
import org.jlab.jlog.Library;
import org.jlab.jlog.LogEntry;
import org.jlab.jlog.LogEntryAdminExtension;
import org.jlab.jlog.exception.AttachmentSizeException;
import org.jlab.jlog.exception.InvalidXMLException;
import org.jlab.jlog.exception.LogCertificateException;
import org.jlab.jlog.exception.LogIOException;
import org.jlab.jlog.exception.MalformedXMLException;
import org.jlab.jlog.exception.SchemaUnavailableException;
import org.jlab.presenter.business.exception.WebAppException;
import org.jlab.presenter.business.util.IOUtil;
import org.jlab.presenter.business.util.TimeUtil;
import org.jlab.presenter.business.util.UrlUtil;
import org.jlab.presenter.persistence.entity.*;
import org.jlab.presenter.persistence.enumeration.PresentationType;
import org.jlab.presenter.persistence.enumeration.SlideType;
import org.jlab.presenter.presentation.util.DailySlideGenerator;
import org.jlab.presenter.presentation.util.PresentationMenuUtil;
import org.jlab.presenter.presentation.util.ShowInfo;
import org.jlab.presenter.presentation.util.TemplateExecutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author ryans
 */
@Stateless
@DeclareRoles({"pd", "cc", "presenter-admin"})
public class PresentationFacade extends AbstractFacade<Presentation> {

    private final static Logger LOGGER
            = Logger.getLogger(PresentationFacade.class.getName());
    @PersistenceContext(unitName = "presenterPU")
    private EntityManager em;
    @Resource
    private SessionContext context;
    @EJB
    PresentationLogFacade presentationLogFacade;
    @EJB
    SlideFacade slideFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PresentationFacade() {
        super(Presentation.class);
    }

    @PermitAll
    public Presentation findWithSlides(BigInteger id) {
        Presentation p = find(id);

        if (p != null) {
            p.getSlideList().size(); // Touch slides to prompt EM to load them (this is a hack due to missing support in JPA!)
        }

        return p;
    }

    @PermitAll
    public int delete(BigInteger presentationId) {
        Query q = em.createNamedQuery("Presentation.delete");
        q.setParameter("presentationId", presentationId);
        return q.executeUpdate();
    }

    @PermitAll
    public Presentation editForRefresh(Presentation presentation) {
        return super.edit(presentation);
    }

    @PermitAll
    @Override
    public Presentation edit(Presentation presentation) {
        String username = checkAuthorized(presentation.getPresentationType());

        presentation.setLastModified(TimeUtil.nowWithoutMillis());

        presentation.setLastUsername(username);

        return super.edit(presentation);
    }

    @PermitAll
    @Override
    public void create(Presentation presentation) {
        String username = checkAuthorized(presentation.getPresentationType());

        presentation.setLastModified(TimeUtil.nowWithoutMillis());

        presentation.setLastUsername(username);

        super.create(presentation);
    }

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isAuthorized(PresentationType type) {
        String[] response = getUnauthorizedReason(type);

        //String username = response[0];
        String reason = response[1];

        return reason == null;
    }

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String checkAuthorized(PresentationType type)
            throws EJBAccessException {
        String[] response = getUnauthorizedReason(type);

        String username = response[0];
        String reason = response[1];

        if (reason != null) {
            throw new EJBAccessException(reason);
        }

        return username;
    }

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String[] getUnauthorizedReason(PresentationType type) {
        String reason = null;

        String username = context.getCallerPrincipal().getName();
        if (username == null || username.isEmpty() || username.equalsIgnoreCase("ANONYMOUS")) {
            reason
                    = "You must be authenticated to perform the requested operation.  Your session may have expired.  Please re-login.";
        } else {

            boolean admin = context.isCallerInRole("presenter-admin");

            if(!admin) {
                switch (type) {
                    case PD_PRESENTATION:
                        boolean programDeputy = context.isCallerInRole("pd");

                        if (!programDeputy) {
                            reason = "You must be a Program Deputy to perform the requested operation";
                        }
                        break;
                    case CC_PRESENTATION:
                        boolean crewChief = context.isCallerInRole("cc");

                        if (!crewChief) {
                            reason = "You must be a Crew Chief to perform the requested operation";
                        }
                        break;
                    case LASO_PRESENTATION:
                    case LO_PRESENTATION:
                    case UITF_PRESENTATION:
                        break;
                    default:
                        reason = "Unknown presentation type: " + type;
                }
            }
        }

        return new String[]{username, reason};
    }

    @PermitAll
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String checkAuthorized() {
        String username = context.getCallerPrincipal().getName();
        if (username == null || username.isEmpty() || username.equalsIgnoreCase("ANONYMOUS")) {
            throw new EJBAccessException(
                    "You must be authenticated to perform the requested operation.  Your session may have expired.  Please re-login.");
        }

        return username;
    }

    public List<String> getPresentationImages(BigInteger presentationId) {
        List<String> images = new ArrayList<>();

        Presentation presentation = find(presentationId);

        populateSyncedInfo(presentation);

        if (presentation != null && presentation.getSlideList() != null) {
            for (Slide slide : presentation.getSlideList()) {
                if (slide instanceof ImageSlide) {
                    ImageSlide imageSlide = (ImageSlide) slide;
                    String image = imageSlide.getImageUrl();
                    if (image != null && !image.isEmpty()) {
                        images.add(image);
                    }
                }
            }
        }

        return images;
    }

    @PermitAll
    public String getPresentationHTML(HttpServletRequest request,
            HttpServletResponse response, BigInteger presentationId, List<String> imageData) throws
            ServletException, IOException {

        StringBuilder builder = new StringBuilder();

        Presentation presentation = find(presentationId);

        populateSyncedInfo(presentation);
        
        if (presentation != null && presentation.getSlideList() != null) {
            builder.append("<div class=\"presentation\">\n");
            ArrayList<String> seeAlsoUrl = new ArrayList<>();
            ArrayList<String> seeAlsoLabel = new ArrayList<>();
            int figureNum = 1;
            for (Slide slide : presentation.getSlideList()) {
                if (slide instanceof IFrameSlide) {
                    IFrameSlide iframe = (IFrameSlide) slide;
                    String url = iframe.getIframeUrl();
                    if (url != null && !url.isEmpty() && !url.equals("about:blank")) {
                        seeAlsoUrl.add(url);
                        seeAlsoLabel.add(iframe.getLabel());
                    }
                } else if (slide instanceof ImageSlide) {
                    ImageSlide imageSlide = (ImageSlide) slide;
                    String image = imageSlide.getImageUrl();
                    if (image != null && !image.isEmpty()) {
                        imageData.add(image);
                    }
                    String imageSlideHtml = TemplateExecutor.execute(request, response, slide);
                    imageSlideHtml = imageSlideHtml.replaceFirst("<div class=\"img-div(.*?)</div>",
                            "[figure:" + figureNum++ + "]");
                    builder.append(imageSlideHtml);
                } else {
                    String html = TemplateExecutor.execute(request, response, slide);
                    //System.out.println("Before: " + html);
                    Document doc = Jsoup.parseBodyFragment(html);
                    Elements placeholders = doc.select(".placeholder");
                    for (Element e : placeholders) {
                        e.text("");
                    }
                    html = doc.body().html();
                    builder.append(html);
                }
            }
            builder.append("<div class=\"see-also\">\n");
            builder.append("<h2>See Also</h2>\n");
            seeAlsoUrl.add(UrlUtil.getPresentationUrl(presentationId));
            seeAlsoLabel.add("View Presentation in Presenter");
            builder.append("<ul>");
            int i = 0;
            for (String see : seeAlsoUrl) {
                String label = seeAlsoLabel.get(i++);

                if (label == null) {
                    label = "Embedded Webpage";
                }

                builder.append("<li><a href=\"");
                builder.append(IOUtil.escapeXml(see));
                builder.append("\">");
                builder.append(IOUtil.escapeXml(label));
                builder.append("</a></li>\n");
            }
            builder.append("</ul>\n");
            builder.append("</div>\n");
            builder.append("</div>\n");
        }

        return builder.toString();
    }

    @PermitAll
    public void createLogIdRecord(String username, BigInteger presentationId, long logId) {
        PresentationLogPK pk = new PresentationLogPK(presentationId, BigInteger.valueOf(logId),
                username);
        PresentationLog log = new PresentationLog(pk);
        presentationLogFacade.create(log);
    }

    @PermitAll
    public Long getLogId(String username, BigInteger presentationId) {
        Long logId = null;
        TypedQuery<PresentationLog> q = em.createNamedQuery(
                "PresentationLog.findByUsernameAndPresentationId", PresentationLog.class);
        q.setParameter("presentationId", presentationId);
        q.setParameter("username", username);
        List<PresentationLog> result = q.getResultList();
        if (result != null && !result.isEmpty()) {
            PresentationLog log = result.get(0);
            if (log != null) {
                logId = log.getPresentationLogPK().getLogId().longValue();
            }
        }
        return logId;
    }

    @PermitAll
    public long sendELog(String title, String body, boolean html, String[] tags, String logbooks,
            String[] attachmentPaths, BigInteger presentationId, PresentationType presentationType)
            throws SchemaUnavailableException, MalformedXMLException,
            InvalidXMLException, LogIOException, AttachmentSizeException, LogCertificateException {

        String logbookServer = System.getenv("LOGBOOK_SERVER");

        // In the absence of a test server an alternative is to use production server,
        // but route entries to TLOG
        String logbookDebug = System.getenv("LOGBOOK_DEBUG");

        if("true".equals(logbookDebug)) {
            logbooks = "TLOG";
            LOGGER.log(Level.INFO, "Using logbook TLOG");
            tags = null;
        }

        if(logbookServer == null) {
            throw new RuntimeException("LOGBOOK_SERVER env not set");
        }

        Properties config = Library.getConfiguration();

        config.setProperty("SUBMIT_URL", "https://" + logbookServer + "/incoming");
        config.setProperty("FETCH_URL", "https://" + logbookServer + "/entry");

        String entrymaker = context.getCallerPrincipal().getName();
        
        String username = "alarms";

        if (presentationType != PresentationType.PD_PRESENTATION && entrymaker != null
                && !entrymaker.isEmpty() && !entrymaker.equalsIgnoreCase("ANONYMOUS")) {
            username = entrymaker;
        }

        Long existingLogId = getLogId(username, presentationId);
        LogEntry entry;

        if (existingLogId != null) {
            entry = LogEntry.getLogEntry(existingLogId, null);
            entry.setTitle(title);
            entry.deleteAttachments();
        } else {
            entry = new LogEntry(title, logbooks);
        }

        ContentType type = ContentType.TEXT;

        if (html) {
            type = ContentType.HTML;
        }

        entry.setBody(body, type);

        entry.setTags(tags);

        for (String attachmentPath : attachmentPaths) {
            entry.addAttachment(attachmentPath);
        }

        LogEntryAdminExtension extension = new LogEntryAdminExtension(entry);
        extension.setAuthor(username);

        // REMEMBER: This field is ignored and the actual submitter is used instead if the actual submitter isn't a special user like "glassfish" or "wildfly".  This means on local testing this won't work!
        //logger.log(Level.FINEST, "Author: {0}", username);        
        if (entrymaker != null && !entrymaker.isEmpty() && !entrymaker.equalsIgnoreCase("ANONYMOUS")
                && !entrymaker.equals(username)) {
            entry.addEntryMakers(entrymaker);
            LOGGER.log(Level.FINEST, "Adding entrymaker: {0}", entrymaker);
        }

        long logId;

        //logger.log(Level.FINEST, "XML: {0}", entry.getXML());        
        logId = entry.submitNow();

        if (existingLogId == null) {
            createLogIdRecord(username, presentationId, logId);
        }

        return logId;
    }

    @PermitAll
    public long publicSendELogRequest(BigInteger presentationId,
                                      HttpServletRequest request, HttpServletResponse response) throws WebAppException {
        Presentation presentation = find(presentationId);

        checkAuthorized();

        return sendELog(presentation, request, response);
    }

    @PermitAll
    public long sendELog(Presentation presentation,
            HttpServletRequest request, HttpServletResponse response) throws WebAppException {
        try {
            List<String> imageData = new ArrayList<>();
            String body = getPresentationHTML(request, response, presentation.getPresentationId(),
                    imageData);

            String title = "";
            String[] tags = null;
            String logbooks = "elog";

            PresentationType presentationType = presentation.getPresentationType();

            long logId = -1;

            switch (presentationType) {
                case PD_PRESENTATION:
                    logId = PresentationMenuUtil.logPd(
                            (PDPresentation) presentation,
                            this,
                            body,
                            imageData);
                    break;
                case CC_PRESENTATION:
                    logId = PresentationMenuUtil.logCc(
                            (CCPresentation) presentation,
                            this,
                            body,
                            imageData);
                    break;
                case LO_PRESENTATION:
                    logId = PresentationMenuUtil.logLo(
                            (LOPresentation) presentation,
                            this,
                            body,
                            imageData);
                    break;
                case LASO_PRESENTATION:
                    logId = PresentationMenuUtil.logLaso(
                            (LASOPresentation) presentation,
                            this,
                            body,
                            imageData);
                    break;
                case UITF_PRESENTATION:
                    logId = PresentationMenuUtil.logUitf(
                            (UITFPresentation) presentation,
                            this,
                            body,
                            imageData);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Presentation Type: " + presentationType);
            }

            return logId;
        } catch (Exception e) {
            throw new WebAppException("General Error", e);
        }
    }

    @PermitAll
    public void merge(Presentation presentation, List<Slide> fromList, boolean newSlidesGoFirst) {
        List<Slide> toList = presentation.getSlideList();

        Map<BigInteger, Slide> updatable = new HashMap<>();
        List<Slide> newSlides = new ArrayList<>();
        List<Slide> toDuplicates = new ArrayList<>();

        // Create map of updatable slides
        for (Slide s : toList) {
            if (s.getSyncFromSlideId() != null) {
                Slide duplicate = updatable.put(s.getSyncFromSlideId(), s);

                if (duplicate != null) {
                    LOGGER.log(Level.WARNING, "Duplicate in toList; SlideId: {0}",
                            duplicate.getSlideId());
                    toDuplicates.add(s);
                }
            }
        }

        // If there are dupilcate sync slides then delete.  This should be rare
        // and represents a input toList that is already bad
        toList.removeAll(toDuplicates);

        // While we are doing sanity checks let's make sure the fromList doesn't 
        // contain duplicates either
        // Comment out filtering duplicates due to the fact that we now allow new non-synced slides which means slideId = null (physics summary graph iframe and fsd graph both are null slideId)
        /*Set<Slide> duplicateFilter = new LinkedHashSet<Slide>();
         for (Slide s : fromList) {
         boolean isNotDuplicate = duplicateFilter.add(s);
         if (!isNotDuplicate) {
         logger.log(Level.WARNING, "Duplicate in fromList; SlideId: {0}", s.getSlideId());
         }
         }

         fromList = new ArrayList<Slide>(duplicateFilter);*/
        // Merge slides that are updatable and copy new ones
        for (int i = 0; i < fromList.size(); i++) {
            Slide s = fromList.get(i);
            if (updatable.containsKey(s.getSlideId())) {
                // Remove as we use them because we need to see what remains...
                Slide existing = updatable.remove(s.getSlideId());
                existing.mergeSlide(s);
            } else {
                Slide n = s.copySlide();
                n.setPresentation(presentation);
                n.setOrderId(Long.valueOf(i)); /*We keep the input order number so we can possibly use it later to determine where this new slide goes; it is overwritten later anyways*/

                newSlides.add(n);
            }
        }

        // The remaining updatables must no longer exist so should be deleted
        for (Slide s : updatable.values()) {
            toList.remove(s);
            slideFacade.remove(s);
        }

        // Add new slides in a helpful way:
        // - try to put new slides right after the last slide from the 
        // same presentation already in the list, otherwise at the beginning.
        // Previously we would otherwise add at end and but we realized we mostly want shift logs up
        // front - also handles scenario were initially 1 preceding day and decide to have more.
        // We can't add to beginning directly as then slides would be in reverse order so we add to 
        // "unplacedList" and then add that collection to beginning.
        //
        // - or if not from an existing synced presentation (or not a synced slide) then try to put them in the same relative order 
        // as the input list (daily graphs for example)
        //
        //toList.addAll(newSlides);
        //
        List<Slide> unplacedList = new ArrayList<>();

        /*We keep track of how many items are stacked on syncId anchor to keep those items in stack order*/
        HashMap<BigInteger, Integer> anchorStackOffset = new HashMap<>();

        for (Slide s : newSlides) {
            //System.err.println("new slide original order: " + s.getOrderId() + "; type: " + s.getSlideType());

            boolean added = false;
            if (s.getSyncFromSlideId() != null) {
                // If slide comes from synced presentation then try to find the last slide already included from that presentation and append there
                for (int i = toList.size() - 1; i >= 0; i--) {
                    BigInteger syncFromSlideId = toList.get(i).getSyncFromSlideId();
                    if (syncFromSlideId != null) {
                        Slide syncSourceTo = slideFacade.find(syncFromSlideId);

                        Slide syncSourceFrom = slideFacade.find(s.getSyncFromSlideId());

                        if (syncSourceTo.getPresentation().equals(syncSourceFrom.getPresentation())) {
                            toList.add(i + 1, s);
                            added = true;
                            break;
                        }
                    }
                }
            }

            if (!added) { // Either wasn't syncable or didn't have slide from same presentation
                // so try to insert in order given by input list relative to closest predeceeding "anchor" synced slide
                //System.err.println("doing the harder thing...");
                Long originalIndex = s.getOrderId(); // We are using input order to help determine order in merged slide set.
                if (originalIndex != null) {
                    for (int i = (originalIndex.intValue()) - 1; i >= 0; i--) {
                        //System.err.println("checking index: " + i);
                        BigInteger sId = fromList.get(i).getSlideId();
                        if (sId != null) {
                            //System.err.println("found preceeding slide w/id: " + sId);
                            for (int j = 0; j < toList.size(); j++) {
                                Slide e = toList.get(j);
                                //System.err.println("comparing to item:  " + e.getSyncFromSlideId());
                                if (e.getSyncFromSlideId() != null && e.getSyncFromSlideId().equals(
                                        sId)) {
                                    Integer offset = anchorStackOffset.get(sId);
                                    if (offset == null) {
                                        //System.err.println("offset is zero");
                                        offset = 0;
                                    } else {
                                        //System.err.println("offset is: " + offset);
                                    }

                                    offset++;

                                    anchorStackOffset.put(sId, offset);

                                    toList.add(j + offset, s);
                                    //System.err.println("Adding at " + (j + offset));
                                    added = true;
                                    break;
                                }
                            }

                            break;
                        }
                    }
                }
            }

            if (!added) { // couldn't add slide behind last slide from same presentation / or it isn't a synced slide and doesn't have preceeding "anchor" slides to be positioned relative to - so just add it to list which will be added to beginning
                //System.err.println("adding to the end: " + (toList.size()));
                unplacedList.add(s);
            }
        }

        // Add unplaced list to beginning of toList by adding toList to end of unplacedList and reassigning reference
        if (newSlidesGoFirst) {
            unplacedList.addAll(toList);
            toList = unplacedList;
        } else {
            toList.addAll(unplacedList);
        }

        // Renumber slides
        long order = 1;
        for (Slide s : toList) {
            s.setOrderId(order++);
        }

        presentation.setSlideList(toList);

        slideFacade.createSlideListForRefresh(newSlides);
        editForRefresh(presentation);
    }

    @PermitAll
    public void append(Presentation presentation, List<Slide> fromList) {

        List<Slide> toList = presentation.getSlideList();

        /*System.out.println("fromList: ");
         for(Slide s: fromList) {
         System.out.println("Slide: " + s);
         System.out.println("Order: " + s.getOrderId());
         }

         System.out.println("toList: ");
         for (Slide s : toList) {
         System.out.println("Slide: " + s);
         System.out.println("Order: " + s.getOrderId());
         }*/
        ArrayList<Slide> newList = new ArrayList<>();
        newList.addAll(fromList);
        newList.addAll(toList);

        // Renumber slides
        long order = 1;
        for (Slide s : newList) {
            s.setOrderId(order++);
        }

        presentation.setSlideList(newList);

        /*insert new slides (merge won't work as related BTARecords are hanging off*/
        for (Slide s : newList) {
            em.persist(s);
        }

        em.merge(presentation);

    }

    @PermitAll
    public void checkSlideOrder(Presentation presentation) {
        List<Slide> slideList = presentation.getSlideList();

        if (slideList != null) {
            long order = 1;
            for (Slide s : slideList) {
                if (s.getOrderId() != order) {
                    LOGGER.log(Level.WARNING,
                            "Slide Order is Corrupt; PresentationId: {0}, SlideId: {1}, Expected: {2}, Found: {3}.  Order will be corrected",
                            new Object[]{presentation.getPresentationId(), s.getSlideId(), order,
                                s.getOrderId()});
                    s.setOrderId(order);
                    em.merge(s); // Bypass security check on edit method
                }

                order++;
            }
        }
    }
    
    @PermitAll
    public void populateSyncedInfo(Presentation presentation) {
        List<Slide> slideList = presentation.getSlideList();

        if (slideList != null) {
            for (Slide s : slideList) {
                if (s.getSyncFromSlideId() != null) {
                    Slide syncedSlide = slideFacade.find(s.getSyncFromSlideId());
                    
                    if(syncedSlide != null) {
                        s.setSyncFromPresentationId(syncedSlide.getPresentation().getPresentationId());
                        s.setSyncFromPresentationType(syncedSlide.getPresentation().getPresentationType());
                    }
                }
            }
        }
    }    

    @PermitAll
    public void removeSyncedSlides(PDPresentation presentation) {
        List<Slide> slideList = presentation.getSlideList();
        List<Slide> copyOfList = new ArrayList<>(slideList);

        for (Slide slide : copyOfList) {
            if (slide.getSyncFromSlideId() != null) {
                slideList.remove(slide);
                slideFacade.remove(slide);
            }
        }

        // Renumber slides
        long order = 1;
        for (Slide s : slideList) {
            s.setOrderId(order++);
        }
    }

    @PermitAll
    public void removeDailyGraphs(PDPresentation presentation) {
        List<Slide> slideList = presentation.getSlideList();
        List<Slide> copyOfList = new ArrayList<>(slideList);

        for (Slide slide : copyOfList) {
            if (slide.getSlideType() == SlideType.IFRAME_SLIDE) {
                IFrameSlide iSlide = (IFrameSlide) slide;
                String url = iSlide.getIframeUrl();

                if (url.contains("physics-summary") || url.contains("fsd-summary")) {
                    slideList.remove(slide);
                    slideFacade.remove(slide);
                }

            }
        }

        // Renumber slides
        long order = 1;
        for (Slide s : slideList) {
            s.setOrderId(order++);
        }
    }

    @PermitAll
    public void insertDailyGraphs(PDPresentation presentation,
            DailySlideGenerator[] dailySlideGeneratorArray) {
        List<Slide> slideList = presentation.getSlideList();
        List<Slide> newList = new ArrayList<>();

        Set<Date> dateList = new HashSet<>();

        long order = 1;

        System.out.println("inserting...");

        if (!slideList.isEmpty()) {
            for (int i = slideList.size() - 1; i >= 0; i--) {
                System.out.println("looping: " + i);
                boolean addDaily = false;
                Date day = null;
                Slide s = slideList.get(i);

                BigInteger syncId = s.getSyncFromSlideId();

                if (syncId != null) {
                    Slide syncedSlide = slideFacade.find(syncId);
                    if (syncedSlide != null) {
                        if (syncedSlide.getPresentation().getPresentationType()
                                == PresentationType.CC_PRESENTATION) {
                            CCPresentation p = (CCPresentation) syncedSlide.getPresentation();
                            day = TimeUtil.getShiftDay(p.getYmd());
                            if (!dateList.contains(day)) {
                                dateList.add(day);
                                addDaily = true;
                            }
                        }
                    }
                }
                s.setOrderId(order++);
                newList.add(s);
                if (addDaily) {
                    if (dailySlideGeneratorArray != null) {
                        for (int j = 0; j < dailySlideGeneratorArray.length; j++) {
                            Slide dailySlide = dailySlideGeneratorArray[j].getSlideForDay(
                                    day);
                            dailySlide.setPresentation(presentation);
                            dailySlide.setOrderId(order++);
                            newList.add(dailySlide);
                        }
                    }
                }
            }
        }

        presentation.setSlideList(newList);
    }

    @PermitAll
    public List<Presentation> getSyncedPresentationList(BigInteger presentationId) {
        Presentation presentation = this.find(presentationId);

        List<Presentation> presentationList = new ArrayList<>();

        if (presentation.getSlideList() != null) {
            for (Slide s : presentation.getSlideList()) {
                if (s.getSyncFromSlideId() != null) {
                    Slide syncedSlide = slideFacade.find(s.getSyncFromSlideId());

                    if (syncedSlide != null) {
                        presentationList.add(syncedSlide.getPresentation());
                    }
                }
            }
        }

        return presentationList;
    }

    @PermitAll
    public List<AuditRecord> getAuditRecordList(BigInteger presentationId) {
        List<Presentation> presentationList = getSyncedPresentationList(presentationId);

        List<AuditRecord> auditList = new ArrayList<>();

        Presentation presentation = find(presentationId);

        auditList.add(getAuditRecord(presentation));

        for (Presentation p : presentationList) {
            auditList.add(getAuditRecord(p));
        }

        return auditList;
    }

    @PermitAll
    public AuditRecord getAuditRecord(Presentation presentation) {
        BigInteger presentationId = BigInteger.ZERO;
        Long lastModifiedMillis = null;
        String user = "ANONYMOUS";
        String description = null;

        if (presentation != null) {

            presentationId = presentation.getPresentationId();

            if (presentation.getLastModified() != null) {
                lastModifiedMillis = presentation.getLastModified().getTime();
            }

            user = presentation.getLastUsername();

            PresentationType presentationType = presentation.getPresentationType();

            switch (presentationType) {
                case PD_PRESENTATION:
                    PDPresentation pdPresentation = (PDPresentation) presentation;
                    description = ShowInfo.getPdShowName(pdPresentation.getPdPresentationType(),
                            pdPresentation.getDeliveryYmd());
                    break;
                case CC_PRESENTATION:
                    CCPresentation ccPresentation = (CCPresentation) presentation;
                    description = ShowInfo.getCcShowName(ccPresentation.getYmd(),
                            ccPresentation.getShift());
                    break;
                case LASO_PRESENTATION:
                    LASOPresentation lasoPresentation = (LASOPresentation) presentation;
                    description = ShowInfo.getLasoShowName(lasoPresentation.getYmd(),
                            lasoPresentation.getShift());
                    break;
                case LO_PRESENTATION:
                    LOPresentation loPresentation = (LOPresentation)presentation;
                    description = ShowInfo.getLoShowName(loPresentation.getYmd(), loPresentation.getShift());
            }
        }

        return new AuditRecord(presentationId, user, lastModifiedMillis, description);
    }

    public class AuditRecord {

        private BigInteger presentationId;
        private String user;
        private Long lastModifiedMillis;
        private String description;

        public AuditRecord(BigInteger presentationId, String user, Long lastModifiedMillis,
                String description) {
            this.presentationId = presentationId;
            this.user = user;
            this.lastModifiedMillis = lastModifiedMillis;
            this.description = description;
        }

        public BigInteger getPresentationId() {
            return presentationId;
        }

        public void setPresentationId(BigInteger presentationId) {
            this.presentationId = presentationId;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public Long getLastModifiedMillis() {
            return lastModifiedMillis;
        }

        public void setLastModifiedMillis(Long lastModifiedMillis) {
            this.lastModifiedMillis = lastModifiedMillis;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
