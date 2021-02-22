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
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author mbrebion
 */
public class TexWriter {

    public static void openPdf() {
        OsRelated.open("output/output.pdf");
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

    public static boolean latexToPdf() {

        long lastTime = OsRelated.lastTimeOfModification("output/output.pdf");
        String out = OsRelated.pdfLatex("./output");
        long newTime = OsRelated.lastTimeOfModification("output/output.pdf");
        if ((lastTime < 0 && newTime > 0) || (newTime > lastTime)) {
            if (OsRelated.isWindows()) {
                OsRelated.killCurrentProcess(); // usefull as pdflatex is started in a terminal window which is paused to let the user see the output.
            }
            return true;
        }
        latexLog = out;
        System.err.println("pdflatex could not latexize the texfile");

        return false;

    }

    public static boolean writeTexFile(List<String> in) {

        File dir = new File("output");
        if (!dir.exists() && !dir.isDirectory()) {
            // create output dir if not existing
            dir.mkdir();

        }
        return OsRelated.writeToFile(in, OsRelated.pathAccordingToOS("output/output.tex"));
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

        }
        File f; // template file
        if (!forceDefault) {
            f = new File(OsRelated.pathAccordingToOS(SavedVariables.getTexModelsPaths() + fileName));
        } else {
            f = new File(OsRelated.pathAccordingToOS(SavedVariables.getMainGitDir() + "/fichiers_utiles/defaultLatexTemplates" + fileName));
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
                getExercicestoTex(exercices, output, imports, localLinks);
                continue;
            }

            output.add(readLine);

        }

        if (imports.size() > 0) {
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
