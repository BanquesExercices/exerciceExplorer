/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TexRessources;

import Helper.OsRelated;
import Helper.SavedVariables;
import exerciceexplorer.Exercice;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author mbrebion
 */
public class TexWriter {

    public static void openPdf() {
        openPdf("output.pdf");
    }
    
    public static void openPdf(String name) {
        OsRelated.open(name);
    }

    protected static void getExercicestoTex(Enumeration<Exercice> exercices, List<String> output, List<String> imports, boolean localLinks) {
        List<String> dummy = new ArrayList<>();
        TexWriter.getExercicestoTex(exercices, output, imports, localLinks, dummy, dummy);
    }

    protected static void getExercicestoTex(Enumeration<Exercice> exercices, List<String> output, List<String> imports, boolean localLinks, List<String> before, List<String> after) {
        int count = 0;
        while (exercices.hasMoreElements()) {
            count++;
            Exercice element = exercices.nextElement();

            output.add("\\resetQ");

            for (String s : before) {
                output.add(s.replace("##", String.valueOf(count)));
            }

            if (localLinks) {
                output.add("\\subimport{./" + element.getName() + "/}{sujet.tex}");
            } else {
                output.add("\\subimport{" + element.getPath().replace("\\", "/") + "/}{sujet.tex}"); // unix pathes are required for Latex, even for windows
            }

            for (String s : after) {
                output.add(s.replace("##", String.valueOf(count)));
            }

            output.add("");
            output.add("");
            imports.addAll(element.getImports());

        }
    }

    public static String latexLog = "";

    public static boolean latexToPdf(String fileNameNoExtension) {

        long lastTime = OsRelated.lastTimeOfModification(fileNameNoExtension+".pdf");
        String out = OsRelated.pdfLatex(".",fileNameNoExtension+".tex");
        long newTime = OsRelated.lastTimeOfModification(fileNameNoExtension+".pdf");
        if ((lastTime < 0 && newTime > 0) || (newTime > lastTime)) {
            if (OsRelated.isWindows()) {
                OsRelated.killCurrentProcess(); // usefull as pdflatex is started in a terminal window which is paused to let the user see the output.
            }
            return true;
        }
        latexLog = out;
        System.err.println("pdflatex could not latexize the texfile " + fileNameNoExtension+".tex");

        return false;

    }
    
    public static boolean latexToPdf() {

        return latexToPdf("output");

    }

    public static boolean writeTexFile(List<String> in) {
        
        return writeTexFile(in,"output.tex");
    }
    
    public static boolean writeTexFile(List<String> in,String fileName) {

        
        return OsRelated.writeToFile(in, fileName);
    }

    /**
     * Templates tex files are parsed and modified to include exercices the path
     * of raccourcis_communs is also updated if required
     *
     * @param exercices the enumeration of exercices to include
     * @param kind the kind of output asked
     * @param localLinks if true, hard copies of exercices are created and
     * pathes are updated
     * @param forceDefault if true, default templates are enforced (usefull to
     * test all exercices)
     * @return
     */
    public static List<String> createTexFile(Enumeration<Exercice> exercices, String kind, boolean localLinks, boolean forceDefault) {

        List<String> output = new ArrayList<>(); // main file
        List<String> imports = new ArrayList<>(); // packages required by a specific exercice

        
        boolean previewMode = false ; // if true, addiational informations are added to file (keywords, readme, last time)
        
        String fileName = "/DSmodel.tex";
        switch (kind) {
            case "DS":
                fileName = "/DSmodel.tex";
                break;
            case "DM":
                fileName = "/DMmodel.tex";
                break;
            case "TD":
                fileName = "/TDmodel.tex";
                break;
            case "Colle":
                fileName = "/Collemodel.tex";
                break;
            case "Preview":
                fileName="/Preview.tex";
                previewMode = true;
                break;

        }
        File f; // template file
        if (forceDefault || previewMode ) {
            f = new File(OsRelated.pathAccordingToOS(SavedVariables.getMainGitDir() + "/fichiers_utiles/defaultLatexTemplates" + fileName));
            
        } else {
            f = new File(OsRelated.pathAccordingToOS(SavedVariables.getTexModelsPaths() + fileName));
        }

        List<String> lines = OsRelated.readFile(f.getAbsolutePath());

        boolean blockTokenFound = false;
        String readLine = "";
        // template is parsed line by line
        while (!lines.isEmpty()) {
            readLine = lines.remove(0);
            // gestion automatique des packages spécifiques à un exercice à inclure
            if (readLine.trim().contains("usepackage{raccourcis_communs}") || readLine.trim().contains("RequirePackage{raccourcis_communs}")) {
                output.add("\\usepackage{" + (SavedVariables.getMainGitDir().replace("\\", "/") + "/fichiers_utiles/raccourcis_communs}").replace("//", "/")); // need uniw path like for latex
                continue;
            }

            // gestion du bloc à recopier pour chaque exo (utile pour une fiche de colle par exemple
            if (readLine.trim().equals("[[")) {

                String nextLine;

                List<String> before = new ArrayList<>();
                List<String> after = new ArrayList<>();
                nextLine = lines.remove(0);
                while (!nextLine.trim().equals("****")) {
                    before.add(nextLine);
                    nextLine = lines.remove(0);
                }
                nextLine = lines.remove(0);
                while (!nextLine.trim().equals("]]")) {
                    after.add(nextLine);
                    nextLine = lines.remove(0);
                }
                getExercicestoTex(exercices, output, imports, localLinks, before, after);
                continue;
            }

            if (readLine.trim().equals("****") && !blockTokenFound) {
                
                if (previewMode){
                    
                    ArrayList<Exercice> exes;
                    exes = Collections.list(exercices);
                    Exercice e = exes.get(0);
                    exercices = Collections.enumeration(exes);
                    
                    output.add("\\vspace{-1cm}");
                    if (e.getCountGiven()>0){
                    output.add("Sujet déjà donné "+e.getCountGiven()+ " fois dont la dernière le " + e.getLastEntry()+" \\\\");
                    
                    }
                    
                    output.add("\\vspace{-1cm}");
                    output.add("\\section*{Readme.txt} ");
                    
                    
                    output.add("\\begin{Verbatim}[breaklines=true]");
                    for (String l : e.getReadme()){
                        output.add(l);
                    }
                    output.add("\\end{Verbatim}");
                    
                    output.add("\\vspace{-5mm}");
                    output.add("\\section*{Mots clés} ");
                    String keywords = "";
                    output.add("\\begin{Verbatim}[breaklines=true]");
                    for (String l : e.getKeywords()){
                        keywords += l+ "    ";
                    }
                    output.add(keywords);
                    output.add("\\end{Verbatim}");
                }
                
                
                getExercicestoTex(exercices, output, imports, localLinks);
                continue;
            }

            output.add(readLine);

        }

        if (!imports.isEmpty()) {
            // imports must be added before \begin{document}
            boolean found = false;
            int count = 0;
            while (!found) {
                found = output.get(count).contains("\\begin{document}");
                count += 1;
            }
            imports.add("");
            imports.add(0, " % IMPORTS automatiques");
            imports.add(" % fin des IMPORTS automatiques");
            imports.add("");
            output.addAll(count - 1, imports);
        }

        return output;
    }

}
