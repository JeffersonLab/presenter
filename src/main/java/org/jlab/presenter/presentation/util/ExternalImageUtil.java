package org.jlab.presenter.presentation.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.jlab.presenter.business.util.IOUtil;
import org.jlab.presenter.persistence.entity.ImageSlide;

/**
 *
 * @author ryans
 */
public class ExternalImageUtil {

    public static final String DAILY_GRAPH_URL = "http://opsweb/performance-plots/daily_graph.gif";
    public static final String WEEKLY_GRAPH_URL = "http://opsweb/performance-plots/weekly_graph.gif";
    public static final String FSD_GRAPH_URL = "http://opsweb/performance-plots/fsddata.gif";
    public static final String CHARGE_GRAPH_URL = "http://opsweb/performance-plots/charge.png";
    
    private ExternalImageUtil() {
        // Can't instantiate publicly
    }

    public static ImageSlide getDailyGraph() throws IOException {
        ImageSlide slide = new ImageSlide();
        
        String imageUri = getImageDataUri(DAILY_GRAPH_URL, "gif");
        slide.setImageUrl(imageUri);
        slide.setLabel("Daily Time Accounting");
        
        return slide;
    }    
    
    public static ImageSlide getWeeklyGraph() throws IOException {
        ImageSlide slide = new ImageSlide();
        
        String imageUri = getImageDataUri(WEEKLY_GRAPH_URL, "gif");
        slide.setImageUrl(imageUri);
        slide.setLabel("Weekly Time Accounting");
        
        return slide;
    }
    
    public static ImageSlide getFsdGraph() throws IOException {
        ImageSlide slide = new ImageSlide();
        
        String imageUri = getImageDataUri(FSD_GRAPH_URL, "gif");
        slide.setImageUrl(imageUri);
        slide.setLabel("FSD Summary");
        
        return slide;
    }
    
    public static ImageSlide getChargeGraph() throws IOException {
        ImageSlide slide = new ImageSlide();
        
        String imageUri = getImageDataUri(CHARGE_GRAPH_URL, "png");
        slide.setImageUrl(imageUri);
        slide.setLabel("Gun Charge Plot");        
        
        return slide;
    }
    
    public static String getImageDataUri(String url, String type) throws IOException {
        URL u = new URL(url);
        
        InputStream in = u.openStream();
        byte[] bytes = IOUtil.inputStreamToBytes(in);
        String data = IOUtil.encodeBase64(bytes);
   
        String header = "data:image/";
        String encoding = ";base64,";
        String builder = header +
                type +
                encoding +
                data;
        
        return builder;
    }
}
