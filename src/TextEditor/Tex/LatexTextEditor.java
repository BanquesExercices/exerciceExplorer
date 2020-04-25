/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextEditor.Tex;

import Helper.SavedVariables;
import TextEditor.Base.BaseTextEditor;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Observer;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.text.TextAction;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.TemplateCompletion;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.RecordableTextAction;

/**
 *
 * @author mbrebion
 */
@SuppressWarnings("serial")
public class LatexTextEditor extends BaseTextEditor {

    static final String MY_LATEX_STYLE = "text/mls";
    protected TexFoldParser tfp;
    protected JMenu debugMenu;
    protected long time = 0;
    protected static Dictionary<String, Integer> tokenDict = null;
    protected static Dictionary<Integer, String> defaultColorDict = null;

    public LatexTextEditor(String text) {
        super(text);
        this.myInit();
    }

    public LatexTextEditor() {
        super();
        time = System.nanoTime();
        this.myInit();

    }

    public static JScrollPane getMyTextAreaInScrollPane(LatexTextEditor instance) {
        RTextScrollPane sp = new RTextScrollPane(instance);
        return sp;
    }

    protected final void myInit() {

        this.setAutoIndentEnabled(true);

        // adding spell checker
        this.setupSpellChecker();

        // adding folding abilities
        this.setCodeFoldingEnabled(true);
        tfp = new TexFoldParser();
        FoldParserManager.get().addFoldParserMapping(MY_LATEX_STYLE, tfp);

        // adding syntax coloring
        this.setupSyntaxColoring();

        // adding auto-completion abilities
        this.setupAutoCompletion();

    }

    public static List<String> getTokenNames() {
        initTokenDict();
        return Collections.list(tokenDict.keys());
    }

    public static int getToken(String name) {
        return tokenDict.get(name);
    }

    public static String getDefaultColor(int token) {
        return defaultColorDict.get(token);
    }

    protected static void initTokenDict() {
        if (tokenDict != null) {
            return;
        }
        tokenDict = new Hashtable<>();
        tokenDict.put("$ ... $", LatexTokenMaker.TOKEN_INLINE_EQ);
        tokenDict.put("{ }", Token.SEPARATOR);
        tokenDict.put("% commentaire", Token.COMMENT_EOL);
        tokenDict.put("Titre", LatexTokenMaker.TOKEN_TITLE);
        tokenDict.put("Partie", LatexTokenMaker.TOKEN_PART);
        tokenDict.put("Sous-partie", LatexTokenMaker.TOKEN_SUB_PART);
        tokenDict.put("Question", LatexTokenMaker.TOKEN_ADDQ);
        tokenDict.put("Réponse", LatexTokenMaker.TOKEN_ADDA);
        tokenDict.put("\\eq { ... } ", LatexTokenMaker.TOKEN_MLE);
        tokenDict.put("Question cachée", LatexTokenMaker.TOKEN_ADDQ_H);

        defaultColorDict = new Hashtable<>();
        defaultColorDict.put(LatexTokenMaker.TOKEN_INLINE_EQ, "0,80,0");
        defaultColorDict.put(Token.SEPARATOR, "0,0,0");
        defaultColorDict.put(Token.COMMENT_EOL, "150,150,150");
        defaultColorDict.put(LatexTokenMaker.TOKEN_TITLE, "180,0,0");
        defaultColorDict.put(LatexTokenMaker.TOKEN_PART, "130,0,0");
        defaultColorDict.put(LatexTokenMaker.TOKEN_SUB_PART, "100,0,0");
        defaultColorDict.put(LatexTokenMaker.TOKEN_ADDQ, "255,0,0");
        defaultColorDict.put(LatexTokenMaker.TOKEN_ADDA, "0,0,255");
        defaultColorDict.put(LatexTokenMaker.TOKEN_MLE, "0,80,0");
        defaultColorDict.put(LatexTokenMaker.TOKEN_ADDQ_H, "200,200,200");

       
    }

    public void advert() {
        tfp.advert();
    }

    public void addObserver(Observer o) {
        tfp.addObserver(o);
    }

    public void removeObserver(Observer o) {
        tfp.removeObserver(o);
    }

    public void clearAllObservers() {
        tfp.clearAllObservers();
    }

    public List<Integer> getQuestionLines() {
        return tfp.questionsList;
    }

    public List<String> getTags() {
        return tfp.tagsList;
    }

