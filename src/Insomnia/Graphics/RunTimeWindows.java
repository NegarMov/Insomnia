package Insomnia.Graphics;

import Insomnia.Connection.Connection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * The class RunTimeWindow is a class to provide a graphical interface whenever
 * the program needs to open a window besides the main window of the program(For
 * example when 'options' or 'create new request' are selected).
 *
 * @author Negar Movaghatian
 * @since 2020.5.5
 */
public class RunTimeWindows extends JFrame {

    private MainWindow mainWindow; // The main window which has interaction whit this window
    private JTextField nameField; // A field which is used for giving the name of a new folder or request
    private JButton create; // A button which creates a new request or folder when clicked
    private JComboBox<String> methodsList; // A Combobox which has the name of the request methods and lets the user
    // to choose one of the methods

    /**
     * Create a new RuntimeWindow. this window is not resizable and its
     * look will change according to the theme.
     */
    public RunTimeWindows(MainWindow mainWindow) {
        setLayout(new BorderLayout());
        getContentPane().setBackground((mainWindow.getTheme().equals("dark"))? Color.DARK_GRAY : Color.WHITE);
        setResizable(false);
        setLocation(250, 150);
        this.mainWindow = mainWindow;
        create = new JButton("CREATE");
        create.addActionListener(new ButtonHandler());
        nameField = new JTextField();
    }

    /**
     * Get some components and set their look according to the application's
     * theme(Dark or light).
     * @param components A list of components to apply the changes to.
     */
    private void setFontAndColor(Component... components) {
        for (Component c : components) {
            c.setForeground(new Color(120, 100, 225));
            c.setBackground((mainWindow.getTheme().equals("dark"))? Color.DARK_GRAY : Color.WHITE);
            c.setFont(new Font("Calibri", Font.PLAIN, 13));
        }
    }

    /**
     * Add all the given components to this window.
     * @param components The components to add to this window.
     */
    private void addComponents(Component... components) {
        for (Component c : components)
            add(c);
    }

    /**
     * A window for when the option menu is selected. It's possible to change the
     * application's theme or manage what should happen when the program is closed
     * or you can choose the follow redirects automatically or not via this window.
     */
    public void options() {

        // Set this window for the new command
        getContentPane().removeAll();
        setLayout(null);
        setTitle("Options");
        setIconImage(new ImageIcon(getClass().getResource("icon/Options.png")).getImage());
        setSize(300, 170);

        // Create components of the window
        JCheckBox redirect = new JCheckBox("  Follow redirects automatically");
        redirect.setLocation(10, 20); redirect.setSize(250, 20);
        redirect.addActionListener(e -> mainWindow.switchFollowRedirects());
        redirect.setSelected(mainWindow.followRedirects());
        JCheckBox exit = new JCheckBox("  Hide in System Tray when closed");
        exit.setLocation(10, 60); exit.setSize(250, 20);
        exit.setSelected(mainWindow.isHideInTraySelected());
        exit.addActionListener(e -> mainWindow.switchHideInTray());
        exit.setSelected(mainWindow.isHideInTraySelected());
        ButtonGroup theme = new ButtonGroup();
        JRadioButton lightTheme = new JRadioButton("  Light theme", true);
        lightTheme.setLocation(10, 100); lightTheme.setSize(120, 20);
        JRadioButton darkTheme = new JRadioButton("  Dark theme", false);
        darkTheme.setLocation(130, 100); darkTheme.setSize(120, 20);
        if (mainWindow.getTheme().equals("light")) {
            lightTheme.setSelected(true);
            darkTheme.setSelected(false);
        }
        else {
            darkTheme.setSelected(true);
            lightTheme.setSelected(false);
        }
        addComponents(redirect, exit, lightTheme, darkTheme);
        theme.add(lightTheme);
        theme.add(darkTheme);

        // Add action listener to components
        lightTheme.addItemListener(e -> {
            mainWindow.setTheme("light");
            getContentPane().setBackground(Color.WHITE);
            setFontAndColor(redirect, exit, lightTheme, darkTheme);
        });
        darkTheme.addItemListener(e -> {
            mainWindow.setTheme("dark");
            getContentPane().setBackground(Color.DARK_GRAY);
            setFontAndColor(redirect, exit, lightTheme, darkTheme);
        });

        // Set the components color and font and add them to this window
        setFontAndColor(redirect, exit, lightTheme, darkTheme);
        addComponents(redirect, exit, lightTheme, darkTheme);

        repaint(); setVisible(true);
    }

