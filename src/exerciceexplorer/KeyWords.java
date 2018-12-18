/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exerciceexplorer;

import Helper.SavedVariables;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author mbrebion
 */
public class KeyWords {

    protected List<String> keywords;
    protected static KeyWords instance = null;

    public static void updateKeywordsList() {
        try {
            getInstance().updateKeyWords();
        } catch (IOException ex) {
            System.err.println("global keyword file not found");
        }

    }

    public static DefaultComboBoxModel<String> getDefaultComboBoxModelModel() {
        DefaultComboBoxModel<String> out = new DefaultComboBoxModel<>();
        getInstance();
        getInstance().keywords.stream().forEach((kw) -> {
            out.addElement(kw);
        });
        return out;
    }

    public static boolean exists(String key) {
        return getInstance().keywords.contains(key);
    }

    public static void addNewKeywords(List<String> keys) {
        getInstance().addKeywords(keys);
    }

    /**
     * @return the keywords
     */
    public static List<String> getKeywords() {
        return getInstance().keywords;
    }

    /////////////////////////////////////////////////////////////////////////////
    protected static KeyWords getInstance() {
        if (instance == null) {
            instance = new KeyWords();
        }
        return instance;
    }

    protected KeyWords() {
        this.keywords = new ArrayList<>();
        try {
            this.updateKeyWords();
        } catch (IOException ex) {
            System.err.println("global keyword file not found");
        }
    }

    public void addKeywords(List<String> keys) {
        BufferedWriter b = null;
        try {
            File f = new File(SavedVariables.getMainGitDir() + "/fichiers_utiles/mots_clefs.txt");
            b = new BufferedWriter(new FileWriter(f, true));
            PrintWriter out = new PrintWriter(b);
            for (String st : keys) {
                if (!keywords.contains(st)) {
                    out.println(st);
                }
            }
            out.flush();

        } catch (IOException ex) {
            Logger.getLogger(KeyWords.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                b.close();
            } catch (IOException ex) {
                Logger.getLogger(KeyWords.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void updateKeyWords() throws FileNotFoundException, IOException {
        this.keywords.clear();
        File f = new File(SavedVariables.getMainGitDir() + "/fichiers_utiles/mots_clefs.txt");
        BufferedReader b = new BufferedReader(new FileReader(f));
        String readLine = "";
        while ((readLine = b.readLine()) != null) {
            if (this.keywords.contains(readLine.trim())) {
                System.out.println("Duplicate keyword in global file : " + readLine.trim());
            }
            this.keywords.add(readLine.trim());
        }
    }

}
