/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Helper.OsRelated;
import Helper.GitWrapper;
import Helper.SavedVariables;
import Helper.Utils;
import TextEditor.Tex.LatexTextEditor;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author mbrebion
 */
public class Options extends JPanel {

    /**
     * Creates new form Options
     */
    public Options() {

        initComponents();

        // better dealing with jTextField : input is saved whenever the text changes : we do not wait for return key anymore
        Utils.addChangeListener(pdflatexInput, (ChangeEvent e) -> {
            SavedVariables.setPdflatexCmd(pdflatexInput.getText());
            // check status
            if (OsRelated.checkPdfLatex()) {
                pdflatexInput.setBackground(Color.green);
            } else {
                pdflatexInput.setBackground(Color.red);
            }
        });

        Utils.addChangeListener(gitFolderInput, (ChangeEvent e) -> {
            SavedVariables.setMainGitDir(gitFolderInput.getText());
            if (MainWindow.getInstance().updateDatabase()) {
                gitFolderInput.setBackground(Color.green);
                GitWrapper.setup();
            } else {
                gitFolderInput.setBackground(Color.red);
            }

        });

        Utils.addChangeListener(templatesFolderInput, (ChangeEvent e) -> {
            SavedVariables.setTexModelsPaths(templatesFolderInput.getText());
            File f = new File(SavedVariables.getTexModelsPaths());
            if (f.exists() && f.isDirectory()) {
                List<String> files = Arrays.asList(f.list());
                if (files.contains("DSmodel.tex") && files.contains("DMmodel.tex") && files.contains("Collemodel.tex") && files.contains("TDmodel.tex")) {
                    if (SavedVariables.getTexModelsPaths().contains("defaultLatexTemplates")) {
                        templatesFolderInput.setBackground(Color.orange);
                    } else {
                        templatesFolderInput.setBackground(Color.green);
                    }
                } else {
                    templatesFolderInput.setBackground(Color.red);
                }
            } else {
                templatesFolderInput.setBackground(Color.red);
            }
        });

        Utils.addChangeListener(outputDirInput, (ChangeEvent e) -> {
            SavedVariables.setOutputDir(outputDirInput.getText());

            File f = new File(SavedVariables.getOutputDir());
            if (f.exists() && f.isDirectory()) {
                List<String> files = Arrays.asList(f.list());
                if (files.contains("DM") && files.contains("DS")) {

                    outputDirInput.setBackground(Color.green);
                    MainWindow.getInstance().creationSujetView1.enableExport(true);
                } else {
                    outputDirInput.setBackground(Color.red);
                    MainWindow.getInstance().creationSujetView1.enableExport(false);
                }
            } else {
                MainWindow.getInstance().creationSujetView1.enableExport(false);
                outputDirInput.setBackground(Color.red);
            }

        });

        // force tests
        templatesFolderInput.setText(templatesFolderInput.getText());

        gitFolderInput.setText(gitFolderInput.getText());

        pdflatexInput.setText(pdflatexInput.getText());

        if (outputDirInput.getText().isEmpty()) {
            outputDirInput.setText("A remplir pour pouvoir exporter");
        } else {
            outputDirInput.setText(outputDirInput.getText());
        }

        Utils.addChangeListener(globalDictTextField, (ChangeEvent e) -> {
            SavedVariables.setGlobalDict(globalDictTextField.getText());
        });

        Utils.addChangeListener(customDictTextField, (ChangeEvent e) -> {
            SavedVariables.setCustomDict(customDictTextField.getText());
        });

        Utils.addChangeListener(colorSetTextField, (ChangeEvent e) -> {
            String color = colorSetTextField.getText();
            int token = LatexTextEditor.getToken((String) tokenComboBox.getSelectedItem());
            try {
                colorSetTextField.setForeground(Utils.getColorFromString(color));
                SavedVariables.setColor(token, color);
            } catch (Exception ex) {
                color = Utils.getStringFromColor(SavedVariables.getColor(token));
                colorSetTextField.setText(color);
                colorSetTextField.setForeground(Utils.getColorFromString(color));
            }
        });

        this.guessEntries();

    }

