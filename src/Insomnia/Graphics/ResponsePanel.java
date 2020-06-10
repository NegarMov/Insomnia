package Insomnia.Graphics;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    private JPanel rawDataPanel;
    private JPanel previewPanel;


    /**
     * Create a new panel which contains the information about responses
     * of the sent request.
     * @param mainWindow The main window which has interaction with this panel.
     */
    public ResponsePanel(MainWindow mainWindow) {
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(370, 700));
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
            setFontAndColor(tab, headerPanel, bodyPanel, statusBar, headerTable, rawDataPanel,
                    headerTable.getTableHeader(), rawDataPanel.getComponent(0), previewPanel);
            tableScrollPane.getViewport().setBackground(Color.WHITE);
            headerTable.getTableHeader().setOpaque(false);
            headerTable.getTableHeader().setBackground(Color.WHITE);
            headerPanel.revalidate();
        }
        else {
            setFontAndColor(tab, headerPanel, bodyPanel, statusBar, headerTable, rawDataPanel,
                    headerTable.getTableHeader(), rawDataPanel.getComponent(0), previewPanel);
            tableScrollPane.getViewport().setBackground(Color.DARK_GRAY);
            headerTable.getTableHeader().setOpaque(false);
            headerTable.getTableHeader().setBackground(Color.DARK_GRAY);
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
        initiatePreview();
        initiateRawData();
        bodyPanel();
        tab.addTab("Body", bodyPanel);
        tab.setTabComponentAt(0, body);
        tab.addTab("Header", headerPanel);

        body.addActionListener(e -> {
            if (body.getSelectedIndex() == 0) {
                bodyPanel.removeAll();
                bodyPanel.add(new JScrollPane(rawDataPanel), BorderLayout.CENTER);
                bodyPanel.repaint();
            }
            else {
                bodyPanel.removeAll();
                bodyPanel.add(new JScrollPane(previewPanel), BorderLayout.CENTER);
                bodyPanel.repaint();
            }
        });

        setFontAndColor(tab, body);
        add(tab, BorderLayout.CENTER);
    }

    private void initiatePreview() {
        previewPanel = new JPanel();
    }

    private void initiateRawData() {
        rawDataPanel = new JPanel();
        JTextArea rawData = new JTextArea();
        rawData.setEditable(false);
        rawDataPanel.add(rawData);
        setFontAndColor(rawData, rawDataPanel);
    }

    public void setRawData(String rawData) {
        ((JTextArea) rawDataPanel.getComponent(0)).setText(rawData);
    }

    public void setPreview(byte[] response) {
        ByteArrayInputStream stream = new ByteArrayInputStream(response);
        try {
            previewPanel = new ImagePanel(ImageIO.read(stream));
        } catch (IOException e) {
            System.err.println("Could not display image: " + e.getMessage());
        }
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
        status.setForeground(Color.DARK_GRAY);
        status.setHorizontalAlignment(0);
        Border border = status.getBorder();
        Border margin = new EmptyBorder(6,10,7,10);
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
        headerTable = new JTable();
        headerTable.setAlignmentX(LEFT_ALIGNMENT);
        headerTable.setRowHeight(30);
        headerTable.setFillsViewportHeight(true);
        tableScrollPane = new JScrollPane(headerTable);
        headerPanel.add(tableScrollPane);
        headerPanel.add(initiateCopyButton());
    }

    /**
     * Update the headers' table according to the response's headers.
     * @param headers The list of the response's headers.
     */
    public void setHeaderValues(HashMap<String, String> headers) {
        String[][] headerArray = new String[headers.size()][2];
        int i = 0;
        for (String name : headers.keySet()) {
            headerArray[i][0] = name;
            headerArray[i++][1] = headers.get(name);
        }
        Object[] columnNames = {"name", "value"};
        DefaultTableModel model = new DefaultTableModel(headerArray, columnNames) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        headerTable.setModel(model);
        headerTable.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
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
        bodyPanel.setLayout(new BorderLayout());
        bodyPanel.add(new JScrollPane(rawDataPanel));
    }

    private class ImagePanel extends JPanel{

        private BufferedImage image;

        public ImagePanel(BufferedImage image) {
            this.image = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);
        }

    }

}
