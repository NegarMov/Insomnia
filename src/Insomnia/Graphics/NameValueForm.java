package Insomnia.Graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;

public class NameValueForm extends JPanel {

    private JPanel firstPair; // First pair which is not editable, it's used to add new pair
    private MainWindow mainWindow; // The main window which has interaction with this panel
    private ArrayList<JPanel> pairs; // The least of the pair in this panel

    public NameValueForm(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        pairs = new ArrayList<>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        firstPair();
        add(firstPair);
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
            setFontAndColor(this, firstPair, firstPair.getComponent(0), firstPair.getComponent(1));
            for (JPanel panel : pairs)
                setFontAndColor(panel, panel.getComponent(2), panel.getComponent(0), panel.getComponent(1)
                        , ((JPanel) panel.getComponent(2)).getComponent(0), ((JPanel) panel.getComponent(2)).getComponent(1));
            revalidate();
        }
        else {
            setFontAndColor(this, firstPair.getComponent(0), firstPair.getComponent(1));
            for (JPanel panel : pairs)
                setFontAndColor(panel, panel.getComponent(2), panel.getComponent(0), panel.getComponent(1)
                        , ((JPanel) panel.getComponent(2)).getComponent(0), ((JPanel) panel.getComponent(2)).getComponent(1));
            revalidate();
        }
    }

    /**
     * Create a new empty pair with a name field, value field, a checkbox to
     * determine if the pair is active or not and a button to delete it.
     * @return An empty pair.
     */
    private JPanel emptyPair() {
        JPanel pair = new JPanel();
        pair.setAlignmentX(LEFT_ALIGNMENT);
        pair.setLayout(new BorderLayout());
        pair.setMaximumSize(new Dimension(350, 35));
        JTextField name = new HintTextField("name");
        name.setPreferredSize(new Dimension(150, 35));
        JTextField value = new HintTextField("value");
        value.setPreferredSize(new Dimension(150, 35));

        JButton delete = new JButton();
        delete.setPreferredSize(new Dimension(17, 17));
        delete.setIcon(new ImageIcon(getClass().getResource("icon/Trash bin.jpg")));
        delete.addActionListener(e -> deletePair((JPanel) (((JButton) e.getSource()).getParent()).getParent()));
        JCheckBox checkBox = new JCheckBox("");
        checkBox.setSelected(true);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(delete); buttonsPanel.add(checkBox);

        setFontAndColor(pair, name, value, buttonsPanel, checkBox, delete);

        pair.add(name, BorderLayout.WEST);
        pair.add(value, BorderLayout.CENTER);
        pair.add(buttonsPanel, BorderLayout.EAST);
        return pair;
    }

    /**
     * Do the initial settings of the send panel at the top of the request
     * settings panel.
     */
    private void firstPair() {
        firstPair = new JPanel();
        firstPair.setLayout(new BorderLayout());
        firstPair.setAlignmentX(LEFT_ALIGNMENT);
        firstPair.setMaximumSize(new Dimension(297, 35));
        JTextField name = new HintTextField("New name");
        name.setPreferredSize(new Dimension(150, 35));
        JTextField value = new HintTextField("New value");
        value.setPreferredSize(new Dimension(150, 35));

        setFontAndColor(firstPair, name, value);

        firstPair.add(name, BorderLayout.WEST);
        firstPair.add(value, BorderLayout.CENTER);
    }

    /**
     * Delete the given pair from the pairs list.
     * @param pair the pair to delete from the list.
     */
    private void deletePair(JPanel pair) {
        if (pairs.indexOf(pair) == pairs.size()-1)
            pairs.get(0).getComponent(0).requestFocus();
        remove(pair);
        pairs.remove(pair);
        revalidate();
        mainWindow.repaint();
    }

    /**
     * Add a new pair with the given name and value.
     * @param name The name of the new pair.
     * @param value The value of the new pair.
     */
    private void addNewPair(String name, String value) {
        JPanel newPair = emptyPair();
        ((JTextField) newPair.getComponent(0)).setText(name);
        ((JTextField) newPair.getComponent(1)).setText(value);
        pairs.add(newPair);
        add(newPair);
        add(firstPair);
        revalidate();
    }

    /**
     * Get a HashMap of all the none-empty name values in this form.
     * @return A HashMap of all the none-empty name values in this form.
     */
    public HashMap<String, String> getPairs() {
        HashMap<String, String> pairsValue =  new HashMap<>();
        for (JPanel pair : pairs) {
            JCheckBox checkbox = ((JCheckBox) ((JPanel) pair.getComponent(2)).getComponent(1));
            if (checkbox.isSelected()) {
                String name = ((JTextField) pair.getComponent(0)).getText();
                String value = ((JTextField) pair.getComponent(1)).getText();
                if (!name.equals("") || !value.equals(""))
                    pairsValue.put(name, value);
            }
        }
        return pairsValue;
    }

    /**
     * Delete all the current pairs and add new ones to it.
     * @param defaultPairs The new pairs to be replaced with the current pairs.
     */
    public void updatePairs(HashMap<String, String> defaultPairs) {
        removeAll();
        pairs.clear();
        for (String name : defaultPairs.keySet())
            addNewPair(name, defaultPairs.get(name));
        add(firstPair);
        revalidate(); repaint();
    }

    /**
     * This class extends JTextField so it has a hint text initially when nothing
     * is typed in it. It also manages what should happen when this text field
     * receives focus.
     */
    class HintTextField extends JTextField implements FocusListener {

        private final String hint;
        private boolean showingHint;

        public HintTextField(final String hint) {
            super(hint);
            this.hint = hint;
            this.showingHint = true;
            super.addFocusListener(this);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (this.getText().isEmpty()) {
                super.setText("");
                showingHint = false;
                if (hint.equals("New name") || hint.equals("New value")) {
                    NameValueForm.this.remove(pairs.size());
                    JPanel newPair = emptyPair();
                    pairs.add(newPair);
                    NameValueForm.this.add(newPair);
                    NameValueForm.this.add(firstPair);
                    if (hint.equals("New name"))
                        pairs.get(pairs.size()-1).getComponent(0).requestFocus();
                    else
                        pairs.get(pairs.size()-1).getComponent(1).requestFocus();
                    NameValueForm.this.revalidate();
                }
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
