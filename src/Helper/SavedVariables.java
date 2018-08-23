/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

/**
 *
 * @author mbrebion
 */
public class SavedVariables {
    
    protected static String texModelsPaths;
    protected static String mainGitDir;

    public static void initSavedVariables() {
        setMainGitDir("/Users/mbrebion/PCSI/commun_PCSI");
        setTexModelsPaths("/Users/mbrebion/PCSI/exerciceWriterUtils");
        
    }

    
    public static String getTexModelsPaths() {
        return texModelsPaths;
    }

    public static void setTexModelsPaths(String in) {
        texModelsPaths = in;
    }

    public static String getMainGitDir() {
        return mainGitDir;
    }

    public static void setMainGitDir(String in) {
        mainGitDir = in;
    }
 
    
    
}
