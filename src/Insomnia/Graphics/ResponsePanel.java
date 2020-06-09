package Insomnia.Graphics;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;

/**
 * This class represents the right panel on the main window which shows
 * the information about the response of the sent request.
 * The class ResponsePanel is a child class of JPanel.
 *
 * @author Negar Movaghatian
 * @since 2020.5.9
 */
public class ResponsePanel extends JPanel {

    private JTabbedPane tab; // The tabs of this panel, it has a 'Header' and 'Body' tab
    private MainWindow mainWindow; // The main window which has interaction with this panel
    private JPanel headerPanel; // The panel which contains a table of headers
    private JPanel bodyPanel; // The body panel of the response
    private JPanel statusBar; // The status bar at the top of the response panel
    private JTable headerTable; // The table which contains the information about the headers
    private JScrollPane tableScrollPane; // The Scroll Pane which is the container of headers' table


    /**
     * Create a new panel which contains the information about responses
     * of the sent request.
     * @param mainWindow The main window which has interaction with this panel.
     */
    public ResponsePanel(MainWindow mainWindow) {
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(350, 700));
        setLayout(new BorderLayout());
        this.mainWindow = mainWindow;
        initiateTabs();
        add(tab, BorderLayout.CENTER);
        initiateStatusBar();
        add(statusBar, BorderLayout.NORTH);
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
     * Change the theme of this panel and its components to light or dark.
     */
    public void setTheme() {
        if (mainWindow.getTheme().equals("light")) {
            setFontAndColor(tab, headerPanel, bodyPanel, statusBar, headerTable);
            tableScrollPane.getViewport().setBackground(Color.WHITE);
            headerPanel.revalidate();
        }
        else {
            setFontAndColor(tab, headerPanel, bodyPanel, statusBar, headerTable, tableScrollPane);
            tableScrollPane.getViewport().setBackground(Color.DARK_GRAY);
            headerPanel.revalidate();
        }
    }

    /**
     * Initiate the tabs of this panel. It has a 'Body' and a 'Header'
     * tab.
     */
    private void initiateTabs() {
        String[] bodyTypes = {"Raw Data", "Preview"};
        JComboBox<String> body = new JComboBox(bodyTypes);
        body.setMaximumRowCount(3);
        headerPanel();
        tab = new JTabbedPane();
        bodyPanel();
        tab.addTab("Body", bodyPanel);
        tab.setTabComponentAt(0, body);
        tab.addTab("Header", headerPanel);

        setFontAndColor(tab, body);

        add(tab, BorderLayout.CENTER);
    }

    /**
     * Initiate the status bar at the top of this panel which contains the response's
     * 'status message' and 'status code', response duration and the volume of the
     * received data.
     */
    private void initiateStatusBar() {

        // Create status bar
        statusBar = new JPanel();
        statusBar.setPreferredSize(new Dimension(350, 40));
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Create status bar components and add them to the status bar
        JLabel status = new JLabel("ERROR");
        status.setOpaque(true); status.setBackground(Color.RED);
        status.setFont(new Font("Calibri", Font.PLAIN, 13));
        status.setForeground(Color.LIGHT_GRAY);
        status.setHorizontalAlignment(0);
        Border border = status.getBorder();
        Border margin = new EmptyBorder(5,10,5,10);
        status.setBorder(new CompoundBorder(border, margin));
        statusBar.add(status);

        JLabel responseTime = new JLabel("0.00s");
        responseTime.setOpaque(true);
        responseTime.setHorizontalAlignment(0);
        responseTime.setPreferredSize(new Dimension(50, 30));
        statusBar.add(responseTime);

        JLabel dataReceived = new JLabel("0.0B");
        dataReceived.setOpaque(true);
        dataReceived.setHorizontalAlignment(0);
        dataReceived.setPreferredSize(new Dimension(50, 30));
        statusBar.add(dataReceived);

        setFontAndColor(responseTime, dataReceived);
    }

    public void editStatusBar(String statusMessage, String time, String dataReceived) {
        JLabel status = (JLabel) statusBar.getComponent(0);
        JLabel responseTime = (JLabel) statusBar.getComponent(1);
        JLabel volume = (JLabel) statusBar.getComponent(2);
        status.setText(statusMessage);
        char c = statusMessage.charAt(0);
        switch (c) {
            case '2':
                status.setBackground(Color.GREEN);
                break;
            case '3':
                status.setBackground(Color.MAGENTA);
                break;
            case '4':
                status.setBackground(Color.ORANGE);
                break;
            case '5':
                status.setBackground(Color.RED);
                break;
            default:
                status.setBackground(Color.GRAY);
        }
        responseTime.setText(time);
        volume.setText(dataReceived);
    }

    /**
     * Create a new header panel which has a table of headers and their
     * name and values.
     */
    private void headerPanel() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        String[] columnNames = {"name", "value"};

        headerTable = new JTable(new String[][]{{"Date", ""}, {"Cache-Control", ""}, {"Pragma", ""}, {"...", ""}}, columnNames);
        headerTable.setAlignmentX(LEFT_ALIGNMENT);
        headerTable.setRowHeight(30);
        headerTable.setFillsViewportHeight(true);
        tableScrollPane = new JScrollPane(headerTable);
        headerPanel.add(tableScrollPane);
        headerPanel.add(initiateCopyButton());
    }

    public void setHeaderValues(HashMap<String, String> headers) {
        String[][] headerArray = new String[headers.size()][2];
        int i = 0;
        for (String name : headers.keySet()) {
            headerArray[i][0] = name;
            headerArray[i++][1] = headers.get(name);
        }
        String[] columnNames = {"name", "value"};
        headerTable = new JTable(new String[][]{{"akjfh", ""}, {"ghjhgj-ag", ""}, {"asdf", ""}, {"...", ""}}, columnNames);
        System.out.println("HELLO!?!?!");
    }

    /**
     * Get all the names and values of the header table to copy t to clipboard later.
     * @return A String which contains all the headers' name and value.
     */
    private String getHeadersInfo() {
        String info = "";
        for (int i=0; i<headerTable.getRowCount(); i++) {
            info = info.concat(headerTable.getValueAt(i, 0) + "  ");
            info = info.concat(headerTable.getValueAt(i, 1) + "\n");
        }
        return info;
    }

    /**
     * Create a 'Copy To Clipboard' button which copies the contents of header
     * table.
     * @return The 'Copy To Clipboard' JButton.
     */
    private JButton initiateCopyButton() {
        JButton copy = new JButton("Copy to Clipboard");
        copy.setPreferredSize(new Dimension(150, 45));
        copy.setAlignmentX(RIGHT_ALIGNMENT);
        setFontAndColor(copy);
        copy.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection stringSelection = new StringSelection(getHeadersInfo());
            clipboard.setContents(stringSelection, null);
        });
        return copy;
    }

    private void bodyPanel() {
        bodyPanel = new JPanel();
    }

}
