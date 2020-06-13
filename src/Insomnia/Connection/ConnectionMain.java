package Insomnia.Connection;

import Insomnia.Graphics.MainWindow;
import javax.swing.*;

/**
 * The class ConnectionMain is a class to work parallel with the GUI part and manage
 * the new created request.
 *
 * @author Negar Movaghatian
 */
public class ConnectionMain extends SwingWorker {

    /**
     * Create a new Connection main.
     * @param mainWindow The main window which this class gets information from.
     */
    public ConnectionMain(MainWindow mainWindow) {
        RequestManager.setMainWindow(mainWindow);
    }

    /**
     * Run a new request via RequestManager.
     */
    @Override
    protected Object doInBackground() {
        RequestManager.runInGUI();
        return null;
    }

    @Override
    protected void done() {
        super.done();
    }
}
