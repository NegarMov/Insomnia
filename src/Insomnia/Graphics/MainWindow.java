package Insomnia.Graphics;

import Insomnia.Connection.StreamUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class provides a GUI for the main window of this application.
 * The class MainWindow is also a child class of JFrame.
 *
 * @author Negar Movaghatian
 * @since 2020.5.5
 */
public class MainWindow extends JFrame {

    private String theme; // The theme of this program, can be either dark or light
    private Dimension lastDimension; // The last Dimension of the window before fullscreen
    private Point lastLocation; // The last Location of the window before fullscreen
    private RequestPanel requestPanel; // The request panel(west) of this window
    private RequestSettingPanel requestSettingPanel; // The request setting panel(middle) of this window
    private ResponsePanel responsePanel; // The response panel(east) of this window
    private boolean hideInTray; // Determines if this window should be hidden in system tray after pressing
    // Quit or should exit completely
    private boolean followRedirects; // Shows if the user wants the program to follow redirects automatically or not

    /**
     * Create a new application window, select the proper Look and Feel and
     * add the basic components to the main window.
     */
    public MainWindow() {

        // Set the Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
        }
        catch (Exception e){
            System.out.println("Couldn't find Look and Feel 'Windows'.");
        }

        // Set the properties of this window such as its size, icon and etc.
        setSize(1000, 700);
        lastDimension = new Dimension(1000, 700);
        setLocation(100, 50);
        lastLocation = new Point(100, 50);
        getContentPane().setBackground(Color.DARK_GRAY);
        setTitle("AUT_Insomnia");
        setIconImage(new ImageIcon(getClass().getResource("icon/Insomnia logo.png")).getImage());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(977, 400));

        // Add listener to close button
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                handleExit();
            }
        });

        // Initiate the fields of this class
        theme = "light";
        followRedirects = false;
        hideInTray = false;
        requestSettingPanel = new RequestSettingPanel(this);
        responsePanel = new ResponsePanel(this);
        requestPanel = new RequestPanel(this);
        StreamUtils.readSettings(this);
        setTheme(theme);
        initiateMenuBar();
        add(requestPanel, BorderLayout.WEST);
        add(responsePanel, BorderLayout.EAST);
        add(requestSettingPanel, BorderLayout.CENTER);
    }

    /**
     * Show the application's window.
     */
    public void showWindow() {
        setVisible(true);
    }

    /**
     * Set the theme of this window and all its components according to user's choice.
     * It can either be 'dark' or 'light'.
     * @param theme the name of the theme to change the theme to.
     */
    public void setTheme(String theme) {
        this.theme = theme;
        getContentPane().setBackground((theme.equals("light"))? Color.LIGHT_GRAY : Color.DARK_GRAY);
        requestPanel.setTheme();
        requestSettingPanel.setTheme();
        responsePanel.setTheme();
    }

    /**
     * Create the top menubar of the main window. This menu includes 'Application',
     * 'View' and 'Help' tabs.
     */
    public void initiateMenuBar() {

        JMenuBar topMenuBar = new JMenuBar();
        MenuActionListener menuActionListener = new MenuActionListener();

        // Application menu
        JMenu application = new JMenu("Application");
        application.setMnemonic('A');
        JMenuItem options = new JMenuItem("Options");
        options.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        JMenuItem quit = new JMenuItem("Quit");
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        application.add(options);
        application.add(quit);
        topMenuBar.add(application);

        options.addActionListener(menuActionListener);
        quit.addActionListener(e -> handleExit());

        // View menu
        JMenu view = new JMenu("View");
        view.setMnemonic('V');
        JMenuItem toggleFullScreen = new JMenuItem("Toggle Full Screen");
        toggleFullScreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        JMenuItem toggleSidebar = new JMenuItem("Toggle Sidebar");
        toggleSidebar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, InputEvent.CTRL_MASK));
        view.add(toggleFullScreen);
        view.add(toggleSidebar);
        topMenuBar.add(view);

        toggleFullScreen.addActionListener(menuActionListener);
        toggleSidebar.addActionListener(menuActionListener);

        // Help menu
        JMenu help = new JMenu("Help");
        help.setMnemonic('H');
        JMenuItem about = new JMenuItem("About");
        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
        help.add(about);
        help.add(helpItem);
        topMenuBar.add(help);

        helpItem.addActionListener(menuActionListener);
        about.addActionListener(menuActionListener);

        add(topMenuBar, BorderLayout.NORTH);
    }

    /**
     * Switch the follow redirects state.
     */
    public void switchFollowRedirects() {
        followRedirects = !followRedirects;
    }

    /**
     * @param followRedirects The new state of follow redirect.
     */
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    /**
     * Change the hide in tray status. Determines if this window should be hidden in system tray
     * after pressing Quit or should exit completely
     */
    public void switchHideInTray() {
        hideInTray = !hideInTray;
    }

    /**
     * @param hideInTray The new state of the hid in tray.
     */
    public void setHideInTray(boolean hideInTray) {
        this.hideInTray = hideInTray;
    }

    /**
     * @return the follow redirects state.
     */
    public boolean followRedirects() {
        return followRedirects;
    }

    /**
     * @return The main theme of this window.
     */
    public String getTheme() {
        return theme;
    }

    /**
     * @return The response panel of this window.
     */
    public ResponsePanel getResponsePanel() {
        return responsePanel;
    }

    /**
     * @return The request panel of the window.
     */
    public RequestPanel getRequestPanel() {
        return requestPanel;
    }

    /**
     * @return The request setting panel of this window.
     */
    public RequestSettingPanel getRequestSettingPanel() {
        return requestSettingPanel;
    }

    /**
     * See if the hide in tray option is selected or not.
     * @return A boolean which is true if the window should hide in system tray
     * and false otherwise.
     */
    public boolean isHideInTraySelected() {
        return hideInTray;
    }


    /**
     * A Class to choose what should happen when items on the menu bar at the top of the
     * program are selected.
     */
    private class MenuActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            RunTimeWindows menuWindows = new RunTimeWindows(MainWindow.this);
            switch (event.getActionCommand()) {
                case "Options":
                    menuWindows.options();
                    break;
                case "About":
                    menuWindows.about();
                    break;
                case "Help":
                    menuWindows.help();
                    break;
                case "Toggle Full Screen":
                    if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                        setSize(lastDimension);
                        setLocation(lastLocation);
                    }
                    else {
                        lastDimension = getSize();
                        lastLocation = getLocation();
                        setExtendedState(JFrame.MAXIMIZED_BOTH);
                    }
                    break;
                case "Toggle Sidebar":
                    requestPanel.setVisible(!requestPanel.isVisible());
                    requestSettingPanel.setPreferredSize(new Dimension(getWidth()/2, getHeight()));
                    if (requestPanel.isVisible())
                        responsePanel.setPreferredSize(new Dimension(350, getHeight()));
                    else
                        responsePanel.setPreferredSize(new Dimension(getWidth()/2, getHeight()));
                    break;
            }
        }
    }

    /**
     * Handle the exit operation of this window. It depends on
     * whether the window should hide in system tray or the program should end when the
     * quit option is selected.
     */
    private void handleExit() {
        TrayIcon icon;
        SystemTray systemTray;
        StreamUtils.saveSettings(this);
        requestPanel.saveAllRequests();
        if (hideInTray) {
            if (SystemTray.isSupported()) {
                systemTray = SystemTray.getSystemTray();
                BufferedImage image = null;
                try {
                    image = ImageIO.read((getClass().getResource("icon/Insomnia logo.png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                PopupMenu menu = new PopupMenu();
                MenuItem closeItem = new MenuItem("Close");
                closeItem.addActionListener(e -> System.exit(0));
                menu.add(closeItem);
                MenuItem openItem = new MenuItem("Open");
                openItem.addActionListener(e -> {
                    setVisible(true);
                    setExtendedState(NORMAL);
                });
                menu.add(openItem);

                icon = new TrayIcon(image, "Insomnia", menu);
                icon.setImageAutoSize(true);
                try {
                    systemTray.add(icon);
                    setVisible(false);
                } catch (AWTException e) {
                    System.err.println("Can't add tray icon.");
                }
            }
        }
        else
            System.exit(0);
    }
}
