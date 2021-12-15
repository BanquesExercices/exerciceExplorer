/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exerciceexplorer;

import Helper.OsRelated;
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
                String importLine = line.replace("%", "");
                if (!importLine.isEmpty()){
                    imports.add(importLine);
                }
            }
        }

        return imports;
    }

    public boolean replaceWordAndUpdate(String before, String after, boolean doReplace) {
        // replace a word (or several words) in file sujet.tex and update

        boolean out = false;
        for (int i = 0; i < content.size(); i++) {
            String line = content.get(i);
            if (line.contains(before)) {
                content.set(i, line.replace(before, after));
                out = true;
            }
        }
        if (doReplace) {
            OsRelated.writeToFile(content, this.getSubjectPath());
        }
        return out;
    }

    /**
     * #i matched in before expression will be copied into #i in after
     * expression
     *
     * @param before is an epression like "\todo{#1}{#2}"
     * @param after is another expression like "\padam{#1} and \padam{#2}
     * @param doChange : changes are only performed when doChange==true;
     * @return
     */
    public boolean replaceExprAndUpdate(String before, String after, boolean doChange) {
        // replace a word (or several words) in file sujet.tex and update
        int blocks = before.split("#\\d").length - 1;
        String begin = before.split("#\\d")[0];
        List<String> blockContents = new ArrayList<>();

        boolean out = false;
        boolean goOn = true;
        int lastLine = 0;

        searchExpr:
        while (goOn) {
            goOn = false;
            searchLine:
            for (int i = 0; i < content.size(); i++) {

                String line = content.get(i);
                int endIndex = 0;
                lastLine = i;

                if (line.contains(begin)) {
                    // this line might contain the required expression
                    int beginIndex = line.indexOf(begin);
                    String next = line.substring(line.indexOf(begin) + begin.length() - 1);
                    endIndex = beginIndex + begin.length() - 1;
                    boolean succes = true;
                    int index = -1;

                    blockContents.clear();

                    searchblock:
                    for (int j = 0; j < blocks; j++) {
                        index = 0;
                        // we then get the next {
                        while (!(next.charAt(index) == '{' && ((index > 0 && next.charAt(index - 1) != '\\') || index == 0))) {
                            index++;
                            if (index == next.length()) {
                                // we reached end of line and did not get the awaited char 
                                goOn = false;
                                succes = false;
                                break searchblock;
                            }
                            endIndex++;

                        }
                        next = next.substring(index + 1);

                        // get block content
                        int bracketCount = 1;
                        index = -1;

                        while (bracketCount > 0) {
                            index++;
                            endIndex++;
                            if (index == next.length()) {
                                if (i < content.size() - 1) {
                                    // end of line, we need to parse the next one
                                    next += "\n" + content.get(lastLine + 1);
                                    lastLine++;
                                    endIndex = 0;
                                } else {
                                    // last line : failed to parse expr
                                    succes = false;
                                    break searchblock;
                                }
                            }

                            if (next.charAt(index) == '{' && ((index > 0 && next.charAt(index - 1) != '\\') || index == 0)) {
                                bracketCount++;
                            }
                            if (next.charAt(index) == '}' && ((index > 0 && next.charAt(index - 1) != '\\') || index == 0)) {
                                bracketCount--;
                            }
                        }
                        blockContents.add(next.substring(0, index));
                        next = next.substring(index + 1);

                    }

                    if (succes) {
                        goOn = true;
                        if (doChange == false) {
                            return true;
                        }

                        // get rid of previous expr
                        // prepare new expr
                        String replacement = after.replace(before, "BEFORE_BEFORE"); // ensure the new string dont contains the begin expr (else we get an infinite loop)
                        boolean et = false;
                        for (int k = 0; k < blocks; k++) {
                            if (blockContents.get(k).contains("&")) {
                                et = true;
                            }
                            // check equations and add align if required
                            replacement = replacement.replace("#" + (k + 1), "" + blockContents.get(k) + "");
                        }

                        // special parsing for equations
                        if (et && replacement.contains("eq{")) {
                            //replacement = replacement.replace("eq{", "eq[align]{");
                        }

                        replacement = replacement.replace("\\n", "\n"); // forcing new lines

                        if (i == lastLine) {
                            // replacement into a single line
                            content.set(i, content.get(i).substring(0, beginIndex) + replacement + content.get(i).substring(endIndex + 1));
                        } else {
                            content.set(i, content.get(i).substring(0, beginIndex));
                            for (int l = i + 1; l < lastLine; l++) {
                                content.set(l, "TODELETE");
                            }
                            content.set(lastLine, (content.get(lastLine) + " ").substring(endIndex + 1));
                            content.set(i, content.get(i) + replacement);
                        }

                        // paste it
                        out = true;
                        break searchLine;
                    }
                }
            }

        }

        if (out && doChange) {
            for (int i = content.size() - 1; i >= 0; i--) {
                if ("TODELETE".equals(content.get(i))) {
                    content.remove(i);
                }
                if (content.get(i).contains("BEFORE_BEFORE")) {
                    content.set(i, content.get(i).replace("BEFORE_BEFORE", before));
                }
            }

            OsRelated.writeToFile(content, this.getSubjectPath());
        }
        return out;
    }

    public boolean replaceKeywordAndUpdate(String before, String after, boolean doReplace) {
        boolean out = false;
        for (int i = 0; i < keywords.size(); i++) {
            if (before.equals(keywords.get(i))) {
                if (doReplace){
                    keywords.set(i, after);
                }
                out = true;
            }
        }
        if (doReplace) {
            OsRelated.writeToFile(keywords, this.getKeywordsPath());
        }
        return out;
    }

    public boolean containsKeyWords(List<String> kws) {
        this.updateKeywords();

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

    // next methods should use the unified readfile method from OsRelated.
    protected void updateKeywords() {
        this.keywords.clear();
        for (String s : OsRelated.readFile(this.getKeywordsPath())) {
            if (s.trim()!=""){
                this.keywords.add(s.trim());
            }
        }

    }

    protected void updateReadme() {
        this.readme.clear();
        for (String s : OsRelated.readFile(this.getReadmePath())) {
            this.readme.add(s.trim());
        }
    }

    protected void updateContent() {
        this.content.clear();
        for (String s : OsRelated.readFile(this.path + "/sujet.tex")) {
            this.content.add(s.trim());
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
        return path + "/README.txt";
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
    
    public void addKeyword(String kw){
        List<String> kws = new ArrayList<>();
        kws.add(kw);
        OsRelated.appendToFile(kws, this.getKeywordsPath());
    }

    public int getCountGiven() {
        return OsRelated.readFile(this.getlastTimePath()).size();
    }

    public String getLastEntry() {
        List<String> out = OsRelated.readFile(this.getlastTimePath());
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
