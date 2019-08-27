package gui;

import controller.Controller;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class MainFrame extends JFrame {
    private JButton btn;
    private ToolBar toolBar;
    private FormPanel formPanel;
    private JFileChooser fileChooser;
    private Controller controller;
    private TablePanel tablePanel;
    private PrefsDialog prefsDialog;
    private Preferences prefs;
    private JSplitPane splitPane;
    private JTabbedPane tabbedPane;
    private MessagePanel messagePanel;

    public MainFrame(){

        super("HR Helper");
        setLayout(new BorderLayout());
        toolBar = new ToolBar();
        formPanel = new FormPanel();
        tablePanel = new TablePanel();
        prefsDialog = new PrefsDialog(this);
        prefs = Preferences.userRoot().node("db");
        messagePanel = new MessagePanel(this);
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Person Database", tablePanel);
        tabbedPane.addTab("Messages", messagePanel);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tabbedPane);
        splitPane.setOneTouchExpandable(true);


        setJMenuBar(createMenuBar());
        controller = new Controller();
        tablePanel.setData(controller.getPeople());

        fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new PersonFileFilter());

        tablePanel.setPersonTableListener(new PersonTableListener(){
                public void rowDeleted(int row){
                    controller.removePerson(row);
                }
            }
        );
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = tabbedPane.getSelectedIndex();
                if (index == 1){
                    messagePanel.refresh();
                }
            }
        });
        toolBar.setToolbarListener(new ToolbarListener() {
            @Override
            public void saveEventOccurred() {
                connect();
                try {
                    controller.save();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Could not save to" +
                            " the database.", "Database Save Error", JOptionPane.ERROR);
                }
            }

            @Override
            public void refreshEventOccurred() {
                connect();
                try {
                    controller.load();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Could not load from" +
                            " the database.", "Database Load Error", JOptionPane.ERROR);
                }
                tablePanel.refresh();
            }

        });
        formPanel.setFormListener(new FormListener() {
            @Override
            public void formEventOccurred(FormEvent ev) {
                controller.addPerson(ev);
                tablePanel.refresh();
            }
        });
        prefsDialog.setPrefsListener(new PrefsListener() {
            public void preferencesSet(String user, String password, int port) {
                prefs.put("user", user);
                prefs.put("password", password);
                prefs.putInt("port", port);

                try {
                    controller.configure(port, user, password);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Unable to re-connect to database.", "Database Connection Problem",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        String user = prefs.get("user", "");
        String password = prefs.get("password", "");
        int port = prefs.getInt("port", 3306);
        prefsDialog.setDefaults(user, password, port);

        try {
            controller.configure(port, user, password);
        } catch (Exception e1) {
            // This shouldn't happen -- database is not connected
            System.err.println("Can't connect to database");
        }

//        add(formPanel, BorderLayout.WEST);
        add(toolBar, BorderLayout.PAGE_START);
//        add(tablePanel, BorderLayout.CENTER);
        add(splitPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    controller.disconnect();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                dispose();
                System.gc();
            }
        });
        setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setMinimumSize(new Dimension(600,400));
        setSize(1000, screenHeight/2);
        setLocationRelativeTo(null);// Center the window on the screen with this one line
        setVisible(true);
    }
    private JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem exportData = new JMenuItem("Export Data...");
        exportData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        controller.saveToFile(fileChooser.getSelectedFile());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "Could not save data to file",
                                "Error Saving File",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        JMenuItem importData = new JMenuItem("Import Data...");
        importData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION){
                    try {
                        controller.loadFromFile(fileChooser.getSelectedFile());
                        tablePanel.refresh();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "Could not load data from file",
                                "Error Loading File",
                                 JOptionPane.ERROR_MESSAGE);
                    } catch (ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "Could not load data from file",
                                "Error Loading File",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(exportData);
        fileMenu.add(importData);
        fileMenu.addSeparator();
        fileMenu.add(exit);

        JMenu windowMenu = new JMenu("Window");
        JMenu showMenu = new JMenu("Show");
        JMenuItem prefsItem = new JMenuItem("Preferences...");
        JMenuItem showFormItem = new JCheckBoxMenuItem("Person Form");
        showFormItem.setSelected(true);
        showMenu.add(showFormItem);
        windowMenu.add(showMenu);
        windowMenu.add(prefsItem);

        menuBar.add(fileMenu);
        menuBar.add(windowMenu);

        showFormItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)e.getSource();
                if(menuItem.isSelected()){
                    splitPane.setDividerLocation((int)formPanel.getMinimumSize().getWidth());
                }
                formPanel.setVisible(menuItem.isSelected());
            }
        });
        prefsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prefsDialog.setVisible(true);
            }
        });
        fileMenu.setMnemonic(KeyEvent.VK_F);
        exit.setMnemonic(KeyEvent.VK_X);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int action = JOptionPane.showConfirmDialog(MainFrame.this, "Do You Really Want" +
                        " to Exit?", "Confirm Exit", JOptionPane.OK_CANCEL_OPTION);
                if(action == JOptionPane.OK_OPTION){
                    WindowListener[] wl = getWindowListeners();
                    for(WindowListener listener:wl){
                        listener.windowClosing(new WindowEvent(MainFrame.this,0));
                    }
                }
        }});
        importData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        prefsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        return menuBar;
    }
    private void connect(){
        try {
            controller.connect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MainFrame.this, "Could not connect to" +
                    " the database.", "Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
