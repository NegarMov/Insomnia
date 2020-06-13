package Insomnia.Graphics;

import Insomnia.Connection.ConnectionMain;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents the middle panel on the main window which
 * has a send panel at the top(It gets a URL and sends request) and two tabs
 * below it; a Body tab and a Header tab.
 * The class RequestSettingPanel is a child class of JPanel.
 *
 * @author Negar Movaghatian
 * @since 2020.5.8
 */
public class RequestSettingPanel extends JPanel {

    private JPanel sendPanel; // The top panel which
    private JComboBox<String> methodsList; // A box for request methods' options
    private HintTextField URL; // A field for URL
    private MainWindow mainWindow; // The main window which has interaction with this panel
    private JTabbedPane tab; // The tabs of the request setting panel
    private NameValueForm headerPanel; // The panel which includes the list of headers
    private NameValueForm queryPanel; // The panel which includes the list of queries
    private JPanel bodyPanel; // The panel of the body tab
    private JPanel binaryUploadPanel; // The panel of the binary upload
    private NameValueForm formDataPanel; // The panel of the form-data

    /**
     * Create a new Request Setting panel.
     * @param mainWindow the main window which has interaction with this panel.
     */
    public RequestSettingPanel(MainWindow mainWindow) {
        setBackground(Color.LIGHT_GRAY);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(380, 700));
        this.mainWindow = mainWindow;
        initiateSendPanel();
        add(sendPanel, BorderLayout.NORTH);
        initiateTabs();
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
            setFontAndColor(tab, headerPanel, queryPanel, bodyPanel, binaryUploadPanel, formDataPanel);
            headerPanel.setTheme();
            queryPanel.setTheme();
            formDataPanel.setTheme();
        }
        else {
            setFontAndColor(tab, headerPanel, queryPanel, bodyPanel, binaryUploadPanel, formDataPanel);
            headerPanel.setTheme();
            queryPanel.setTheme();
            formDataPanel.setTheme();
        }
    }

    /**
     * Create the send panel at the top of the request setting panel.
     */
    private void initiateSendPanel() {

        // Create send panel
        String[] methods = {"GET", "DELETE", "POST", "PUT"};
        sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());
        sendPanel.setPreferredSize(new Dimension(350, 40));

        // Create send panel components and add them to the send panel
        methodsList = new JComboBox(methods);
        methodsList.setMaximumRowCount(5);
        methodsList.setPreferredSize(new Dimension(75, 40));
        methodsList.addItemListener(e -> {
            if (mainWindow.getRequestPanel() != null)
                mainWindow.getRequestPanel().setFocusedRequestMethod(e.getItem().toString());
        });
        sendPanel.add(methodsList, BorderLayout.WEST);

        URL = new HintTextField("http://apapi.haditabatabaei.ir/docs");
        URL.setPreferredSize(new Dimension(200, 40));
        sendPanel.add(URL, BorderLayout.CENTER);

        JButton send = new JButton("Send");
        send.setPreferredSize(new Dimension(75, 40));
        send.addActionListener(e -> new ConnectionMain(mainWindow).execute());

        sendPanel.add(send, BorderLayout.EAST);

        setFontAndColor(sendPanel, methodsList, URL, send);
    }

    /**
     * Initiate the tabs of this panel. A 'Body' tab which includes body types: 'Form Data',
     * 'JSON' and 'Binary Data' and a 'Header' tab which includes an editable list of headers.
     */
    private void initiateTabs() {
        String[] bodyTypes = {"Form Data", "Binary Data"};
        JComboBox<String> body = new JComboBox(bodyTypes);
        body.setMaximumRowCount(2);
        headerPanel = new NameValueForm(mainWindow);
        queryPanel = new NameValueForm(mainWindow);
        formDataPanel = new NameValueForm(mainWindow);
        initiateBinaryPanel();
        bodyPanel = new JPanel();
        bodyPanel.add(new JScrollPane(formDataPanel));
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        tab = new JTabbedPane();
        tab.addTab("Body", bodyPanel);
        tab.setTabComponentAt(0, body);
        tab.addTab("Header", new JScrollPane(headerPanel));
        tab.addTab("Query", new JScrollPane(queryPanel));

        body.addActionListener(e -> {
            if (body.getSelectedIndex() == 0) {
                bodyPanel.removeAll();
                bodyPanel.add(new JScrollPane(formDataPanel));
                bodyPanel.repaint();
            }
            else {
                bodyPanel.removeAll();
                bodyPanel.add(binaryUploadPanel);
                bodyPanel.repaint();
            }
        });

        setFontAndColor(tab, body);

        add(tab, BorderLayout.CENTER);
    }

    /**
     * Setup the upload binary panel which is a way to upload files using file chooser.
     */
    private void initiateBinaryPanel() {
        binaryUploadPanel = new JPanel();
        binaryUploadPanel.setLayout(null);

        JLabel selectedFileLabel = new JLabel("SELECTED FILE");
        selectedFileLabel.setSize(120, 30); selectedFileLabel.setLocation(10, 20);
        binaryUploadPanel.add(selectedFileLabel);

        JTextField selectedFileField = new JTextField("No file selected");
        selectedFileField.setEditable(false);
        selectedFileField.setSize(340, 30); selectedFileField.setLocation(10, 50);
        binaryUploadPanel.add(selectedFileField);

        JButton resetButton = new JButton("Reset File");
        resetButton.setSize(95, 35); resetButton.setLocation(10, 100);
        binaryUploadPanel.add(resetButton);

        JButton chooseButton = new JButton("Choose File");
        chooseButton.setSize(95, 35); chooseButton.setLocation(120, 100);
        binaryUploadPanel.add(chooseButton);


        resetButton.addActionListener(e -> selectedFileField.setText("No file selected"));
        chooseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedFileField.setText(selectedFile.getAbsolutePath());
            }
        });

        setFontAndColor(selectedFileLabel, selectedFileField, resetButton, chooseButton);
    }

    /**
     * update the properties of this panel.
     * @param method The new method to be shown at the top of this panel.
     * @param URL The URL to be shown at the top of this panel.
     * @param formData The form data to set the form data panel values to.
     * @param headers The headers to set the header panel values to.
     * @param query The query items to set the query panel values to.
     * @param path The binary file path to show in the upload binary panel.
     */
    public void setProperties(String method, String URL, HashMap<String, String> formData,
                              HashMap<String, String> headers, HashMap<String, String> query, String path) {
        methodsList.setSelectedItem(method);

        if (!URL.isEmpty())
            this.URL.setText(URL);
        else
            this.URL.reset();

        if (!path.isEmpty())
            ((JTextField) binaryUploadPanel.getComponent(1)).setText(path);

        formDataPanel.updatePairs(formData);
        headerPanel.updatePairs(headers);
        queryPanel.updatePairs(query);
    }

    /**
     * @return The URL written on this panel.
     */
    public String getURL() {
        return URL.getText();
    }

    /**
     * @return The method selected on this panel.
     */
    public String getMethod() {
        return (String) methodsList.getSelectedItem();
    }

    /**
     * @return The binary file path selected on this panel.
     */
    public String getBinaryFilePath() {
        return ((JTextField) binaryUploadPanel.getComponent(1)).getText();
    }

    /**
     * @return A boolean which shows if the user wants to upload a binary file or not.
     */
    public boolean uploadBinary() {
        return !((JTextField) binaryUploadPanel.getComponent(1)).getText().equals("No file selected");
    }

    /**
     * @return The headers written on this panel.
     */
    public HashMap<String, String> getHeaders() {
        return headerPanel.getPairs();
    }

    /**
     * @return The form data written on this panel.
     */
    public HashMap<String, String> getFormData() {
        return formDataPanel.getPairs();
    }

    /**
     * @return The query items written on this panel.
     */
    public HashMap<String, String> getQueries() {
        return queryPanel.getPairs();
    }

    /**
     * This class extends JTextField so it has a hint text initially when nothing
     * is typed in it. It also manages what should happen when this text field
     * receives focus.
     */
    class HintTextField extends JTextField implements FocusListener {

        private final String hint; // The hint text shown in this field before any value is entered
        private boolean showingHint; // Determines if the hint should be shown now or not

        public HintTextField(final String hint) {
            super(hint);
            this.hint = hint;
            this.showingHint = true;
            super.addFocusListener(this);
        }

        public void reset() {
            setText(hint);
            this.showingHint = true;
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (this.getText().isEmpty()) {
                super.setText("");
                showingHint = false;
            }
        }
        @Override
        public void focusLost(FocusEvent e) {
            if (this.getText().isEmpty()) {
                super.setText(hint);
                showingHint = true;
            }
        }

        @Override
        public String getText() {
            return showingHint ? "" : super.getText();
        }

        @Override
        public void setText(String t) {
            super.setText(t);
            showingHint = false;
        }
    }
}
