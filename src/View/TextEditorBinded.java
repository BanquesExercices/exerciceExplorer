/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Helper.OsRelated;
import Helper.SavedVariables;
import Helper.Utils;
import TextEditor.Base.BaseTextEditor;
import TextEditor.Tex.LatexTextEditor;
import TextEditor.Tex.Coloring.LatexTokenMaker;
import TextEditor.Text.NormalTextEditor;
import java.io.File;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import TextEditor.Base.FileProcessor;
import java.awt.Color;
import java.awt.Font;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

/**
 *
 * @author mbrebion
 */
@SuppressWarnings("serial")
public class TextEditorBinded extends javax.swing.JPanel implements FileProcessor, Observer {

    /**
     * Creates new form TextEditorBinded
     */
    public final static int exerciceFile = 1, texFile = 2, textFile = 0, keywordsFile = 3; // used for syntax coloration
    protected int syntaxStyle = textFile; // default choice : no color

    protected File f;
    protected List<Observer> obs;
    protected Observable myObs = new Observable();
    protected DocumentListener docList;

    protected boolean hasChanged = false;
    protected long lastUpdate;

// menu bar
    protected AbstractAction previous, next, undo, redo, save;
    protected JMenu edition, source;
    protected JMenuBar menuBar;

    // speficic to tex files
    protected ArrayList<String> previousTags = new ArrayList<>();
    // TextArea
    public BaseTextEditor textArea;

    // find and replace
    protected SearchContext context;

    public TextEditorBinded() {
        initComponents();
        obs = new ArrayList<>();

        // specific to find and replace
        findTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                TextEditorBinded.this.findAction(true,TextEditorBinded.this.findTextField.getText() );
            }

            public void removeUpdate(DocumentEvent e) {
                TextEditorBinded.this.findAction(true,TextEditorBinded.this.findTextField.getText());
            }

