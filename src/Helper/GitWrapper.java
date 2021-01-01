/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import View.GitCredential;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 *
 * @author mbrebion
 */
public class GitWrapper {

    protected static Repository repository;
    protected static String login = "none";
    protected static String mdpEncrypted = "none";
    protected static String SALT = "pqsrze";
    protected static String repoKind;
    protected static String ALGO = "Blowfish";
    protected static List<File> files = new ArrayList<File>();
    protected static boolean conflictDetected = false;
    protected static Git git;
    public static boolean statusButton = true, pullButton = false, pushButton = false, commitButton = false;

    public static boolean isConflictDetected() {
        return conflictDetected;
    }

    public static String getRepoKind() {
        return repoKind;
    }

    public static boolean setup() {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        try {
            repository = repositoryBuilder.setGitDir(new File(SavedVariables.getMainGitDir() + "/.git"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .setMustExist(true)
                    .build();

            String url = repository.getConfig().getString("remote", "origin", "url");
            if (url.contains("https")) {
                repoKind = GitCredential.HTTPS;
            } else {
                repoKind = GitCredential.SSH;

            }

            git = new Git(repository);

            // we get git credentials for github if user has previously agreed to save them (in an encrpyted way)
            if (SavedVariables.getPersistentCredential()) {
                login = SavedVariables.getGitLogin();
                mdpEncrypted = SavedVariables.getEncryptedGitMDP();
            }

            return true;
        } catch (IOException ex) {
            Logger.getLogger(GitWrapper.class.getName()).log(Level.SEVERE, null, ex);

        } catch (NoWorkTreeException ex) {
            Logger.getLogger(GitWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // https://www.adeveloperdiary.com/java/how-to-easily-encrypt-and-decrypt-text-in-java/
    protected static String encrypt(String strClearText, String strKey) throws Exception {
        String strData = "";

        byte[] key = (strKey + SALT).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit

        try {
            SecretKeySpec skeyspec = new SecretKeySpec(key, ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            byte[] encrypted = cipher.doFinal(strClearText.getBytes("UTF-8"));
            strData = Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return "";

        }
        return strData;

    }

    protected static String decrypt(String strEncrypted, String strKey) throws Exception {
        String strData = "";

        byte[] key = (strKey + SALT).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit

        byte[] encryptedData = Base64.getDecoder().decode(strEncrypted);

        try {
            SecretKeySpec skeyspec = new SecretKeySpec(key, ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted = cipher.doFinal(encryptedData);
            strData = new String(decrypted);

        } catch (Exception e) {
            //e.printStackTrace();
            return "";
        }
        return strData;
    }

    public static void setCredentials(String myLogin, char[] myMdp, boolean persistence) {
        try {
            login = myLogin;
            StringBuilder builder = new StringBuilder();
            for (char value : myMdp) {
                builder.append(value);
            }
            String mdp = builder.toString();
            mdpEncrypted = encrypt(mdp, login);
            if (persistence) {
                SavedVariables.setPersistentCredential(true);
                SavedVariables.setGitLogin(myLogin);
                SavedVariables.setEncryptedGitMDP(mdpEncrypted);
            } else {
                SavedVariables.setPersistentCredential(false);
            }
        } catch (Exception ex) {
            System.out.println("problème avec la gestion du mot de passe");
        }

    }

    public static void eraseCredentials() {
        SavedVariables.setPersistentCredential(false);
    }

    public static String status() {
        String out = "git Status : \n";
        try {
            boolean nothingToDo = true;
            boolean needPush = false;
            boolean needPull = false;

            try {

                FetchResult fst = git.fetch().setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, decrypt(mdpEncrypted, login))).call();

            } catch (org.eclipse.jgit.api.errors.TransportException te) {
                System.out.println("probleme d'autentification");
                te.printStackTrace();

                GitCredential gc = new GitCredential();
                gc.setVisible(true);
                return "abort";
            } catch (Exception e) {
                System.out.println("problem during password decryption");
            }

            try {
                BranchTrackingStatus trackingStatus = BranchTrackingStatus.of(repository, repository.getBranch());
                if (trackingStatus.getBehindCount() > 0) {
                    out += "\n   " + trackingStatus.getBehindCount() + " nouveau(x) commit(s) sur github. \n Effectuez un pull pour les obtenir\n\n";
                    needPull = true;
                }

                if (trackingStatus.getAheadCount() > 0) {
                    out += "\n   " + trackingStatus.getAheadCount() + " commit(s) non envoyés sur github \n\n";
                    needPush = true;
                }
                conflictDetected = false;
            } catch (NullPointerException e) {

                conflictDetected = true;

            }

            Status st = git.status().call();

            // get all files to add 
            files.clear();
            st.getUntracked().forEach(name -> {
                files.add(new File(name));
            });
            st.getModified().forEach(name -> {
                files.add(new File(name));
            });
            st.getConflicting().forEach(name -> {
                files.add(new File(name));
            });
            st.getMissing().forEach(name -> {
                files.add(new File(name));
            });
            st.getUncommittedChanges().forEach(name -> {
                files.add(new File(name));
            });

            if (st.getUncommittedChanges().size() > 0) {
                nothingToDo = false;
                out += " -> " + st.getUncommittedChanges().size() + " fichier(s) modifié(s) \n";
            }

            if (st.getUntracked().size() > 0) {
                nothingToDo = false;
                out += " -> " + st.getUntracked().size() + " nouveau(x) fichier(s) \n";

            }

            if (st.getMissing().size() > 0) {
                nothingToDo = false;
                out += " -> " + st.getMissing().size() + " fichier(s) supprimé(s) \n";
            }

            if (st.getConflicting().size() > 0) {
                nothingToDo = false;
                out += " -> " + st.getConflicting().size() + " fichier(s) conflictuels :  \n";
                for (String f : st.getConflicting()) {
                    out += "   * " + f;
                }
            }

            if (conflictDetected) {

                out += " Valider le pull une fois les conflits réglés.";
                pullButton = true;
                pushButton = false;
                commitButton = false;
            } else if (nothingToDo) {

                out += " Aucune modification locale \n depuis le dernier commit";
                pullButton = needPull;
                pushButton = needPush && !needPull;
                commitButton = false;

            } else {
                pullButton = false;
                pushButton = false;
                commitButton = true;
            }

        } catch (GitAPIException | IOException ex) {
            Logger.getLogger(GitWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out;
    }

    public static String commit() {
        try {
            String commitMessage = JOptionPane.showInputDialog("commentaire du commit");
            for (File f : files) {
                git.add().addFilepattern(f.getPath()).call();
                System.out.println(f.getAbsolutePath());
            }
            RevCommit cst = git.commit().setMessage(commitMessage).call();
            return cst.getShortMessage();
        } catch (GitAPIException ex) {
            Logger.getLogger(GitWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "problem with commit";

    }

    public static String push() {
        String output = "";
        try {

            git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, decrypt(mdpEncrypted, login))).call();

            //JOptionPane.showMessageDialog(null, output);
            return "push effectué";
        } catch (GitAPIException ex) {
            System.out.println("probleme d'autentification");
            GitCredential gc = new GitCredential();
            gc.setVisible(true);
        } catch (Exception e) {
            System.out.println("problem during password decryption");
        }
        return "problème lors du push";
    }

    public static String rebaseContinue() {
        try {
            for (File f : files){
                git.add().addFilepattern(f.getPath() ).call();
                System.out.println(f.getAbsolutePath());
            }
            git.rebase().setOperation(RebaseCommand.Operation.CONTINUE).call();
            return "Changements validés.";
        } catch (GitAPIException ex) {
            System.out.println("problème lors du rebase --continue");
            return "Erreur lors du rebase --continue";
        }

    }

    public static String pull() {

        String output = "";
        try {

            PullResult out = git.pull().setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, decrypt(mdpEncrypted, login))).setRebase(true).call();
            if (out.isSuccessful()) {
                return "pull effectué avec succes";
            } else {
                return "problème rencontré lors du pull";
            }

        } catch (GitAPIException ex) {
            System.out.println("probleme d'autentification");
            GitCredential gc = new GitCredential();
            gc.setVisible(true);
        } catch (Exception e) {
            System.out.println("problem during password decryption");
        }
        return "problème lors du pull";

    }

   

}
