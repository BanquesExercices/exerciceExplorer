/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Helper.GitWrapper;
import Helper.SavedVariables;
import static View.Options.setUIFont;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
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
import javax.swing.UIManager;
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

    protected boolean firstDocumentOpened = false;
    protected ReadmeEditor re = null;
    protected KeywordsEditor ke = null;
    protected CompoEditor ce = null;
    protected SubjectEditor se = null;
    protected ChangeListener cl = null;
    protected CheckExercicesPanel cep = null;

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

        System.setProperty("file.encoding", "UTF-8");
        
        SavedVariables.instanciate(this.getClass());
        this.installTheme();
        setUIFont(new javax.swing.plaf.FontUIResource("SansSerif", Font.PLAIN, SavedVariables.getFontSize()));
        // change the default font size (usefull on 4K displays)
        
        UIManager.put("SplitPane.dividerSize", 15);
        UIManager.put("SplitPaneDivider.gripDotCount", 3);
        UIManager.put("SplitPaneDivider.gripDotSize", 7);
        UIManager.put("SplitPaneDivider.gripGap", 14);

        initComponents();

        jSplitPane1.setOneTouchExpandable(true);

        Options options2 = new Options();
        jTabbedPane1.addTab("Options", options2);

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
        loadSetItem.addActionListener((ActionEvent e) -> {
            MainWindow.this.creationSujetView1.loadSet();
        });
        file.add(loadSetItem);

        file.addSeparator();

        // add all displayed exercices to composition
        JMenuItem addAllItem = new JMenuItem("ajouter tous les exercices");
        addAllItem.addActionListener((ActionEvent e) -> {
            MainWindow.this.creationSujetView1.addAllExercices();
        });
        file.add(addAllItem);

        // remove all exercices from composition
        JMenuItem removeAllItem = new JMenuItem("retirer tous les exercices");
        removeAllItem.addActionListener((ActionEvent e) -> {
            MainWindow.this.creationSujetView1.removeAllExercices();
        });
        file.add(removeAllItem);

        file.addSeparator();
        // exit
        JMenuItem exitItem = new JMenuItem("Quitter");

        exitItem.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        file.add(exitItem);

        // global : 
        replaceKeywordAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.editorTabbedPane.insertTab("remplacement de mot clef", null, new ReplaceKeywordPanel(), "", 0);
                if (!MainWindow.this.firstDocumentOpened) {
                    firstDocumentOpened = true;

                    MainWindow.this.pack();
                }
                MainWindow.this.editorTabbedPane.setSelectedIndex(0);
            }
        };

        replaceWordAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.editorTabbedPane.insertTab("remplacement de mot", null, new ReplaceWordPanel(), "", 0);
                if (!MainWindow.this.firstDocumentOpened) {
                    firstDocumentOpened = true;

                    MainWindow.this.pack();
                }
                MainWindow.this.editorTabbedPane.setSelectedIndex(0);
            }
        };

        checkAllExercicesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (MainWindow.this.cep ==null){
                    MainWindow.this.cep = new CheckExercicesPanel();
                }else{
                    return;
                }
                MainWindow.this.editorTabbedPane.insertTab("Test de compilation", null, MainWindow.this.cep, "", 0);
                if (!MainWindow.this.firstDocumentOpened) {
                    firstDocumentOpened = true;

                    MainWindow.this.pack();
                }
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
    
    public void removeCheckExercicesPanel(){
        if (this.cep!=null){
                this.editorTabbedPane.remove(this.cep);
                this.cep=null;           
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
        
        this.editorTabbedPane.removeChangeListener(cl);
        boolean warningUnsavedContent = false;
       
        boolean toDelete = false;

        // first check whether a file about to be closed by this opperation require a save action
        if (re != null) {
            toDelete = true;
            if (SavedVariables.getAutoSave()) {
                re.saveFile();
            } else {
                warningUnsavedContent = re.needSaving() || warningUnsavedContent;
            }

        }
        if (ke != null) {
            if (SavedVariables.getAutoSave()) {
                ke.saveFile();
            } else {
                warningUnsavedContent = ke.needSaving() || warningUnsavedContent;
            }

        }
        if (se != null) {
            if (SavedVariables.getAutoSave()) {
                se.saveFile();
            } else {
                warningUnsavedContent = se.needSaving() || warningUnsavedContent;
            }

        }

        if (warningUnsavedContent) {
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
            if (cep!=null){
            this.editorTabbedPane.addTab("Test de compilation", cep);    
            
            
            }
            if (ce != null) {
                this.editorTabbedPane.addTab("Composition", ce);                
            }
            
        }
        
        // then check if a file about to be opened has unpulled changes from remote
        // this check is performed on another thread in order not to slow the IHM 
        new Thread(() -> {
            boolean warningNewContentOnRemote = GitWrapper.isFileModifiedOnOrigin(ex.getReadmePath()) || GitWrapper.isFileModifiedOnOrigin(ex.getSubjectPath()) || GitWrapper.isFileModifiedOnOrigin(ex.getKeywordsPath());
            
            if (warningNewContentOnRemote) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(MainWindow.this, "<html> L'exercice que vous souhaitez editer possède une version plus recente sur github.com <br> Il est donc <span style='color:green'>vivement conseillé</span> d'effectuer un git pull avant.</html>");
                });
            }
        }).start();


        
        
        re = new ReadmeEditor(ex);
        this.editorTabbedPane.addTab("Readme",re);
        this.editorTabbedPane.setSelectedComponent(re);

        ke = new KeywordsEditor(ex);
        editorTabbedPane.addTab("Mots clés",ke);

        SwingUtilities.invokeLater(() -> {
            se = new SubjectEditor(ex);
            editorTabbedPane.addTab("sujet.tex", se);
        } // on slow machines, this enables the readme file to be display a bit faster than keywords and subjects
        );

        this.editorTabbedPane.addChangeListener(cl);
        this.updateMenuBar();
        
        if (!this.firstDocumentOpened) {
            firstDocumentOpened = true;
            
            this.pack();
        }

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
        
        if (!this.firstDocumentOpened) {
            firstDocumentOpened = true;
            
            this.pack();
        }
    }

    public void installTheme(){
        int theme = SavedVariables.getTheme();
        switch (theme) {
            case 0:
                FlatLightLaf.install(); // nice flat look and feel
                break;
            case 1:
                FlatIntelliJLaf.install();
                break;
            case 2:
                FlatDarkLaf.install();             
                break;
            case 3:
                FlatDarculaLaf.install();
                break;
            default:
                break;
        }
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        creationSujetView1 = new View.CreationCompoView();
        editorTabbedPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerSize(20);
        jSplitPane1.setForeground(new java.awt.Color(204, 204, 204));

        jTabbedPane1.addTab("Composition", creationSujetView1);

        jSplitPane1.setLeftComponent(jTabbedPane1);
        jSplitPane1.setRightComponent(editorTabbedPane);
        editorTabbedPane.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
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

                
                //FlatLightLaf.install(); // nice flat look and feel

                javax.swing.ToolTipManager.sharedInstance().setDismissDelay(12000);
                javax.swing.ToolTipManager.sharedInstance().setInitialDelay(400);

                MainWindow.getInstance().setVisible(true);
                

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public View.CreationCompoView creationSujetView1;
    public javax.swing.JTabbedPane editorTabbedPane;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
