package org.jlab.presenter.presentation.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.jlog.exception.AttachmentSizeException;
import org.jlab.jlog.exception.InvalidXMLException;
import org.jlab.jlog.exception.LogCertificateException;
import org.jlab.jlog.exception.LogIOException;
import org.jlab.jlog.exception.MalformedXMLException;
import org.jlab.jlog.exception.SchemaUnavailableException;
import org.jlab.presenter.business.session.PresentationFacade;
import org.jlab.presenter.business.util.IOUtil;
import org.jlab.presenter.persistence.entity.PDPresentation;
import org.jlab.presenter.persistence.enumeration.PresentationType;

/**
 *
 * @author ryans
 */
public class PresentationMenuUtil {

    private final static Logger logger =
            Logger.getLogger(PresentationMenuUtil.class.getName());

    public static void delete(HttpServletRequest request, HttpServletResponse response,
            BigInteger presentationId) throws IOException {
        response.sendRedirect("pd-menu");
    }

    private PresentationMenuUtil() {
        // Can't instantiate publicly
    }

    public static String getPdfUrl(String contextPath, BigInteger presentationId) {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<>();
        params.put("url", List.of(contextPath + "/presentation?presentationId=" + presentationId));
        params.put("type", List.of("pdf"));
        params.put("filename", List.of("presentation.pdf"));
        params.put("format", List.of("Letter"));
        params.put("landscape", List.of("true"));
        params.put("top", List.of("0.5in"));
        params.put("right", List.of("0.5in"));
        params.put("bottom", List.of("0.5in"));
        params.put("left", List.of("0.5in"));
        params.put("printBackground", List.of("true"));
        params.put("waitUntil", Arrays.asList("load", "networkidle2"));
        return contextPath + "/convert" + ServletUtil.buildQueryString(params, "UTF-8");
    }

    public static String getAbsolutePdfUrl(String contextPath, BigInteger presentationId) {
        return "https://" + System.getenv("PROXY_SERVER") + getPdfUrl(contextPath, presentationId);
    }

    public static void openOrExport(HttpServletRequest request,
            HttpServletResponse response, BigInteger presentationId)
            throws IOException {
        String action = request.getParameter("action");

        if ("Export PDF".equals(action)) {
            response.sendRedirect(PresentationMenuUtil.getPdfUrl(request.getContextPath(), presentationId));
        } else {
            response.sendRedirect(response.encodeRedirectURL(
                    request.getContextPath() + "/presentation?presentationId="
                    + presentationId + "#1"));
        }
    }

    private static File createPdf(File dir, String name, String contextPath, BigInteger presentationId) throws IOException {
        File tmp = new File(dir, name + ".pdf");

        if (!tmp.createNewFile()) {
            throw new IllegalStateException("Unable to create a new file: " + tmp.getAbsolutePath());
        }

        URL url = new URL(getAbsolutePdfUrl(contextPath, presentationId));

        InputStream in = null;
        FileOutputStream out = null;      
        
        try {
            in = url.openStream();
            out = new FileOutputStream(tmp);

            copy(in, out);            
        } finally {
            IOUtil.closeQuietly(in);
            IOUtil.closeQuietly(out);            
        }
        
        return tmp;
    }
    
    private static File imageDataUriToFile(File dir, String name, String data) throws IOException {
        if(data == null || !data.startsWith("data:image/")) {
            throw new IllegalArgumentException("Not an image data URI");
        }
        
        int dataStart = data.indexOf(',');
        int extEnd = data.indexOf(';');
        String ext = data.substring(11, extEnd);
        File file = new File(dir, name + "." + ext);
        
        if (!file.createNewFile()) {
            throw new IllegalStateException("Unable to create a new file: " + file.getAbsolutePath());
        }        
        
        InputStream in = null;
        FileOutputStream out = null;      
        
        try {
            in = new ByteArrayInputStream(IOUtil.decodeBase64(data.substring(dataStart)));
            out = new FileOutputStream(file);

            copy(in, out);            
        } finally {
            IOUtil.closeQuietly(in);
            IOUtil.closeQuietly(out);            
        }        
        
        return file;
    }
    
