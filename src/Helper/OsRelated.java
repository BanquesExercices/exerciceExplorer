/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import TexRessources.TexWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mbrebion ressource :
 *
 */
public class OsRelated {

    private static String OS = null;
    private static Process p = null;

    ///////////////////////////////////
    ////// Dealing with files /////////
    ///////////////////////////////////
    public static long lastTimeOfModification(String path) {
        File output = new File(pathAccordingToOS(path));
        if (output.exists()) {
            return output.lastModified();
        } else {
            return -1;
        }

    }

    // OS tasks
    public static String pathAccordingToOS(String path) {
        if (OsRelated.isWindows()) {
            return path.replace("/", "\\").replace("\\\\", "\\").replace("` ", " ").replace(" ", "` ");
        } else {
            return path.replace("\\", "/").replace("//", "/");
        }
    }

    public static boolean appendToFile(List<String> in, String path) {
        BufferedWriter b;
        try {
            b = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));
            
            int count = 0;
            for (String line : in) {
                b.write(line);
                count++;
                if (count < in.size()) {
                    b.write("\n"); // enforcing Unix style EOL
                }
            }
            b.close();

        } catch (IOException ex) {
            System.out.println(path + " file cannot be outputed");
            return false;
        }
        return true;
    }

    public static boolean writeToFile(List<String> in, String path) {

        File f = new File(pathAccordingToOS(path));
        BufferedWriter b;
        try {

            b = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));

            int count = 0;
            for (String line : in) {
                b.write(line);
                count++;
                if (count < in.size()) {
                    b.write("\n"); // enforcing Unix style EOL
                }
            }

            b.close();

        } catch (IOException ex) {
            System.err.println(path + " file cannot be outputed");
            return false;
        }
        return true;
    }

    public static List<String> readFile(String path) {
        ArrayList<String> out = new ArrayList<>();
        File f = new File(path);
        BufferedReader b;
        try {
            b = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
        } catch (FileNotFoundException ex) {
            System.out.println("File not found while reading file : " + path);
            return out;
        } catch (UnsupportedEncodingException ex) {
            return out;
        }

        String readLine = "";
        try {

            while ((readLine = b.readLine()) != null) {
                out.add(readLine);
            }
        } catch (IOException ex) {
            System.out.println("Problem while reading file : " + path);
            return out;
        }
        return out;

    }

    public static String readFileOneString(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.println("File not found while reading : " + path);
            Logger.getLogger(TexWriter.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }

    }

    public static boolean fileExists(String path) {
        File f = new File(pathAccordingToOS(path));
        return f.exists() && !f.isDirectory();

    }

    public static void copy(String from, String to) {
        OsRelated.execo(new String[]{"cp", "-r", pathAccordingToOS(from), pathAccordingToOS(to)});
    }
    
    public static void remove(String path) {
        OsRelated.execo(new String[]{"rm", pathAccordingToOS(path)});
    }

    public static void openDirectoryOf(String PathToFile) {

        if (OsRelated.isWindows()) {
            String path = PathToFile.substring(0, PathToFile.lastIndexOf("\\"));
            open(path);
        } else {
            String path = PathToFile.substring(0, PathToFile.lastIndexOf("/"));
            open(path);
        }

    }

    public static void open(String pathToFile) {
        if (OsRelated.isWindows()) {

            //OsRelated.execo(new String[]{"ii",pathAccordingToOS(pathToFile)});
            OsRelated.execoWindows(new String[]{"ii", pathAccordingToOS(pathToFile)}, 0.5, ".", true);
        } else {
            String cmd = "";
            if (System.getProperty("os.name").startsWith("Mac OS X")) {
                cmd = "open";
            } else {
                cmd = "xdg-open";
            }
            OsRelated.execo(new String[]{cmd, pathAccordingToOS(pathToFile)});
        }
    }

    public static String pdfLatex(String outputDir, String fileName) {
        String[] out;
        if (OsRelated.isWindows()) {
            String[] command = new String[]{"pdflatex", "-no-shell-escape", "-halt-on-error", "-interaction=nonstopmode", fileName};

            out = OsRelated.execoWindows(command, 400, ".", false); // force exec from cmd and not powershell : unlimited wait time as user may end the cmd window himself

        } else {
            String[] command = new String[]{pathAccordingToOS(SavedVariables.getPdflatexCmd()), "-no-shell-escape", "-halt-on-error", "-interaction=nonstopmode", fileName};

            out = OsRelated.execoUnix(command, 30, "."); // une durée d'attente de 30 secondes max semble un bon compromis.

        }
        return out[1];
    }

    public static boolean checkPdfLatex() {
        String[] command = {OsRelated.pathAccordingToOS(SavedVariables.getPdflatexCmd()), "-halt-on-error", "-no-shell-escape", " -version"};
        //String[] command = {"pdflatex", "-halt-on-error", "-no-shell-escape", " -version"};

        String out = OsRelated.execo(command)[1];
        return out.contains("pdfTeX");
    }

    public static String guessPdfLAtexPath() {
        if (OsRelated.isWindows()) {
            return OsRelated.execo(new String[]{"Get-Command pdflatex | Select-Object -ExpandProperty Definition"})[1];
        } else {
            return "/Library/TeX/texbin/pdflatex";
            //return OsRelated.execo(new String[]{"which", "pdflatex"})[1];
        }
    }

    // exec command
    protected static String[] execo(String[] command) {
        return execo(command, 1.0, ".");
    }

    protected static String[] execo(String[] command, String location) {
        return execo(command, 1.0, location);
    }

    protected static String[] execo(String[] command, double delay) {
        return execo(command, delay, ".");
    }

    protected static String[] execoWindows(String[] command, double delay, String location, boolean powershell) {

        int out = 1;

        ProcessBuilder pb = new ProcessBuilder();
        List<String> Command = new ArrayList<>();
        if (powershell) {
            Command.add("powershell");
            Command.add("-Command");
        } else {
            Command.add("cmd.exe");
            Command.add("/c");
            Command.add("start");
            Command.add("/w");
            Command.add("cmd");
            Command.add("/c");
        }
        String lastItem = "";
        for (String s : command) {
            lastItem += " " + s;
        }

        lastItem = lastItem.trim();
        //lastItem = lastItem.strip();
        if (!powershell) {
            lastItem += "|| pause";
        }

        Command.add(lastItem);

        pb.command(Command);
        pb.directory(new File(location));
        System.out.println("Executing command from " + pb.directory().getAbsolutePath());
        //pb.inheritIO();
        StringBuilder output = new StringBuilder(120);
        try {

            pb.redirectError();

            InputStream is = null;

            p = pb.start();

            long startTime = System.currentTimeMillis();
            // get process output (input from java point of view)
            is = p.getInputStream();

            int in;
            try {
                while (System.currentTimeMillis() - startTime < delay * 1000 && p.isAlive()) {
                    Thread.sleep(10);
                }
            } catch (InterruptedException ex) {
                System.out.println(ex.getStackTrace());
            }
            try {
                out = p.exitValue();
            } catch (IllegalThreadStateException ex) {
                out = -2; // -2 means unfinished task
                return new String[]{String.valueOf(out), ""};
            }
            System.out.println("durée : " + (System.currentTimeMillis() - startTime));

            while ((in = is.read()) != -1) {
                output.append((char) in);
            }

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return new String[]{String.valueOf(out), output.toString()};
    }

    protected static String[] execoUnix(String[] command, double delay, String location) {
        int out = -1;

        ProcessBuilder pb = new ProcessBuilder();
        List<String> Command = new ArrayList<>();
        Command.add("sh");
        Command.add("-c");
        String lastItem = "";
        for (String s : command) {
            lastItem += " " + s;
        }

        lastItem = lastItem.trim();
        //lastItem = lastItem.strip();
        Command.add(lastItem);

        pb.command(Command);
        pb.directory(new File(location));
        //pb.inheritIO();
        StringBuilder output = new StringBuilder(120);

        pb.redirectError();
        InputStream is = null;

        try {
            p = pb.start();

            long startTime = System.currentTimeMillis();
            // get process output (input from java point of view)
            is = p.getInputStream();

            int in;
            while (System.currentTimeMillis() - startTime < delay * 1000 && (in = is.read()) != -1) {
                output.append((char) in);
            }

        } catch (IOException exp) {
            System.out.flush();
            System.out.println(exp.getMessage());
            System.err.flush();
            System.err.println("problem with command : " + command[0]);
        }
        return new String[]{String.valueOf(out), output.toString()};
    }

    public static void killCurrentProcess() {
        p.destroyForcibly();
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
        }
    }

    protected static String[] execo(String[] command, double delay, String location) {
        /**
         * This function can only be called after previous calls has been
         * terminated.
         */

        if (OsRelated.isWindows()) {
            return execoWindows(command, delay, location, true);
        } else {
            return execoUnix(command, delay, location);
        }
    }

    public static String getOsName() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        return OS;
    }

    public static boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

}
