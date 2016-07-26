import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alex on 26/07/2016.
 */
@SuppressWarnings("unchecked")
class ClientView extends JFrame {
    private static final int NEW_CLIENT = 1;
    private static final int EDIT_CLIENT = 2;
    private static final int NEW_EMPLOYEE = 3;
    private static final int REMOVE_EMPLOYEE = 4;

    private final JFrame thisFrame;
    private final EmpFilter empFilter = new EmpFilter();
    private final LinkedList<Employee> empFilterList;
    private final String[] tableColumns = {"Last Name", "First Name", "Policy Manager"};
    private final JTable table;
    private final MyTableModel tableModel;
    private final TableRowSorter<MyTableModel> sorter;
    private final JTextField filterText;
    private final Controller controller = new Controller();
    private final ClientModel model = new ClientModel();
    private JLabel label;
    private JCheckBox checkBox;
    private JPanel leftPanel;
    private List<RowFilter<MyTableModel, Object>> filterList;
    private ArrayList<Object[]> data;
    private JButton button;
    private int row;
    private JDialog window;
    private JTextField lNameText;
    private JTextField fNameText;
    private JComboBox empList;
    private String fName;
    private String lName;
    private String emp;
    private Object[] eCData;

    public ClientView() {
        showErr(model.getErr());
        setLayout(new BorderLayout());

        // Building Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenu fileNewMenu = new JMenu("New");
        fileNewMenu.setMnemonic(KeyEvent.VK_N);
        fileMenu.add(fileNewMenu);
        JMenuItem menuItem = new JMenuItem("Client");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(controller);
        menuItem.setActionCommand("newClientWindow");
        fileNewMenu.add(menuItem);
        menuItem = new JMenuItem("Employee");
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(controller);
        menuItem.setActionCommand("newEmpWindow");
        fileNewMenu.add(menuItem);
        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.addActionListener(controller);
        menuItem.setActionCommand("exit");
        fileMenu.add(menuItem);
        menuBar.add(fileMenu);
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        menuItem = new JMenuItem("Remove Employee");
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(controller);
        menuItem.setActionCommand("removeEmpWindow");
        editMenu.add(menuItem);
        menuBar.add(editMenu);
        this.setJMenuBar(menuBar);
        // End of Menu Stuff

        // Building Employee List
        leftPanel = buildLeftPanel();
        add(leftPanel, BorderLayout.WEST);
        // End of Employee Stuff

        // Building Table
        data = model.getData();
        tableModel = new MyTableModel();
        sorter = new TableRowSorter<MyTableModel>(tableModel);
        table = new JTable(tableModel);
        table.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent m) {
                row = table.rowAtPoint(m.getPoint());
                setValues(row);
                if (m.getButton() == MouseEvent.BUTTON3) {
                    table.setRowSelectionInterval(row, row);
                    JPopupMenu pMenu = createPopUp();
                    pMenu.show(table, m.getX(), m.getY());
                } else if (m.getClickCount() == 2) {
                    String url = model.createURL(new Object[]{lName, fName,
                            emp}, ClientModel.CLIENT);
                    try {
                        Runtime.getRuntime().exec(
                                "explorer.exe \"" + url + "\"");
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null,
                                "Unable to open location");
                    }
                }
            }
        });
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        filterText = new JTextField();
        filterText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                newFilter();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                newFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                newFilter();
            }
        });
        empFilterList = new LinkedList<Employee>();
        filterList = new ArrayList<RowFilter<MyTableModel, Object>>();
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel tmp = new JPanel();
        tmp.setLayout(new BoxLayout(tmp, BoxLayout.LINE_AXIS));
        label = new JLabel(" Filter: ");
        tmp.add(new JSeparator(SwingConstants.VERTICAL));
        tmp.add(label);
        tmp.add(filterText);
        // tmp.setBorder(BorderFactory.createLoweredBevelBorder());
        JPanel center = new JPanel(new BorderLayout());
        center.add(tmp, BorderLayout.SOUTH);
        center.add(scrollPane, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        // Done Table Stuff
        thisFrame = this;
    }

    private JPanel buildLeftPanel() {
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        label = new JLabel(" Policy Managers:");
        leftPanel.add(label);
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        leftPanel.add(separator);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        checkBox = new JCheckBox("Unassigned");
        checkBox.addItemListener(empFilter);
        checkBox.setActionCommand("^\\s*$");
        leftPanel.add(checkBox);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        for (int i = 0; i < model.employees.size(); i++) {
            checkBox = new JCheckBox(model.employees.get(i));
            checkBox.addItemListener(empFilter);
            checkBox.setActionCommand("(?i)" + model.employees.get(i) + "(?-i)");
            leftPanel.add(checkBox);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        checkBox = new JCheckBox("Dead");
        checkBox.addItemListener(empFilter);
        checkBox.setActionCommand("(?i)Dead(?-i)");
        leftPanel.add(checkBox);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        button = new JButton("Fetch Client Info");
        button.addActionListener(controller);
        button.setActionCommand("fetchData");
        leftPanel.add(button);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return leftPanel;
    }

    private void setValues(int row) {
        lName = (String) table.getValueAt(row, 0);
        fName = (String) table.getValueAt(row, 1);
        emp = (String) table.getValueAt(row, 2);
    }

    private JPopupMenu createPopUp() {
        JPopupMenu pMenu = new JPopupMenu();
        JMenuItem menu = new JMenuItem("Edit");
        menu.setActionCommand("editClientWindow");
        menu.addActionListener(controller);
        pMenu.add(menu);
        return pMenu;
    }

    private JDialog createEmployeeWindow(int type) {
        window = new JDialog(this, "Default");
        window.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        window.setSize(200, 80);
        window.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        fNameText = new JTextField();
        fNameText.setColumns(15);
        empList = new JComboBox(model.getEmployees().toArray());
        label = new JLabel("Name: ");

        JButton button = new JButton();
        button.addActionListener(controller);
        button.setAlignmentX(CENTER_ALIGNMENT);

        JPanel panel = new JPanel();
        panel.add(label);

        switch (type) {
            case NEW_EMPLOYEE:
                window.setTitle("Create Employee");
                button.setText("Create");
                button.setActionCommand("createEmployee");
                panel.add(fNameText);
                mainPanel.add(panel);
                mainPanel.add(button);
                window.add(mainPanel);
                break;
            case REMOVE_EMPLOYEE:
                window.setTitle("Remove Employee");
                button.setText("Remove");
                button.setActionCommand("removeEmployee");
                panel.add(empList);
                mainPanel.add(panel);
                mainPanel.add(button);
                window.add(mainPanel);
                break;
        }
        return window;
    }

    private JDialog createClientWindow(int type) {

        JPanel tmpPanel = new JPanel();
        tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.PAGE_AXIS));
        JPanel panel = new JPanel();
        JLabel label = new JLabel("First Name:");
        fNameText = new JTextField();
        fNameText.setColumns(20);
        panel.add(label);
        panel.add(fNameText);
        panel.setAlignmentX(RIGHT_ALIGNMENT);
        tmpPanel.add(panel);
        tmpPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel = new JPanel();
        label = new JLabel("Last Name:");
        lNameText = new JTextField();
        lNameText.setColumns(20);
        panel.add(label);
        panel.add(lNameText);
        panel.setAlignmentX(RIGHT_ALIGNMENT);
        tmpPanel.add(panel);
        tmpPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel = new JPanel();
        label = new JLabel("Policy Manager:");
        empList = new JComboBox(model.employees.toArray());
        empList.insertItemAt("None", 0);
        empList.setSelectedIndex(0);
        panel.add(label);
        panel.add(empList);
        panel.setAlignmentX(RIGHT_ALIGNMENT);
        tmpPanel.add(panel);
        tmpPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel = new JPanel();

        switch (type) {
            case NEW_CLIENT:
                checkBox = new JCheckBox("Commercial");
                checkBox.addActionListener(controller);
                checkBox.setActionCommand("commercial");
                panel.add(checkBox);
                checkBox = new JCheckBox("Farm");
                checkBox.addActionListener(controller);
                checkBox.setActionCommand("farm");
                panel.add(checkBox);
                checkBox = new JCheckBox("Hab");
                checkBox.addActionListener(controller);
                checkBox.setActionCommand("hab");
                panel.add(checkBox);
                checkBox = new JCheckBox("Life");
                checkBox.addActionListener(controller);
                checkBox.setActionCommand("life");
                panel.add(checkBox);
                window = new JDialog(this, "Create Client");
                button = new JButton("Create");
                button.setActionCommand("createClient");
                break;
            case EDIT_CLIENT:

                eCData = new Object[]{lName, fName, emp};
                window = new JDialog(this, "Edit Client");
                button = new JButton("Edit");
                button.setActionCommand("editClient");
                fNameText.setText(fName);
                fNameText.setEditable(false);
                lNameText.setText(lName);
                lNameText.setEditable(false);
                int found = 0;

                if (emp == null || emp.isEmpty()) {
                    empList.setSelectedIndex(0);
                } else {
                    for (int i = 0; i < model.employees.size(); i++) {
                        if (emp.equals(empList.getItemAt(i + 1))) {
                            empList.setSelectedIndex(i + 1);
                            found = 1;
                        }
                    }
                    if (found == 0) {
                        empList.setSelectedIndex(0);
                    }
                }
                checkBox = new JCheckBox("Commercial");
                checkBox.addActionListener(controller);
                checkBox.setActionCommand("commercial");
                if (new File(model.createURL(eCData, ClientModel.BUSI)).exists()) {
                    checkBox.setSelected(true);
                    checkBox.setEnabled(false);
                }
                panel.add(checkBox);
                checkBox = new JCheckBox("Farm");
                checkBox.addActionListener(controller);
                checkBox.setActionCommand("farm");
                if (new File(model.createURL(eCData, ClientModel.FARM)).exists()) {
                    checkBox.setSelected(true);
                    checkBox.setEnabled(false);
                }
                panel.add(checkBox);
                checkBox = new JCheckBox("Hab");
                checkBox.addActionListener(controller);
                checkBox.setActionCommand("hab");
                if (new File(model.createURL(eCData, ClientModel.HAB)).exists()) {
                    checkBox.setSelected(true);
                    checkBox.setEnabled(false);
                }
                panel.add(checkBox);
                checkBox = new JCheckBox("Life");
                checkBox.addActionListener(controller);
                checkBox.setActionCommand("life");
                if (new File(model.createURL(eCData, ClientModel.LIFE)).exists()) {
                    checkBox.setSelected(true);
                    checkBox.setEnabled(false);
                }
                panel.add(checkBox);
                break;
        }
        panel.setAlignmentX(RIGHT_ALIGNMENT);
        tmpPanel.add(panel);
        panel = new JPanel();
        button.addActionListener(controller);
        panel.add(button);
        panel.setAlignmentX(RIGHT_ALIGNMENT);
        tmpPanel.add(panel);
        window.add(tmpPanel, BorderLayout.WEST);
        window.setSize(250, 230);
        window.setResizable(false);
        window.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        return window;
    }

    private void newFilter() {
        RowFilter<MyTableModel, Object> rf;
        // If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter("(?i)" + filterText.getText() + "(?-i)");
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        ArrayList<RowFilter<MyTableModel, Object>> tmp = new ArrayList<RowFilter<MyTableModel, Object>>();
        tmp.add(RowFilter.orFilter(filterList));
        tmp.add(rf);
        if (filterList.isEmpty())
            sorter.setRowFilter(rf);
        else
            sorter.setRowFilter(RowFilter.andFilter(tmp));
    }

    private int showErr(int err) {

        switch (err) {
            case ClientModel.NO_ERR:
                break;
            case ClientModel.CFG_NOT_FOUND:
                JOptionPane.showMessageDialog(this, "config.xml not found!");
                System.exit(0);
            case ClientModel.CLIENT_EXISTS:
                JOptionPane.showMessageDialog(this, "Client Already Exists!");
                break;
            case ClientModel.CLIENT_NOT_EXISTS:
                JOptionPane.showMessageDialog(this, "Client Does Not Exist!");
                break;
            case ClientModel.INVALID_CFG:
                JOptionPane.showMessageDialog(this,
                        "Something is wrong with config.xml!");
                System.exit(0);
            case ClientModel.POLICY_EXISTS:
                JOptionPane.showMessageDialog(this,
                        "Policy Already Exists For Client!");
                break;
            case ClientModel.RENAME_FAIL:
                JOptionPane.showMessageDialog(this, "Could Not Rename Client!");
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown Error!");
                break;
        }
        return err;
    }

    private class MyTableModel extends AbstractTableModel {
        @Override
        public int getColumnCount() {
            return tableColumns.length;
        }

        public String getColumnName(int col) {
            return tableColumns[col];
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public Object getValueAt(int row, int col) {

            return data.get(row)[col];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            Object[] tmp = data.get(row);
            tmp[col] = value;
            data.set(row, tmp);
            fireTableCellUpdated(row, col);
        }

        public void addRow(Object[] row) {
            data.add(row);
            fireTableDataChanged();
        }

    }

    private class Employee {
        final String empName;
        final RowFilter<MyTableModel, Object> filter;

        public Employee(String name, RowFilter<MyTableModel, Object> rf) {
            empName = name;
            filter = rf;
        }

        public String getName() {
            return empName;
        }

        public RowFilter<MyTableModel, Object> getFilter() {
            return filter;
        }
    }

    private class EmpFilter implements ItemListener {
        String empName;
        RowFilter<MyTableModel, Object> filter;

        @Override
        public void itemStateChanged(ItemEvent e) {
            empName = ((JCheckBox) (e.getSource())).getActionCommand();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                filter = RowFilter.regexFilter(empName, 2);
                empFilterList.add(new Employee(empName, filter));
            } else {
                for (int i = 0; i < empFilterList.size(); i++) {
                    if (empFilterList.get(i).getName().equals(empName)) {
                        empFilterList.remove(i);
                    }
                }
            }
            filterList = new ArrayList<RowFilter<MyTableModel, Object>>();
            for (Employee anEmpFilterList : empFilterList) {
                filterList.add(anEmpFilterList.getFilter());
            }
            newFilter();
        }
    }

    private class Controller implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("exit")) {
                System.exit(0);
            }
            if (e.getActionCommand().equals("newClientWindow")) {
                JDialog window = createClientWindow(NEW_CLIENT);
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            }
            if (e.getActionCommand().equals("editClientWindow")) {
                JDialog window = createClientWindow(EDIT_CLIENT);
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            }
            if (e.getActionCommand().equals("newEmpWindow")) {
                JDialog window = createEmployeeWindow(NEW_EMPLOYEE);
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            }
            if (e.getActionCommand().equals("removeEmpWindow")) {
                JDialog window = createEmployeeWindow(REMOVE_EMPLOYEE);
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            }
            if (e.getActionCommand().equals("fetchData")) {
                model.fetchClientData();
                data = model.getData();
                tableModel.fireTableDataChanged();
            }
            if (e.getActionCommand().equals("hab"))
                model.toggleHab();
            if (e.getActionCommand().equals("farm"))
                model.toggleFarm();
            if (e.getActionCommand().equals("commercial"))
                model.toggleBusi();
            if (e.getActionCommand().equals("life"))
                model.toggleLife();
            if (e.getActionCommand().equals("createEmployee")) {
                if (fNameText.getText().contains("_") || fNameText.getText().contains(",")) {
                    JOptionPane.showMessageDialog(window,
                            "Invalid Name!\nPlease don't use _ or ,");
                    return;
                }
                if (fNameText.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(window,
                            "Please enter a name!");
                    return;
                }
                model.addEmployee(fNameText.getText());
                thisFrame.remove(leftPanel);
                leftPanel = buildLeftPanel();
                thisFrame.add(leftPanel, BorderLayout.WEST);
                thisFrame.validate();
                thisFrame.repaint();
                window.dispose();
            }
            if (e.getActionCommand().equals("removeEmployee")) {
                model.removeEmployee((String) empList.getSelectedItem());
                thisFrame.remove(leftPanel);
                leftPanel = buildLeftPanel();
                thisFrame.add(leftPanel, BorderLayout.WEST);
                thisFrame.validate();
                thisFrame.repaint();
                window.dispose();
            }
            if (e.getActionCommand().equals("editClient")) {
                model.setCData(eCData);
                if (empList.getSelectedItem().equals(eCData[2])) {
                    if (showErr(model.add()) == 0) {
                        window.dispose();
                        model.resetNewClient();
                    }
                } else {
                    String nEmp;
                    if ((nEmp = (String) empList.getSelectedItem())
                            .equals("None"))
                        nEmp = "";
                    if (showErr(model.changeEmp(nEmp)) == 0) {
                        table.setValueAt(nEmp, row, 2);
                        eCData[2] = nEmp;
                        model.setCData(eCData);
                        if (showErr(model.add()) == 0) {
                            window.dispose();
                            model.resetNewClient();
                        }
                    }
                }
            }
            if (e.getActionCommand().equals("createClient")) {
                String fName;
                String lName;
                String emp;
                if (fNameText.getText().contains("_")) {
                    JOptionPane.showMessageDialog(window,
                            "Invalid First Name!\nPlease don't use _");
                    return;
                }
                if (lNameText.getText().contains("_")
                        || lNameText.getText().contains(",")) {
                    JOptionPane.showMessageDialog(window,
                            "Invalid Last Name!\nPlease don't use _ or ,");
                    return;
                }
                if (lNameText.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(window,
                            "Please Enter a Last Name!");
                    return;
                }
                fName = fNameText.getText();
                lName = lNameText.getText();
                emp = (String) empList.getSelectedItem();
                if (emp.equals("None"))
                    emp = "";

                model.setCData(new Object[]{lName, fName, emp});

                if (showErr(model.createClient()) == 0) {
                    fName = fNameText.getText();
                    lName = lNameText.getText();
                    tableModel.addRow(new Object[]{lName, fName, emp});
                    model.resetNewClient();
                    window.dispose();
                }

            }
        }
    }
}
