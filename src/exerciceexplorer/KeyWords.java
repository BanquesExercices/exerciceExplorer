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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
        getInstance().updateKeyWords();
        

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
        this.updateKeyWords();
       
    }

    public void addKeywords(List<String> keys) {
            //  add the new keywords, sort, write down and update jcomboboxes
        
            // mix all keywords (check for duplicate)
            for (String key : keys) {
                if (!keywords.contains(key)) {
                    keywords.add(key);
                }
            }

            // sort all key words
            this.sortKeyWords();

           
            // update comboboxes
            for (JComboBox jcb : jcbs) {
                jcb.setModel(getDefaultComboBoxModelModel(jcb));
            }

        

    }
    
    
    protected void sortKeyWords(){
            Collator frCollator = Collator.getInstance(Locale.FRENCH);
            frCollator.setStrength(Collator.PRIMARY);
            Collections.sort(keywords, frCollator);        
    }
    

    public void updateKeyWords() {
        
        this.keywords.clear();
        
        // a set is used to deal with duplicates
        Set<String> set=new HashSet<>();
        // we read all exercices and add every keywords once
        for (Exercice e : ExerciceFinder.getExercices()){
            for (String kw : e.getKeywords()){
                set.add(kw);
               
            }
        }
        
        this.keywords.addAll(set);
        this.sortKeyWords();
        
        // remove blank keywords (if any, they should be first elements in the sorted list)
        while(keywords.get(0).trim().isEmpty()){
            keywords.remove(0);
        }

    }

}
