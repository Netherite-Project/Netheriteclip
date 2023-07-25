package top.leavesmc.leavesclip;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AutoUpdate {
    public static String autoUpdateCorePath;
    public static String autoUpdateDir = "auto_update";
    public static boolean useAutoUpdateJar = false;

    public static void init() {
        File workingDirFile = new File(autoUpdateDir);
        if (!(workingDirFile.isDirectory() && workingDirFile.exists())) return;
        File corePathFile = new File(autoUpdateDir + "/core.path");
        if (!(corePathFile.isFile() && corePathFile.exists())) return;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(corePathFile))) {
            String firstLine = bufferedReader.readLine();
            if (firstLine == null) return;

            autoUpdateCorePath = firstLine;
            File jarFile = new File(autoUpdateCorePath);
            if (!(jarFile.isFile() && jarFile.exists())) {
                System.out.println("The specified server core: " + autoUpdateCorePath + " does not exist. Using the original jar!");
                return;
            }

            useAutoUpdateJar = true;
            System.out.println("Using server core: " + autoUpdateCorePath + " provide by Leavesclip-Auto-Update");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static InputStream getResourceStream(String jarPath, String name) {
        if (!useAutoUpdateJar) return AutoUpdate.class.getResourceAsStream(name);
        name = name.replaceFirst("/", "");
        InputStream result = null;

        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(jarPath));
            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().equals(name)) {
                    result = new ByteArrayInputStream(zipInputStream.readAllBytes());
                    break;
                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();

            if (result == null) {
                throw new IOException(name + " not found in our jar or in the " + jarPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
