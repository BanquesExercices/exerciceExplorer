/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import exerciceexplorer.Exercice;
import exerciceexplorer.KeyWords;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JOptionPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

/**
 *
 * @author mbrebion
 */
public class KeywordsEditor extends javax.swing.JPanel implements Observer {

    /**
     * Creates new form SubjectEditor
     */
    protected Exercice ex;

    public KeywordsEditor() {
        initComponents();
    }

    public KeywordsEditor(Exercice ex) {
        initComponents();
        this.ex = ex;
        this.textEditorBinded1.bindToFile(ex.getKeywordsPath());
        this.textEditorBinded1.addObserver(this);
    }

    private boolean contains(String name) {
        boolean out = false;
        List<String> split = textEditorBinded1.getText();
        for (String s : split) {
            if (s.trim().equals(name)) {
                out = true;
            }
        }
        return out;
    }

    private void appendKeyword(String name) {
        if (!contains(name)) {
            textEditorBinded1.append(name);
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
        keywordPicker = new javax.swing.JComboBox();
        AutoCompleteDecorator.decorate(keywordPicker);
        ComboBoxCellEditor editor = new ComboBoxCellEditor(keywordPicker);
        CellEditorListener listener = new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                String name=(String)keywordPicker.getSelectedItem();
                KeywordsEditor.this.appendKeyword(name);
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
            }
        };
        editor.addCellEditorListener(listener);
        jLabel2 = new javax.swing.JLabel();
        textEditorBinded1 = new View.TextEditorBinded();

        jButton1.setText("jButton1");

        keywordPicker.setModel(KeyWords.getDefaultComboBoxModelModel());

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel2.setText("Ajout :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(12, 12, 12)
                .addComponent(keywordPicker, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(textEditorBinded1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(keywordPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(0, 0, 0)
                .addComponent(textEditorBinded1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox keywordPicker;
    private View.TextEditorBinded textEditorBinded1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void update(Observable o, Object arg) {
        if (arg == this) {
            return;
        }
        List<String> typedKeywords = this.textEditorBinded1.getText();
        // check for new keywords
        List<String> newKeywords = new ArrayList<>();
        for (String st : typedKeywords) {
            if (!KeyWords.exists(st)) {
                newKeywords.add(st);
            }
        }
        if (newKeywords.size() > 0) {
            // new elements to add
            int output = JOptionPane.showConfirmDialog(null, "il y a " + newKeywords.size()+" nouveaux mots clefs.", "Ajout des nouveaux mots clefs ?", JOptionPane.YES_NO_OPTION);
            if (output == JOptionPane.YES_OPTION) {
                KeyWords.addNewKeywords(newKeywords);
                KeyWords.updateKeywordsList();
            }
        }

        ex.NotifyKeyWordsChanged();
    }

    public void saveFile() {
        this.textEditorBinded1.SaveFile();
    }

}
