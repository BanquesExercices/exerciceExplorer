/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import java.awt.Color;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.text.Normalizer;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

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
    
    /**
 * Installs a listener to receive notification when the text of any
 * {@code JTextComponent} is changed. Internally, it installs a
 * {@link DocumentListener} on the text component's {@link Document},
 * and a {@link PropertyChangeListener} on the text component to detect
 * if the {@code Document} itself is replaced.
 * 
 * @param text any text component, such as a {@link JTextField}
 *        or {@link JTextArea}
 * @param changeListener a listener to receieve {@link ChangeEvent}s
 *        when the text is changed; the source object for the events
 *        will be the text component
 * @throws NullPointerException if either parameter is null
 */
public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
    Objects.requireNonNull(text);
    Objects.requireNonNull(changeListener);
    DocumentListener dl = new DocumentListener() {
        private int lastChange = 0, lastNotifiedChange = 0;

        @Override
        public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            lastChange++;
            SwingUtilities.invokeLater(() -> {
                if (lastNotifiedChange != lastChange) {
                    lastNotifiedChange = lastChange;
                    changeListener.stateChanged(new ChangeEvent(text));
                }
            });
        }
    };
    text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
        Document d1 = (Document)e.getOldValue();
        Document d2 = (Document)e.getNewValue();
        if (d1 != null) {
            d1.removeDocumentListener(dl);
        }
        if (d2 != null) {
            d2.addDocumentListener(dl);
        }
        dl.changedUpdate(null);
    });
    Document d = text.getDocument();
    if (d != null) {
        d.addDocumentListener(dl);
    }
}
    
}


