package gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Utils {
    public static String getFileExtension(String name){

        int pointIndex = name.lastIndexOf(".");
        if(pointIndex == -1){
            return null;
        }
        if(pointIndex == name.length() - 1){
            return null;
        }
        return name.substring(pointIndex+1);
    }
    public static ImageIcon createImage(String path){
        URL url = System.class.getResource(path);
        if(url == null){
            System.err.println("Unable to load image at: " + path);
            return null;
        }
        return new ImageIcon(url);
    }
    public static Font createFont(String path){
        Font font = null;
        URL url = System.class.getResource(path);
        if(url == null){
            System.err.println("Unable to load font at: " + path);
            return null;
        }
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, url.openStream());
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return font;
    }
}
