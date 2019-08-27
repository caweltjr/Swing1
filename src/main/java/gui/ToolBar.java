package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolBar extends JToolBar implements ActionListener {
    private JButton saveBtn;
    private JButton refreshBtn;
    private ToolbarListener toolbarListener;

    public ToolBar(){
        // comment out setBorder if you want the toolbar to be draggable
        setBorder(BorderFactory.createLoweredBevelBorder());
        // uncomment if you don't want the toolbar to be draggable
//        setFloatable(false);
        saveBtn = new JButton("");
        saveBtn.setIcon(Utils.createImage("/src/main/java/images/Save16.gif"));
        saveBtn.setToolTipText("Save to Database");
        refreshBtn = new JButton("");
        refreshBtn.setIcon(Utils.createImage("/src/main/java/images/Refresh16.gif"));
        refreshBtn.setToolTipText("Load From Database");

        saveBtn.addActionListener(this);
        refreshBtn.addActionListener(this);

        add(saveBtn);
        add(refreshBtn);
    }
    public void setToolbarListener(ToolbarListener listener){
        this.toolbarListener = listener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clicked = (JButton)e.getSource();
        if(clicked == saveBtn){
            if(toolbarListener != null) {
                toolbarListener.saveEventOccurred();
            }
        }
        else if(clicked == refreshBtn){
            if(toolbarListener != null) {
                toolbarListener.refreshEventOccurred();
            }
        }
    }

}
