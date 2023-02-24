package org.jlab.presenter.presentation.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ryans
 */
public class ServletUtil {
    private ServletUtil() {
        // Can't instantiate publicly
    }    
    
    public static String buildQueryString(Map<String, List<String>> params, String encoding) {
        StringBuilder builder = new StringBuilder();

        for (String key : params.keySet()) {
            if (key == null || key.isEmpty()) {
                continue;
            }
            List<String> values = params.get(key);
            for (String value : values) {
                if (value == null) {
                    value = "";
                }
                builder.append("&");
                try {
                    builder.append(URLEncoder.encode(key, encoding));
                    builder.append("=");
                    builder.append(URLEncoder.encode(value, encoding));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("JVM doesn't support encoding: " + encoding, e);
                }
            }
        }

        if (builder.length() > 0) {
            builder.replace(0, 0, "?");
        }

        return builder.toString();
    }    
}
