/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import java.util.prefs.Preferences;

/**
 *
 * @author mbrebion
 */
public class SavedVariables {

    public static Preferences prefs;

    public static String getOpenCmd() {

        if (prefs != null) {
            return prefs.get("open", "");
        } else {
            return "";
        }
    }

    public static void setOpenCmd(String openCmd) {
        prefs.put("open", openCmd);
    }

    public static String getPdflatexCmd() {

        if (prefs != null) {
            return prefs.get("pdflatex", "");
        } else {
            return "";
        }
    }

    public static void setPdflatexCmd(String pdflatexCmd) {
        prefs.put("pdflatex", pdflatexCmd);
    }

    public static String getTexModelsPaths() {
        
        
        if (prefs != null) {
            return prefs.get("templates", "");
        } else {
            return "";
        }
    }

    public static void setTexModelsPaths(String in) {
        prefs.put("templates", in);
    }

    public static String getMainGitDir() {

        if (prefs != null) {
            return prefs.get("mainGit", "");
        } else {
            return "";
        }
    }

    public static void setMainGitDir(String in) {
        prefs.put("mainGit", in);
    }

}
