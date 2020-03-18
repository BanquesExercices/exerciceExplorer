/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import javax.swing.JOptionPane;

/**
 *
 * @author mbrebion
 */
public class GitWrapper {

    public static String status() {
        System.out.println(ExecCommand.execo(new String[]{"git", "fetch"}, SavedVariables.getMainGitDir()));
        String output = ExecCommand.execo(new String[]{"git", "status"}, SavedVariables.getMainGitDir())[1];
        return output;
    }

    public static String commit() {
        String commitMessage = "\"" + JOptionPane.showInputDialog("commentaire du commit") + "\"";
        String output = ExecCommand.execo(new String[]{"git", "add", "."}, SavedVariables.getMainGitDir())[1];
        output += " \n  " + ExecCommand.execo(new String[]{"git", "commit", "-a", "-m", commitMessage}, SavedVariables.getMainGitDir())[1];
        //JOptionPane.showMessageDialog(null, output);
        return output;

    }

    public static String push() {
        String output = ExecCommand.execo(new String[]{"git", "push"}, SavedVariables.getMainGitDir())[1];
        //JOptionPane.showMessageDialog(null, output);
        return output;
    }

    public static String pull() {
        String output = ExecCommand.execo(new String[]{"git", "pull"}, SavedVariables.getMainGitDir())[1];
        if (output.contains("CONFLICT")){
            ExecCommand.execo(new String[]{"git", "merge", "--abort" }, SavedVariables.getMainGitDir());
        }
        return output;
    }

}
