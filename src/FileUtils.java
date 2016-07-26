import java.io.File;

/**
 * Created by Alex on 26/07/2016.
 */
class FileUtils {

    public static void createShortcut(File source, File destination) {
        if (source.exists()) {
            try {
                File tmp = File.createTempFile("tmpfile", ".bat");
                java.io.FileWriter fw = new java.io.FileWriter(tmp);
                fw.write("@echo off\n");
                fw.write("set SCRIPT=\"%TEMP%\\%RANDOM%-%RANDOM%-%RANDOM%-%RANDOM%.vbs\"\n");
                fw.write("echo Set oWS = WScript.CreateObject(\"WScript.Shell\") >> %SCRIPT%\n");
                fw.write("echo sLinkFile = \"" + destination.getAbsolutePath()
                        + ".lnk\" >> %SCRIPT%\n");
                fw.write("echo Set oLink = oWS.CreateShortcut(sLinkFile) >> %SCRIPT%\n");
                fw.write("echo oLink.TargetPath = \""
                        + source.getAbsolutePath() + "\" >> %SCRIPT%\n");
                fw.write("echo oLink.Save >> %SCRIPT%\n");
                fw.write("cscript /nologo %SCRIPT%\n");
                fw.write("del %SCRIPT%\n");
                fw.close();
                try {
                    Runtime rt = Runtime.getRuntime();
                    Process pc = rt.exec("cmd /c " + tmp.getAbsolutePath());
                    pc.waitFor();
                    tmp.delete();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        } else
            System.out.println("Source doesn't exist: "
                    + source.getAbsolutePath());
    }
}