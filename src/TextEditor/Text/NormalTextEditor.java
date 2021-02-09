/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextEditor.Text;

import Helper.SavedVariables;
import TextEditor.Base.BaseTextEditor;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author mbrebion
 */
@SuppressWarnings("serial")
public class NormalTextEditor extends BaseTextEditor {

    public NormalTextEditor(String text) {
        super(text);
        this.myInit();
    }

    public NormalTextEditor() {
        super();        
        this.myInit();

    }

    public static JScrollPane getMyTextAreaInScrollPane(NormalTextEditor instance) {
        RTextScrollPane sp = new RTextScrollPane(instance);
        return sp;
    }

    protected final void myInit() {
        // theme
        if (SavedVariables.getTheme() >= 2) {
            try {
                Theme theme = Theme.load(getClass().getResourceAsStream(
                        "/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
                theme.apply(this);
            } catch (IOException ioe) { // Never happens
                ioe.printStackTrace();
            }
        }
        // font : 
        this.setFont(new javax.swing.plaf.FontUIResource("SansSerif", Font.PLAIN, SavedVariables.getFontSize()));
        
        // adding auto-completion abilities
        this.setupAutoCompletion();

    }

    @Override
    public void setupSpellChecker() {
        this.setDictParser();
        //parser.setSpellCheckableTokenIdentifier(new LatexSpellCheckableTokenIdentifier());
        parser.setSquiggleUnderlineColor(Color.BLACK);
        this.clearParsers();
        this.addParser(parser);
    }

    @Override
    public void setupAutoCompletion() {

        DefaultCompletionProvider provider = new DefaultCompletionProvider();
        provider.setAutoActivationRules(true, "");
        // name
        provider.addCompletion(new BasicCompletion(provider, "Maxence Miguel-Brebion", "Maxence Miguel-Brebion", "Maxence Miguel-Brebion"));
        provider.addCompletion(new BasicCompletion(provider, "Elise Antoine", "Elise Antoine", "Elise Antoine"));
        // ...

        AutoCompletion acb = new AutoCompletion(provider);
        acb.install(this);
        acb.setAutoCompleteEnabled(true);
        acb.setAutoActivationEnabled(true);
        acb.setAutoCompleteSingleChoices(false);
        //acb.setParameterAssistanceEnabled(true);
        acb.setChoicesWindowSize(260, 90);
    }

    private void setupJMenus() {

    }

    @Override
    public void addToMenuBar(JMenuBar menuBar) {
        if (!JMenuesEdited) {
            this.setupJMenus();
            JMenuesEdited = true;
        }
        menuBar.add(editMenu);
    }

    @Override
    public void removeToMenuBar(JMenuBar menuBar) {
        if (!JMenuesEdited) {
            this.setupJMenus();
            JMenuesEdited = true;
        }
        menuBar.remove(editMenu);
    }

    private JMenuItem createMenuItem(Action action) {
        JMenuItem item = new JMenuItem(action);
        item.setToolTipText(null); // Swing annoyingly adds tool tip text to the menu item
        return item;
    }

}
