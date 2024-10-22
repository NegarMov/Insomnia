package Insomnia.Connection;

import Insomnia.Graphics.MainWindow;
import Insomnia.Graphics.RequestSettingPanel;
import Insomnia.Graphics.ResponsePanel;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The class RequestManager manages when to create and run requests. It also shows a list of the
 * saved requests and is capable to run any of them.
 *
 * @author Negar Movaghatian
 */
public class RequestManager {

    private static InputHandler handler; // The handler to get input from
    private static MainWindow mainWindow; // The main window to get information from in GUI part
    private static RequestSettingPanel settingPanel; // The setting panel which has interaction with this manager
    private static ResponsePanel responsePanel; // The response panel which has interaction with this manager

    static {
        handler = new InputHandler();
    }

    /**
     * Get an input using class InputHandler and create a new request if it returns 'new request',
     * and print error if the request is not valid and the return value is 'invalid input'.
     */
    public static void runInConsole() {
        String  result = handler.getInput();
        switch (result) {
            case "new request": // Create a new request and run it
                if (handler.getUrl().size() == 0) { // Print error in case no URL was detected
                    System.out.println("No URL found");
                    break;
                }
                for (int i=0; i<handler.getUrl().size(); i++) {
                    Connection connection = new Connection("", handler.getUrl().get(i), handler.getMethod(),
                            handler.isFollowRedirect(), handler.ShowResponseHeaders(), handler.hasFileName(),
                            handler.getFileName(), handler.uploadBinary(), handler.getBinaryFilePath(),
                            handler.getFormData(), handler.getHeaders(), new HashMap<>());
                    if (handler.isSaveFile())
                        StreamUtils.saveRequest(connection);

                    long startTime = System.nanoTime();
                    connection.runConnection();
                    connection.printResponseInfo();
                    long elapsedTime = System.nanoTime() - startTime;
                    System.out.printf("\nResponse Time: %.2f second(s)\n\n", (float) elapsedTime / 1_000_000_000.0);
                }
                break;
            case "invalid input": // The input was invalid; print error message
                System.out.println("The command's syntax is not correct.");
                break;
        }
    }

    /**
     * Run a request by getting its information from the GUI part and sending the response information
     * to it.
     */
    public static void runInGUI() {
        if (settingPanel.getURL().equals("")) { // Print error in case no URL was detected
            JOptionPane.showMessageDialog(null, "Can't leave the URL field empty.",
                    "No URL Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a new connection and update it according to the last changes on request setting panel
        Connection connection = mainWindow.getRequestPanel().getFocusedRequest();
        connection.updateRequest(mainWindow.followRedirects(), putQueryItems(settingPanel.getURL(), settingPanel.getQueries()),
                settingPanel.getMethod(), settingPanel.uploadBinary(), settingPanel.getBinaryFilePath(),
                settingPanel.getFormData(), settingPanel.getHeaders(), new HashMap<>());

        // Run the new connection
        long startTime = System.nanoTime();
        connection.runConnection();
        connection.printResponseInfo();
        if (!connection.getErrors().equals("")) { // The connection had some error; print the error messages
            responsePanel.editStatusBar("ERROR", "0.00s", "0.0B");
            responsePanel.setRawData(connection.getErrors());
            responsePanel.setHeaderValues(new HashMap<>());
        }
        else { // The connection ran successfully; print response information
            responsePanel.setHeaderValues(connection.getHeaders());
            responsePanel.setRawData(connection.getResponseText());
            if (connection.isImage())
                responsePanel.setPreview(connection.getResponseBytes());
            else
                responsePanel.resetPreview();

            long elapsedTime = System.nanoTime() - startTime;
            responsePanel.editStatusBar(connection.getResponseMessage(), String.format("%.2fs",
                    (float) elapsedTime / 1_000_000_000.0), connection.getResponseSize());
            System.out.printf("\nResponse Time: %.2f second(s)\n\n", (float) elapsedTime / 1_000_000_000.0);
        }
    }

    /**
     * Run a specific request with the given index among the list of saved requests.
     * @param requestNumber The index of request to run.
     */
    public static void runRequest(int requestNumber) {
        LinkedList<Connection> savedConnections = StreamUtils.readRequests();
        if (requestNumber > savedConnections.size() || requestNumber < 1) { // The request does not exist; Print error
            System.err.println("There is no request with index " + requestNumber);
            return;
        }
        Connection connection = savedConnections.get(requestNumber - 1);
        System.out.println("\n\nSending request to: " + connection.getUrlString());
        long startTime = System.nanoTime();
        connection.runConnection();
        connection.printResponseInfo();
        long elapsedTime = System.nanoTime() - startTime;
        System.out.printf("\nResponse Time: %.2f second(s)\n\n", (float) elapsedTime / 1_000_000_000.0);
    }

    /**
     * Print a list of saved request.
     */
    public static void showSavedRequests() {
        LinkedList<Connection> savedConnections = StreamUtils.readRequests();
        int counter = 1;
        for (Connection request : savedConnections)
            System.out.println((counter++) + ". " + request);
        System.out.println();
    }

    /**
     * Set the args array of the handler.
     * @param args The arg String array to pass to the input handler.
     */
    public static void setArgs(String[] args) {
        handler.setArgs(args);
    }

    /**
     * @param mainWindow The main window which has interaction with this class.
     */
    public static void setMainWindow(MainWindow mainWindow) {
        RequestManager.mainWindow = mainWindow;
        settingPanel = mainWindow.getRequestSettingPanel();
        responsePanel = mainWindow.getResponsePanel();
    }

    /**
     * Get query items as HashMap and add them in proper format to the end of the URL.
     * @param url The raw url to append query items to.
     * @param query The list of the queries to add to the url.
     * @return The final URL with query items appended to its end.
     */
    private static String putQueryItems(String url, HashMap<String, String> query) {
        if (query.size() !=0 ) {
            url = url.concat("?");
            int counter = 0;
            for (String queryKey : query.keySet()) {
                for (int i=0; i<queryKey.length(); i++) { // Add name
                    char c = queryKey.charAt(i);
                    url = url.concat((c == ' ')? "%20" : ("" + c));
                }
                url = url.concat("=");
                for (int i=0; i<query.get(queryKey).length(); i++) { // Add value
                    char c = query.get(queryKey).charAt(i);
                    url = url.concat((c == ' ')? "%20" : ("" + c));
                }
                if (++counter != query.size())
                    url = url.concat("&");
            }
        }
        return url;
    }
}