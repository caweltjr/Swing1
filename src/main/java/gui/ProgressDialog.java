package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ProgressDialog extends JDialog {
    private JButton cancelButton;
    private JProgressBar progressBar;
    private ProgressDialogListener progressDialogListener;

    @Override
    public void setVisible(final boolean visible) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() { // this code below will keep the p.b.visible until it completes
                if(!visible){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println("Thread threw InterruptedException");
                    }
                    ProgressDialog.super.setVisible(visible);
                }else{
                    progressBar.setValue(0);
                }
                if(visible){
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                }else{
                    setCursor(Cursor.getDefaultCursor());
                }
                ProgressDialog.super.setVisible(visible);
            }
        });
    }

    public ProgressDialog(Window parent, String title) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(progressDialogListener != null){
                    progressDialogListener.progressDialogCanceled();
                }
            }
        });
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Retrieving Messages...");
        progressBar.setMaximum(10);
//        progressBar.setIndeterminate(true); will just show the bar going back and forth

        setLayout(new FlowLayout());
        Dimension size = cancelButton.getPreferredSize();
        size.width = 400;
        progressBar.setPreferredSize(size);
        add(progressBar);
        add(cancelButton);
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);// ***IMPORTANT-how to stop the X button
                                                                // on the upper right corner from working
        addWindowListener(new WindowAdapter() {              // have to add this listener to get it to act
                                                            // like the Cancel button
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if(progressDialogListener != null){
                    progressDialogListener.progressDialogCanceled();
                }
            }
        });
    }
    public void setMaximum(int count){
        progressBar.setMaximum(count);
    }
    public void setValue(int value){
        int progress = 100 * value/progressBar.getMaximum();
        progressBar.setString(String.format("%d%% Complete", progress));
        progressBar.setValue(value);
    }
    public void setListener(ProgressDialogListener listener){
        progressDialogListener = listener;
    }
}
