/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exerciceexplorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mbrebion
 */
public class Exercice {

    public static final String DS = "DS", TD = "TD", Colle = "Colle";

    protected String name, path, kind;
    protected List<String> keywords = new ArrayList<>();

    
    
    public Exercice(String name, String path, String kind) {
        this.name = name;
        this.path = path;
        if (kind.equals(DS) || kind.equals(TD) || kind.equals(Colle)) {
            this.kind = kind;
        } else {
            this.kind = DS;
        }
        
        try {
            this.updateKeywords();
        } catch (IOException ex) {
            System.err.println("keyword file not found for " + this.path);
        }
    }

    
    public boolean containsKeyWords(List<String> kws){
        boolean out=true;
        for (String kw : kws){
            if (this.keywords.contains(kw)==false){
                out=false;
            }
        }
        return out;
    }
    
    protected void updateKeywords() throws FileNotFoundException, IOException {
        this.keywords.clear();
        File f = new File(this.path + "/mots_clefs.txt");
        BufferedReader b = new BufferedReader(new FileReader(f));
        String readLine = "";
        while ((readLine = b.readLine()) != null) {
            this.keywords.add(readLine.trim());
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    // getters & setters
    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * @param kind the kind to set
     */
    public void setKind(String kind) {
        this.kind = kind;
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
