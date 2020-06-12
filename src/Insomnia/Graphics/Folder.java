package Insomnia.Graphics;

import Insomnia.Connection.Connection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * This class represents a folder it contains a list of the request, has a
 * name and can be either open or closed.
 * Also this class is a child class of JPanel.
 *
 * @author Negar Movaghatian
 * @since 2020.5.6
 */
public class Folder extends JPanel {

    private ArrayList<Connection> requests; // The list of the requests in this folder
    private String name; // The name of the folder
    private boolean isOpen; // Determines if this folder is open now or close
    private int width; // The width of the button which represents this folder
    private RequestPanel requestPanel; // The request panel which this folder is located in

    /**
     * Create a new folder with the given name.
     * @param name The name of the folder.
     * @param requestPanel
     */
    public Folder(String name, RequestPanel requestPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        requests = new ArrayList<>();
        this.requestPanel = requestPanel;
        this.name = name;
        isOpen = true;
        width = requestPanel.getWidth();
        add(createFolderButton());
    }

    /**
     * Create a button which contains a 'Folder' icon and the folder's name.
     * @return A button which represents the folder.
     */
    private JButton createFolderButton() {
        JButton folderButton = new JButton("           " + name);
        folderButton.addActionListener(e -> setOpen());
        folderButton.setIcon(new ImageIcon(getClass().getResource("icon/Folder.png")));
        folderButton.setMaximumSize(new Dimension(width, 40));

        return folderButton;
    }

    /**
     * Create a button which contains a request's name and method.
     * @param request The request to create the button for.
     * @return A button which represents the request.
     */
    private JButton createRequestButton(Connection request) {
        JButton requestButton = new JButton();
        requestButton.setLayout(new BorderLayout());
        JLabel name = new JLabel("\u25BA  " + request.getName());
        JLabel method = new JLabel(request.getMethod());
        name.setForeground(new Color(120, 100, 225));
        method.setForeground(new Color(120, 100, 225));
        requestButton.add(name, BorderLayout.WEST);
        requestButton.add(method, BorderLayout.EAST);
        requestButton.setMaximumSize(new Dimension(width, 40));
        requestButton.addActionListener(e -> requestPanel.setFocusedRequest(requestButton));

        return requestButton;
    }

    /**
     * Add a new request to this folder.
     * @param request The request to add to this folder.
     */
    public void newRequest(Connection request) {
        requests.add(request);
        add(createRequestButton(request));

        revalidate();
    }

    /**
     * Open this folder if it's closed and vice versa.
     */
    private void setOpen() {
        isOpen = !isOpen;
        if (isOpen)
            for (Connection request: requests)
                add(createRequestButton(request));
        else {
            removeAll();
            add(createFolderButton());
        }
        revalidate();
    }
}