            public void insertUpdate(DocumentEvent e) {
                TextEditorBinded.this.findAction(true,TextEditorBinded.this.findTextField.getText());
            }
        });
        
        replaceWhatTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                TextEditorBinded.this.findAction(true,TextEditorBinded.this.replaceWhatTextField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                TextEditorBinded.this.findAction(true,TextEditorBinded.this.replaceWhatTextField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                TextEditorBinded.this.findAction(true,TextEditorBinded.this.replaceWhatTextField.getText());
            }
        });

        
        //jButton2.setBorderPainted(false); 
        jButton2.setContentAreaFilled(false); 
        //jButton2.setFocusPainted(false); 
        //jButton2.setOpaque(false);
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

        String[] split = textArea.getText().split("\n");
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

        JScrollPane sp = LatexTextEditor.getMyTextAreaInScrollPane(textArea);
        jPanel1.add(sp);

        if (path.endsWith("sujet.tex")) {
            this.syntaxStyle = exerciceFile;
            textArea = new LatexTextEditor();
            ((LatexTextEditor) textArea).addObserver(this);

        } else if (path.endsWith(".tex")) {
            this.syntaxStyle = texFile;
            textArea = new LatexTextEditor();

        } else if (path.endsWith("mots_clefs.txt")) {
            this.syntaxStyle = keywordsFile;
            textArea = new NormalTextEditor();
        } else {
            // default is textFile
            this.syntaxStyle = textFile;
            textArea = new NormalTextEditor();
            
        }

        textArea.dealWithFileProcessorJMenu(this); // enabling saving from JMenuBar

        jPanel1.removeAll();
        jPanel1.add(BaseTextEditor.getMyTextAreaInScrollPane(textArea));
        f = new File(path);
        this.updateView();
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                TextEditorBinded.this.changeOccured();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                TextEditorBinded.this.changeOccured();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        textArea.setCaretPosition(0);
    }

    public void updateView() {

        String Content = OsRelated.readFileOneString(f.getAbsolutePath());
        this.lastUpdate = System.currentTimeMillis();
        this.textArea.setText(Content);
        this.textArea.discardAllEdits();
        this.checkSafe();

        if (this.syntaxStyle == exerciceFile) {

            // show question number according to carret
            textArea.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    updateQuestionLabel();
                }
            });
        } else {
            this.texPanel.setVisible(false);
        }
        this.resetHasChanged();
    }

    protected boolean checkSafe() {
        // check that file as not been modified elesewhere
        boolean status = f.lastModified() <= (this.lastUpdate + 100);
        reloadButton.setVisible(!status);
        return status;
    }

    
    
   

    ///////////////////////////////////////////////
    ////////// find and replace  //////////////////
    ///////////////////////////////////////////////
    
    /////////////////// find //////////////////////
    
    @Override
    public void toggleFindPanel() {
        if (!this.findPanel.isVisible()) {
            this.startFindPanel();
        } else {
            this.endFindPanel();
        }
    }
    
    public void setupFindAction(String expr) {
        context = new SearchContext();
        context.setSearchFor(expr);
        context.setMatchCase(true);
        context.setRegularExpression(false);
    }

    
     public int findAction(boolean forward, String text) {
        this.setupFindAction(text);
        int caretLoc = textArea.getCaretPosition();
        context.setSearchForward(forward);
        
        SearchResult sr = SearchEngine.find(textArea, context);
        this.nbLabel.setText("  ("+ sr.getMarkedCount() + ")  ");
        this.replaceNbLabel.setText("  ("+ sr.getMarkedCount() + ")  ");
        
        boolean found = sr.getMarkedCount() > 0;
        if (!found) {
            this.findTextField.setForeground(Color.red);
            this.replaceWhatTextField.setForeground(Color.red);
        } else {
            this.findTextField.setForeground(Color.black);
            this.replaceWhatTextField.setForeground(Color.black);
        }
        int newCaret = textArea.getCaretPosition();
        textArea.setCaretPosition(caretLoc);
        return newCaret;
    }
    
    public void findActionMove(boolean forward) {
        int newCaret = this.findAction(forward,TextEditorBinded.this.findTextField.getText());
        textArea.setCaretPosition(newCaret-1);
    }

    public void startFindPanel() {
        this.findPanel.setVisible(true);
        this.replacePanel.setVisible(false);
        this.findTextField.setText("...");
        this.findTextField.selectAll();
        this.findTextField.requestFocusInWindow();

    }

    public void endFindPanel() {
        this.findPanel.setVisible(false);
        this.context.setSearchFor("abcdzpretztpo");
        SearchResult sr = SearchEngine.find(textArea, context);
    }

    
    /////////////////// replace //////////////////////
    
     @Override
    public void toggleReplacePanel() {
         if (!this.replacePanel.isVisible()) {
            this.startReplacePanel();
        } else {
            this.endReplacePanel();
        }
    }
    
     public void startReplacePanel() {
         this.findPanel.setVisible(false);
        this.replacePanel.setVisible(true);
        this.replaceWhatTextField.setText("...");
        this.replaceWhatTextField.selectAll();
        this.replaceWhatTextField.requestFocusInWindow();
        this.replaceWithTextField.setText("");
        this.replaceNbLabel.setText("");
    }

    public void endReplacePanel() {
        this.replacePanel.setVisible(false);
        this.context.setSearchFor("abcdzpretztpo");
        SearchResult sr = SearchEngine.find(textArea, context);
    }
    
    
    public void replaceNext(){
        context.setSearchFor(this.replaceWhatTextField.getText());
        context.setReplaceWith(this.replaceWithTextField.getText());
        context.setSearchForward(true);
        
        SearchResult sr =  SearchEngine.find(textArea, context);
        int begin = sr.getMatchRange().getStartOffset();
        SearchEngine.replace(textArea, context);
        
        textArea.setCaretPosition(begin);
        this.findAction(true,this.replaceWhatTextField.getText() );
    }
    
    public void replaceAll(){
        context.setSearchFor(this.replaceWhatTextField.getText());
        context.setReplaceWith(this.replaceWithTextField.getText());
        context.setSearchForward(true);
        SearchResult sr = SearchEngine.replaceAll(textArea, context);
        textArea.setCaretPosition(sr.getMatchRange().getEndOffset());
        this.findAction(true,this.replaceWhatTextField.getText() );
    }
    
    
    ///////// end of find & replace section ///////////////
    
    
    @Override
    public boolean saveFile() {
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
            if (this.syntaxStyle == keywordsFile) {
                Collator frCollator = Collator.getInstance(Locale.FRENCH);
                frCollator.setStrength(Collator.PRIMARY);
                Collections.sort(out, frCollator);
            }
            resetHasChanged();
            OsRelated.writeToFile(out, f.getAbsolutePath());
            notifyAllObserver("save");
            
            return true;
        }
        return false;
    }

    public void append(String s) {
        textArea.append(s + "\n");
    }

    protected void clearDisplay() {
        textArea.setText("");
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
        jButton2 = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();
        texPanel = new javax.swing.JPanel();
        amountQLabel = new javax.swing.JLabel();
        versionChooserComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        findPanel = new javax.swing.JPanel();
        findTextField = new javax.swing.JTextField();
        nbLabel = new javax.swing.JLabel();
        nextButton = new javax.swing.JButton();
        previousButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        replacePanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        replaceWhatTextField = new javax.swing.JTextField();
        replaceNbLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(30, 0), new java.awt.Dimension(149, 0), new java.awt.Dimension(30, 32767));
        replaceExitButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        replaceWithTextField = new javax.swing.JTextField();
        replaceNextButton = new javax.swing.JButton();
        replaceAllButton = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

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
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_ic_save_48px_352084 _disabled.png"))); // NOI18N
        jButton1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_ic_save_48px_352084_selected.png"))); // NOI18N
        jButton1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_ic_save_48px_352084_rollOver.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_pen-checkbox_353430.png"))); // NOI18N
        jButton2.setToolTipText("Ouvrir dans un editeur externe");
        jButton2.setBorder(null);
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_pen-checkbox_353430_selected.png"))); // NOI18N
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(0, 0, 0)
                .addComponent(versionChooserComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        texPanelLayout.setVerticalGroup(
            texPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(texPanelLayout.createSequentialGroup()
                .addGroup(texPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(amountQLabel)
                    .addComponent(versionChooserComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(5, 5, 5))
        );

        findPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Find", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 0, 14))); // NOI18N
        this.findPanel.setVisible(false);
        findPanel.setLayout(new javax.swing.BoxLayout(findPanel, javax.swing.BoxLayout.LINE_AXIS));

        findTextField.setText("...");
        findTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findTextFieldActionPerformed(evt);
            }
        });
        findPanel.add(findTextField);

        nbLabel.setText("(0)");
        findPanel.add(nbLabel);

        nextButton.setText("suivant");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        findPanel.add(nextButton);

        previousButton.setText("précédant");
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });
        findPanel.add(previousButton);

        exitButton.setText("x");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        findPanel.add(exitButton);

        replacePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Replace", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 0, 14))); // NOI18N
        this.replacePanel.setVisible(false);
        replacePanel.setLayout(new javax.swing.BoxLayout(replacePanel, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setText("  Find what :        ");
        jPanel3.add(jLabel1);

        replaceWhatTextField.setText("...");
        jPanel3.add(replaceWhatTextField);

        replaceNbLabel.setText("(0)");
        jPanel3.add(replaceNbLabel);
        jPanel3.add(filler1);

        replaceExitButton.setText("x");
        replaceExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceExitButtonActionPerformed(evt);
            }
        });
        jPanel3.add(replaceExitButton);

        replacePanel.add(jPanel3);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jLabel4.setText("  Replace with :   ");
        jPanel4.add(jLabel4);

        replaceWithTextField.setText("...");
        jPanel4.add(replaceWithTextField);

        replaceNextButton.setText("Replace Next");
        replaceNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceNextButtonActionPerformed(evt);
            }
        });
        jPanel4.add(replaceNextButton);

        replaceAllButton.setText("Replace All");
        replaceAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceAllButtonActionPerformed(evt);
            }
        });
        jPanel4.add(replaceAllButton);

        replacePanel.add(jPanel4);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/openFolder.png"))); // NOI18N
        jButton3.setToolTipText("Ouvrir le dossier");
        jButton3.setBorder(null);
        jButton3.setBorderPainted(false);
        jButton3.setContentAreaFilled(false);
        jButton3.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/openFolder_selected.png"))); // NOI18N
        jButton3.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/openFolder_rollOver.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(texPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(reloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
            .addComponent(findPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(replacePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(replacePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(findPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(texPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(reloadButton)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.saveFile();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (f == null) {
            return;
        }
        OsRelated.open(f.getAbsolutePath());
    }//GEN-LAST:event_jButton2ActionPerformed

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        // force a update to the text file (usefull if an external change has been observed. 
        // In this case, it is not possible to save the file anymore
        this.updateView();
    }//GEN-LAST:event_reloadButtonActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
    }//GEN-LAST:event_formFocusGained

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        this.endFindPanel();
    }//GEN-LAST:event_exitButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        this.findActionMove(true);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
        this.findActionMove(false);
    }//GEN-LAST:event_previousButtonActionPerformed

    private void findTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findTextFieldActionPerformed
        this.findActionMove(true);
    }//GEN-LAST:event_findTextFieldActionPerformed

    private void replaceExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceExitButtonActionPerformed
        this.endReplacePanel();
    }//GEN-LAST:event_replaceExitButtonActionPerformed

    private void replaceNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceNextButtonActionPerformed
        this.replaceNext();
    }//GEN-LAST:event_replaceNextButtonActionPerformed

    private void replaceAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceAllButtonActionPerformed
       this.replaceAll();
    }//GEN-LAST:event_replaceAllButtonActionPerformed

    private void texPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_texPanelComponentShown

    }//GEN-LAST:event_texPanelComponentShown

    private void versionChooserComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_versionChooserComboBoxActionPerformed
        LatexTokenMaker.login = (String) versionChooserComboBox.getSelectedItem();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ((LatexTextEditor) textArea).updateWholeDocumentHighlighting();
            }
        });
    }//GEN-LAST:event_versionChooserComboBoxActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        //open directory of exercice
        
        OsRelated.openDirectoryOf(f.getAbsolutePath());
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel amountQLabel;
    private javax.swing.JButton exitButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel findPanel;
    private javax.swing.JTextField findTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel nbLabel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton previousButton;
    private javax.swing.JButton reloadButton;
    private javax.swing.JButton replaceAllButton;
    private javax.swing.JButton replaceExitButton;
    private javax.swing.JLabel replaceNbLabel;
    private javax.swing.JButton replaceNextButton;
    private javax.swing.JPanel replacePanel;
    private javax.swing.JTextField replaceWhatTextField;
    private javax.swing.JTextField replaceWithTextField;
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

    public void updateMenuBarView(boolean show) {
        if (show) {
            textArea.addToMenuBar(menuBar);
        } else {
            textArea.removeToMenuBar(menuBar);
        }
    }

    public void setMenuBar(JMenuBar menuBar) {
        this.menuBar = menuBar;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////   specific to exercice files      ///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * @return the questionAmount
     */
    public int getQuestionAmount() {
        return ((LatexTextEditor) textArea).getQuestionLines().size();
    }

    public void updateQuestionLabel() {
        int shownQ = ((LatexTextEditor) textArea).getQuestionNumber();
        this.amountQLabel.setText("Q : " + shownQ + "/" + getQuestionAmount());
    }

    public void updateVersionTags() {
        List<String> newTags = ((LatexTextEditor) textArea).getTags();
        if (!previousTags.equals(newTags)) {
            String current = (String) this.versionChooserComboBox.getSelectedItem();
            this.versionChooserComboBox.removeAllItems();

            if (!newTags.contains("original")) {
                this.versionChooserComboBox.addItem("original");
            }

            for (String version : newTags) {
                this.versionChooserComboBox.addItem(version);
            }
            this.previousTags = (ArrayList<String>) ((ArrayList<String>) newTags).clone();
            this.versionChooserComboBox.setSelectedItem(current);
        }

    }

    @Override
    public void update(Observable o, Object arg) {
        this.updateVersionTags();
        this.updateQuestionLabel();
    }

    

}
