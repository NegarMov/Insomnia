package Insomnia.Graphics;

import Insomnia.Connection.Connection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents the left panel on the main window which
 * contains a list of the current requests. It is possible to add a new
 * request via this part.
 * The class RequestPanel is a child class of JPanel.
 *
 * @author Negar Movaghatian
 * @since 2020.5.5
 */
public class RequestPanel extends JPanel {

    private HashMap<JButton, Connection> requests; // A list of requests
    private JPanel requestsPanel; // A Panel which contains all the requests of this window
    private JButton focusedRequestButton; // The button(request) which is selected at the moment
    private MainWindow mainWindow; // The main window which has interaction with this panel

    /**
     * Create a new request panel.
     * @param mainWindow The main window which has interaction with this panel.
     */
    public RequestPanel(MainWindow mainWindow) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 700));
        requests = new HashMap<>();
        this.mainWindow = mainWindow;
        requestsPanel = new JPanel();
        requestsPanel.setLayout(new BoxLayout(requestsPanel, BoxLayout.Y_AXIS));
        add(requestsPanel, BorderLayout.CENTER);
        initiateButton();
        initiateRequests();
        setTheme();
    }

    /**
     * Change the theme of this panel and its components to light or dark.
     */
    public void setTheme() {
        if (mainWindow.getTheme().equals("light")) {
            setBackground(Color.LIGHT_GRAY);
            requestsPanel.setBackground(Color.LIGHT_GRAY);
        }
        else {
            setBackground(new Color(80, 80, 80));
            requestsPanel.setBackground(new Color(80, 80, 80));
        }
    }

    /**
     * Create the 'Add request' button.
     */
    private void initiateButton() {
        JButton addRequest = new JButton("             Add a new request");
        addRequest.setForeground(new Color(120, 100, 225));
        addRequest.setBackground(Color.WHITE);
        addRequest.setFont(new Font("Calibri", Font.PLAIN, 13));
        addRequest.setIcon(new ImageIcon(getClass().getResource("icon/Add.png")));
        addRequest.setPreferredSize(new Dimension(250, 40));

        addRequest.addActionListener(e -> new RunTimeWindows(mainWindow).newRequest());

        add(addRequest, BorderLayout.NORTH);
    }

    /**
     * Add all the requests which existed the last time the program was closed.
     */
    private void initiateRequests() {
        /*for (String folder : folders.keySet()) {
            if (!folder.equals("-")) {
                JButton folderButton = new JButton(folder);
                requestsPanel.add(folderButton);
            }
        }

        for (Connection request : requests) {
            JButton requestButton = new JButton(request.getName() + "   " + request.getMethod());
            requestsPanel.add(requestButton);
        }*/
    }

    /**
     * Add a new request to the requests' panel.
     * @param request The request to add.
     */
    public void addRequest(Connection request) {
        if (!requests.isEmpty())
            saveLastRequest();
        JButton requestButton = new JButton();
        requestButton.setLayout(new BorderLayout());
        JLabel name = new JLabel(request.getName());
        JLabel method = new JLabel(request.getMethod());
        name.setForeground(new Color(120, 100, 225));
        method.setForeground(new Color(120, 100, 225));
        requestButton.add(name, BorderLayout.WEST);
        requestButton.add(method, BorderLayout.EAST);
        requestButton.setMaximumSize(new Dimension(getWidth(), 40));
        focusedRequestButton = requestButton;
        requestButton.addActionListener(e -> {
            saveLastRequest();
            focusedRequestButton = requestButton;
            mainWindow.getRequestSettingPanel().setProperties(request.getMethod(), request.getUrlString(),
                    request.getFormData(), request.getRequestHeaders(), request.getQuery(),
                    request.getBinaryFileName());
        });
        requestsPanel.add(requestButton);
        requests.put(requestButton, request);
        requestsPanel.revalidate();
    }

    public void saveLastRequest() {
        RequestSettingPanel settingPanel = mainWindow.getRequestSettingPanel();
        requests.get(focusedRequestButton).updateRequest(mainWindow.followRedirects(), settingPanel.getURL(),
                settingPanel.getMethod(), settingPanel.uploadBinary(), settingPanel.getBinaryFilePath(),
                settingPanel.getFormData(), settingPanel.getHeaders(), settingPanel.getQueries());
    }

    public void setFocusedRequestMethod(String method) {
        if (focusedRequestButton != null)
            ((JLabel) focusedRequestButton.getComponent(1)).setText(method);
    }

    public Connection getFocusedRequest() {
        return requests.get(focusedRequestButton);
    }
}
