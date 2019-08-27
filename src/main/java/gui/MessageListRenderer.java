package gui;

import model.Message;

import javax.swing.*;
import java.awt.*;

/*
 * Note -- this demonstrates using a arbitrary component as a list box renderer.
 * (Probably overkill in this case to use JPanel when JLabel could be used directly)
 */

public class MessageListRenderer implements ListCellRenderer {

    private JPanel panel;
    private JLabel label;

    private Color selectedColor;
    private Color normalColor;

    public MessageListRenderer() {
        panel = new JPanel();
        label = new JLabel();
        label.setFont(Utils.createFont("/src/main/java/fonts/CrimewaveBB.ttf").deriveFont(Font.PLAIN, 22));

        selectedColor = new Color(210, 210, 255);
        normalColor = Color.white;

        label.setIcon(Utils.createImage("/src/main/java/images/Information24.gif"));

        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        panel.add(label);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        Message message = (Message)value;
        label.setText(message.getTitle());
        panel.setBackground(cellHasFocus ? selectedColor: normalColor);

        return panel;
    }

}
