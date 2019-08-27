package gui;

import model.EmploymentCategory;
import model.Person;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TablePanel extends JPanel {

    private JTable table;
    private PersonTableModel tableModel;
    private JPopupMenu popup;
    private PersonTableListener personTableListener;

    public TablePanel() {

        tableModel = new PersonTableModel();
        table = new JTable(tableModel);
        table.setShowGrid(true);
        popup = new JPopupMenu();
        table.setDefaultRenderer(EmploymentCategory.class, new EmploymentCategoryRenderer());
        table.setDefaultEditor(EmploymentCategory.class, new EmploymentCategoryEditor());
        table.setRowHeight(25); // increase the height of the rows of the table
        JMenuItem removeItem = new JMenuItem("Delete Row");
        popup.add(removeItem);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                table.getSelectionModel().setSelectionInterval(row,row);
                super.mousePressed(e);
                if (e.getButton() == MouseEvent.BUTTON3) { // right mouse button
                    popup.show(table, e.getX(), e.getY());
                }
            }
        });
        removeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if(personTableListener != null){
                    personTableListener.rowDeleted(row);
                    tableModel.fireTableRowsDeleted(row,row);
                }
            }
        });
        setLayout(new BorderLayout());
        initializeHeaderAlignment();
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void setData(List<Person> db) {
        tableModel.setData(db);
    }

    public void refresh() {
        tableModel.fireTableDataChanged();
    }

    public void setPersonTableListener(PersonTableListener personTableListener) {
        this.personTableListener = personTableListener;
    }
    private void initializeHeaderAlignment() {
        DefaultTableCellRenderer headerCellRenderer = new DefaultTableCellRenderer();
        headerCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        this.table.getTableHeader().setDefaultRenderer(headerCellRenderer);
    }
}
