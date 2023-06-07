package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class Joiner {

    private final String srcDir;
    private final String filename;

    Joiner(String srcDir, String filename) {
        this.srcDir = srcDir;
        this.filename = filename;
    }

    void join() throws IOException {
        File ofile = new File(srcDir + "/" + filename);
        FileOutputStream fos;
        FileInputStream fis;
        byte[] fileBytes;
        int bytesRead = 0;

        var files = new File(srcDir).listFiles((dir, name) -> nameMatches(name));
        if (files == null) {
            System.out.println("No files matched. Goodbye.");
            System.exit(-1);
        }
        var filesL = Arrays.asList(files);
        Collections.sort(filesL);

        fos = new FileOutputStream(ofile, true);
        for (File file : files) {
            System.out.println("Adding " + file.getName());
            fis = new FileInputStream(file);
            fileBytes = new byte[(int) file.length()];
            bytesRead = fis.read(fileBytes, 0, (int) file.length());
            assert (bytesRead == fileBytes.length);
            assert (bytesRead == (int) file.length());
            fos.write(fileBytes);
            fos.flush();
            fis.close();
        }
        fos.close();
    }

    private boolean nameMatches(String name) {
        if (!name.startsWith(filename)) return false;
        int i = name.lastIndexOf(".");
        if (i == -1 || i >= name.length() - 2 || name.charAt(i + 1) != 'p') return false;
        i += 2;
        while (i < name.length()) if (!Character.isDigit(name.charAt(i++))) return false;
        return true;
    }

}
