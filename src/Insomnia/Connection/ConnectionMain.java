package Insomnia.Connection;

import Insomnia.Graphics.MainWindow;
import Insomnia.Graphics.RequestSettingPanel;
import Insomnia.Graphics.ResponsePanel;

import javax.swing.*;

public class ConnectionMain extends SwingWorker {

    public ConnectionMain(MainWindow mainWindow, RequestSettingPanel settingPanel, ResponsePanel responsePanel) {
        RequestManager.setMainWindow(mainWindow);
        RequestManager.setSettingPanel(settingPanel);
        RequestManager.setResponsePanel(responsePanel);
    }

    @Override
    protected Object doInBackground() throws Exception {
        RequestManager.runInGUI();
        return null;
    }

    @Override
    protected void done() {
        super.done();
    }
}
