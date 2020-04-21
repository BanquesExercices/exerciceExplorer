/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import java.awt.Color;
import java.awt.Frame;
import java.text.Normalizer;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author mbrebion
 */
public class Utils {

    public static void showLongTextMessageInDialog(String longMessage, Frame frame) {
        JTextArea textArea = new JTextArea(18, 50);
        textArea.setText(longMessage);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(frame, scrollPane);
    }
    
    public static void showShortTextMessageInDialog(String longMessage, Frame frame) {
        JTextArea textArea = new JTextArea(4, 50);
        textArea.setText(longMessage);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(frame, scrollPane);
    }
    
     public static Color getColorFromString(String in){
            int r, g, b;
            r = Integer.decode(in.split(",")[0]);
            g = Integer.decode(in.split(",")[1]);
            b = Integer.decode(in.split(",")[2]);
           return new Color(r, g, b);
    }
     
     public static String getStringFromColor(Color c){
            return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
    }
    
    public static String stripAccents(String s) 
{
    String out  = Normalizer.normalize(s, Normalizer.Form.NFD);
    return out.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
}
}
