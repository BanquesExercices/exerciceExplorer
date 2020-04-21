/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Helper.SavedVariables;
import exerciceexplorer.Exercice;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author mbrebion
 */
@SuppressWarnings("serial")
public final class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    public static final int modeReadme = 1, modeComposition = 2, modeNone = 0;
    protected int mode;

    ReadmeEditor re = null;
    KeywordsEditor ke = null;
    CompoEditor ce = null;
    SubjectEditor se = null;
    ChangeListener cl = null;

    JMenuBar menuBar;
    protected AbstractAction replaceKeywordAction, replaceWordAction, checkAllExercicesAction;
    protected JMenu global;

    public MainWindow() {
        SavedVariables.prefs = Preferences.userNodeForPackage(this.getClass());

        initComponents();
        this.creationSujetView1.setMw(this);
        this.options2.setMw(this);

        // menubar
        menuBar = new JMenuBar();

        this.getRootPane().setJMenuBar(menuBar);

        cl = (ChangeEvent e) -> {
            MainWindow.this.updateMenuBar();
        };

        setMainMenuBarItems();

    }

    protected void setMainMenuBarItems() {
        replaceKeywordAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.editorTabbedPane.insertTab("remplacement de mot clef", null, new ReplaceKeywordPanel(MainWindow.this), "", 0);
                MainWindow.this.editorTabbedPane.setSelectedIndex(0);
            }
        };

        replaceWordAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.editorTabbedPane.insertTab("remplacement de mot clef", null, new ReplaceWordPanel(MainWindow.this), "", 0);
                MainWindow.this.editorTabbedPane.setSelectedIndex(0);
            }
        };

        checkAllExercicesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.editorTabbedPane.insertTab("Test de compilation", null, new CheckExercicesPanel(MainWindow.this), "", 0);
                MainWindow.this.editorTabbedPane.setSelectedIndex(0);
            }
        };

        // menu items
        global = new JMenu("Global");

        JMenuItem replaceKeywordMI = new JMenuItem("Remplacer un mot_clef");
        global.add(replaceKeywordMI);
        replaceKeywordMI.addActionListener(replaceKeywordAction);

        JMenuItem replaceWordMI = new JMenuItem("Remplacer un mot");
        global.add(replaceWordMI);
        replaceWordMI.addActionListener(replaceWordAction);

        JMenuItem checkExercicesMI = new JMenuItem("Compiler tous les exercices");
        global.add(checkExercicesMI);
        checkExercicesMI.addActionListener(checkAllExercicesAction);

        this.updateGlobalMenuBarStatus();

    }

    public void updateGlobalMenuBarStatus() {
        menuBar.remove(global);
        if (SavedVariables.getMultiEdit()) {
            menuBar.add(global);
        }

    }

    protected void updateMenuBar() {
        menuBar.removeAll();
        this.updateGlobalMenuBarStatus();

        ArrayList<MenuBarItemProvider> editingPanels = new ArrayList<>();
        
        editingPanels.add(re);
        editingPanels.add(ke);
        if (ce!=null){
            editingPanels.add(ce);
        }
        editingPanels.add(se);
        
        
        for (MenuBarItemProvider mbip : editingPanels) {
            SwingUtilities.invokeLater(() -> {
                mbip.setMenuBar(menuBar);
                mbip.updateMenuBarView(MainWindow.this.editorTabbedPane.getSelectedComponent() == mbip || mbip == ce);
            });
        }

       
       

    }

    public void updateDatabase() {
        this.creationSujetView1.updateDataBase();
        // should we do more here ? (removing oppened tabs ?)
    }

    public void setExerciceDisplay(Exercice ex) {

        this.editorTabbedPane.removeChangeListener(cl);
        boolean warning = false;
        boolean toDelete = false;

        if (re != null) {
            toDelete = true;
            if (SavedVariables.getAutoSave()) {
                re.saveFile();
            } else {
                warning = re.needSaving() || warning;
            }

        }
        if (ke != null) {
            if (SavedVariables.getAutoSave()) {
                ke.saveFile();
            } else {
                warning = ke.needSaving() || warning;
            }

        }
        if (se != null) {
            if (SavedVariables.getAutoSave()) {
                se.saveFile();
            } else {
                warning = se.needSaving() || warning;
            }

        }

        if (warning) {
            int result = JOptionPane.showConfirmDialog(this, "Voulez vous sauvegarder les fichiers en cours ? (si non, les dernières modifications non sauvegardées seront effacées)");
            if (result == JOptionPane.YES_OPTION) {
                re.saveFile();
                se.saveFile();
                ke.saveFile();
            }
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        if (toDelete) {
            this.editorTabbedPane.removeAll();
            if (ce != null) {
                this.editorTabbedPane.insertTab("Composition", null, ce, "", 0);
            }
        }

        re = new ReadmeEditor(ex);
        this.editorTabbedPane.insertTab("Readme", null, re, "", 0);
        this.editorTabbedPane.setSelectedComponent(re);

        
        ke = new KeywordsEditor(ex);
        editorTabbedPane.insertTab("Mots clés", null, ke, "", 1);

        SwingUtilities.invokeLater(new Runnable() {
            // on slow machines, this enables the readme file to be display a bit faster than keywords and subjects
            @Override
            public void run() {
                se = new SubjectEditor(ex);
                editorTabbedPane.insertTab("sujet.tex", null, se, "", 2);
            }
        });

        this.editorTabbedPane.addChangeListener(cl);
        this.updateMenuBar();

    }

    public void focusToComposition() {
        if (ce != null) {
            this.editorTabbedPane.setSelectedComponent(ce);
        }
    }

    public void setSubjectDisplay(List<String> lines) {
        if (ce != null) {
            this.editorTabbedPane.remove(ce);
            ce.updateMenuBarView(false);
        }
        ce = new CompoEditor(lines);
        ce.setMenuBar(menuBar);
        ce.updateMenuBarView(true);
        this.editorTabbedPane.insertTab("Composition", null, ce, "", 0);
        this.editorTabbedPane.setSelectedIndex(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        creationSujetView1 = new View.CreationCompoView();
        jScrollPane1 = new javax.swing.JScrollPane();
        options2 = new View.Options();
        middlePane = new javax.swing.JPanel();
        rightPane = new javax.swing.JPanel();
        editorTabbedPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.addTab("Composition", creationSujetView1);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(options2);

        jTabbedPane1.addTab("Options", jScrollPane1);

        middlePane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        middlePane.setPreferredSize(new java.awt.Dimension(7, 2));

        javax.swing.GroupLayout middlePaneLayout = new javax.swing.GroupLayout(middlePane);
        middlePane.setLayout(middlePaneLayout);
        middlePaneLayout.setHorizontalGroup(
            middlePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 3, Short.MAX_VALUE)
        );
        middlePaneLayout.setVerticalGroup(
            middlePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        editorTabbedPane.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N

        javax.swing.GroupLayout rightPaneLayout = new javax.swing.GroupLayout(rightPane);
        rightPane.setLayout(rightPaneLayout);
        rightPaneLayout.setHorizontalGroup(
            rightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 482, Short.MAX_VALUE)
            .addGroup(rightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(editorTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE))
        );
        rightPaneLayout.setVerticalGroup(
            rightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(rightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(editorTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE))
        );

        editorTabbedPane.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(middlePane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(rightPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(middlePane, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
            .addComponent(rightPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if (System.getProperty("os.name").startsWith("Mac OS X")) {
                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Exercice Explorer");
                }

                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                new MainWindow().setVisible(true);

                // error reporting : 
                File file = new File("err.txt");
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(file);
                    PrintStream ps = new PrintStream(fos);
                    System.setErr(ps);
                } catch (FileNotFoundException ex) {
                    System.err.println("Le fichier d'erreur n'a pu être créé/lu");
                }

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private View.CreationCompoView creationSujetView1;
    public javax.swing.JTabbedPane editorTabbedPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel middlePane;
    private View.Options options2;
    private javax.swing.JPanel rightPane;
    // End of variables declaration//GEN-END:variables
}
