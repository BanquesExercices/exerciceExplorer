/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exerciceexplorer;

import Helper.SavedVariables;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

/**
 *
 * @author mbrebion
 */
public class KeyWords {

    protected List<String> keywords;

    public KeyWords() {
        this.keywords = new ArrayList<>();
        try {
            this.updateKeyWords();
        } catch (IOException ex) {
            System.err.println("global keyword file not found");
        }
    }

    public DefaultComboBoxModel <String> getDefaultComboBoxModelModel() {
        DefaultComboBoxModel<String> out = new DefaultComboBoxModel<>();
        keywords.stream().forEach((kw) -> {
            out.addElement(kw);
        });
        return out;
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

    /**
     * @return the keywords
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * @param keywords the keywords to set
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

}
