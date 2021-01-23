/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TextEditor.Base;

import Helper.SavedVariables;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.RecordableTextAction;

/**
 *
 * @author mbrebion
 */
@SuppressWarnings("serial")
public class BaseTextEditor extends RSyntaxTextArea {

    protected boolean JMenuesEdited = false;
    protected JMenu editMenu;
    protected static boolean dictLoaded = false;
    protected static SpellingParser parser;

    public BaseTextEditor(String text) {
        super(text);
        this.setLineWrap(true);
        this.setTabSize(3);
        this.initiateEditMenu();        
    }


    public BaseTextEditor() {
        super();
        this.setLineWrap(true);
        this.setTabSize(3);
        initiateEditMenu();
    }

    protected void initiateEditMenu() {
        editMenu = new JMenu("Edit");
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.UNDO_ACTION)));
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.REDO_ACTION)));
        editMenu.addSeparator();
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.CUT_ACTION)));
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.COPY_ACTION)));
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.PASTE_ACTION)));
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.DELETE_ACTION)));
        editMenu.addSeparator();
        editMenu.add(createMenuItem(RTextArea.getAction(RTextArea.SELECT_ALL_ACTION)));
        editMenu.addSeparator();

    }

    public void setDictParser() {
        if (dictLoaded) {
            return;
        }
        String file = SavedVariables.getGlobalDict();
        String customFile = SavedVariables.getCustomDict();

        try {
            
            
            Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            
            SpellDictionaryHashMap dict = new SpellDictionaryHashMap(reader);
            parser = new SpellingParser(dict);
            //parser.setSpellCheckableTokenIdentifier(new LatexSpellCheckableTokenIdentifier());
            try {
                parser.setUserDictionary(new File(customFile));
            } catch (IOException ex) {
                System.err.println("problem with custom dict file");
            }
            dictLoaded = true;
        } catch (FileNotFoundException ex) {
            System.err.println("dict file not found");
        } catch (IOException ex) {
            System.err.println("dict of bad format");
        }

    }

    public static JScrollPane getMyTextAreaInScrollPane(BaseTextEditor instance) {
        RTextScrollPane sp = new RTextScrollPane(instance);
        return sp;
    }

    public void dealWithFileProcessorJMenu(FileProcessor s) {
        RecordableTextAction ts = new RecordableTextAction("Sauvegarder") {
            @Override
            public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
                s.saveFile();
            }

            @Override
            public String getMacroID() {
                return "ts";
            }
        };
        int mod = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        ts.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mod));
        editMenu.add(ts);
        editMenu.addSeparator();

        AbstractAction find = new AbstractAction("Find") {

            @Override
            public void actionPerformed(ActionEvent e) {
                s.toggleFindPanel();
            }
        };

        JMenuItem findItem = new JMenuItem(find);
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, mod));
        editMenu.add(findItem);

        AbstractAction replace = new AbstractAction("Replace") {

            @Override
            public void actionPerformed(ActionEvent e) {
                s.toggleReplacePanel();
            }
        };

        JMenuItem replaceItem = new JMenuItem(replace);
        replaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, mod));
        editMenu.add(replaceItem);

        // add reload autocomplete edit here
        AbstractAction reload = new AbstractAction("Reload AC") {

            @Override
            public void actionPerformed(ActionEvent e) {
                BaseTextEditor.this.setupAutoCompletion();
            }
        };
        JMenuItem reloadACItem = new JMenuItem(reload);
        editMenu.add(reloadACItem);

    }

    public void setupSyntaxColoring() {
    }

    public void setupSpellChecker() {
    }

    public void setupAutoCompletion() {
    }

    private void setupJMenus() {
    }

    public void addToMenuBar(JMenuBar menuBar) {

    }

    public void removeToMenuBar(JMenuBar menuBar) {
    }

    private JMenuItem createMenuItem(Action action) {
        JMenuItem item = new JMenuItem(action);
        item.setToolTipText(null); // Swing annoyingly adds tool tip text to the menu item
        return item;
    }

}
