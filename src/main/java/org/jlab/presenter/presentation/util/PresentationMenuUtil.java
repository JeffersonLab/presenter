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
import org.jlab.presenter.persistence.entity.*;
import org.jlab.presenter.persistence.enumeration.PresentationType;

/**
 * @author ryans
 */
public class PresentationMenuUtil {

  private static final Logger logger = Logger.getLogger(PresentationMenuUtil.class.getName());

  public static void delete(
      HttpServletRequest request, HttpServletResponse response, BigInteger presentationId)
      throws IOException {
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
    return System.getenv("FRONTEND_SERVER_URL") + getPdfUrl(contextPath, presentationId);
  }

  public static void openOrExport(
      HttpServletRequest request, HttpServletResponse response, BigInteger presentationId)
      throws IOException {
    String action = request.getParameter("action");

    if ("Export PDF".equals(action)) {
      response.sendRedirect(
          PresentationMenuUtil.getPdfUrl(request.getContextPath(), presentationId));
    } else {
      response.sendRedirect(
          response.encodeRedirectURL(
              request.getContextPath() + "/presentation?presentationId=" + presentationId + "#1"));
    }
  }

  private static File createPdf(
      File dir, String name, String contextPath, BigInteger presentationId) throws IOException {
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
    if (data == null || !data.startsWith("data:image/")) {
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
    for (String data : imageData) {
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
      List<String> imageData,
      PresentationType presentationType)
      throws IOException,
          SchemaUnavailableException,
          MalformedXMLException,
          InvalidXMLException,
          LogIOException,
          AttachmentSizeException,
          LogCertificateException {
    File dir = IOUtil.createTempDir();

    long logId;

    try {
      List<File> imageFiles = createImageFiles(dir, imageData);

      List<String> files = new ArrayList<String>();
      for (File f : imageFiles) {
        files.add(f.getAbsolutePath());
      }

      logId =
          presentationFacade.sendELog(
              title,
              body,
              true,
              tags,
              logbooks,
              files.toArray(new String[0]),
              presentationId,
              presentationType);
    } finally {

      for (File f : dir.listFiles()) {
        if (!f.delete()) {
          logger.log(Level.WARNING, "Unable to delete temporary file: {0}", f.getAbsolutePath());
        }
      }

      if (!dir.delete()) {
        logger.log(
            Level.WARNING, "Unable to delete temporary directory: {0}", dir.getAbsolutePath());
      }
    }

    return logId;
  }

  public static long copy(InputStream in, FileOutputStream out) throws IOException {
    ReadableByteChannel rbc = Channels.newChannel(in);
    return out.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
  }

  public static long logPd(
      PDPresentation presentation, PresentationFacade facade, String body, List<String> images)
      throws LogIOException,
          SchemaUnavailableException,
          MalformedXMLException,
          InvalidXMLException,
          AttachmentSizeException,
          IOException,
          LogCertificateException {
    String title =
        ShowInfo.getPdShowName(presentation.getPdPresentationType(), presentation.getDeliveryYmd());
    String[] tags = ShowInfo.getPdTags(presentation.getPdPresentationType());
    String logbooks = ShowInfo.getPdLogbooks();

    return PresentationMenuUtil.log(
        presentation.getPresentationId(),
        title,
        body,
        tags,
        logbooks,
        facade,
        images,
        presentation.getPresentationType());
  }

  public static long logLaso(
      LASOPresentation presentation, PresentationFacade facade, String body, List<String> images)
      throws LogIOException,
          SchemaUnavailableException,
          MalformedXMLException,
          InvalidXMLException,
          AttachmentSizeException,
          IOException,
          LogCertificateException {
    String title = ShowInfo.getLasoShowName(presentation.getYmd(), presentation.getShift());
    String[] tags = ShowInfo.getLasoTags();
    String logbooks = ShowInfo.getLasoLogbooks();

    return PresentationMenuUtil.log(
        presentation.getPresentationId(),
        title,
        body,
        tags,
        logbooks,
        facade,
        images,
        presentation.getPresentationType());
  }

  public static long logUitf(
      UITFPresentation presentation, PresentationFacade facade, String body, List<String> images)
      throws LogIOException,
          SchemaUnavailableException,
          MalformedXMLException,
          InvalidXMLException,
          AttachmentSizeException,
          IOException,
          LogCertificateException {
    String title = ShowInfo.getUitfShowName(presentation.getYmd(), presentation.getShift());
    String[] tags = ShowInfo.getUitfTags();
    String logbooks = ShowInfo.getUitfLogbooks();

    return PresentationMenuUtil.log(
        presentation.getPresentationId(),
        title,
        body,
        tags,
        logbooks,
        facade,
        images,
        presentation.getPresentationType());
  }

  public static long logCc(
      CCPresentation presentation, PresentationFacade facade, String body, List<String> images)
      throws LogIOException,
          SchemaUnavailableException,
          MalformedXMLException,
          InvalidXMLException,
          AttachmentSizeException,
          IOException,
          LogCertificateException {
    String title = ShowInfo.getCcShowName(presentation.getYmd(), presentation.getShift());
    String[] tags = ShowInfo.getCcTags();
    String logbooks = ShowInfo.getCcLogbooks();

    return PresentationMenuUtil.log(
        presentation.getPresentationId(),
        title,
        body,
        tags,
        logbooks,
        facade,
        images,
        presentation.getPresentationType());
  }

  public static long logLo(
      LOPresentation presentation, PresentationFacade facade, String body, List<String> images)
      throws LogIOException,
          SchemaUnavailableException,
          MalformedXMLException,
          InvalidXMLException,
          AttachmentSizeException,
          IOException,
          LogCertificateException {
    String title = ShowInfo.getLoShowName(presentation.getYmd(), presentation.getShift());
    String[] tags = ShowInfo.getCcTags();
    String logbooks = ShowInfo.getLoLogbooks();

    return PresentationMenuUtil.log(
        presentation.getPresentationId(),
        title,
        body,
        tags,
        logbooks,
        facade,
        images,
        presentation.getPresentationType());
  }
}