    public void guessEntries() {

        if ("".equals(this.pdflatexInput.getText())) {
            this.pdflatexInput.setText(OsRelated.guessPdfLAtexPath().trim());
        }

        if ("".equals(this.gitFolderInput.getText())) {
            String localpath = Options.class.getProtectionDomain().getCodeSource().getLocation().getPath();

            try {
                String decodedPath = OsRelated.pathAccordingToOS(URLDecoder.decode(localpath, "UTF-8").trim().split("bpep")[0] + "bpep");
                if (OsRelated.isWindows()) {
                    decodedPath = decodedPath.replaceFirst("\\\\", "");
                    // this prevent windows to include a "\" before C:
                }
                this.gitFolderInput.setText(decodedPath);

            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if ("".equals(this.templatesFolderInput.getText())) {

            String localpath = Options.class.getProtectionDomain().getCodeSource().getLocation().getPath();

            try {
                String decodedPath = OsRelated.pathAccordingToOS(URLDecoder.decode(localpath, "UTF-8").trim().split("bpep")[0] + "bpep/fichiers_utiles/defaultLatexTemplates");
                this.templatesFolderInput.setText(decodedPath);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        //
        if ("".equals(this.globalDictTextField.getText())) {

            String localpath = Options.class.getProtectionDomain().getCodeSource().getLocation().getPath();

            try {
                String decodedPath = OsRelated.pathAccordingToOS(URLDecoder.decode(localpath, "UTF-8").trim().split("bpep")[0] + "bpep/fichiers_utiles/dict_fra.txt");
                this.globalDictTextField.setText(decodedPath);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if ("zzz".equals(this.customDictTextField.getText())) {

            String localpath = Options.class.getProtectionDomain().getCodeSource().getLocation().getPath();

            try {
                String decodedPath = OsRelated.pathAccordingToOS(URLDecoder.decode(localpath, "UTF-8").trim().split("bpep")[0] + "bpep/untracked/dico_perso.txt");
                this.customDictTextField.setText(decodedPath);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Options.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public boolean isCompletionRequired() {
        return "zzz".equals(this.gitFolderInput.getText());
    }

    public void setGitOutputText(String txt) {
        this.gitOutputText.setText(txt);
        this.gitOutputText.setRows(this.gitOutputText.getLineCount());
        this.revalidate();
        this.repaint();
    }

    public void appendGitOutputText(String txt) {
        this.gitOutputText.append(txt);
        this.gitOutputText.setRows(this.gitOutputText.getLineCount());
        this.revalidate();
        this.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        gitPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        statusButton = new javax.swing.JButton();
        pushButton = new javax.swing.JButton();
        pullButton = new javax.swing.JButton();
        commitButton = new javax.swing.JButton();
        gitOutputText = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        actionsPanels = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        editorPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        spellCheckBox = new javax.swing.JCheckBox();
        spellPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        globalDictTextField = new javax.swing.JTextField();
        customDictTextField = new javax.swing.JTextField();
        autoCompletionCheckBox = new javax.swing.JCheckBox();
        coloringCheckBox = new javax.swing.JCheckBox();
        coloringPanel = new javax.swing.JPanel();
        tokenComboBox = new javax.swing.JComboBox<>();
        colorSetTextField = new javax.swing.JTextField();
        titlesCheckBox = new javax.swing.JCheckBox();
        jSeparator4 = new javax.swing.JSeparator();
        newPathesPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        pdflatexInput = new javax.swing.JTextField();
        templatesFolderInput = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        outputDirInput = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        gitFolderInput = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(300, 526));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel1.setPreferredSize(new java.awt.Dimension(400, 522));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        jLabel7.setFont(new java.awt.Font("Lucida Grande", 3, 13)); // NOI18N
        jLabel7.setText("Git :");

        jPanel5.setLayout(new java.awt.GridLayout(1, 0, 3, 0));

        statusButton.setText("Status");
        statusButton.setToolTipText("<html>\nVérifie les actions GIT à effectuer.\n<br>\nCette commande est équivalente à la saisie sur le terminal de :  \n<ol>\n    <li> git fetch \n    <li> git status\n</ol>\n</html>");
        statusButton.setMaximumSize(null);
        statusButton.setMinimumSize(new java.awt.Dimension(60, 22));
        statusButton.setPreferredSize(new java.awt.Dimension(60, 22));
        statusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusButtonActionPerformed(evt);
            }
        });
        jPanel5.add(statusButton);

        pushButton.setText("Push");
        pushButton.setToolTipText("<html>\nEnvoie le dernier commit effetué (sauvegarde) sur le serveur. \n<br>\nLes conflits (plusieurs personnes modifiant le même fichier en même temps) n'étant pas gérés à partir de l'interface graphique. Il faudra dans ce cas utiliser un logiciel dédié ou bien le terminal.\n</html>");
        pushButton.setEnabled(false);
        pushButton.setMaximumSize(null);
        pushButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pushButtonActionPerformed(evt);
            }
        });
        jPanel5.add(pushButton);

        pullButton.setText("Pull");
        pullButton.setToolTipText("Réccupère les nouvelles données (dernier commit) depuis le serveur.");
        pullButton.setEnabled(false);
        pullButton.setMaximumSize(null);
        pullButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pullButtonActionPerformed(evt);
            }
        });
        jPanel5.add(pullButton);

