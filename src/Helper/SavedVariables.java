/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import static Helper.Utils.getColorFromString;
import TextEditor.Tex.LatexTextEditor;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author mbrebion
 */
public class SavedVariables {

    protected static Preferences prefs;

    public static void instanciate(Class c) {
        prefs = Preferences.userNodeForPackage(c);
    }

    public static void removeAllPrefs() {
        try {
            prefs.clear();
            prefs.flush();

        } catch (BackingStoreException ex) {
            Logger.getLogger(SavedVariables.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //theme
        public static int getTheme() {

        if (prefs != null) {
            return Integer.valueOf(prefs.get("theme", "0"));
        } else {
            return 0;
        }
    }

    public static void setTheme(int theme) {
        prefs.put("theme", String.valueOf(theme));
    }

    // font size
    public static int getFontSize() {

        if (prefs != null) {
            return Integer.valueOf(prefs.get("IHMfs", "13"));
        } else {
            return 13;
        }
    }

    public static void setFontSize(int fs) {
        prefs.put("IHMfs", String.valueOf(fs));
    }

    ////// git credentials
    public static String getGitLogin() {

        if (prefs != null) {
            return prefs.get("glg", "");
        } else {
            return "";
        }
    }

    public static void setGitLogin(String gitLogin) {
        prefs.put("glg", gitLogin);
    }

    public static String getEncryptedGitMDP() {

        if (prefs != null) {
            return prefs.get("gemdp", "");
        } else {
            return "";
        }
    }

    public static void setEncryptedGitMDP(String emdp) {
        prefs.put("gemdp", emdp);
    }

    public static boolean getPersistentCredential() {
        if (prefs != null) {
            return "true".equals(prefs.get("persistentCredential", ""));
        } else {
            return false;
        }
    }

    public static void setPersistentCredential(boolean pc) {
        if (pc) {
            prefs.put("persistentCredential", "true");
        } else {
            prefs.put("persistentCredential", "false");
            setGitLogin("");
            setEncryptedGitMDP("");
        }
    }

    ////// ihm preferences
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

    public static void setOutputDir(String output) {
        prefs.put("outputDir", output);
    }

    public static String getOutputDir() {
        if (prefs != null) {
            return prefs.get("outputDir", "");
        } else {
            return "";
        }
    }

    public static boolean getAutoSave() {
        if (prefs != null) {
            return "true".equals(prefs.get("autoSave", ""));
        } else {
            return false;
        }
    }

    public static void setAutoSave(boolean as) {
        if (as) {
            prefs.put("autoSave", "true");
        } else {
            prefs.put("autoSave", "false");
        }

    }

    public static void setMultiEdit(boolean me) {
        if (me) {
            prefs.put("multiEdit", "true");
        } else {
            prefs.put("multiEdit", "false");
        }

    }

    public static boolean getMultiEdit() {
        if (prefs != null) {
            return "true".equals(prefs.get("multiEdit", ""));
        } else {
            return false;
        }
    }

    public static void setSpellCheck(boolean me) {
        if (me) {
            prefs.put("spellCheck", "true");
        } else {
            prefs.put("spellCheck", "false");
        }

    }

    public static boolean getSpellCheck() {
        if (prefs != null) {
            return "true".equals(prefs.get("spellCheck", ""));
        } else {
            return false;
        }
    }

    public static void setAutoCompletion(boolean me) {
        if (me) {
            prefs.put("autoCompletion", "true");
        } else {
            prefs.put("autoCompletion", "false");
        }

    }

    public static boolean getAutoCompletion() {
        if (prefs != null) {
            return "true".equals(prefs.get("autoCompletion", ""));
        } else {
            return false;
        }
    }

    public static void setColoring(boolean me) {
        if (me) {
            prefs.put("coloring", "true");
        } else {
            prefs.put("coloring", "false");
        }

    }

    public static boolean getColoring() {
        if (prefs != null) {
            return "true".equals(prefs.get("coloring", ""));
        } else {
            return false;
        }
    }

    public static void setBigTitles(boolean me) {
        if (me) {
            prefs.put("bigTitles", "true");
        } else {
            prefs.put("bigTitles", "false");
        }

    }

    public static boolean getBigTitles() {
        if (prefs != null) {
            return "true".equals(prefs.get("bigTitles", ""));
        } else {
            return false;
        }
    }

    public static void setGlobalDict(String me) {
        prefs.put("globalDict", me);
    }

    public static String getGlobalDict() {
        if (prefs != null) {
            return prefs.get("globalDict", "");
        } else {
            return "../src/Content/dic_fra.txt";
        }
    }

    public static void setCustomDict(String me) {
        prefs.put("customDict", me);
    }

    public static String getCustomDict() {
        if (prefs != null) {
            return prefs.get("customDict", "");
        } else {
            return "";
        }
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

    public static void setColor(int token, String color) {
        try {
            getColorFromString(color); // if this fails, we don't store the color.
            prefs.put("color" + token, color);
        } catch (Exception e) {
            System.out.println("no saving !");
        }
    }

    public static Color getColor(int token) {
        String out = LatexTextEditor.getDefaultColor(token);
        if (prefs != null) {
            out = prefs.get("color" + token, out);
        }
        return getColorFromString(out);
    }

}
