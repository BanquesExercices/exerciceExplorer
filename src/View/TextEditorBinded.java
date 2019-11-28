/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Helper.TextLineNumber;
import Helper.ExecCommand;
import Helper.SavedVariables;
import Helper.Utils;
import Helper.MyStyledEditorKit;
import Helper.CustomDocumentFilter;
import TexRessources.TexWriter;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 *
 * @author mbrebion
 */
@SuppressWarnings("serial")
public class TextEditorBinded extends javax.swing.JPanel {

    /**
     * Creates new form TextEditorBinded
     */
    public final static int exerciceFile = 1, texFile = 2, textFile = 0, keywordsFile=3 ; // used for syntax coloration
    protected int syntaxStyle = textFile; // default choice : no color

    protected CustomDocumentFilter cdf;
    protected File f;
    protected StyledDocument doc;
    protected List<Observer> obs;
    protected Observable myObs = new Observable();
    protected DocumentListener docList;
    protected boolean hasChanged = false;
    protected long lastUpdate;
    private UndoManager undoManager;
    protected UndoableEditListener uel;

    // menu bar
    protected AbstractAction previous, next, undo, redo, save;
    protected JMenu edition, source;
    protected JMenuBar menuBar;

    // speficic to tex files
    protected int questionAmount;
    protected ArrayList<String> previousTags = new ArrayList<>();

    public TextEditorBinded() {
        initComponents();

        uel = new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        };

        obs = new ArrayList<>();

        undoManager = new UndoManager();
        jTextPane1.setEditorKit(new MyStyledEditorKit());
        doc = jTextPane1.getStyledDocument();

        createMenuBar();

        docList = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                changeOccured();
                notifyAllObserver();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changeOccured();
                notifyAllObserver();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

