package org.jlab.presenter.presentation.controller;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.jlab.presenter.presentation.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet controller for proxying requests to Image Magic Resize service even for external / public users.
 *
 * This controller only allows relative URLs (relative to hosting server).
 *
 * @author ryans
 */
@WebServlet(name = "Resize", urlPatterns = {"/resize"})
@MultipartConfig(maxFileSize = 15728640, maxRequestSize = 20971520)
public class Resize extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(
            Resize.class.getName());

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // All of this proxy complexity is really just to ensure only authenticated users are using the resize service.
        String username = request.getUserPrincipal().getName();

        if (username == null || username.isEmpty() || username.equalsIgnoreCase("ANONYMOUS")) {
            throw new ServletException(
                    "You must be authenticated to perform the requested operation.  Your session may have expired.  Please re-login.");
        }

        String resizeServer = System.getenv("RESIZE_URL");

        resizeServer = resizeServer + "/convert";


        /**
         * Amazingly, sending multipart (binary data) form post is not supported in Java 11 or Java EE 8.
         * Looks like maybe supported in Jakarta EE 10, which just came out a few months ago (September 2022).
         * Uploading a binary file on the web was defined in 1998 (https://www.rfc-editor.org/rfc/rfc2388).
         *
         * Note: receiving multipart data IS supported.
         *
         * https://github.com/jakartaee/rest/issues/418
         *
         * Apparently gold standard is still https://hc.apache.org/
         *
         * We need to POST with multipart/form-data instead of application/x-www-form-urlencoded
         *
         * */

        Part filePart = request.getPart("file");
        String width = request.getParameter("width");
        String height = request.getParameter("height");
        String allowStretching = request.getParameter("allow-stretching");
        String ignoreAspectRatio = request.getParameter("ignore-aspect-ratio");
        String outputDataUri = request.getParameter("output-data-uri");
        String forcePng = request.getParameter("force-png");

        /*Map<String, List<String>> params = new HashMap<>();

        if (width != null) { params.put("width", List.of(width)); }
        if (height != null) { params.put("height", List.of(height)); }
        if (allowStretching != null) { params.put("allowStretching", List.of(allowStretching)); }
        if (ignoreAspectRatio != null) { params.put("ignoreAspectRatio", List.of(ignoreAspectRatio)); }
        if (outputDataUri != null) { params.put("outputDataUri", List.of(outputDataUri)); }
        if (forcePng != null) { params.put("forcePng", List.of(forcePng)); }

        String queryParams = ServletUtil.buildQueryString(params, "UTF-8");

        resizeServer = resizeServer + queryParams;*/

        LOGGER.log(Level.FINE, "Resize URL Request: " + resizeServer);

        HttpClient client = HttpClient.newHttpClient();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .addBinaryBody(filePart.getName(),
                        filePart.getInputStream(),
                        ContentType.parse(filePart.getContentType()),
                        filePart.getSubmittedFileName());

        if (width != null) { builder.addTextBody("width", width); }
        if (height != null) { builder.addTextBody("height", height); }
        if (allowStretching != null) { builder.addTextBody("allow-stretching", allowStretching); }
        if (ignoreAspectRatio != null) { builder.addTextBody("ignore-aspect-ratio", ignoreAspectRatio); }
        if (outputDataUri != null) { builder.addTextBody("output-data-uri", outputDataUri); }
        if (forcePng != null) { builder.addTextBody("force-png", forcePng); }

        HttpEntity httpEntity = builder.build();

        Pipe pipe = Pipe.open();

        new Thread(() -> {
            try (OutputStream outputStream = Channels.newOutputStream(pipe.sink())) {
                httpEntity.writeTo(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        System.out.println("Content-Type: " + httpEntity.getContentType().getValue());

        HttpRequest resizeRequest = HttpRequest.newBuilder()
                .uri(URI.create(resizeServer))
                .header("Content-Type", httpEntity.getContentType().getValue())
                .POST(HttpRequest.BodyPublishers.ofInputStream(() -> Channels.newInputStream(pipe.source())))
                .build();

        try {
            HttpResponse<InputStream> resizeResponse = client.send(
                    resizeRequest, HttpResponse.BodyHandlers.ofInputStream());

            int statusCode = resizeResponse.statusCode();

            LOGGER.log(Level.FINE, "resize status code: " + statusCode);

            HttpHeaders headers = resizeResponse.headers();

            Optional<String> contentType = headers.firstValue("Content-Type");
            Optional<String> contentDisposition = headers.firstValue("Content-Disposition");

            if(contentType.isPresent()) {
                LOGGER.log(Level.FINE, "resize response Content-Type: " + contentType.get());
                response.setHeader("Content-Type", contentType.get());
            }

            if(contentDisposition.isPresent()) {
                LOGGER.log(Level.FINE, "resize response Content-Disposition: " + contentDisposition.get());
                response.setHeader("Content-Disposition", contentDisposition.get());
            }

            InputStream in = resizeResponse.body();
            OutputStream out = response.getOutputStream();

            in.transferTo(out);

            in.close();
        } catch(InterruptedException e) {
            throw new ServletException("Unable to proxy resize", e);
        }
    }
}