    public int getQuestionNumber() {
        int out = 0;
        int offset = this.getCaretLineNumber();

        for (int i : tfp.questionsList) {
            if (i > offset+1) {
                return out;
            }
            out++;
        }
        return out;
    }

    @Override
    public void setupSpellChecker() {
        this.setDictParser();
        parser.setSpellCheckableTokenIdentifier(new LatexSpellCheckableTokenIdentifier());
        parser.setSquiggleUnderlineColor(Color.BLACK);
        this.clearParsers();
        this.addParser(parser);

    }

    @Override
    public void setupSyntaxColoring() {
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping(MY_LATEX_STYLE, LatexTokenMaker.class.getName());
        this.setSyntaxEditingStyle(MY_LATEX_STYLE);
        SyntaxScheme scheme = this.getSyntaxScheme();

        if (SavedVariables.getBigTitles()) {
            scheme.getStyle(LatexTokenMaker.TOKEN_TITLE).font = new Font("Times New Roman", Font.PLAIN, 20);
            scheme.getStyle(LatexTokenMaker.TOKEN_PART).font= new Font("Times New Roman", Font.PLAIN, 16);
            scheme.getStyle(LatexTokenMaker.TOKEN_SUB_PART).font= new Font("Times New Roman", Font.PLAIN, 14);
        }

        for (int token : Collections.list(defaultColorDict.keys())) {
            scheme.getStyle(token).foreground = SavedVariables.getColor(token);
        }
        // duplicated tokens
        scheme.getStyle(LatexTokenMaker.TOKEN_ADDA_H).foreground = SavedVariables.getColor(LatexTokenMaker.TOKEN_ADDQ_H);
        scheme.getStyle(LatexTokenMaker.TOKEN_MLE_IN_A).foreground = SavedVariables.getColor(LatexTokenMaker.TOKEN_MLE);
        scheme.getStyle(LatexTokenMaker.TOKEN_MLE_IN_Q).foreground = SavedVariables.getColor(LatexTokenMaker.TOKEN_MLE);

    }

    @Override
    public void setupAutoCompletion() {

        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        provider.setAutoActivationRules(true, "");
        AutoCompletion acb = new AutoCompletion(provider);
        acb.setAutoCompleteEnabled(true);
        acb.setAutoActivationEnabled(true);
        acb.setAutoCompleteSingleChoices(false);
        acb.setParameterAssistanceEnabled(true);
        acb.setChoicesWindowSize(260, 90);
        
        // general latex templates
        provider.addCompletion(new TemplateCompletion(provider, "begin{itemize}", "begin{itemize} ... \\end{itemize}", "begin{itemize}\n\t\\item ${cursor}\n\t\\item \n\\end{itemize} ", "Liste", "Liste"));
        provider.addCompletion(new TemplateCompletion(provider, "begin{enumerate}", "begin{enumerate} ... \\end{enumerate}", "begin{enumerate}\n\t\\item ${cursor}\n\t\\item \n\\end{enumerate} ", "Liste numérotée", "Liste numérotée"));
        provider.addCompletion(new TemplateCompletion(provider, "begin{env}", "begin{env} ... \\end{env}", "begin{${env}}\n\t \n\\end{${env}} ", "nouvel environnement", "nouvel environnement"));
        
        
        // specific latex templates
        provider.addCompletion(new TemplateCompletion(provider, "partie{", "partie{...}", "partie{${cursor}}\n", "Partie", "Partie"));
        provider.addCompletion(new TemplateCompletion(provider, "sousPartie{", "sousPartie{...}", "sousPartie{${cursor}}\n", "Sous-partie", "Sous-partie"));
        provider.addCompletion(new TemplateCompletion(provider, "fig{", "fig{0.8}{...}", "fig{0.8}{${cursor}}\n", "Figure", "Figure"));
        provider.addCompletion(new TemplateCompletion(provider, "eq{", "eq{...}", "eq{\n\t${cursor}\n}", "Equation", "Equation"));
        provider.addCompletion(new TemplateCompletion(provider, "addQ[", "addQ[...]{...}{...}", "addQ[${cursor}]{\n\t\n}{\n\t\n}", "Question selon version", "Question selon version"));
        provider.addCompletion(new TemplateCompletion(provider, "addQ{", "addQ{...}{...}", "addQ{\n\t${cursor}\n}{\n\t\n}", "Question", "Question"));
        provider.addCompletion(new TemplateCompletion(provider, "enonce{", "enonce{...}", "enonce{\n\t${cursor}\n}", "Enonce", "Enonce"));
        provider.addCompletion(new TemplateCompletion(provider, "tcols{", "tcols{0.49}{0.49}{...}{...}", "tcols{0.49}{0.49}{\n\t${cursor}\n}{\n\t\n}", "Double colonne", "Double Colonne"));

        // maths accelerators templates
        provider.addCompletion(new TemplateCompletion(provider, "frac{", "frac{...}{...}", "frac{${cursor}}{}", "Fraction", "Fraction"));

        
        acb.install(this);
    }