        commitButton.setText("Commit");
        commitButton.setToolTipText("Réalise une sauvegarde (commit) de la base de donnée. Ce commit pourra ensuite être envoyé sur le serveur (push)");
        commitButton.setEnabled(false);
        commitButton.setMaximumSize(null);
        commitButton.setMinimumSize(new java.awt.Dimension(70, 29));
        commitButton.setPreferredSize(new java.awt.Dimension(70, 29));
        commitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commitButtonActionPerformed(evt);
            }
        });
        jPanel5.add(commitButton);

        gitOutputText.setEditable(false);
        gitOutputText.setBackground(javax.swing.UIManager.getDefaults().getColor("ProgressBar.background"));
        gitOutputText.setColumns(26);
        gitOutputText.setLineWrap(true);
        gitOutputText.setRows(1);
        gitOutputText.setTabSize(4);
        gitOutputText.setText("    Cliquez sur status pour une MaJ.");
        gitOutputText.setMinimumSize(new java.awt.Dimension(1, 120));

        javax.swing.GroupLayout gitPanelLayout = new javax.swing.GroupLayout(gitPanel);
        gitPanel.setLayout(gitPanelLayout);
        gitPanelLayout.setHorizontalGroup(
            gitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gitPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(gitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(gitOutputText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        gitPanelLayout.setVerticalGroup(
            gitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gitPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel7)
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(gitOutputText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jPanel1.add(gitPanel);

        jSeparator2.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel1.add(jSeparator2);

        jLabel9.setFont(new java.awt.Font("Lucida Grande", 3, 13)); // NOI18N
        jLabel9.setText("Actions :");

        jCheckBox1.setSelected(SavedVariables.getAutoSave());
        jCheckBox1.setText("Sauvegarde auto");
        jCheckBox1.setToolTipText("<html>\nLes fichiers sont <em>automatiquement</em> sauvegardés quand les onglets sont cachés.\n<br>\nCela peut être utile lorsque vous selectionnez un nouvel exercice (l'ancien disparait).\n</html>");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setSelected(SavedVariables.getMultiEdit());
        jCheckBox2.setText("Mode avancé");
        jCheckBox2.setToolTipText("<html> Permet d'activer le mode <em>avancé</em> <br> \nqui permet de modifier simultanément plusieurs exercices <br>\n(find and replace) sur les mots clefs ou bien des expressions présentes <br>\ndans les fichiers sujet.tex et aussi de verifier si l'ensemble des exercices compile sans erreur.\n</html>");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(13, 8, 26, 1));
        jSpinner1.setValue(SavedVariables.getFontSize());
        jSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinner1StateChanged(evt);
            }
        });

        jLabel3.setText("Taille police IHM");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Light", "IntelliJ", "Dark", "Darcula" }));
        jComboBox1.setSelectedIndex(SavedVariables.getTheme());
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel12.setText("Thème");

        jCheckBox3.setSelected(SavedVariables.getpreviewSave());
        jCheckBox3.setText("Sauvegarde des previews");
        jCheckBox3.setToolTipText("<html>\nLes previews d'exercices seront sauvegardées dans les dossiers d'exercices\n<br>\nElle pourront donc être affichées beaucoup plus vite et un mécanisme se charge de les mettres à jour si besoin (modification du sujet, du readme ou des mots clés).\n<br>\nCes fichiers (preview.pdf) ne seront pas partagés sous git car ils peuvent allourdir considérablement la bpep.\n</html>");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout actionsPanelsLayout = new javax.swing.GroupLayout(actionsPanels);
        actionsPanels.setLayout(actionsPanelsLayout);
        actionsPanelsLayout.setHorizontalGroup(
            actionsPanelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionsPanelsLayout.createSequentialGroup()
                .addGroup(actionsPanelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(actionsPanelsLayout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox2)
                        .addGap(0, 10, Short.MAX_VALUE))
                    .addGroup(actionsPanelsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(actionsPanelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(actionsPanelsLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(actionsPanelsLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(4, 4, 4)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(15, 15, 15)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSpinner1)))))
                .addContainerGap())
        );
        actionsPanelsLayout.setVerticalGroup(
            actionsPanelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionsPanelsLayout.createSequentialGroup()
                .addComponent(jLabel9)
                .addGap(2, 2, 2)
                .addGroup(actionsPanelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2)
                    .addComponent(jCheckBox3))
                .addGap(2, 2, 2)
                .addGroup(actionsPanelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)))
        );

        jPanel1.add(actionsPanels);

        jSeparator3.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel1.add(jSeparator3);

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 3, 13)); // NOI18N
        jLabel10.setText("Editeur :");

        spellCheckBox.setSelected(SavedVariables.getSpellCheck());
        spellCheckBox.setText("Correcteur orthographique");
        spellCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spellCheckBoxActionPerformed(evt);
            }
        });

        spellPanel.setVisible(SavedVariables.getSpellCheck());

        jLabel2.setText("Dictionnaire global");

        jLabel11.setText("Dictionnaire personel");

        globalDictTextField.setText(SavedVariables.getGlobalDict()
        );
        globalDictTextField.setToolTipText("<html>\nChemin d'acces (absolu) vers le dictionnaire principal\n<ul>\n    <li> Fichier à encodage UTF-8  </li>\n    <li> Un seul mot par ligne  </li>\n    <li> Ce fichier ne sera pas modifié par l'application   </li>\n</ul>\nLe dictionnaire sera utilisé pour la verification orthographique (aucune vérification sur la grammaire n'est effectuée !). <br>\n<em> Un exmple de dictionnaire se trouve dans le repertoire \"fichiers_utiles\" de la BPEP. </em>\n</html>");
        globalDictTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nPerformed(evt);
            }
        });

        customDictTextField.setText(SavedVariables.getCustomDict()
        );
        customDictTextField.setToolTipText("<html>\nChemin d'acces (absolu) vers le dictionnaire personnel. \n<ul>\n    <li> Fichier à encodage UTF-8  </li>\n    <li> Un seul mot par ligne  </li>\n    <li> L'application pourra ajouter de nouveaux mots de manière définitive à ce dictionnaire  </li>\n</ul>\nCe fichier (facultatif) pourra acceuillir par exemple des termes scientifiques liés aux exercices et absents du dictionnaire principal.\n</html>");
        customDictTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customDictTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout spellPanelLayout = new javax.swing.GroupLayout(spellPanel);
        spellPanel.setLayout(spellPanelLayout);
        spellPanelLayout.setHorizontalGroup(
            spellPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spellPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(spellPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(spellPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(globalDictTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                    .addComponent(customDictTextField))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        spellPanelLayout.setVerticalGroup(
            spellPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spellPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(spellPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(globalDictTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(spellPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(customDictTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        autoCompletionCheckBox.setSelected(SavedVariables.getAutoCompletion());
        autoCompletionCheckBox.setText("auto-completion");
        autoCompletionCheckBox.setToolTipText("Permet d'utiliser l'auto-complétion (déclanchée après la saisie du caractère \"\\\").");
        autoCompletionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoCompletionCheckBoxActionPerformed(evt);
            }
        });

        coloringCheckBox.setSelected(SavedVariables.getColoring());
        coloringCheckBox.setText("Coloration syntaxique");
        coloringCheckBox.setToolTipText("<html>\nPermet  d'utiliser la coloration syntaxique.\n<br>\n Bien qu'optimisée, cette option peut rallentir la saisie de texte <br> sur certaines anciennes machines et peut donc être desactivée.\n</html>");
        coloringCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coloringCheckBoxActionPerformed(evt);
            }
        });

        coloringPanel.setVisible(SavedVariables.getColoring());

        tokenComboBox.setToolTipText("Type d'élément");
        for (String s:LatexTextEditor.getTokenNames()){
            tokenComboBox.addItem(s);
        }
        tokenComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tokenComboBoxActionPerformed(evt);
            }
        });

        colorSetTextField.setText("jTextField1");
        colorSetTextField.setToolTipText("Couleur au format r,g,b");
        tokenComboBoxAction();
        colorSetTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorSetTextFieldActionPerformed(evt);
            }
        });

        titlesCheckBox.setSelected(SavedVariables.getBigTitles());
        titlesCheckBox.setText("Titres et parties en grand");
        titlesCheckBox.setToolTipText("Affiche les titres des parties et sous parties en plus grand");
        titlesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titlesCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout coloringPanelLayout = new javax.swing.GroupLayout(coloringPanel);
        coloringPanel.setLayout(coloringPanelLayout);
        coloringPanelLayout.setHorizontalGroup(
            coloringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(coloringPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tokenComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorSetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(coloringPanelLayout.createSequentialGroup()
                .addComponent(titlesCheckBox)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        coloringPanelLayout.setVerticalGroup(
            coloringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(coloringPanelLayout.createSequentialGroup()
                .addGroup(coloringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tokenComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colorSetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(titlesCheckBox)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout editorPanelLayout = new javax.swing.GroupLayout(editorPanel);
        editorPanel.setLayout(editorPanelLayout);
        editorPanelLayout.setHorizontalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(spellPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10))
                    .addComponent(spellCheckBox)
                    .addComponent(coloringPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(editorPanelLayout.createSequentialGroup()
                        .addComponent(autoCompletionCheckBox)
                        .addGap(50, 50, 50)
                        .addComponent(coloringCheckBox)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        editorPanelLayout.setVerticalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editorPanelLayout.createSequentialGroup()
                .addComponent(jLabel10)
                .addGap(0, 0, 0)
                .addComponent(spellCheckBox)
                .addGap(0, 0, 0)
                .addComponent(spellPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(autoCompletionCheckBox)
                    .addComponent(coloringCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(coloringPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        jPanel1.add(editorPanel);

        jSeparator4.setMaximumSize(new java.awt.Dimension(32767, 10));
        jPanel1.add(jSeparator4);

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 3, 13)); // NOI18N
        jLabel8.setText("Chemins d'accès :");

        pdflatexInput.setText(SavedVariables.getPdflatexCmd()
        );
        pdflatexInput.setToolTipText("<html>\nChemin d'acces (absolu) vers l'executable pdflatex. <br>\n(Peut être obtenu en tappant \"which pdflatex\" dans un terminal)\n</html>");
        pdflatexInput.setCaretPosition(0);
        pdflatexInput.setMinimumSize(new java.awt.Dimension(6, 6));
        pdflatexInput.setPreferredSize(new java.awt.Dimension(240, 24));
        pdflatexInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pdflatexInputActionPerformed(evt);
            }
        });

        templatesFolderInput.setText(SavedVariables.getTexModelsPaths());
        templatesFolderInput.setToolTipText("<html>\nChemin d'acces (absolu) vers le dossier contenant les templates latex :\n<ul>\n    <li> DSmodel.tex  </li>\n    <li> DMmodel.tex  </li>\n    <li> Collemodel.tex  </li>\n    <li> TDmodel.tex  </li>\n</ul>\nCes documents servent de template pour l'inclusion des exercices et doivent inclure le fichier fichiers_utiles/raccourcis_communs.sty <br> et contenir une ligne : ****\n<br> \nqui sera remplacée par l'import des exercices lors de l'édition d'une composition.\n<br>\n<br>\n<em> Des exemples de fichiers fonctionnels se trouvent dans le répertoire path/To/Git/Dir/fichiers_utiles/defaultLatexTemplates <br> (utilsation non conseillée à long terme : couleur orange) </em>\n<br>\n<br>\n<em> Il est préférable d'utiliser ces propres templates en compiant le contenu du dossier precedant puis en <br> modifiants les fichiers suivant ses besoins (couleur verte) </em>\n</html>");
        templatesFolderInput.setPreferredSize(new java.awt.Dimension(240, 24));

        jLabel6.setText("dossier d'export");

        outputDirInput.setText(SavedVariables.getOutputDir());
        outputDirInput.setToolTipText("<html>\nChemin d'acces (absolu) vers le dossier contenant vos DSs/DMs de l'année en cours. <br>\nCe dossier doit contenir un sous dossier nommé DM et un autre nommé DS. <br>\nAinsi, une composition type DS exportée sera placé dans le sous dossier DS de manière incrémentielle (exemple DS5 si un dossier DS4 est déjà présent).\n<br> <br>\nLes composition exportées seront copiées de manière pérènne à cet endroit et ne seront pas affectées par des modifications ultérieurs de la BPEP.\n</html>");
        outputDirInput.setPreferredSize(new java.awt.Dimension(240, 24));

        jLabel1.setText("pdflatex");

        gitFolderInput.setText(SavedVariables.getMainGitDir());
        gitFolderInput.setToolTipText("Chemin d'acces (absolu) vers le dossier contenant le repository git de la BPEP.");
        gitFolderInput.setPreferredSize(new java.awt.Dimension(240, 24));
        gitFolderInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gitFolderInputActionPerformed(evt);
            }
        });

        jLabel4.setText("dossier git");

        jLabel5.setText("dossier modèles");

        javax.swing.GroupLayout newPathesPanelLayout = new javax.swing.GroupLayout(newPathesPanel);
        newPathesPanel.setLayout(newPathesPanelLayout);
        newPathesPanelLayout.setHorizontalGroup(
            newPathesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newPathesPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(newPathesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(newPathesPanelLayout.createSequentialGroup()
                        .addGroup(newPathesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addGroup(newPathesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel4)
                                .addComponent(jLabel1)))
                        .addGap(10, 10, 10)
                        .addGroup(newPathesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(outputDirInput, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                            .addComponent(templatesFolderInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(gitFolderInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pdflatexInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(10, 10, 10))
        );
        newPathesPanelLayout.setVerticalGroup(
            newPathesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(newPathesPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel8)
                .addGap(10, 10, 10)
                .addGroup(newPathesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(pdflatexInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(newPathesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(gitFolderInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(newPathesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(templatesFolderInput, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(newPathesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(outputDirInput, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel1.add(newPathesPanel);

        jScrollPane1.setViewportView(jPanel1);

        add(jScrollPane1);
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        SavedVariables.setAutoSave(jCheckBox1.isSelected());
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void pushButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pushButtonActionPerformed
        this.setGitOutputText("requête en cours ...");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String output = GitWrapper.push();
                Options.this.setGitOutputText(output);
                String status = GitWrapper.status();
                Options.this.checkStatus();
                Options.this.appendGitOutputText("\n - - - - - - -\n" + status);
            }
        });

    }//GEN-LAST:event_pushButtonActionPerformed

    private void commitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commitButtonActionPerformed
        this.setGitOutputText("requête en cours ...");

        SwingUtilities.invokeLater(() -> {
            GitWrapper.commit();
            Options.this.setGitOutputText("commit effectué");
            String status = GitWrapper.status();
            Options.this.checkStatus();
            Options.this.appendGitOutputText("\n - - - - - - -\n" + status);
        });

    }//GEN-LAST:event_commitButtonActionPerformed

    private void pullButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pullButtonActionPerformed
        this.setGitOutputText("requête en cours ...");

        SwingUtilities.invokeLater(() -> {
            if (GitWrapper.isConflictDetected()) {
                GitWrapper.rebaseContinue();
            } else {
                GitWrapper.pull();
                String out = GitWrapper.pull();
                if (out.contains("succes")) {
                    Options.this.setGitOutputText(out);
                    MainWindow.getInstance().updateDatabase();
                } else {

                    Options.this.setGitOutputText("Conflits à résoudre. \n  -> Utilisez soit exerciceExplorer, le terminal ou github desktop pour les identifier/résoudre");

                }
            }
            String status = GitWrapper.status();
            Options.this.checkStatus();
            Options.this.appendGitOutputText("\n - - - - - - -\n" + status);
        });


    }//GEN-LAST:event_pullButtonActionPerformed

    private void checkStatus() {
        this.commitButton.setEnabled(GitWrapper.commitButton);
        this.pushButton.setEnabled(GitWrapper.pushButton);
        if (GitWrapper.isConflictDetected()) {
            this.pullButton.setText("Rebase");
        } else {
            this.pullButton.setText("Pull");
        }
        this.pullButton.setEnabled(GitWrapper.pullButton);

    }

    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public void askStatusUpdateAndPrint() {

        new Thread(() -> {

            SwingUtilities.invokeLater(() -> {
                this.setGitOutputText("requête en cours ...");
            });

            String out = GitWrapper.status();

            SwingUtilities.invokeLater(() -> {
                if (GitWrapper.isConflictDetected()) {
                    Options.this.pullButton.setText("Valider");
                } else {
                    Options.this.pullButton.setText("Pull");
                }
                Options.this.setGitOutputText(out);
                Options.this.checkStatus();
            });

        }).start();

    }

    private void statusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusButtonActionPerformed
        askStatusUpdateAndPrint();
    }//GEN-LAST:event_statusButtonActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        SavedVariables.setMultiEdit(jCheckBox2.isSelected());
        MainWindow.getInstance().updateGlobalMenuBarStatus();
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void autoCompletionCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoCompletionCheckBoxActionPerformed
        SavedVariables.setAutoCompletion(autoCompletionCheckBox.isSelected());
    }//GEN-LAST:event_autoCompletionCheckBoxActionPerformed

    private void spellCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spellCheckBoxActionPerformed
        spellPanel.setVisible(spellCheckBox.isSelected());
        SavedVariables.setSpellCheck(spellCheckBox.isSelected());
    }//GEN-LAST:event_spellCheckBoxActionPerformed

    private void coloringCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coloringCheckBoxActionPerformed
        coloringPanel.setVisible(coloringCheckBox.isSelected());
        SavedVariables.setColoring(coloringCheckBox.isSelected());
    }//GEN-LAST:event_coloringCheckBoxActionPerformed

    private void nPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nPerformed
        SavedVariables.setGlobalDict(globalDictTextField.getText());
    }//GEN-LAST:event_nPerformed

    private void titlesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titlesCheckBoxActionPerformed
        SavedVariables.setBigTitles(titlesCheckBox.isSelected());
    }//GEN-LAST:event_titlesCheckBoxActionPerformed

    private void colorSetTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorSetTextFieldActionPerformed


    }//GEN-LAST:event_colorSetTextFieldActionPerformed

    private void tokenComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tokenComboBoxActionPerformed
        tokenComboBoxAction();
    }//GEN-LAST:event_tokenComboBoxActionPerformed

    private void gitFolderInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gitFolderInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gitFolderInputActionPerformed

    private void customDictTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customDictTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_customDictTextFieldActionPerformed

    private void pdflatexInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdflatexInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pdflatexInputActionPerformed

    private void jSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinner1StateChanged
        SavedVariables.setFontSize((int) jSpinner1.getValue());
        setUIFont(new javax.swing.plaf.FontUIResource("SansSerif", Font.PLAIN, SavedVariables.getFontSize()));
        SwingUtilities.updateComponentTreeUI(getTopLevelAncestor());
    }//GEN-LAST:event_jSpinner1StateChanged

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        int choice = jComboBox1.getSelectedIndex();
        SavedVariables.setTheme(choice);
        MainWindow.getInstance().installTheme();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        SavedVariables.setPreviewSave(jCheckBox3.isSelected());
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    public void tokenComboBoxAction() {
        int token = LatexTextEditor.getToken((String) tokenComboBox.getSelectedItem());
        Color c = SavedVariables.getColor(token);
        colorSetTextField.setForeground(c);
        colorSetTextField.setText(Utils.getStringFromColor(c));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel actionsPanels;
    private javax.swing.JCheckBox autoCompletionCheckBox;
    private javax.swing.JTextField colorSetTextField;
    private javax.swing.JCheckBox coloringCheckBox;
    private javax.swing.JPanel coloringPanel;
    private javax.swing.JButton commitButton;
    private javax.swing.JTextField customDictTextField;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JTextField gitFolderInput;
    private javax.swing.JTextArea gitOutputText;
    private javax.swing.JPanel gitPanel;
    private javax.swing.JTextField globalDictTextField;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JPanel newPathesPanel;
    private javax.swing.JTextField outputDirInput;
    private javax.swing.JTextField pdflatexInput;
    private javax.swing.JButton pullButton;
    private javax.swing.JButton pushButton;
    private javax.swing.JCheckBox spellCheckBox;
    private javax.swing.JPanel spellPanel;
    private javax.swing.JButton statusButton;
    private javax.swing.JTextField templatesFolderInput;
    private javax.swing.JCheckBox titlesCheckBox;
    private javax.swing.JComboBox<String> tokenComboBox;
    // End of variables declaration//GEN-END:variables
}
