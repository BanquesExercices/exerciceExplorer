/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Helper.OsRelated;
import Helper.Utils;
import TexRessources.TexWriter;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 *
 * @author mbrebion
 */
public class CompoEditor extends javax.swing.JPanel implements MenuBarItemProvider {

    protected StyledDocument doc;
    protected JMenuBar menuBar;
    protected JMenu compilation;
    protected AbstractAction compileAndOpen, compileWithoutOpen;

    /**
     * Creates new form SubjectEditor
     */
    public CompoEditor() {
        initComponents();
    }

    public CompoEditor(List<String> lines) {
        initComponents();
        this.doc = jTextPane1.getStyledDocument();
        this.setText(lines);
        this.createMenubar();
    }

    public void createMenubar() {
        // actions
        compileAndOpen = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compileAction(true);
            }
        };

        compileWithoutOpen = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compileAction(false);
            }
        };

        compilation = new JMenu("Compilation");

        JMenuItem compileAO = new JMenuItem("compile and open");
        compilation.add(compileAO);
        compileAO.addActionListener(compileAndOpen);
        compileAO.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    }

    public void updateMenuBarView(boolean show) {
        menuBar.remove(compilation);
        if (show) {
            menuBar.add(compilation);
        }
    }

    public void setMenuBar(JMenuBar jmb) {
        this.menuBar = jmb;
    }

    protected void setText(List<String> lines) {
        jTextPane1.setText("");
        for (String line : lines) {
            this.append(line);
        }
    }

    protected void append(String s) {
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

    protected List<String> getLines() {
        String[] split = jTextPane1.getText().split("\r?\n");
        List<String> lines = new ArrayList<>();
        for (String s : split) {
            lines.add(s);
        }
        return lines;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        compileButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_pen-checkbox_353430.png"))); // NOI18N
        jButton2.setToolTipText("Ouvrir dans un editeur externe");
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_pen-checkbox_353430_selected.png"))); // NOI18N
        jButton2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Content/if_pen-checkbox_353430_rollOver.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        compileButton.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        compileButton.setText("Compiler");
        compileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(compileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(compileButton)
                .addContainerGap())
        );

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(jTextPane1);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 169, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void compileAction(boolean open) {
        this.compileButton.setEnabled(false);

        new Thread(() -> {
            List<String> lines = CompoEditor.this.getLines();
            TexWriter.writeTexFile(lines);
            if (TexWriter.latexToPdf()) {
                if (open) {
                   
                        TexWriter.openPdf();
                    
                }
            } else {
                if (!OsRelated.isWindows()) {
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(CompoEditor.this);
                    Utils.showLongTextMessageInDialog(TexWriter.latexLog, topFrame);
                }
            }
            SwingUtilities.invokeLater(() -> {
                CompoEditor.this.compileButton.setEnabled(true);
            });
        }).start();

    }

    private void compileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compileButtonActionPerformed
        this.compileAction(true);
    }//GEN-LAST:event_compileButtonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        List<String> lines = this.getLines();
        TexWriter.writeTexFile(lines);
        OsRelated.open("output.tex");
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton compileButton;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