    private void setupJMenus() {

        // debug : 
        debugMenu = new JMenu("Debug");
        debugMenu.add(createMenuItem(new TextAction("printLastTokens") {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (int i = 0; i < LatexTextEditor.this.getLineCount(); i++) {
                    System.out.println((i + 1) + " :  " + ((RSyntaxDocument) LatexTextEditor.this.getDocument()).getLastTokenTypeOnLine(i));
                }

            }
        }));

        debugMenu.add(createMenuItem(new TextAction("switchToOriginal") {
            @Override
            public void actionPerformed(ActionEvent e) {
                LatexTokenMaker.login = "original";
                LatexTextEditor.this.updateWholeDocumentHighlighting();

            }
        }));
                

        debugMenu.add(createMenuItem(new TextAction("switchToMMB") {
            @Override
            public void actionPerformed(ActionEvent e) {
                LatexTokenMaker.login = "mmb";
                LatexTextEditor.this.updateWholeDocumentHighlighting();
            }
        }));
        
        debugMenu.add(createMenuItem(new TextAction("parseText") {
            @Override
            public void actionPerformed(ActionEvent e) {
                LatexParserBase  lpi = new LatexParserImpl();
                lpi.parseText( LatexTextEditor.this.getText());
                LatexTextEditor.this.setText(lpi.outputResult());
                
            }
            
        }));

        RecordableTextAction tq = new RecordableTextAction("toggle questions") {

            @Override
            public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {

                for (int i = 0; i < LatexTextEditor.this.getFoldManager().getFoldCount(); i++) {
                    Fold f = LatexTextEditor.this.getFoldManager().getFold(i);
                    if (f.getFoldType() == TexFoldParser.QUESTION_FOLD) {
                        f.toggleCollapsedState();
                    }
                }
            }

            @Override
            public String getMacroID() {
                return "tq";
            }
        };
        int mod = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        tq.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, mod));
        editMenu.add(tq);

        RecordableTextAction tr = new RecordableTextAction("toggle réponses") {

            @Override
            public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {

                for (int i = 0; i < LatexTextEditor.this.getFoldManager().getFoldCount(); i++) {
                    Fold f = LatexTextEditor.this.getFoldManager().getFold(i);
                    if (f.getFoldType() == TexFoldParser.ANSWER_FOLD) {
                        f.toggleCollapsedState();
                    }
                }
            }

            @Override
            public String getMacroID() {
                return "tr";
            }
        };
        
        tr.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.SHIFT_MASK+ mod));
        editMenu.add(tr);

        RecordableTextAction te = new RecordableTextAction("toggle énoncés") {

            @Override
            public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {

                for (int i = 0; i < LatexTextEditor.this.getFoldManager().getFoldCount(); i++) {
                    Fold f = LatexTextEditor.this.getFoldManager().getFold(i);
                    if (f.getFoldType() == TexFoldParser.ENONCE_FOLD) {
                        f.toggleCollapsedState();
                    }
                }
            }

            @Override
            public String getMacroID() {
                return "te";
            }
        };

        te.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.SHIFT_MASK+mod));
        editMenu.add(te);
        editMenu.addSeparator();

    }

    @Override
    public void addToMenuBar(JMenuBar menuBar) {
        if (!JMenuesEdited) {
            this.setupJMenus();
            JMenuesEdited = true;
        }
        menuBar.add(editMenu);
        menuBar.add(debugMenu);
    }

    @Override
    public void removeToMenuBar(JMenuBar menuBar) {
        if (!JMenuesEdited) {
            this.setupJMenus();
            JMenuesEdited = true;
        }
        menuBar.remove(editMenu);
        menuBar.remove(debugMenu);
    }

    private JMenuItem createMenuItem(Action action) {
        JMenuItem item = new JMenuItem(action);
        item.setToolTipText(null); // Swing annoyingly adds tool tip text to the menu item
        return item;
    }

}
