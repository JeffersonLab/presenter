package org.jlab.presenter.presentation.controller;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

        String username = request.getUserPrincipal().getName();

        if (username == null || username.isEmpty() || username.equalsIgnoreCase("ANONYMOUS")) {
            throw new ServletException(
                    "You must be authenticated to perform the requested operation.  Your session may have expired.  Please re-login.");
        }

        String resizeServer = System.getenv("RESIZE_URL");

        //response.setHeader("content-type", "application/png");
        resizeServer = resizeServer + "/convert";

        LOGGER.log(Level.INFO, "Resize URL Request: " + resizeServer);

        /**
         * Amazingly, sending multipart (binary data) form post is not supported in Java 11 or Java EE 8.
         * Looks like maybe supported in Jakarta EE 10, which just came out a few months ago (September 2022).
         * Uploading a binary file on the web was defined in 1998 (https://www.rfc-editor.org/rfc/rfc2388).
         *
         * https://github.com/jakartaee/rest/issues/418
         *
         * Apparently gold standard is still https://hc.apache.org/
         *
         * We need to POST with multipart/form-data instead of application/x-www-form-urlencoded
         *
         * */

        HttpClient client = HttpClient.newHttpClient();

        ServletInputStream sis = request.getInputStream();

        HttpRequest resizeRequest = HttpRequest.newBuilder()
                .uri(URI.create(resizeServer))
                .POST(HttpRequest.BodyPublishers.ofInputStream(() -> sis))
                .build();

        try {
            HttpResponse<InputStream> resizeResponse = client.send(
                    resizeRequest, HttpResponse.BodyHandlers.ofInputStream());

            InputStream in = resizeResponse.body();
            OutputStream out = response.getOutputStream();

            in.transferTo(out);

            in.close();
        } catch(InterruptedException e) {
            throw new ServletException("Unable to proxy resize", e);
        }
    }
}
