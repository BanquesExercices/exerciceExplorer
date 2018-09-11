/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import java.awt.Frame;
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
    
    
}
