/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Helper.ExecCommand;
import Helper.ListTransferHandler;
import Helper.SavedVariables;
import Helper.Utils;
import TexRessources.TexWriter;
import exerciceexplorer.Exercice;
import exerciceexplorer.ExerciceFinder;
import exerciceexplorer.KeyWords;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
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
    protected MainWindow mw;

    public CreationCompoView() {

        ef = new ExerciceFinder();
        selectedKeyWords = new ArrayList<>();
        selectedExericesModel = new DefaultListModel<>();
        initComponents();
        this.updateModel();
        
        jLabel1.setText("1");
        choixExercice.addListSelectionListener((ListSelectionEvent e) -> {
            if (choixExercice.getSelectedValue() != null) {
                this.mw.setExerciceDisplay((Exercice) choixExercice.getSelectedValue());
                this.listeExercices.clearSelection();
                this.jLabel1.setText(String.valueOf(choixExercice.getSelectedIndex()+1));
            }
        });
        

        listeExercices.addListSelectionListener((ListSelectionEvent e) -> {
            if (listeExercices.getSelectedValue() != null) {
                this.mw.setExerciceDisplay((Exercice) listeExercices.getSelectedValue());
                this.choixExercice.clearSelection();
            }

        });

    }

    public void updateDataBase() {
        ef.updateList();
        this.updateModel();

    }

    public Exercice getSelectedExercice() {
        return (Exercice) choixExercice.getSelectedValue();
    }

    public void setMw(MainWindow mw) {
        this.mw = mw;
    }

    public void removeKeyword(KeyWordView kwv) {
        selectedKeyWords.remove(kwv);
        keyWordsHolder.remove(kwv);
        keyWordsHolder.revalidate();
        keyWordsHolder.repaint();
        this.updateModel();
    }

    
    public void updateModel() {
        List<String> kws = new ArrayList<>();
        for (KeyWordView kwv : selectedKeyWords) {
            kws.add(kwv.nameString);
        }
        ListModel lm=null;
        switch (this.jComboBox2.getSelectedIndex()) {
            case 0: // all kind
                lm= this.ef.getListModel(kws);
                break;

            case 1: // DS
                lm=this.ef.getListModel(kws, Exercice.DS);
                break;

            case 2: // Colle
                lm=this.ef.getListModel(kws, Exercice.Colle);
                break;

            case 3: // TD
                lm=this.ef.getListModel(kws, Exercice.TD);
                break;
        }
        this.jLabel7.setText(String.valueOf(lm.getSize()));
        choixExercice.setModel(lm);
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        outputTypes = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        listeExercices = new javax.swing.JList();
        listeExercices.setDragEnabled(true);
        listeExercices.setModel(selectedExericesModel);
        listeExercices.setDropMode(DropMode.INSERT);
        listeExercices.setTransferHandler(new ListTransferHandler(selectedExericesModel));
        editButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        keywordPicker = new javax.swing.JComboBox();
        AutoCompleteDecorator.decorate(keywordPicker);
        ComboBoxCellEditor editor = new ComboBoxCellEditor(keywordPicker);
        CellEditorListener listener = new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                String name=(String)keywordPicker.getSelectedItem();
                KeyWordView kwv = new KeyWordView(name, CreationCompoView.this);
                CreationCompoView.this.selectedKeyWords.add(kwv);
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
        jScrollPane4 = new javax.swing.JScrollPane();
        keyWordsHolder = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 45), new java.awt.Dimension(0, 45), new java.awt.Dimension(32767, 45));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 80), new java.awt.Dimension(0, 80), new java.awt.Dimension(32767, 80));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 70), new java.awt.Dimension(0, 70), new java.awt.Dimension(32767, 70));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 55), new java.awt.Dimension(0, 55), new java.awt.Dimension(32767, 55));
        jSeparator2 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        jSeparator1.setPreferredSize(new java.awt.Dimension(0, 8));

        outputTypes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DS", "DM", "Colle", "TD" }));
        outputTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputTypesActionPerformed(evt);
            }
        });

        listeExercices.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listeExercices.setVisibleRowCount(7);
        jScrollPane1.setViewportView(listeExercices);

        editButton.setText("Editer");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Type");

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel3.setText("Nouvel exercice : ");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "*", "DS", "Colle", "TD" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        keywordPicker.setModel(KeyWords.getDefaultComboBoxModelModel());
        keywordPicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keywordPickerActionPerformed(evt);
            }
        });

        jLabel4.setText("Mot clef");

        choixExercice.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(choixExercice);

        jScrollPane4.setBorder(null);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        keyWordsHolder.setMinimumSize(new java.awt.Dimension(0, 70));
        keyWordsHolder.setPreferredSize(new java.awt.Dimension(8, 70));

        javax.swing.GroupLayout keyWordsHolderLayout = new javax.swing.GroupLayout(keyWordsHolder);
        keyWordsHolder.setLayout(keyWordsHolderLayout);
        keyWordsHolderLayout.setHorizontalGroup(
            keyWordsHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(keyWordsHolderLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        keyWordsHolderLayout.setVerticalGroup(
            keyWordsHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, keyWordsHolderLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jScrollPane4.setViewportView(keyWordsHolder);
        this.keyWordsHolder.setLayout(new FlowLayout());

        jSeparator2.setPreferredSize(new java.awt.Dimension(0, 8));

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel5.setText("Selection : ");

        jLabel6.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel6.setText("Composition :");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DS", "Colle", "TD" }));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_new10_216291.png"))); // NOI18N
        jButton2.setToolTipText("créer un nouvel exercice");
        jButton2.setBorderPainted(false);
        jButton2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_new10_216291_selected.png"))); // NOI18N
        jButton2.setRolloverEnabled(true);
        jButton2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_new10_216291_rollOver.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        exportButton.setText("Exporter");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("----");
        jLabel1.setMaximumSize(new java.awt.Dimension(42, 16));
        jLabel1.setMinimumSize(new java.awt.Dimension(42, 16));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("----");

        jLabel8.setText("   /");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane4)
            .addComponent(jScrollPane1)
            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(8, 8, 8)
                .addComponent(jLabel2)
                .addGap(2, 2, 2)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel4)
                .addGap(2, 2, 2)
                .addComponent(keywordPicker, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(6, 6, 6)
                .addComponent(outputTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editButton)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(exportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7))))
                .addGap(4, 4, 4))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(keywordPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editButton)
                    .addComponent(jLabel6)
                    .addComponent(exportButton))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                .addGap(8, 8, 8))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        List<String> cc = TexWriter.outputTexFile(selectedExericesModel.elements(), (String) outputTypes.getSelectedItem(),false);
        this.mw.setSubjectDisplay(cc);
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

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String title = JOptionPane.showInputDialog("Titre de l'exercice");
        title = Utils.stripAccents(title); // dir with accents causes trouble ...
        this.ef.createExercice((String) jComboBox3.getSelectedItem(), title);
        this.updateDataBase();
        this.mw.setExerciceDisplay(this.ef.getExercice(title));
    }//GEN-LAST:event_jButton2ActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        String kind=""+outputTypes.getSelectedItem();
        String outDir = SavedVariables.getOutputDir() + "/" + kind;
        
        // look for new directory to create
        File directory = new File(outDir);
        File[] fList = directory.listFiles(
                new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return !file.isHidden();
                    }
                }
        );

        int maxValue=0;
        for (File file : fList) {
            maxValue= Math.max(maxValue, Integer.parseInt(file.getName().replace(kind,"")));
        }
        int compoNumber=maxValue+1; // on crée le prochain sujet
        File compoDir= new File(outDir+"/"+kind+compoNumber);
        compoDir.mkdir();
        
        // output subject.tex
        List<String> cc = TexWriter.outputTexFile(selectedExericesModel.elements(), kind,true);
        TexWriter.writeToFile(cc, compoDir+"/"+kind+compoNumber+".tex");
        Enumeration<Exercice> exercices = selectedExericesModel.elements();
        while (exercices.hasMoreElements()) {
            Exercice exo = exercices.nextElement();
            ExecCommand.execo(new String[]{"cp","-r",exo.getPath(),compoDir+"/" }, 100);
        }
        
        
    }//GEN-LAST:event_exportButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList choixExercice;
    private javax.swing.JButton editButton;
    private javax.swing.JButton exportButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JButton jButton2;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel keyWordsHolder;
    private javax.swing.JComboBox keywordPicker;
    private javax.swing.JList listeExercices;
    private javax.swing.JComboBox outputTypes;
    // End of variables declaration//GEN-END:variables
}
