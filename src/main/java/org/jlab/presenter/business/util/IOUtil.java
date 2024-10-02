package org.jlab.presenter.business.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

public class IOUtil {

    private static final Logger logger = Logger.getLogger(
            IOUtil.class.getName());

    private IOUtil() {
        // Can't instantiate publicly
    }

    /**
     * Reads in an InputStream fully and returns the result as a String.
     *
     * @param is The InputStream
     * @param encoding The character encoding of the String
     * @return The String representation of the data
     */
    public static String streamToString(InputStream is, String encoding) {
        String str = "";

        Scanner scan = new Scanner(is, encoding).useDelimiter("\\A");

        if (scan.hasNext()) {
            str = scan.next();
        }

        return str;
    }

    /**
     * Closes a Closeable without generating any checked Exceptions. If an
     * IOException does occur while closing it is logged as a WARNING.
     *
     * @param c The Closeable
     */
    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                // Supressed, but logged
                logger.log(Level.WARNING, "Unable to close resource.", e);
            }
        }
    }

    public static <T> T nvl(T a, T b) {
        return (a == null) ? b : a;
    }

    public static File createTempDir() {
        final int TEMP_DIR_ATTEMPTS = 10;
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = System.currentTimeMillis() + "-";

        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within "
                + TEMP_DIR_ATTEMPTS + " attempts (tried "
                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }

    /**
     * Encodes an array of bytes to base64.
     *
     * @param data The bytes
     * @return A base64 encoded String
     */
    public static String encodeBase64(byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }

    /**
     * Decodes a base64 String to an array of bytes.
     *
     * @param data The base64 encoded String
     * @return The bytes
     */
    public static byte[] decodeBase64(String data) {
        return DatatypeConverter.parseBase64Binary(data);
    }

    public static byte[] inputStreamToBytes(final InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = in.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }
    
    /* My implementation is terrible so just delegate to fn:escapeXml taglib impl instead */
    public static String escapeXml(String input) {

        // Avoid NPE
        if(input == null) {
            input = "";
        }

        /*String output = input;

        // Note: if input contains entities already this will break!
        
        if (input != null) {
            output = output.replace("&", "&#038;"); // Must do this one first as & within other replacements
            output = output.replace("\"", "&#034;");            
            output = output.replace("'", "&#039;");
            output = output.replace("<", "&#060;");
            output = output.replace(">", "&#062;");
        }
        return output;*/
        
        return org.apache.taglibs.standard.functions.Functions.escapeXml(input);
    }


    public static String doHtmlGet(String urlStr, int connectTimeout, int readTimeout) throws IOException {
        URL url;
        HttpURLConnection con;

        url = new URL(urlStr);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        con.setConnectTimeout(connectTimeout);
        con.setReadTimeout(readTimeout);

        return streamToString(con.getInputStream(), "UTF-8");
    }

    public static String doHtmlPost(String urlStr, int connectTimeout, int readTimeout) throws IOException {
        URL url;
        HttpURLConnection con;
   
        url = new URL(urlStr);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        
        con.setConnectTimeout(connectTimeout);
        con.setReadTimeout(readTimeout);
        
        return streamToString(con.getInputStream(), "UTF-8");
    }
}
