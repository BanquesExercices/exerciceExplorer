/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Helper.SavedVariables;
import static View.Options.setUIFont;
import com.formdev.flatlaf.FlatLightLaf;
import exerciceexplorer.Exercice;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
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

    protected boolean firstDocumentOpened = false;
    protected ReadmeEditor re = null;
    protected KeywordsEditor ke = null;
    protected CompoEditor ce = null;
    protected SubjectEditor se = null;
    protected ChangeListener cl = null;

    JMenuBar menuBar;
    protected AbstractAction replaceKeywordAction, replaceWordAction, checkAllExercicesAction, toZeroAction, gitCredentialAction;
    protected JMenu global, file;

    private static MainWindow instance = null;

    /*
    the unique instance of MainWindow may be obtained by static calls
     -> We should then avoid spaghetti code ! 
     */
    public static synchronized MainWindow getInstance() {
        if (instance == null) {
            instance = new MainWindow();
        }
        return instance;
    }

    private MainWindow() {

        if (instance != null) {
            System.err.println("Tentative de création d'une nouvelle instance de MainWindow.");
            System.err.println("C'est interdit !");
            return;
        }

        SavedVariables.instanciate(this.getClass());
        // change the default font size (usefull on 4K displays)
                setUIFont(new javax.swing.plaf.FontUIResource("Serif",Font.PLAIN,SavedVariables.getFontSize()));

        initComponents();

        if (options2.isCompletionRequired()) {
            this.jTabbedPane1.setSelectedComponent(options2);
        }

        // menubar
        menuBar = new JMenuBar();

        this.getRootPane().setJMenuBar(menuBar);

        cl = (ChangeEvent e) -> {
            MainWindow.this.updateMenuBar();
        };

        setMainMenuBarItems();
        updateMenuBar();

    }

    protected void setMainMenuBarItems() {

        // file : 
        file = new JMenu("Fichier");

        // new exercice
        JMenuItem newExo = new JMenuItem("Nouvel exercice");
        newExo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.creationSujetView1.toggleCreateNewExercicePanel();
            }
        });
        int mod = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        newExo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, mod));
        file.add(newExo);

        // export the set of exercices
        JMenuItem exportItem = new JMenuItem("Exporter la composition");
        exportItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.creationSujetView1.exportSetOFExercices();
            }
        });
        file.add(exportItem);

        // load a set of exercices
        JMenuItem loadSetItem = new JMenuItem("Charger une composition");
        loadSetItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.creationSujetView1.loadSet();
            }
        });
        file.add(loadSetItem);

        // exit
        JMenuItem exitItem = new JMenuItem("Quitter");

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        file.add(exitItem);

        // global : 
        replaceKeywordAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.editorTabbedPane.insertTab("remplacement de mot clef", null, new ReplaceKeywordPanel(), "", 0);
                MainWindow.this.editorTabbedPane.setSelectedIndex(0);
            }
        };

        replaceWordAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.editorTabbedPane.insertTab("remplacement de mot", null, new ReplaceWordPanel(), "", 0);
                MainWindow.this.editorTabbedPane.setSelectedIndex(0);
            }
        };

        checkAllExercicesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.editorTabbedPane.insertTab("Test de compilation", null, new CheckExercicesPanel(), "", 0);
                MainWindow.this.editorTabbedPane.setSelectedIndex(0);
            }
        };

        toZeroAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SavedVariables.removeAllPrefs();
            }
        };

        gitCredentialAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GitCredential gc = new GitCredential();
                gc.setVisible(true);
            }
        };

        // menu items
        global = new JMenu("Avancé");

        JMenuItem replaceKeywordMI = new JMenuItem("Remplacer un mot_clef");
        global.add(replaceKeywordMI);
        replaceKeywordMI.addActionListener(replaceKeywordAction);

        JMenuItem replaceWordMI = new JMenuItem("Remplacer un mot");
        global.add(replaceWordMI);
        replaceWordMI.addActionListener(replaceWordAction);

        JMenuItem checkExercicesMI = new JMenuItem("Compiler tous les exercices");
        global.add(checkExercicesMI);
        checkExercicesMI.addActionListener(checkAllExercicesAction);

        JMenuItem toZeroMI = new JMenuItem("Remise à zero des preferences");
        global.add(toZeroMI);
        toZeroMI.addActionListener(toZeroAction);

        JMenuItem gitCredentialMI = new JMenuItem("Paramètres Github");
        global.add(gitCredentialMI);
        gitCredentialMI.addActionListener(gitCredentialAction);

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
        menuBar.add(file);
        // file menu

        this.updateGlobalMenuBarStatus();

        ArrayList<MenuBarItemProvider> editingPanels = new ArrayList<>();

        editingPanels.add(re);
        editingPanels.add(ke);
        if (ce != null) {
            editingPanels.add(ce);
        }
        editingPanels.add(se);

        for (MenuBarItemProvider mbip : editingPanels) {
            SwingUtilities.invokeLater(() -> {
                if (mbip != null) {
                    mbip.setMenuBar(menuBar);
                    mbip.updateMenuBarView(MainWindow.this.editorTabbedPane.getSelectedComponent() == mbip || mbip == ce);
                }
            });
        }

    }

    public boolean updateDatabase() {
        return this.creationSujetView1.updateDataBase();
    }

    public void setExerciceDisplay(Exercice ex) {
        if (!this.firstDocumentOpened) {
            firstDocumentOpened = true;
            rightPane.setVisible(true);
            this.pack();
        }
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
        options2 = new View.Options();
        middlePane = new javax.swing.JPanel();
        rightPane = new javax.swing.JPanel();
        editorTabbedPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.addTab("Composition", creationSujetView1);
        jTabbedPane1.addTab("Options", options2);

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

        rightPane.setVisible(false);

        editorTabbedPane.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N

        javax.swing.GroupLayout rightPaneLayout = new javax.swing.GroupLayout(rightPane);
        rightPane.setLayout(rightPaneLayout);
        rightPaneLayout.setHorizontalGroup(
            rightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
            .addGroup(rightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rightPaneLayout.createSequentialGroup()
                    .addComponent(editorTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        rightPaneLayout.setVerticalGroup(
            rightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 533, Short.MAX_VALUE)
            .addGroup(rightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(editorTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING))
        );

        editorTabbedPane.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(middlePane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(rightPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(middlePane, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
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
            @Override
            public void run() {
                if (System.getProperty("os.name").startsWith("Mac")) {
                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Exercice Explorer");
                }
                
                
                FlatLightLaf.install(); // nice flat look and feel

                javax.swing.ToolTipManager.sharedInstance().setDismissDelay(12000);
                javax.swing.ToolTipManager.sharedInstance().setInitialDelay(400);

                MainWindow.getInstance().setVisible(true);

               

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private View.CreationCompoView creationSujetView1;
    public javax.swing.JTabbedPane editorTabbedPane;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel middlePane;
    private View.Options options2;
    private javax.swing.JPanel rightPane;
    // End of variables declaration//GEN-END:variables
}