    /**
     * Show the about menu which contains the information about the author of this
     * application.
     */
    public void about() {

        // Set this window for the new command
        getContentPane().removeAll();
        setSize(350, 180);
        setLayout(null);
        setTitle("About");
        setIconImage(new ImageIcon(getClass().getResource("icon/About.png")).getImage());

        // Create components of the window
        JLabel aboutInfo1 = new JLabel("This project is written by: Negar Movaghatian");
        aboutInfo1.setSize(300, 20); aboutInfo1.setLocation(20, 20);
        JLabel aboutInfo2 = new JLabel("Student ID: 9831062");
        aboutInfo2.setSize(300, 20); aboutInfo2.setLocation(20, 50);
        JLabel aboutInfo3 = new JLabel("Email: n.movaghatian@aut.ac.ir");
        aboutInfo3.setSize(300, 20); aboutInfo3.setLocation(20, 80);
        JLabel aboutInfo4 = new JLabel("Since: 2020.5.5");
        aboutInfo4.setSize(300, 20); aboutInfo4.setLocation(20, 110);

        // Set the components color and font and add them to this window
        setFontAndColor(aboutInfo1, aboutInfo2, aboutInfo3, aboutInfo4);
        addComponents(aboutInfo1, aboutInfo2, aboutInfo3, aboutInfo4);

        repaint(); setVisible(true);
    }

    /**
     * A window for when the help menu is selected which contains information
     * about this application and how to use it.
     */
    public void help() {

        // Set this window for the new command
        getContentPane().removeAll();
        setSize(400, 280);
        setTitle("Help");
        setIconImage(new ImageIcon(getClass().getResource("icon/Help.png")).getImage());

        // Create components of the window
        JTextArea help = new JTextArea("This Project is a simple HTTP client. You can create an HTTP request, send" +
                " it for a server and see the response it sends back.\nUse the left panel to create a new request and" +
                " after, use the middle panel to set the properties of your request. You can set its method and URL" +
                " at the top and add additional properties like its headers, request body or query parameters from the" +
                " panels below. You can also upload a file from 'upload binary' tab.\nIn the right panel you can see" +
                " the response of your request. At the top you can see the request's status and below, the response body" +
                ", preview (in case it's an image) and headers.\nUse 'option' menu to customize the program according" +
                " to your needs.");
        help.setMargin(new Insets(25, 10, 10, 10));
        help.setLineWrap(true);
        help.setWrapStyleWord(true);
        help.setEditable(false);
        help.setSize(300, 20); help.setLocation(20, 20);

        // Set the components color and font and add them to this window
        setFontAndColor(help);
        add(help);

        repaint(); setVisible(true);
    }

    /**
     * A window to create a new request by asking the request's method, name and folder
     * with.
     */
    public void newRequest() {

        // Set this window for the new command
        getContentPane().removeAll();
        setSize(550, 180);
        setLayout(null);
        setTitle("New Request");
        setIconImage(new ImageIcon(getClass().getResource("icon/Add.png")).getImage());

        String[] methods = {"GET", "DELETE", "POST", "PUT"};

        JLabel name = new JLabel("Name: ");
        name.setSize(100, 20); name.setLocation(20, 20);
        JLabel method = new JLabel("Method: ");
        method.setSize(100, 20); method.setLocation(430, 20);

        // Create or initiate the components of this window
        methodsList = new JComboBox(methods);
        methodsList.setMaximumRowCount(5);
        methodsList.setSize(80, 30); methodsList.setLocation(430, 47);

        nameField.setSize(350, 25); nameField.setLocation(20, 50);

        create.setSize(80, 30); create.setLocation(20, 100);

        // Set the components color and font and add them to this window
        setFontAndColor(name, method, methodsList, create, nameField);
        create.setOpaque(true); create.setBackground(new Color(120, 100, 225));
        nameField.setOpaque(true); nameField.setBackground(Color.LIGHT_GRAY);
        addComponents(name, nameField, method, methodsList, create);

        repaint(); setVisible(true);
    }

    /**
     * Class ButtonHandler is a class to handle the actions after any button of
     * any of the runtime windows is pressed.
     */
    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (nameField.getText().trim().length() == 0)
                JOptionPane.showMessageDialog(RunTimeWindows.this, "Can't leave name field empty.",
                        "Empty Name Field", JOptionPane.ERROR_MESSAGE);
            else {
                mainWindow.getRequestPanel().addRequest(new Connection(nameField.getText(),
                                "", methodsList.getSelectedItem().toString(),
                                mainWindow.followRedirects(), false,
                                false, "", false, "",
                                new HashMap<>(), new HashMap<>(), new HashMap<>()));
                mainWindow.getRequestSettingPanel().setProperties(methodsList.getSelectedItem().toString(),
                        "", new HashMap<>(), new HashMap<>(), new HashMap<>(), "");
                dispose();
            }
        }
    }
}