                //changeOccured();
                //notifyAllObserver();
            }
        };

        doc.addDocumentListener(docList);

    }

    protected void changeOccured() {
        hasChanged = true;
        jButton1.setEnabled(true);
    }

    protected void resetHasChanged() {
        hasChanged = false;
        jButton1.setEnabled(false);
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public List<String> getText() {
        String[] split = jTextPane1.getText().split("\n");
        ArrayList<String> out = new ArrayList<>();
        Charset charset = Charset.forName("UTF-8"); // ensure only utf8 character
        for (String s : split) {
            out.add(charset.decode(charset.encode(s)).toString());
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
        }else if (path.endsWith("mots_clefs.txt")) {
            this.syntaxStyle = keywordsFile;
        }
        // default is textFile

        f = new File(path);

        this.updateView();
        this.resetHasChanged();
        this.registerUndoableManager();

    }

    public void registerUndoableManager() {
        doc.addUndoableEditListener(uel);
    }

    public void unRegisterUndoableManager() {
        doc.removeUndoableEditListener(uel);
    }

    protected boolean checkSafe() {
        // check that file as not been modified elesewhere
        boolean status = f.lastModified() <= (this.lastUpdate + 100);
        reloadButton.setVisible(!status);
        return status;
    }

    
    protected boolean saveFile() {
        if (hasChanged) { // prevent intempestive saving

            if (!this.checkSafe()) {
                String out = "Fichier " + f.getAbsolutePath() + " modifié depuis l'exterieur. Impossible de sauvegarder";
                System.err.println(out);
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                Utils.showShortTextMessageInDialog(out, topFrame);
                return false;
            }

            this.lastUpdate = System.currentTimeMillis();
            resetHasChanged();
            List<String> out = this.getText();
            if (this.syntaxStyle==keywordsFile) {
                Collator frCollator = Collator.getInstance(Locale.FRENCH);
                frCollator.setStrength(Collator.PRIMARY);
                Collections.sort(out, frCollator);
                System.out.println("PINGGG");
            }
            resetHasChanged();
            TexWriter.writeToFile(out, f.getAbsolutePath());
            notifyAllObserver("save");
            return true;
        }
        return false;
    }

    public void updateView() {
        List<String> lines = TexWriter.readFile(f.getAbsolutePath());
        this.lastUpdate = System.currentTimeMillis();

        this.clearDisplay();
        this.checkSafe();

        for (String line : lines) {
            append(line);
        }

        if (this.syntaxStyle == exerciceFile) {
            // numbering lines
            TextLineNumber tln = new TextLineNumber(jTextPane1);
            jScrollPane1.setRowHeaderView(tln);
            tln.setBorderGap(2);

            // coloring 
            cdf = new CustomDocumentFilter(this);
            ((AbstractDocument) jTextPane1.getDocument()).setDocumentFilter(cdf);

            // show question number according to carret
            jTextPane1.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    updateQuestionLabel();
                }
            });
        } else {
            this.texPanel.setVisible(false);
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
        changeOccured();
    }

    protected void clearDisplay() {
        jTextPane1.setText("");
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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jButton2 = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();
        texPanel = new javax.swing.JPanel();
        amountQLabel = new javax.swing.JLabel();
        versionChooserComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(50, 50));
        setPreferredSize(new java.awt.Dimension(364, 100));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_ic_save_48px_352084.png"))); // NOI18N
        jButton1.setToolTipText("Sauvegarder");
        jButton1.setBorder(null);
        jButton1.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_ic_save_48px_352084 _disabled.png"))); // NOI18N
        jButton1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_ic_save_48px_352084_selected.png"))); // NOI18N
        jButton1.setRolloverEnabled(true);
        jButton1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_ic_save_48px_352084_rollOver.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jScrollPane1.setMinimumSize(new java.awt.Dimension(50, 50));

        jTextPane1.setContentType("plain"); // NOI18N
        jTextPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_pen-checkbox_353430.png"))); // NOI18N
        jButton2.setToolTipText("Ouvrir dans un editeur externe");
        jButton2.setBorderPainted(false);
        jButton2.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_pen-checkbox_353430_selected.png"))); // NOI18N
        jButton2.setRolloverEnabled(true);
        jButton2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_pen-checkbox_353430_rollOver.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        reloadButton.setText("Recharger");
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        texPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                texPanelComponentShown(evt);
            }
        });

        amountQLabel.setText("  ");

        versionChooserComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "original" }));
        versionChooserComboBox.setMaximumSize(new java.awt.Dimension(200, 32767));
        versionChooserComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                versionChooserComboBoxActionPerformed(evt);
            }
        });

        jLabel2.setText("V :");

        javax.swing.GroupLayout texPanelLayout = new javax.swing.GroupLayout(texPanel);
        texPanel.setLayout(texPanelLayout);
        texPanelLayout.setHorizontalGroup(
            texPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(texPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(amountQLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(2, 2, 2)
                .addComponent(versionChooserComboBox, 0, 105, Short.MAX_VALUE)
                .addGap(2, 2, 2))
        );
        texPanelLayout.setVerticalGroup(
            texPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, texPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(texPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(amountQLabel)
                    .addComponent(versionChooserComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(texPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(reloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(texPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(reloadButton)))
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.saveFile();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (f == null) {
            return;
        }
        ExecCommand.execo(new String[]{SavedVariables.getOpenCmd(), f.getAbsolutePath()}, 0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        // force a update to the text file (usefull if an external change has been observed. 
        // In this case, it is not possible to save the file anymore
        this.updateView();
    }//GEN-LAST:event_reloadButtonActionPerformed

    private void versionChooserComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_versionChooserComboBoxActionPerformed
        this.cdf.setSelectedVersion((String) versionChooserComboBox.getSelectedItem());
    }//GEN-LAST:event_versionChooserComboBoxActionPerformed

    private void texPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_texPanelComponentShown
    }//GEN-LAST:event_texPanelComponentShown

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
    }//GEN-LAST:event_formFocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel amountQLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextPane jTextPane1;
    private javax.swing.JButton reloadButton;
    private javax.swing.JPanel texPanel;
    private javax.swing.JComboBox versionChooserComboBox;
    // End of variables declaration//GEN-END:variables

    public void notifyAllObserver(Object mess) {
        for (Observer ob : this.obs) {
            ob.update(myObs, mess);
        }
    }

    public void notifyAllObserver() {
        for (Observer ob : this.obs) {
            ob.update(myObs, ob);
        }
    }

    public void updateMenuBarView() {
        try {
            this.menuBar.add(edition);

            if (this.syntaxStyle == exerciceFile) {
                this.menuBar.add(source);
            }
        } catch (Exception e) {
        }

    }

    public void createMenuBar() {
        // actions

        previous = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int offset = TextEditorBinded.this.cdf.getPreviousOffset(TextEditorBinded.this.jTextPane1.getCaretPosition() - 1);
                TextEditorBinded.this.jTextPane1.setCaretPosition(offset);
            }
        };

        next = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int offset = TextEditorBinded.this.cdf.getNextOffset(TextEditorBinded.this.jTextPane1.getCaretPosition());
                TextEditorBinded.this.jTextPane1.setCaretPosition(offset);
            }
        };

        undo = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                } catch (CannotUndoException exp) {
                    exp.printStackTrace();
                }
            }
        };

        save = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextEditorBinded.this.saveFile();
            }
        };

        redo = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                } catch (CannotUndoException exp) {
                    exp.printStackTrace();
                }
            }
        };

        // menu items
        edition = new JMenu("Edition");

        JMenuItem undoItem = new JMenuItem("undo");
        edition.add(undoItem);
        undoItem.addActionListener(undo);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        JMenuItem redoItem = new JMenuItem("redo");
        edition.add(redoItem);
        redoItem.addActionListener(redo);
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));

        JMenuItem saveItem = new JMenuItem("sauvegarde");
        edition.add(saveItem);
        saveItem.addActionListener(save);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        source = new JMenu("source");

        JMenuItem nextItem = new JMenuItem("aller à la question suivante");
        source.add(nextItem);
        nextItem.addActionListener(next);
        nextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        JMenuItem previousItem = new JMenuItem("aller à la question précédente");
        source.add(previousItem);
        previousItem.addActionListener(previous);
        previousItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    }

    public void setMenuBar(JMenuBar menuBar) {
        this.menuBar = menuBar;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////   specific to exercice files      ///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void updateColoring() {
        this.cdf.updateTextStyles(-1);
    }

    /**
     * @return the questionAmount
     */
    public int getQuestionAmount() {
        return questionAmount;
    }

    /**
     * @param questionAmount the questionAmount to set
     */
    public void setQuestionAmount(int questionAmount) {
        if (questionAmount != this.questionAmount) {
            this.questionAmount = questionAmount;
            updateQuestionLabel();
        }
    }

    public void updateQuestionLabel() {
        this.amountQLabel.setText("Q : " + this.cdf.getQuestionNumber(this.jTextPane1.getCaretPosition()) + "/" + this.questionAmount);
    }

    public void updateVersionTags() {
        if (!previousTags.equals(this.cdf.versionTags)) {
            String current = (String) this.versionChooserComboBox.getSelectedItem();
            this.versionChooserComboBox.removeAllItems();

            if (!this.cdf.versionTags.contains("original")) {
                this.versionChooserComboBox.addItem("original");
            }

            for (String version : this.cdf.versionTags) {
                this.versionChooserComboBox.addItem(version);
            }
            this.previousTags = (ArrayList<String>) this.cdf.versionTags.clone();
            this.versionChooserComboBox.setSelectedItem(current);
        }

    }

}

///////////////// word wrapping fix : see https://stackoverflow.com/questions/11000220/strange-text-wrapping-with-styled-text-in-jtextpane-with-java-7

