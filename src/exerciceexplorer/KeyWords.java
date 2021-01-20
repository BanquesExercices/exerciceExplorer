/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exerciceexplorer;

import Helper.OsRelated;
import Helper.SavedVariables;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author mbrebion
 */
public class KeyWords {

    protected List<String> keywords;
    protected static KeyWords instance = null;
    protected static Collection<JComboBox> jcbs = new HashSet<>();

    public static void updateKeywordsList() {
        try {
            getInstance().updateKeyWords();
        } catch (IOException ex) {
            System.err.println("global keyword file not found");
        }

    }
    
    public static boolean replaceKeywordAndUpdate(String before, String after) {
        boolean out=false;
        for (int i = 0; i < instance.keywords.size(); i++) {
            if (before.equals(instance.keywords.get(i))) {
                instance.keywords.set(i, after);
                out=true;
            }
        }
        OsRelated.writeToFile(instance.keywords,SavedVariables.getMainGitDir() + "/fichiers_utiles/mots_clefs.txt" );
        for (JComboBox jcb : jcbs) {
                jcb.setModel(getDefaultComboBoxModelModel(jcb));
            }
        return out;
    }

    public static DefaultComboBoxModel<String> getDefaultComboBoxModelModel(JComboBox jcb) {
        // add the jComboBox to the list of comboboxes which uses this model
        jcbs.add(jcb);

        // create the model
        DefaultComboBoxModel<String> out = new DefaultComboBoxModel<>();
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
        //  add the new keywords, sort, write down and update jcomboboxes
        BufferedWriter b = null;
        BufferedReader r = null;
        try {
            File f = new File(SavedVariables.getMainGitDir() + "/fichiers_utiles/mots_clefs.txt");

            // mix all keywords (check for duplicate)
            for (String key : keys) {
                if (!keywords.contains(key)) {
                    keywords.add(key);
                }
            }

            //keywords.sort(null); // sort
            Collator frCollator = Collator.getInstance(Locale.FRENCH);
            frCollator.setStrength(Collator.PRIMARY);
            Collections.sort(keywords, frCollator);

            b = new BufferedWriter(new FileWriter(f)); // write back to textfile
            PrintWriter out = new PrintWriter(b);
            for (String st : keywords) {
                out.println(st);
            }
            out.flush();

            for (JComboBox jcb : jcbs) {
                jcb.setModel(getDefaultComboBoxModelModel(jcb));
            }

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
        //BufferedReader b = new BufferedReader(new FileReader(f));
        BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
        
        String readLine = "";
        while ((readLine = b.readLine()) != null) {
            if (this.keywords.contains(readLine.trim())) {
                System.out.println("Duplicate keyword in global file : " + readLine.trim());
            }
            this.keywords.add(readLine.trim());
        }
        b.close();
    }

}
