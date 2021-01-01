/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Helper;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mbrebion ressource :
 *
 */
public class OsRelated {

    private static String OS = null;
    private static PowerShell powerShell = null;
    private static String lastLocation = "===";

    // OS tasks
    public static String pathAccordingToOS(String path) {
        if (OsRelated.isWindows()) {
            return path.replace("/", "\\").replace("\\\\", "\\").replace("` ", " ").replace(" ", "` ");
        } else {
            return path.replace("\\", "/").replace("//", "/");
        }
    }

    public static void copy(String from, String to) {
        OsRelated.execo(new String[]{"cp", "-r", pathAccordingToOS(from), pathAccordingToOS(to)});
    }

    public static void openDirectoryOf(String PathToFile) {

        if (OsRelated.isWindows()) {
            String path = PathToFile.substring(0, PathToFile.lastIndexOf("\\"));
            System.out.println(path);
            open(path);
        } else {
            String path = PathToFile.substring(0, PathToFile.lastIndexOf("/"));
            open(path);
        }

    }

    public static void open(String pathToFile) {
        if (OsRelated.isWindows()) {
            
            OsRelated.execo(new String[]{"ii",pathAccordingToOS(pathToFile)});
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

    public static String pdfLatex(String outputDir) {
        String[] command =new String[]{pathAccordingToOS(SavedVariables.getPdflatexCmd()), "-halt-on-error", "-no-shell-escape","-interaction=nonstopmode", "-output-directory='"+ pathAccordingToOS(outputDir)+"'", pathAccordingToOS("./output/output.tex")}; 
        String[] out = OsRelated.execo(command,5); // une udrée d'attente de 5 secondes max semble un bon compromis.
        for (String s : command){
            System.out.print(s+" ");
        }
        System.out.println("");
        return out[1];
    }

    public static boolean checkPdfLatex() {
        String[] command = {OsRelated.pathAccordingToOS(SavedVariables.getPdflatexCmd()),"-halt-on-error", "-no-shell-escape"," -version"};
        String out = OsRelated.execo(command)[1];
        return out.contains("pdfTeX");
    }

    public static String guessPdfLAtexPath() {
        if (OsRelated.isWindows()) {
            return OsRelated.execo(new String[]{"Get-Command pdflatex | Select-Object -ExpandProperty Definition"})[1];
        } else {
            return OsRelated.execo(new String[]{"command", "-v", "pdflatex"})[1];
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

    protected static String[] execoWindows(String[] command, double delay, String location) {
        String cmd = "";
        for (String s : command) {
            cmd += s + " ";
        }

        if (powerShell == null || location != OsRelated.lastLocation) {
            OsRelated.lastLocation = location;
            if (powerShell != null) {
                powerShell.close();
            }
            powerShell = PowerShell.openSession(location);
            Map<String, String> myConfig = new HashMap<>();
            myConfig.put("maxWait", "14000");
            powerShell.configuration(myConfig);
        }

        PowerShellResponse response = powerShell.executeCommand(cmd);
        if (response.isTimeout()) {
            return new String[]{String.valueOf(1), "too long command :  " + cmd};
        } else {
            return new String[]{String.valueOf(0), response.getCommandOutput()};
        }

    }

    protected static String[] execoUnix(String[] command, double delay, String location) {
        int out = -1;

        ProcessBuilder pb = new ProcessBuilder();
        List<String> Command = new ArrayList<>();
        Command.add("sh");
        Command.add("-c");
        String lastItem = "";
        for (String s : command){
            lastItem+=" "+s;
        }
        lastItem = lastItem.strip();
                
        Command.add(lastItem);
        
        pb.command(Command);
        pb.directory(new File(location));
        //pb.inheritIO();
        StringBuilder output = new StringBuilder(120);

        pb.redirectError();
        InputStream is = null;

        try {
            Process p = pb.start();

           long startTime = System.currentTimeMillis();
            // get process output (input from java point of view)
            is = p.getInputStream();

            int in;
            while (System.currentTimeMillis() - startTime < delay*1000 && (in = is.read()) != -1) {
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

    ;
    
    protected static String[] execo(String[] command, double delay, String location) {
        /**
         * This function can only be called after previous calls has been
         * terminated.
         */

        if (OsRelated.isWindows()) {
            return execoWindows(command, delay, location);
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
