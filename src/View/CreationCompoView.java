/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Helper.GitWrapper;
import Helper.ListTransferHandler;
import Helper.OsRelated;
import Helper.SavedVariables;
import Helper.Utils;
import TexRessources.TexWriter;
import exerciceexplorer.Exercice;
import exerciceexplorer.ExerciceFinder;
import exerciceexplorer.KeyWords;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JFileChooser;
import javax.swing.ListModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

/**
 *
 * @author mbrebion
 */
public class CreationCompoView extends javax.swing.JPanel {

    /**
     * Creates new form CreationSujet
     */
    protected exerciceexplorer.ExerciceFinder ef;
    protected KeyWords kw;
    protected List<KeyWordView> selectedKeyWords;
    protected DefaultListModel<Exercice> selectedExericesModel;
    public static List<Exercice> displayedExercices = new ArrayList<>();

    public CreationCompoView() {

        ef = new ExerciceFinder();
        selectedKeyWords = new ArrayList<>();
        selectedExericesModel = new DefaultListModel<>();
        initComponents();
        this.updateModel();
        this.keywordsScrollPane.setVisible(false);

        jLabel1.setText("1");

        choixExercice.addListSelectionListener((ListSelectionEvent e) -> {

            ListTransferHandler lth = (ListTransferHandler) choixExercice.getTransferHandler();

            if (choixExercice.getSelectedValue() != null && !lth.isOnDrag()) {
                MainWindow.getInstance().setExerciceDisplay((Exercice) choixExercice.getSelectedValue());
                
                
                
                this.listeExercices.clearSelection();
                this.jLabel1.setText(String.valueOf(choixExercice.getSelectedIndex() + 1));
            }
        });

        listeExercices.addListSelectionListener((ListSelectionEvent e) -> {
            ListTransferHandler lth = (ListTransferHandler) listeExercices.getTransferHandler();
            if (listeExercices.getSelectedValue() != null && !lth.isOnDrag()) {
                MainWindow.getInstance().setExerciceDisplay((Exercice) listeExercices.getSelectedValue());
                this.choixExercice.clearSelection();
            }
        });

        this.newExercicePane.setVisible(false);
        this.newTitleTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateTitle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateTitle();
            }
        });
    }

    public void addAllExercices() {
        selectedExericesModel.clear();
        for (int i = 0; i < choixExercice.getModel().getSize(); i++) {
            selectedExericesModel.addElement((Exercice) choixExercice.getModel().getElementAt(i));
        }
    }

    public void removeAllExercices() {
        selectedExericesModel.clear();
    }

    public void validateTitle() {
        String title = this.newTitleTextField.getText();
        boolean valid = this.ef.isNameAvailable(title);
        if (valid) {
            this.newTitleTextField.setForeground(Color.black);
            this.createNewExoButton.setEnabled(true);
        } else {
            this.newTitleTextField.setForeground(Color.red);
            this.createNewExoButton.setEnabled(false);
        }
    }

    public void showCreateNewExercicePanel() {
        this.newExercicePane.setVisible(true);
        this.newTitleTextField.setText(" ... titre de l'exercice ...");
        this.newTitleTextField.selectAll();
        this.newTitleTextField.requestFocusInWindow();
    }

    public void toggleCreateNewExercicePanel() {
        if (this.newExercicePane.isVisible()) {
            this.hideCreateNewExercicePanel();
        } else {
            this.showCreateNewExercicePanel();
        }

    }

    public void hideCreateNewExercicePanel() {
        this.newExercicePane.setVisible(false);
    }

    public boolean updateDataBase() {
        boolean out = ef.updateList();
        this.updateModel();
        KeyWords.updateKeywordsList();
        return out;
    }

    public Exercice getSelectedExercice() {
        return (Exercice) choixExercice.getSelectedValue();
    }

    public void removeKeyword(KeyWordView kwv) {
        selectedKeyWords.remove(kwv);
        keyWordsHolder.remove(kwv);
        keyWordsHolder.revalidate();
        keyWordsHolder.repaint();
        if (selectedKeyWords.isEmpty()) {
            this.keywordsScrollPane.setVisible(false);
        }
        this.updateModel();
    }

    public void updateModel() {
        List<String> kws = new ArrayList<>();
        for (KeyWordView kwv : selectedKeyWords) {
            kws.add(kwv.nameString);
        }
        ListModel lm = null;
        switch (this.jComboBox2.getSelectedIndex()) {
            case 0: // all kind
                lm = this.ef.getListModel(kws);
                break;

            case 1: // DS
                lm = this.ef.getListModel(kws, Exercice.DS);
                break;

            case 2: // Colle
                lm = this.ef.getListModel(kws, Exercice.Colle);
                break;

            case 3: // TD
                lm = this.ef.getListModel(kws, Exercice.TD);
                break;
        }
        this.jLabel7.setText(String.valueOf(lm.getSize()));
        choixExercice.setModel(lm);
        // 

        displayedExercices.clear();
        for (int i = 0; i < lm.getSize(); i++) {
            displayedExercices.add((Exercice) lm.getElementAt(i));
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

        jLabel3 = new javax.swing.JLabel();
        outputTypes = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        listeExercices = new javax.swing.JList();
        listeExercices.setDragEnabled(true);
        listeExercices.setModel(selectedExericesModel);
        listeExercices.setDropMode(DropMode.INSERT);
        listeExercices.setTransferHandler(new ListTransferHandler(selectedExericesModel));
        editButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        keywordPicker = new javax.swing.JComboBox();
        AutoCompleteDecorator.decorate(keywordPicker);
        ComboBoxCellEditor editor = new ComboBoxCellEditor(keywordPicker);
        CellEditorListener listener = new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                String name=(String)keywordPicker.getSelectedItem();
                // check wether the keyword is already selected.
                for (KeyWordView k : CreationCompoView.this.selectedKeyWords){
                    if (k.getNameString().equals(name)){
                        return;
                    }
                }
                KeyWordView kwv = new KeyWordView(name, CreationCompoView.this);
                CreationCompoView.this.selectedKeyWords.add(kwv);
                CreationCompoView.this.keywordsScrollPane.setVisible(true);
                CreationCompoView.this.keyWordsHolder.add(kwv);
                CreationCompoView.this.keyWordsHolder.revalidate();
                CreationCompoView.this.updateModel();
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
            }
        };
        editor.addCellEditorListener(listener);
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        choixExercice = new javax.swing.JList();
        choixExercice.setDragEnabled(true);
        choixExercice.setTransferHandler(new ListTransferHandler(selectedExericesModel));
        keywordsScrollPane = new javax.swing.JScrollPane();
        keyWordsHolder = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        exportButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        newExercicePane = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jComboBox3 = new javax.swing.JComboBox();
        newTitleTextField = new javax.swing.JTextField();
        createNewExoButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        bpepKeywordPicker = new javax.swing.JComboBox<>();
        AutoCompleteDecorator.decorate(bpepKeywordPicker);
        jSeparator3 = new javax.swing.JSeparator();

        jLabel3.setText("jLabel3");

        outputTypes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DS", "DM", "Colle", "TD" }));
        outputTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputTypesActionPerformed(evt);
            }
        });

        jScrollPane1.setMaximumSize(new java.awt.Dimension(150, 150));

        listeExercices.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listeExercices.setPreferredSize(null);
        listeExercices.setVisibleRowCount(5);
        jScrollPane1.setViewportView(listeExercices);

        editButton.setText("Editer");
        editButton.setToolTipText("Permet de créer la composition regroupant les exercices selectionnés.");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Type");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "*", "DS", "Colle", "TD" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        keywordPicker.setModel(KeyWords.getDefaultComboBoxModelModel(keywordPicker));
        keywordPicker.setMaximumSize(null);
        keywordPicker.setMinimumSize(null);
        keywordPicker.setPreferredSize(null);
        keywordPicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keywordPickerActionPerformed(evt);
            }
        });

        jLabel4.setText("Mot clef");

        choixExercice.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        choixExercice.setToolTipText("Faite glisser un element vers la zone ci dessous pour l'inclure dans la composition.");
        choixExercice.setVisibleRowCount(10);
        jScrollPane3.setViewportView(choixExercice);

        keywordsScrollPane.setBorder(null);
        keywordsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        keyWordsHolder.setMinimumSize(new java.awt.Dimension(0, 70));
        keywordsScrollPane.setViewportView(keyWordsHolder);

        jLabel5.setText("Filtres & Selection");

        jLabel6.setText("Composition :");

        exportButton.setText("Exporter");
        exportButton.setToolTipText("<html>\nPermet d'exporter une composition de manière pérenne\n<br>\nDeux choix sont proposés :\n<ol>\n    <li> export des liens ; vous continuez à profiter des MaJ des exercices \n    <li> export en dur ; les fichiers necessaire à la compilation de la composition sont tous recopiés\n</ol>\n\n<em> L'export n'est possible que si un chemin est spécifié dans les options </em>\n\n</html>");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("----");
        jLabel1.setMaximumSize(new java.awt.Dimension(42, 16));
        jLabel1.setMinimumSize(new java.awt.Dimension(42, 16));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("----");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText(" sur");

        newExercicePane.setPreferredSize(new java.awt.Dimension(496, 50));
        newExercicePane.setLayout(new javax.swing.BoxLayout(newExercicePane, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel1.setPreferredSize(new java.awt.Dimension(82, 50));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DS", "Colle", "TD" }));
        jComboBox3.setMaximumSize(new java.awt.Dimension(250, 40));
        jComboBox3.setPrototypeDisplayValue("_Colle___");
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });
        jPanel2.add(jComboBox3);

        jPanel1.add(jPanel2);

        newTitleTextField.setText(" ... Titre de l'exercice ...");
        newTitleTextField.setPreferredSize(new java.awt.Dimension(155, 20));
        newTitleTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTitleTextFieldActionPerformed(evt);
            }
        });
        jPanel1.add(newTitleTextField);

        createNewExoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_new10_216291.png"))); // NOI18N
        createNewExoButton.setToolTipText("créer un nouvel exercice");
        createNewExoButton.setBorderPainted(false);
        createNewExoButton.setContentAreaFilled(false);
        createNewExoButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_new10_216291_selected.png"))); // NOI18N
        createNewExoButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_new10_216291_rollOver.png"))); // NOI18N
        createNewExoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewExoButtonActionPerformed(evt);
            }
        });
        jPanel1.add(createNewExoButton);

        cancelButton.setText("x");
        cancelButton.setPreferredSize(new java.awt.Dimension(45, 29));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton);

        newExercicePane.add(jPanel1);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel9.setText("Thème  :  ");
        jPanel3.add(jLabel9);

        bpepKeywordPicker.setModel(KeyWords.getMainComboBoxModelModel());
        bpepKeywordPicker.setMaximumSize(null);
        bpepKeywordPicker.setPreferredSize(null);
        bpepKeywordPicker.setPrototypeDisplayValue("hey hey my my ohoh Ohio !");
        jPanel3.add(bpepKeywordPicker);

        newExercicePane.add(jPanel3);

        jSeparator3.setPreferredSize(new java.awt.Dimension(50, 20));
        newExercicePane.add(jSeparator3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel8))
                .addContainerGap())
            .addComponent(newExercicePane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(keywordsScrollPane)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(keywordPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel5))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(outputTypes, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(editButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(exportButton))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newExercicePane, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(keywordPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(keywordsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 35, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editButton)
                    .addComponent(exportButton))
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        List<String> cc;
        cc = TexWriter.createTexFile(selectedExericesModel.elements(), (String) outputTypes.getSelectedItem(), false, false);
        MainWindow.getInstance().setSubjectDisplay(cc);
    }//GEN-LAST:event_editButtonActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        this.updateModel();
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void keywordPickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keywordPickerActionPerformed
    }//GEN-LAST:event_keywordPickerActionPerformed

    private void outputTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputTypesActionPerformed

    }//GEN-LAST:event_outputTypesActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void createNewExoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewExoButtonActionPerformed

        String title = Utils.stripAccents(this.newTitleTextField.getText()); // dir with accents causes trouble ...
        this.ef.createExercice((String) jComboBox3.getSelectedItem(), title);
        this.updateDataBase();
        this.ef.getExercice(title).addKeyword(KeyWords.getMainKeywordStart()+bpepKeywordPicker.getSelectedItem());
        MainWindow.getInstance().setExerciceDisplay(this.ef.getExercice(title));
        this.newExercicePane.setVisible(false);
    }//GEN-LAST:event_createNewExoButtonActionPerformed

    public void exportSetOFExercices() {
        ExportWindow ew = new ExportWindow(this);
        ew.setVisible(true);
    }


    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        exportSetOFExercices();
    }//GEN-LAST:event_exportButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.newExercicePane.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void newTitleTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTitleTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newTitleTextFieldActionPerformed

    public String getOutputType() {
        return (String) outputTypes.getSelectedItem();
    }
    
    
    public void enableExport(boolean value){
        exportButton.setEnabled(value);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> bpepKeywordPicker;
    private javax.swing.JButton cancelButton;
    private javax.swing.JList choixExercice;
    private javax.swing.JButton createNewExoButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPanel keyWordsHolder;
    private javax.swing.JComboBox keywordPicker;
    private javax.swing.JScrollPane keywordsScrollPane;
    private javax.swing.JList listeExercices;
    private javax.swing.JPanel newExercicePane;
    private javax.swing.JTextField newTitleTextField;
    private javax.swing.JComboBox outputTypes;
    // End of variables declaration//GEN-END:variables

    public void loadSet() {
        JFileChooser choix = new JFileChooser();
        choix.setCurrentDirectory(new File(SavedVariables.getOutputDir()));
        choix.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String s = f.getName();
                return s.endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "blabla";
            }
        });
        int retour = choix.showOpenDialog(this);
        if (retour == JFileChooser.APPROVE_OPTION) {

            List<String> lines = OsRelated.readFile(choix.getSelectedFile().getAbsolutePath());
            if (lines.isEmpty()) {
                return;
            }

            String readLine = "";
            String kind;
            String number;
            String name;
            selectedExericesModel.clear();
            try {
                kind = lines.remove(0).split(":")[1].trim();
                number = lines.remove(0).split(":")[1].trim();
                name = lines.remove(0).split(":")[1].trim();

                while (!lines.isEmpty()) {
                    readLine = lines.remove(0);
                    System.out.println(readLine);
                    Exercice ex = ExerciceFinder.getExerciceByPath(readLine.trim());
                    if (ex != null) {
                        this.selectedExericesModel.addElement(ex);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Problème lors de la lecture du fichier " + choix.getSelectedFile().getAbsolutePath());
                return;
            }
            this.outputTypes.setSelectedItem(kind);

        }
    }

}
