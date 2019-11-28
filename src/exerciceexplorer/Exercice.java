/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exerciceexplorer;

import TexRessources.TexWriter;
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
public class Exercice implements Comparable<Exercice> {

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

    
    public boolean replaceWordAndUpdate(String before, String after) {
        // replace a word (or several words) in file sujet.tex and update
        
        boolean out=false;
        for (int i = 0; i < content.size(); i++) {
            String line  = content.get(i);
            if (line.contains(before)) {
                content.set(i, line.replace(before, after));
                out=true;
            }
        }
        TexWriter.writeToFile(content, this.getSubjectPath());
        return out;
    }
    
    public boolean replaceKeywordAndUpdate(String before, String after) {
        boolean out=false;
        for (int i = 0; i < keywords.size(); i++) {
            if (before.equals(keywords.get(i))) {
                keywords.set(i, after);
                out=true;
            }
        }
        TexWriter.writeToFile(keywords, this.getKeywordsPath());
        return out;
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

    public void NotifyKeyWordsChanged() {
        this.updateKeywords();
    }

    public void NotifyReadmeChanged() {

    }

    public void NotifySubjectChanged() {

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

    public String getSubjectPath() {
        return path + "/sujet.tex";
    }

    public String getKeywordsPath() {
        return path + "/mots_clefs.txt";
    }

    public String getReadmePath() {
        return path + "/readme.txt";
    }

    public String getlastTimePath() {
        return path + "/lastTime.txt";
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

    public int getCountGiven() {
        return TexWriter.readFile(this.getlastTimePath()).size();
    }

    public String getLastEntry() {
        List<String> out = TexWriter.readFile(this.getlastTimePath());
        if (out.size() > 0) {
            return out.get(out.size() - 1);
        } else {
            return "N.A.";
        }
    }

    @Override
    public int compareTo(Exercice o) {
        return this.name.compareToIgnoreCase(o.name);
    }

}
