package eu.visceral.registration.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MailManHandler {
    private String participantsList;
    private String competition;

    public MailManHandler(String competition) {
        this.competition = competition.replaceAll(" ", "_");
        this.participantsList = System.getProperty("user.home") + "/participants-" + this.competition + ".txt";
        ;
    }

    public void removeLineFromFile(String lineToRemove) {
        try {
            File inputFile = new File(participantsList);
            if (!inputFile.exists()) {
                inputFile.createNewFile();
                return;
            }

            // Construct the new file that will later be renamed to the original
            // filename.
            File tempFile = new File(inputFile.getAbsolutePath() + ".tmp");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(participantsList));
            PrintWriter printWriter = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            // Read from the original file and write to the new
            // unless content matches data to be removed.
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.trim().contains(lineToRemove)) {
                    printWriter.println(line);
                    printWriter.flush();
                }
            }
            printWriter.close();
            bufferedReader.close();

            // Delete the original file
            if (!inputFile.delete()) {
                System.err.println("Could not delete original file");
                return;
            }

            // Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inputFile))
                System.err.println("Could not rename file");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addLineToFile(String lineToAdd) {
        PrintWriter printWriter = null;
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(participantsList, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            printWriter = new PrintWriter(bufferedWriter);
            printWriter.println(lineToAdd);
        } catch (IOException e) {
            System.err.println("File writing/opening failed at some stage.");
        } finally {
            try {
                if (printWriter != null) {
                    printWriter.close(); // Will close bufferedWriter and
                                         // fileWriter too
                } else if (bufferedWriter != null) {
                    bufferedWriter.close(); // Will close fileWriter too
                } else if (fileWriter != null) {
                    fileWriter.close();
                } else {
                    System.err.println("Failed to close stream.");
                }
            } catch (IOException e) {
                System.err.println("Closing the file writers failed for some obscure reason");
            }
        }
    }

    public void syncMailManList() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("sudo sync_members -w=no -g=no -d=no -a=no -f " + participantsList + " participants-" + this.competition.toLowerCase());
            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
