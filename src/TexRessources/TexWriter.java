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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    protected static void getExercicestoTex(List<Exercice> exercices, List<String> output, List<String> imports, boolean localLinks) {
        List<String> dummy = new ArrayList<>();
        TexWriter.getExercicestoTex(exercices, output, imports, localLinks, dummy, dummy);
    }

    protected static void doTable(List<Exercice> exercices, List<String> output) {
        System.out.println("create table");
        Map<String, List<Integer>> techs = new HashMap<>();
        int exoNb = 0;

        for (Exercice e : exercices) {
            exoNb += 1;

            for (String kw : e.getKeywords()) {
                if (kw.startsWith("technique")) {
                    techs.putIfAbsent(kw, new ArrayList<Integer>());
                    techs.get(kw).add(exoNb);
                }
            }
        }
        output.add(" ");
        output.add("\\begin{center}");
        String ccs = "r" + "c".repeat(exercices.size());
        output.add("    \\begin{tabular}[!h]{" + ccs + "} ");

        String names = "        ";
        for (int i=0 ; i<exercices.size();i++){
        names += "&"+TexWriter.intToRoman(i+1);
        }
        output.add(names+"\\\\");
        output.add("        \\midrule");
        int count=0;
        for (String tech : techs.keySet()) {
            count+=1;
            List<String> crosses = new ArrayList<>();
            for (int i = 0; i < exercices.size(); i++) {
                crosses.add(" ");
            }
            for (int ind : techs.get(tech)) {
                crosses.set(ind-1, "\\checkmark ");
            }
            String crs = "";
            for (String s : crosses) {
                crs += "&" + s;
            }

            String col="";
            if (count%2==1){
             col="\\rowcolor{black!6} ";   
            }
            output.add("        "+col + tech.replace("technique:", "") + crs + " \\\\");
            
        }
        output.add("    \\end{tabular}");
        output.add("\\end{center}");

        /**
         *
         * \begin{center} \begin{tabular}[!h]{cccccc} $R_E=R_C$ & $R$ & $V_0$ &
         * $C$ & $\alpha_P$	& $f$\\ \hline \SI{680}{\Omega} & \SI{47}{k\Omega} &
         * \SI{15}{V} & \SI{22}{\mu F} & 1/3 & $\ge \SI{1}{kHz}$ \end{tabular}
         * \end{center}
         */
        System.out.println("found : " + techs.size());

    }

    protected static void getExercicestoTex(List<Exercice> exercices, List<String> output, List<String> imports, boolean localLinks, List<String> before, List<String> after) {
        int count = 0;
        for (Exercice element : exercices) {
            count++;

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

        long lastTime = OsRelated.lastTimeOfModification(fileNameNoExtension + ".pdf");
        String out = OsRelated.pdfLatex(".", fileNameNoExtension + ".tex");
        long newTime = OsRelated.lastTimeOfModification(fileNameNoExtension + ".pdf");
        if ((lastTime < 0 && newTime > 0) || (newTime > lastTime)) {
            if (OsRelated.isWindows()) {
                OsRelated.killCurrentProcess(); // usefull as pdflatex is started in a terminal window which is paused to let the user see the output.
            }
            latexLog = "";
            return true;
        }
        latexLog = out;
        System.err.println("pdflatex could not latexize the texfile " + fileNameNoExtension + ".tex");

        return false;

    }

    public static boolean latexToPdf() {

        return latexToPdf("output");

    }

    public static boolean writeTexFile(List<String> in) {

        return writeTexFile(in, "output.tex");
    }

    public static boolean writeTexFile(List<String> in, String fileName) {

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
        List<Exercice> exos = Collections.list(exercices);

        boolean previewMode = false; // if true, addiational informations are added to file (keywords, readme, last time)

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
                fileName = "/Preview.tex";
                previewMode = true;
                break;

        }
        File f; // template file
        if (forceDefault || previewMode) {
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
                getExercicestoTex(exos, output, imports, localLinks, before, after);
                continue;
            }

            if (readLine.trim().equals("@doTable")) {
                doTable(exos, output);
                continue;
            }

            if (readLine.trim().equals("****") && !blockTokenFound) {

                if (previewMode) {
                    // additional content in case of preview (in app)

                    ArrayList<Exercice> exes;
                    exes = Collections.list(exercices);
                    Exercice e = exes.get(0);
                    exercices = Collections.enumeration(exes);

                    output.add("\\vspace{-1cm}");
                    if (e.getCountGiven() > 0) {
                        output.add("Sujet déjà donné " + e.getCountGiven() + " fois dont la dernière le \\verb@" + e.getLastEntry() + "@ \\\\");

                    }

                    output.add("\\vspace{-1cm}");
                    output.add("\\section*{Readme.txt} ");

                    output.add("\\begin{Verbatim}[breaklines=true]");
                    for (String l : e.getReadme()) {
                        output.add(l);
                    }
                    output.add("\\end{Verbatim}");

                    output.add("\\vspace{-5mm}");
                    output.add("\\section*{Mots clés} ");
                    String keywords = "";
                    output.add("\\begin{Verbatim}[breaklines=true]");
                    for (String l : e.getKeywords()) {
                        keywords += l + "    ";
                    }
                    output.add(keywords);
                    output.add("\\end{Verbatim}");
                }

                getExercicestoTex(exos, output, imports, localLinks);
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

    
    // convert int to roman Strings
    private static final TreeMap<Integer, String> treemap = new TreeMap<>();

    static {
        treemap.put(1000, "M");
        treemap.put(900, "CM");
        treemap.put(500, "D");
        treemap.put(400, "CD");
        treemap.put(100, "C");
        treemap.put(90, "XC");
        treemap.put(50, "L");
        treemap.put(40, "XL");
        treemap.put(10, "X");
        treemap.put(9, "IX");
        treemap.put(5, "V");
        treemap.put(4, "IV");
        treemap.put(1, "I");

    }

    public static final String intToRoman(int number) {
        int l = treemap.floorKey(number);
        if (number == l) {
            return treemap.get(number);
        }
        return treemap.get(l) + intToRoman(number - l);
    }

}
