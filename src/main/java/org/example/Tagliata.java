package org.example;

import java.io.IOException;

public class Tagliata {

    public static void main(String[] args) throws IOException {
        var aaa = new Args.ArgsBuilder()
                .addCommand("s", "split", "Split file")
                .addCommand("j", "join", "Joins parts")
                .addOption("-c", "--chunk-size", "<chunk size> (default is 25M)")
                .addOption("-d", "--destination", "Destination path")
                .addOption("-i", "--input", "Path of dir containing files to join")
                .build();

        aaa.parse(args);

        String destPath;
        String sourcePath;

        int chunkSize;

        if (aaa.isCommandPresent("split") && aaa.isCommandPresent("join")) {
            System.out.println("Split or Join? Make up your mind! Goodbye.");
            System.exit(-1);
        } else if (aaa.isCommandPresent("split")) {
            if (aaa.isOptionPresent("destination")) destPath = aaa.getOptionValue("destination");
            else destPath = System.getProperty("user.dir");
            if (aaa.isOptionPresent("chunksize")) chunkSize = parseChunksize(aaa.getOptionValue("chunksize"));
            else chunkSize = 25000000;
            var files = aaa.getArguments();
            if (files.size() == 0) exit("Give me a file.", -1);
            files.forEach(f -> {
                try {
                    System.out.println("Splitting '" + f + "'\nChunk size: " + chunkSize + "'\nDestination: '" + destPath + "'");
                    new Splitter(f, destPath, chunkSize).split();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else if (aaa.isCommandPresent("join")) {
            if (aaa.isOptionPresent("input")) sourcePath = aaa.getOptionValue("input");
            else sourcePath = System.getProperty("user.dir");
            var files = aaa.getArguments();
            if (files.size() != 1) exit("Please give exactly one file name for join operation.", -1);
            var file = files.get(0);
            new Joiner(sourcePath, file).join();
        }

//        var s = new Splitter("c:\\Users\\u99180\\ws\\reptilefoot\\ppb-madagascar.avi", "c:\\khbmunka\\Workspace\\tagliata\\out");
//        s.split();

//        var j = new Joiner("c:\\khbmunka\\Workspace\\tagliata\\out", "ppb-madagascar.avi");
//        j.join();

    }

    private static int parseChunksize(String cs) {
        char prefixCh = cs.charAt(cs.length() - 1);
        int pre = 1;
        if (!Character.isDigit(prefixCh)) {
            cs = cs.substring(0, cs.length() - 1);
            pre = applyPrefix(prefixCh);
        }
        try {
            return Integer.parseInt(cs) * pre;
        } catch (NumberFormatException e) {
            exit("Bad chunk size.", -1);
        }

        return -1;
    }

    private static int applyPrefix(char pre) {
        return switch (pre) {
            case 'k' -> 1000;
            case 'M' -> 1000000;
            case 'G' -> 1000000000;
            default -> -1;
        };
    }

    private static void exit(String msg, int ev) {
        System.out.println(msg);
        System.out.println("Goodbye.");
        System.exit(ev);
    }

}