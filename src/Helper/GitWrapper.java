/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import View.GitCredential;
import exerciceexplorer.ExerciceFinder;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

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
    protected static Git git = null;
    protected static boolean noMoreRecentRemoteCommit = true;
    protected static long lastFetchTS = -1;
    protected static int fetchRefreshDuration = 600;  // time in second before a new fetch is required
    public static boolean statusButton = true, pullButton = false, pushButton = false, commitButton = false;
    protected static Status lastSatus;

    public static final int NOERROR = 0, LOGINERROR = 1, REBASEERROR = 2;

    public static boolean isConflictDetected() {
        return conflictDetected;
    }

    public static boolean isOn() {
        return git != null;
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
            System.out.println("url : " + url);
            if (url.contains("https")) {
                repoKind = GitCredential.HTTPS;
            } else {
                repoKind = GitCredential.SSH;
                // not dealed
                
            }
            if (! url.contains("ups")){
                System.out.println("mauvais repo (pas le gitéa de l'ups)");
                return false;
            }

            git = new Git(repository);

            // we get git credentials for gitea if user has previously agreed to save them (in an encrpyted way)
            if (SavedVariables.getPersistentCredential()) {
                login = SavedVariables.getGitLogin();
                mdpEncrypted = SavedVariables.getEncryptedGitMDP();
            }

            noMoreRecentRemoteCommit = false;

            new Thread(() -> {
                ExerciceFinder.updateExercicesTimes();
            }).start();

            // il reste pleins de bugs !!!
            // mauvais ordres
            // non mise à jour auto en fonction du type de tri
            // trop lent
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

    protected static String decrypt(String strEncrypted, String strKey) throws UnsupportedEncodingException {
        String strData = "";

        byte[] key = (strKey + SALT).getBytes("UTF-8");
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(GitWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public static int fetch() {
        try {

            // do fetch
            FetchResult fst = git.fetch().setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, decrypt(mdpEncrypted, login))).call();
            lastFetchTS = System.currentTimeMillis();
            // check whether local branch is equal or forward to remote branch
            RevWalk revWalk = new RevWalk(repository);
            RevCommit local = revWalk.parseCommit(repository.resolve(repository.getBranch()));
            RevCommit remote = revWalk.parseCommit(repository.resolve("origin/" + repository.getBranch()));
            noMoreRecentRemoteCommit = revWalk.isMergedInto(remote, local);

        } catch (org.eclipse.jgit.api.errors.TransportException te) {
            System.out.println("probleme d'autentification");

            GitCredential.displayGitCredential();
            return LOGINERROR;

        } catch (Exception e) {
            System.out.println("problem during fetch");
            return REBASEERROR;
        }
        return NOERROR;
    }

    public static boolean isFileModifiedOnOrigin(String path) {
        // if head is ahaed to origin, there is no problem and nothing to check
        if (noMoreRecentRemoteCommit) {
            return false;
        }

        // ask a fetch only if last one is too old
        if (System.currentTimeMillis() - lastFetchTS > fetchRefreshDuration * 1000) {
            fetch();
        }

        // if here, origin is ahead of head and we must check that file at path has not been modified in origin 
        path = path.replaceFirst(Pattern.quote(SavedVariables.getMainGitDir()), "");
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }

        try {

            // local branch
            CanonicalTreeParser currentTreeParser = new CanonicalTreeParser();
            ObjectId treeId = git.getRepository().resolve("HEAD^{tree}");
            ObjectReader reader = repository.newObjectReader();
            currentTreeParser.reset(reader, treeId);

            // remote (origin) branch
            CanonicalTreeParser RemoteTreeParser = new CanonicalTreeParser();
            treeId = repository.resolve("origin/" + repository.getBranch() + "^{tree}");
            reader = repository.newObjectReader();
            RemoteTreeParser.reset(reader, treeId);

            // one should provide path starting from repository and not full path
            TreeFilter tf = PathFilter.create(path);
            List<DiffEntry> out = git.diff().setOldTree(currentTreeParser).setNewTree(RemoteTreeParser).setShowNameAndStatusOnly(true).setPathFilter(tf).call();
            return !out.isEmpty();

        } catch (IncorrectObjectTypeException ex) {
            Logger.getLogger(GitWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RevisionSyntaxException | GitAPIException | IOException | java.lang.NullPointerException ex) {
            Logger.getLogger(GitWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public static String status() {

        String out = "git Status : \n";
        try {
            boolean nothingToDo = true;
            boolean needPush = false;
            boolean needPull = false;

            if (!isConflictDetected()) {
                int fetchError = fetch();
                if (fetchError == REBASEERROR) {
                    conflictDetected = true;
                    //return "Problème lors du fetch (rebasage en cours ?) \n   
                } else if (fetchError == LOGINERROR) {
                    return "problème d'indentifiants d'accès à gitéa.com \n   -> Re-essayez après les avoir saisis.";
                }
            } else {
                //in conflict case, all concerned files are added before status is called to check whther conflitcs are solved
                for (String s : lastSatus.getConflicting()) {
                    git.add().addFilepattern(s).call();
                }
            }

            try {
                BranchTrackingStatus trackingStatus = BranchTrackingStatus.of(repository, repository.getBranch());
                if (trackingStatus.getBehindCount() > 0) {
                    out += "\n   " + trackingStatus.getBehindCount() + " nouveau(x) commit(s) sur gitéa. \n Effectuez un pull pour les obtenir\n\n";
                    needPull = true;
                }

                if (trackingStatus.getAheadCount() > 0) {
                    out += "\n   " + trackingStatus.getAheadCount() + " commit(s) non envoyés sur gitéa \n\n";
                    needPush = true;
                }
                conflictDetected = false;
            } catch (NullPointerException e) {

                out += "Branche : " + repository.getBranch() + " non présente sur gitéa \n";

            }

            lastSatus = git.status().call();

            // get all files to add 
            files.clear();
            lastSatus.getUntracked().forEach(name -> {
                files.add(new File(name));
            });
            lastSatus.getModified().forEach(name -> {
                files.add(new File(name));
            });
            lastSatus.getConflicting().forEach(name -> {
                files.add(new File(name));
            });
            lastSatus.getMissing().forEach(name -> {
                files.add(new File(name));
            });

            lastSatus.getUncommittedChanges().forEach(name -> {
                files.add(new File(name));
            });

            if (lastSatus.getUncommittedChanges().size() - lastSatus.getMissing().size() > 0) {
                nothingToDo = false;
                out += " -> " + lastSatus.getUncommittedChanges().size() + " fichier(s) modifié(s) \n";  // -> 1
            }

            if (lastSatus.getUntracked().size() > 0) {
                nothingToDo = false;
                out += " -> " + lastSatus.getUntracked().size() + " nouveau(x) fichier(s) \n";  // -> 0

            }

            if (lastSatus.getMissing().size() > 0) {
                nothingToDo = false;
                out += " -> " + lastSatus.getMissing().size() + " fichier(s) supprimé(s) \n"; // -> 1
                for (String f : lastSatus.getMissing()) {
                    git.rm().addFilepattern(f).call();
                }
            }

            if (lastSatus.getConflicting().size() > 0) {
                nothingToDo = false;
                out += " -> " + lastSatus.getConflicting().size() + " fichier(s) conflictuels :  \n";
                for (String f : lastSatus.getConflicting()) {
                    out += "   * " + f;

                }
            }

            if (conflictDetected) {

                pullButton = lastSatus.getConflicting().size() == 0;
                if (pullButton) {
                    out += " Tous les conflits sont réglés, vous pouvez poursuivre le pull (Rebase)";
                } else {
                    out += " Les conflits doivent être réglés avant de poursuivre...";
                }

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

                git.add().addFilepattern(f.getPath().replace("\\", "/")).call(); // même sous windows ; il faut des "/" pour les chemins d'acces.
                System.out.println(f.getAbsolutePath());
            }
            //git.add().addFilepattern(".").call();
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
            GitCredential.displayGitCredential();
        } catch (Exception e) {
            System.out.println("problem during password decryption");
        }
        return "problème lors du push";
    }

    public static String rebaseContinue() {
        try {
            for (File f : files) {
                git.add().addFilepattern(f.getPath()).call();
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

        } catch (org.eclipse.jgit.api.errors.TransportException te) {
            return "problème d'identifiants";
        } catch (GitAPIException ex) {
            return "problème rencontré lors du pull";
        } catch (UnsupportedEncodingException ex) {
            return "problème d'identifiants";
        }

    }

    public static String relativePath(String path) {
        // return path relative to repo 
        String repo = git.getRepository().getDirectory().getParentFile().getName();
        String fs = path.replace("\\", "/").replace("//", "/").split(repo + "/")[1].trim();    // forcing / separator whatever the OS as requested by git log
        return fs;
    }

    public static long[] getTimes(String path) {
        // retrieve times of first and last commits concerning file at path path.
        // output longs[] {firstcommit ; lastcommit}
        // This method may take a long time as all git tree is walked.
        
        try {
            Iterator<RevCommit> irc = git.log().addPath(relativePath(path)).call().iterator();

            long maxtime = -1;
            long mintime = -1;

            while (irc.hasNext()) {
                long time = irc.next().getCommitTime();
                if (time > maxtime || maxtime<0) {
                    maxtime = time;
                }
                if (time < mintime || mintime<0) {
                    mintime = time;
                }
            }
            return new long[]{mintime,maxtime};

        } catch (Exception ex) {
            Logger.getLogger(GitWrapper.class.getName()).log(Level.SEVERE, null, ex);
            return new long[]{0,0};
        }
    }

    

}