    private static List<File> createImageFiles(File dir, List<String> imageData) throws IOException {
        List<File> files = new ArrayList<File>();
        
        int num = 1;
        for(String data: imageData) {
            File f = imageDataUriToFile(dir, "image" + num++, data);
            files.add(f);
        }
        
        return files;
    }
    
    public static long log(
            BigInteger presentationId,
            String title,
            String body,
            String[] tags,
            String logbooks,
            PresentationFacade presentationFacade,
            List<String> imageData, PresentationType presentationType) throws IOException, SchemaUnavailableException,
            MalformedXMLException, InvalidXMLException, LogIOException, AttachmentSizeException,
            LogCertificateException {
        File dir = IOUtil.createTempDir();
        
        long logId;
        
        try {
            List<File> imageFiles = createImageFiles(dir, imageData);
            
            List<String> files = new ArrayList<String>();
            for(File f: imageFiles) {
                files.add(f.getAbsolutePath());
            }
            
            logId = presentationFacade.sendELog(title, body, true, tags, logbooks, files.toArray(new String[0]), presentationId, presentationType);
        } finally {

            for (File f : dir.listFiles()) {
                if (!f.delete()) {
                    logger.log(Level.WARNING, "Unable to delete temporary file: {0}", f.getAbsolutePath());
                }
            }

            if (!dir.delete()) {
                logger.log(Level.WARNING, "Unable to delete temporary directory: {0}", dir.getAbsolutePath());
            }
        }

        return logId;
    }

    public static void logAndRedirect(HttpServletRequest request,
                                      HttpServletResponse response,
                                      BigInteger presentationId,
                                      String title,
                                      String body,
                                      String[] tags,
                                      String logbooks,
                                      String redirect,
                                      PresentationFacade presentationFacade,
                                      List<String> imageData,
                                      PresentationType presentationType) throws ServletException, IOException {

        long logId = logWithPdf(request.getContextPath(), presentationId, title, body, tags, logbooks, presentationFacade,
                imageData, presentationType);

        response.sendRedirect(response.encodeRedirectURL(
                request.getContextPath() + redirect + "?elogId=" + logId));
    }

    public static long logWithPdf(String contextPath,
                                  BigInteger presentationId,
                                  String title,
                                  String body,
                                  String[] tags,
                                  String logbooks,
                                  PresentationFacade presentationFacade,
                                  List<String> imageData,
                                  PresentationType presentationType) {

        File dir = IOUtil.createTempDir();
        
        long logId;
        
        try {
            File pdf = createPdf(dir, title, contextPath, presentationId);
            List<File> imageFiles = createImageFiles(dir, imageData);
            
            List<String> files = new ArrayList<String>();
            for(File f: imageFiles) {
                files.add(f.getAbsolutePath());
            }
            files.add(pdf.getAbsolutePath());
            
            logId = presentationFacade.sendELog(title, body, true, tags, logbooks, files.toArray(new String[0]), presentationId, presentationType);
        } catch (Exception e) {
            logId = -1L;
            logger.log(Level.SEVERE, "Unable to write to elog", e);
        } finally {

            for (File f : dir.listFiles()) {
                if (!f.delete()) {
                    logger.log(Level.WARNING, "Unable to delete temporary file: {0}", f.getAbsolutePath());
                }
            }

            if (!dir.delete()) {
                logger.log(Level.WARNING, "Unable to delete temporary directory: {0}", dir.getAbsolutePath());
            }
        }
        return logId;
    }

    public static long copy(InputStream in, FileOutputStream out) throws IOException {
        ReadableByteChannel rbc = Channels.newChannel(in);
        return out.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    public static long logPd(PDPresentation presentation, PresentationFacade facade, String body,
                             List<String> images) throws ServletException,
            LogIOException, SchemaUnavailableException, MalformedXMLException, InvalidXMLException,
            AttachmentSizeException, IOException, LogCertificateException {
        String title = ShowInfo.getPdShowName(presentation.getPdPresentationType(),
                presentation.getDeliveryYmd());
        String[] tags = ShowInfo.getPdTags(presentation.getPdPresentationType());
        String logbooks = ShowInfo.getPdLogbooks();

        return PresentationMenuUtil.log(presentation.getPresentationId(),title, body, tags, logbooks,
                facade, images, presentation.getPresentationType());
    }
}
