package Insomnia.Connection;

import Insomnia.Graphics.MainWindow;
import javax.swing.*;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * The class StreamUtils is a class to work with streams, write to or read from them.
 * It works with HttpURLConnection output stream and writes and reads from the the last
 * request files.
 *
 * @author Negar Movaghatian
 */
public class StreamUtils {

    private final static String REQUESTS_DIR = "." + File.separator + "data" + File.separator + "Requests";
    private final static String OUTPUT_DIR = "." + File.separator + "data" + File.separator + "Output";
    private final static String SETTINGS_DIR = "." + File.separator + "data" + File.separator;

    static {
        createDirectory(REQUESTS_DIR);
        createDirectory(OUTPUT_DIR);
    }

    /**
     * Read all the bytes written in the connection's input stream and collect them in an array.
     * @param reader The input stream of the HttpURLConnection.
     * @return All the bytes written in the connection's input stream.
     */
    public static byte[] getStreamBytes(BufferedInputStream reader) {
        try {
            byte[] streamBytes = new byte[reader.available()];
            reader.read(streamBytes, 0, reader.available());
            return streamBytes;
        } catch (IOException e) {
            System.err.println("Could not read information from server: " + e.getMessage());
        }
        return null;
    }

    /**
     * Write the output of the connection in a file.
     * @param streamBytes The bytes of the file.
     * @param fileName The name of the file.
     */
    public static void fileWriter(byte[] streamBytes, String fileName) {
        try (FileOutputStream writer = new FileOutputStream(OUTPUT_DIR + File.separator + fileName)) {
            if (streamBytes != null)
                for (byte streamByte : streamBytes)
                    writer.write(streamByte);
        } catch (IOException e) {
            System.err.println("An unexpected error occurred while writing the output file: " + e.getMessage());
        }
    }

    /**
     * Get the response body as text.
     * @param streamBytes The bytes of the request body.
     * @return A string which contains the response of the request body.
     */
    public static String getResponseBodyText(byte[] streamBytes) {
        String toString = "";
        if (streamBytes != null)
            for (byte streamByte : streamBytes)
                toString = toString.concat((char) streamByte + "");
        return toString;
    }

    /**
     * Save the given request into a file.
     * @param connection The connection to save into a file.
     */
    public static void saveRequest(Connection connection) {
        try (FileOutputStream output = new FileOutputStream(REQUESTS_DIR + File.separator + "req" +
                System.currentTimeMillis() + ".bin")) {
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);
            objectOutput.writeObject(connection);
        } catch (IOException e) {
            System.err.println("An unexpected error occurred while saving this request: " + e.getMessage());
        }
    }

    /**
     * Read all the requests in the request directory.
     * @return A list of all the saved connections.
     */
    public static LinkedList<Connection> readRequests() {
        LinkedList<Connection> connections = new LinkedList<>();
        File folder = new File(REQUESTS_DIR);

        if (folder.listFiles() != null) {
            for (File request : folder.listFiles())
                try (FileInputStream input = new FileInputStream(request)) {
                    ObjectInputStream objectInput = new ObjectInputStream(input);
                    connections.add((Connection) objectInput.readObject());
                } catch (ClassNotFoundException | IOException e) {
                    System.err.println("An unexpected error occurred while reading saved requests: " + e.getMessage());
                }
        }

        return connections;
    }

    /**
     * Creates e new directory with the given file name in case it does not exist.
     */
    private static void createDirectory(String dirName) {
        File file = new File(dirName);
        if (!file.exists()) {
            try {
                if (!file.mkdir())
                    System.err.println("Could not create directory");
            } catch (Exception e) {
                System.err.println("Could not create" + dirName + "directory: " + e.getMessage());
            }
        }
    }

    /**
     * Checks if any file exist with the given path.
     * @param filePath The path to check.
     * @return True if the file path exist and false otherwise.
     */
    public static boolean isPathValid(String filePath) {
        File file = new File(filePath);
        if (file.exists())
            return true;
        System.out.println("Could not find this file path : " + filePath);
        return false;
    }

    /**
     * Save the last settings applied on this app.
     */
    public static void saveSettings(MainWindow mainWindow) {
        String content = mainWindow.followRedirects() + " " + mainWindow.isHideInTraySelected() + " "
                + mainWindow.getTheme();
        try (BufferedWriter output = new BufferedWriter(new FileWriter(SETTINGS_DIR + "Settings.bin"))) {
            output.write(content);
            output.flush();
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(null, "Could not save user settings.",
                    "Failed to Save File", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Read the last settings before closing the program.
     * @param mainWindow The main window to apply the settings to.
     */
    public static void readSettings(MainWindow mainWindow) {
        try (FileInputStream input = new FileInputStream(SETTINGS_DIR + "Settings.bin")) {
            Scanner scanner = new Scanner(input);
            mainWindow.setFollowRedirects(scanner.nextBoolean());
            mainWindow.setHideInTray(scanner.nextBoolean());
            mainWindow.setTheme(scanner.next());
        } catch (IOException exception) {}
    }

    public static void clearDirectory() {
        File dir = new File(REQUESTS_DIR);
        for(File file: dir.listFiles())
            if (!file.isDirectory())
                file.delete();
    }
}
