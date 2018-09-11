/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exerciceexplorer;

import java.io.BufferedReader;
import java.io.File;
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
    protected List<String> readme = new ArrayList<>();
    protected List<String> content = new ArrayList<>();

    public Exercice(String name, String path, String kind) {
        this.name = name;
        this.path = path;
        if (kind.equals(DS) || kind.equals(TD) || kind.equals(Colle)) {
            this.kind = kind;
        } else {
            this.kind = DS;
        }

    }

    public List<String> getImports() {
        /**
         * return latex packages which must be manually imported for this
         * exercice
         */
        List<String> imports = new ArrayList<>();
        boolean status = false;

        for (String line : this.getContent()) {
            // the file is re read by getContent to take into account changes which can be made elsewhere
            if (line.trim().contains("%debutImport")) {
                status = true;
                continue;
            }
            if (line.trim().contains("%finImport")) {
                status = false;
                continue;
            }
            if (status) {
                imports.add(line.replace("%", ""));
            }
        }

        return imports;
    }

    public boolean containsKeyWords(List<String> kws) {
        if (keywords.isEmpty()) {
            this.updateKeywords();
        }

        boolean out = true;
        for (String kw : kws) {
            if (keywords.contains(kw) == false) {
                out = false;
            }
        }
        return out;
    }

    
    public void NotifyKeyWordsChanged(){
        this.updateKeywords();
    }
    
    public void NotifyReadmeChanged(){
    
    }
    
    public void NotifySubjectChanged(){
    
    }
    
    protected void updateKeywords() {

        try {
            this.keywords.clear();
            File f = new File(this.getKeywordsPath());
            BufferedReader b = new BufferedReader(new FileReader(f));
            String readLine = "";
            while ((readLine = b.readLine()) != null) {
                this.keywords.add(readLine.trim());

            }
        } catch (IOException ex) {
            System.err.println("keyword file not found for " + this.path);
        }
    }

    protected void updateReadme() {
        try {
            this.readme.clear();
            File f = new File(this.getReadmePath());
            BufferedReader b = new BufferedReader(new FileReader(f));
            String readLine = "";
            while ((readLine = b.readLine()) != null) {
                this.readme.add(readLine.trim());
            }
        } catch (IOException ex) {
            System.err.println("readme file not found for " + this.path);
        }

    }

    protected void updateContent() {
        try {
            this.content.clear();
            File f = new File(this.path + "/sujet.tex");
            BufferedReader b = new BufferedReader(new FileReader(f));
            String readLine = "";
            while ((readLine = b.readLine()) != null) {
                this.content.add(readLine.trim());
            }
        } catch (IOException ex) {
            System.err.println("sujet.tex file not found for " + this.path);
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
    
        public String getSubjectPath(){
        return path+"/sujet.tex";
    }
    
    public String getKeywordsPath(){
        return path+"/mots_clefs.txt";
    }
    
    public String getReadmePath(){
        return path+"/readme.txt";
    }


    /**
     * @return the kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * @return the keywords
     */
    public List<String> getKeywords() {
        this.updateKeywords();
        return keywords;
    }

    public List<String> getReadme() {
        this.updateReadme();
        return readme;
    }

    public List<String> getContent() {
        this.updateContent();
        return content;
    }

}
