package Insomnia.Connection;

import Insomnia.Graphics.MainWindow;
import Insomnia.Graphics.RequestSettingPanel;
import Insomnia.Graphics.ResponsePanel;

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
    private static RequestSettingPanel settingPanel;
    private static ResponsePanel responsePanel;

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
            case "new request":
                if (handler.getUrl().size() == 0) {
                    System.out.println("No URL found");
                    break;
                }
                for (int i=0; i<handler.getUrl().size(); i++) {
                    Connection connection = new Connection(handler.getUrl().get(i), handler.getMethod(),
                            handler.isFollowRedirect(), handler.ShowResponseHeaders(), handler.hasFileName(),
                            handler.getFileName(), handler.uploadBinary(), handler.getBinaryFilePath(),
                            handler.getFormData(), handler.getHeaders());
                    if (handler.isSaveFile())
                        StreamUtils.saveRequest(connection);

                    long startTime = System.nanoTime();
                    connection.runConnection();
                    connection.printResponseInfo();
                    long elapsedTime = System.nanoTime() - startTime;
                    System.out.printf("\nResponse Time: %.2f second(s)\n\n", (float) elapsedTime / 1_000_000_000.0);
                }
                break;
            case "invalid input":
                System.out.println("The command's syntax is not correct.");
                break;
        }
    }

    public static void runInGUI() {
        if (settingPanel.getURL().equals("")) {
            System.out.println("No URL found");
            return;
        }
        Connection connection = new Connection(putQueryItems(settingPanel.getURL(), settingPanel.getQueries()),
                settingPanel.getMethod(), mainWindow.followRedirects(), false, false,
                "", settingPanel.uploadBinary(), settingPanel.getBinaryFilePath(), settingPanel.getFormData(),
                settingPanel.getHeaders());
        long startTime = System.nanoTime();
        connection.runConnection();
        connection.printResponseInfo();
        responsePanel.setHeaderValues(connection.getHeaders());
        responsePanel.setRawData(connection.getResponseText());
        responsePanel.setPreview(connection.getResponseBytes());
        long elapsedTime = System.nanoTime() - startTime;
        responsePanel.editStatusBar(connection.getResponseMessage(), String.format("%.2fs", (float) elapsedTime / 1_000_000_000.0), connection.getResponseSize());
        System.out.printf("\nResponse Time: %.2f second(s)\n\n", (float) elapsedTime / 1_000_000_000.0);
    }

    /**
     * Run a specific request with the given index among the list of saved requests.
     * @param requestNumber The index of request to run.
     */
    public static void runRequest(int requestNumber) {
        LinkedList<Connection> savedConnections = StreamUtils.readRequests();
        if (requestNumber > savedConnections.size() || requestNumber < 1) {
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

    public static void setMainWindow(MainWindow mainWindow) {
        RequestManager.mainWindow = mainWindow;
    }

    public static void setSettingPanel(RequestSettingPanel settingPanel) {
        RequestManager.settingPanel = settingPanel;
    }

    public static void setResponsePanel(ResponsePanel responsePanel) {
        RequestManager.responsePanel = responsePanel;
    }

    private static String putQueryItems(String url, HashMap<String, String> query) {
        if (query.size() !=0 ) {
            url = url.concat("?");
            int counter = 0;
            for (String queryKey : query.keySet()) {
                for (int i=0; i<queryKey.length(); i++) {
                    char c = queryKey.charAt(i);
                    url = url.concat((c == ' ')? "%20" : ("" + c));
                }
                url = url.concat("=");
                for (int i=0; i<query.get(queryKey).length(); i++) {
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

//http://apapi.haditabatabaei.ir/tests/get/buffer/pic
//bgcolor=rgb(100, 200, 50)
//text=OMF IT"S WORKING
