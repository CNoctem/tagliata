package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Splitter {

    private final String infile;

    private final int chunkSize;

    private final String outDir;

    public Splitter(String infilePath, String outDir, int chunkSize) {
        this.infile = infilePath;
        this.chunkSize = chunkSize;
        this.outDir = outDir;
    }

    public Splitter(String infilePath, String outDir) {
        this(infilePath, outDir, 25000000);
    }

    void split() throws IOException {
        System.out.println("Splitting: " + infile);
        System.out.println("Chunksize: " + chunkSize);

        File ifile = new File(infile);
        var infileName = infile.substring(infile.lastIndexOf(File.separator) + 1);
        FileInputStream fis;
        String newName;
        FileOutputStream chunk;
        var fileSize = ifile.length();
        int nChunks = 0, read, readLength = chunkSize;
        byte[] byteChunk;

        int padSize = (int) Math.log10((double)fileSize / (double)chunkSize) + 1;

        fis = new FileInputStream(ifile);
        while (fileSize > 0) {
            if (fileSize <= chunkSize) {
                readLength = (int) fileSize;
            }
            byteChunk = new byte[readLength];
            read = fis.read(byteChunk, 0, readLength);
            fileSize -= read;
            assert (read == byteChunk.length);
            nChunks++;
            var newFilename = infileName + ".p" + padLeftZeros("" + (nChunks - 1), padSize);
            newName = outDir + "/" + newFilename;
            chunk = new FileOutputStream(newName);
            chunk.write(byteChunk);
            System.out.println("Created " + newFilename);
            chunk.flush();
            chunk.close();
        }
        fis.close();
    }

    public static String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }


}
