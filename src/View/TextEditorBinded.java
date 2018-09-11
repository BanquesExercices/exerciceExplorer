/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Helper.ObservableInterface;
import TexRessources.TexWriter;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author mbrebion
 */
public class TextEditorBinded extends javax.swing.JPanel implements ObservableInterface {

    /**
     * Creates new form TextEditorBinded
     */
    public final static int exerciceFile = 1, texFile = 2, textFile = 0; // used for syntax coloration
    protected int syntaxStyle = textFile; // default choice : no color

    protected HashMap<String, SimpleAttributeSet> corresp = new HashMap<>();
    SimpleAttributeSet blackSet = new SimpleAttributeSet();
    
    protected File f;
    protected StyledDocument doc;
    protected List<Observer> obs;
    protected Observable myObs = new Observable();
    protected DocumentListener docList;
    
    public TextEditorBinded() {
        initComponents();
        
        StyleConstants.setItalic(blackSet, true);
        StyleConstants.setForeground(blackSet, Color.black);
        obs = new ArrayList<>();
        doc = jTextPane1.getStyledDocument();
        docList = new DocumentListener() {
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (TextEditorBinded.this.jCheckBox1.isSelected()) {
                    TextEditorBinded.this.SaveFile();
                } else {
                    TextEditorBinded.this.jButton1.setFont(TextEditorBinded.this.jButton1.getFont().deriveFont(Font.BOLD));
                }
                
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        processChangedLines(e.getDocument(), e.getOffset(), e.getLength());
                    }
                });
                
                notifyAllObservers();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (TextEditorBinded.this.jCheckBox1.isSelected()) {
                    TextEditorBinded.this.SaveFile();
                } else {
                    TextEditorBinded.this.jButton1.setFont(TextEditorBinded.this.jButton1.getFont().deriveFont(Font.BOLD));
                }
                notifyAllObservers();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                if (TextEditorBinded.this.jCheckBox1.isSelected()) {
                    TextEditorBinded.this.SaveFile();
                } else {
                    TextEditorBinded.this.jButton1.setFont(TextEditorBinded.this.jButton1.getFont().deriveFont(Font.BOLD));
                }
                notifyAllObservers();
            }
        };
        
        doc.addDocumentListener(docList);
    }
    
    public List<String> getText() {
        String[] split = jTextPane1.getText().split("\n");
        ArrayList<String> out = new ArrayList<>();
        for (String s : split) {
            out.add(s);
        }
        return out;
    }
    
    public void addObserver(Observer ob) {
        this.obs.add(ob);
    }
    
    public void removeObserver(Observer ob) {
        this.obs.remove(ob);
    }
    
    public void bindToFile(String path) {
        // set syntax color mode according to file kind.
        if (path.endsWith("sujet.tex")) {
            this.syntaxStyle = exerciceFile;
        } else if (path.endsWith(".tex")) {
            this.syntaxStyle = texFile;
        }
        this.setCorresp();
        f = new File(path);
        this.updateView();
    }
    
    protected void SaveFile() {
        TexWriter.writeToFile(getText(), f.getAbsolutePath());
    }
    
    public void updateView() {
        List<String> lines = TexWriter.readFile(f.getAbsolutePath());
        for (String line : lines) {
            append(line);
        }
        
    }
    
    public void append(String s) {
        try {
            if (doc.getLength() == 0) {
                doc.insertString(0, s, null);
            } else {
                doc.insertString(doc.getLength(), "\n" + s, null);
            }
            
        } catch (BadLocationException ex) {
            Logger.getLogger(TextEditorBinded.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        setMinimumSize(new java.awt.Dimension(50, 50));
        setPreferredSize(new java.awt.Dimension(364, 100));

        jButton1.setText("Sauver");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Auto save");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jScrollPane1.setMinimumSize(new java.awt.Dimension(50, 50));
        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 185, Short.MAX_VALUE)
                .addComponent(jButton1))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.SaveFile();
        TextEditorBinded.this.jButton1.setFont(TextEditorBinded.this.jButton1.getFont().deriveFont(Font.PLAIN));
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        this.jButton1.setVisible(!jCheckBox1.isSelected());

    }//GEN-LAST:event_jCheckBox1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void notifyAllObservers() {
        for (Observer ob : this.obs) {
            ob.update(myObs, ob);
        }
    }

    ///////////////// Syntax coloration
    protected void processChangedLines(Document doc, int offset, int length) {
        Element rootElement = doc.getDefaultRootElement();
        int startLine = rootElement.getElementIndex(offset);
        int endLine = rootElement.getElementIndex(offset + length);
        
        for (int i = startLine; i <= endLine; i++) {
            try {
                int lineStart = rootElement.getElement(i).getStartOffset();
                int lineEnd = rootElement.getElement(i).getEndOffset() - 1;
                String lineText = doc.getText(lineStart, lineEnd - lineStart);
                applyHighlighting(doc, lineText, lineStart);
                
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditorBinded.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    protected void applyHighlighting(Document doc, String text, int offset) {
        boolean toBeColored = false;
        for (String s : corresp.keySet()) {
            if (text.contains(s)) {
                toBeColored = true;
            }
        }
        if (!toBeColored) {
            return;
        }
        // if here, at least one word must be colored
        doc.removeDocumentListener(docList); // prevent infinite loop 
        
        try {
            // we first put normal color everywhere in the line : 
            doc.remove(offset, text.length());
            doc.insertString(offset, text, blackSet);
            
            // we then check syntax words 
            for (String s : corresp.keySet()) {
                int begin = text.indexOf(s);
                
                if (begin > -1) {
                    System.out.println(begin);
                    doc.remove(begin + offset, s.length());
                    doc.insertString(begin + offset, s, corresp.get(s));
                }
            }
            
        } catch (BadLocationException ex) {
            System.err.println(ex.getMessage() + " | " + ex.offsetRequested());
        }
        doc.addDocumentListener(docList);
    }
    
    protected void setCorresp() {
        if (this.syntaxStyle == exerciceFile) {
            this.corresp.clear();
            SimpleAttributeSet redset = new SimpleAttributeSet();
            StyleConstants.setItalic(redset, true);
            StyleConstants.setForeground(redset, Color.red);
            
            SimpleAttributeSet blueset = new SimpleAttributeSet();
            StyleConstants.setItalic(blueset, true);
            StyleConstants.setForeground(blueset, Color.blue);
            
            this.corresp.put("\\addQ", redset);
            this.corresp.put("\\partie", blueset);
            this.corresp.put("\\sousPartie", blueset);
            this.corresp.put("\\enonce", blueset);
            
        }
    }
}
