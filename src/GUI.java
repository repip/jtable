

import com.mysql.jdbc.ResultSetRow;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * GUI class used to display and interact with information from a MySQL database
 * graphically.
 *
 * @author Casey Scarborough
 * @since 2013-05-15
 * @see Database
 */
public class GUI extends JFrame {

    // Create buttons, labels, text fields, a new JTable, a Font, and the dates
    private JButton addCustomer, removeCustomer;
    private JLabel errorMessage;
    private JTextField tfFirstName, tfLastName, tfPhoneNumber, tfEmailAddress, tfCity, tfState, tfDateRegistered;
    private JTable table;
    private java.util.Date dateDateRegistered, sqlDateRegistered;
    private Font font;
    private Customer c = new Customer();

    /**
     * Constructor for the GUI class.
     *
     * @param db the database object used to manipulate the database.
     */
    public GUI(Database db) {
        super();
        table = new JTable(db.defaultTableModel);

        // If the font is available, set it, if not, use the Serif font
        font = new Font("Sans Serif", Font.PLAIN, 14);

        table.setFont(font);
        table.setRowHeight(table.getRowHeight() + 8);
        table.setAutoCreateRowSorter(true);

        // Create a new mouse listener and assign it to the table
        ListenForMouse mouseListener = new ListenForMouse();
        table.addMouseListener(mouseListener);

        // Create a JScrollPane and add it to the center of the window
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        // Set button values
        addCustomer = new JButton("Add Customer");
        removeCustomer = new JButton("Remove Customer");

        // Add action listeners to the buttons to listen for clicks
        ListenForAction actionListener = new ListenForAction();
        addCustomer.addActionListener(actionListener);
        removeCustomer.addActionListener(actionListener);

        // Set the text field widths and values
        tfFirstName = new JTextField("First Name", 6);
        tfLastName = new JTextField("Last Name", 8);
        tfPhoneNumber = new JTextField("Phone Number", 8);
        tfEmailAddress = new JTextField("Email Address", 14);
        tfCity = new JTextField("City", 8);
        tfState = new JTextField("State", 3);
        tfDateRegistered = new JTextField("Date Registered", 9);

        // Create a focus listener and add it to each text field to remove text when clicked on
        ListenForFocus focusListener = new ListenForFocus();
        tfFirstName.addFocusListener(focusListener);
        tfLastName.addFocusListener(focusListener);
        tfPhoneNumber.addFocusListener(focusListener);
        tfEmailAddress.addFocusListener(focusListener);
        tfCity.addFocusListener(focusListener);
        tfState.addFocusListener(focusListener);
        tfDateRegistered.addFocusListener(focusListener);

        // Create a new panel and add the text fields and add/remove buttons to it
        JPanel inputPanel = new JPanel();
        inputPanel.add(tfFirstName);
        inputPanel.add(tfLastName);
        inputPanel.add(tfPhoneNumber);
        inputPanel.add(tfEmailAddress);
        inputPanel.add(tfCity);
        inputPanel.add(tfState);
        inputPanel.add(tfDateRegistered);

        inputPanel.add(addCustomer);
        inputPanel.add(removeCustomer);

        // Change settings and add the error message to the error panel
        errorMessage = new JLabel("");
        errorMessage.setForeground(Color.red);
        JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        errorPanel.add(errorMessage);

        // set the input panel to the bottom and the error panel to the top
        this.add(inputPanel, BorderLayout.SOUTH);
        this.add(errorPanel, BorderLayout.NORTH);

        // Center the ID column in the table
        DefaultTableCellRenderer centerColumns = new DefaultTableCellRenderer();
        centerColumns.setHorizontalAlignment(JLabel.CENTER);
        TableColumn tc = table.getColumn("ID");
        tc.setCellRenderer(centerColumns);
    }

    /**
     * ActionListener implementation to listen for actions such as button
     * clicks.
     *
     * @author Casey Scarborough
     */
    private class ListenForAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {





            if (e.getSource() == addCustomer) { // If the user clicks Add MainCRUD, add the information into the database
                // Create variables to hold information to be inserted, and get the info from the text fields
                //String firstName, lastName, phoneNumber, emailAddress, city, state, dateRegistered;

                int customerID = 0;
                c.setFirst_Name(tfFirstName.getText());
                c.setLast_Name(tfLastName.getText());
                c.setPhone_Number(tfPhoneNumber.getText());
                c.setEmail_Address(tfEmailAddress.getText());
                c.setCity(tfCity.getText());
                c.setState(tfState.getText());
                String dateRegistered = (tfDateRegistered.getText());



                // Check that the state matches the required format, if not display an error and return
                if (!c.getState().matches("[A-Za-z]{2}")) {
                    errorMessage.setText("A state should be a two-letter abbreviation.");
                    return;
                }

                // Check that the date matches the required format, if not display an error and return
                if (!dateRegistered.matches("(19|20)[0-9][0-9]([-])(0[1-9]|1[012])([-])(0[1-9]|[12][0-9]|3[01])")) {
                    errorMessage.setText("The date should be in the following format: YYYY-MM-DD");
                    return;
                }

                // Convert the date
                c.setDate_Registered((Date) getADate(dateRegistered));

                errorMessage.setText(c.toString());


                try { // Attempt to insert the information into the database
                    MainCRUD.db.rows.moveToInsertRow();
                    MainCRUD.db.rows.updateString("first_name", c.getFirst_Name().toString());
                    MainCRUD.db.rows.updateString("last_name", c.getLast_Name().toString());
                    MainCRUD.db.rows.updateString("phone_number", c.getPhone_Number().toString());
                    MainCRUD.db.rows.updateString("email_address", c.getEmail_Address().toString());
                    MainCRUD.db.rows.updateString("city", c.getCity().toString());
                    MainCRUD.db.rows.updateString("state", c.getState().toString());
                    MainCRUD.db.rows.updateDate("date_registered", c.getDate_Registered());

                    MainCRUD.db.rows.insertRow();
                    MainCRUD.db.rows.updateRow();

                    MainCRUD.db.rows.last();
                    ResultSet r = MainCRUD.db.rows;
                    customerID = MainCRUD.db.rows.getInt(1);
                    Object[] customer = {customerID, r.getString(2), r.getString(3), r.getString(4), r.getString(5), r.getString(6), r.getString(7),r.getDate(8)};
                    MainCRUD.db.defaultTableModel.addRow(customer); // Add the row to the screen
                    errorMessage.setText(""); // Remove the error message if one was displayed
                } catch (Exception e2) { // Catch any exceptions and display appropriate errors
                    System.out.println(e2.getMessage());

                }
            } else if (e.getSource() == removeCustomer) {
                try { // If the user clicked remove customer, delete from database and remove from table
                    MainCRUD.db.defaultTableModel.removeRow(table.getSelectedRow());
                    MainCRUD.db.rows.absolute(table.getSelectedRow());
                    MainCRUD.db.rows.deleteRow();
                } catch (ArrayIndexOutOfBoundsException e1) {
                    System.out.println(e1.getMessage());
                    errorMessage.setText("To delete a customer, you must first select a row.");
                } catch (SQLException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * FocusListener implementation used to listen for JTextFields being focused
     * on.
     *
     */
    private class ListenForFocus implements FocusListener {
        // My terrible and possibly hack-ish way of implementing 'placeholders' in the JTextFields.

        public void focusGained(FocusEvent e) { // If a text field gains focus and has the default text, remove the text
            if (tfFirstName.getText().equals("First Name") && e.getSource() == tfFirstName) {
                tfFirstName.setText("");
            } else if (tfLastName.getText().equals("Last Name") && e.getSource() == tfLastName) {
                tfLastName.setText("");
            } else if (tfPhoneNumber.getText().equals("Phone Number") && e.getSource() == tfPhoneNumber) {
                tfPhoneNumber.setText("");
            } else if (tfEmailAddress.getText().equals("Email Address") && e.getSource() == tfEmailAddress) {
                tfEmailAddress.setText("");
            } else if (tfCity.getText().equals("City") && e.getSource() == tfCity) {
                tfCity.setText("");
            } else if (tfState.getText().equals("State") && e.getSource() == tfState) {
                tfState.setText("");
            } else if (tfDateRegistered.getText().equals("Date Registered") && e.getSource() == tfDateRegistered) {
                tfDateRegistered.setText("");
            }
        }

        public void focusLost(FocusEvent e) { // If the text field loses focus and is blank, set the default text back
            if (tfFirstName.getText().equals("") && e.getSource() == tfFirstName) {
                tfFirstName.setText("First Name");
            } else if (tfLastName.getText().equals("") && e.getSource() == tfLastName) {
                tfLastName.setText("Last Name");
            } else if (tfPhoneNumber.getText().equals("") && e.getSource() == tfPhoneNumber) {
                tfPhoneNumber.setText("Phone Number");
            } else if (tfEmailAddress.getText().equals("") && e.getSource() == tfEmailAddress) {
                tfEmailAddress.setText("Email Address");
            } else if (tfCity.getText().equals("") && e.getSource() == tfCity) {
                tfCity.setText("City");
            } else if (tfState.getText().equals("") && e.getSource() == tfState) {
                tfState.setText("State");
            } else if (tfDateRegistered.getText().equals("") && e.getSource() == tfDateRegistered) {
                tfDateRegistered.setText("Date Registered");
            }
        }
    }

    /**
     * ListenForMouse class that listens for mouse events on cells so that they
     * can be updated.
     *
     *
     */
    private class ListenForMouse extends MouseAdapter {

        public void mouseReleased(MouseEvent mouseEvent) {



            // If the mouse is released and the click was a right click
            if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                // Create a dialog for the user to enter new data
                String value = JOptionPane.showInputDialog(null, "Enter Cell Value:");
                if (value != null) { try {
                    // If they entered info, update the database
                    table.setValueAt(value, table.getSelectedRow(), table.getSelectedColumn());
                    String updateColumn;

                    MainCRUD.db.rows.absolute(table.getSelectedRow() + 1);
                    updateColumn = MainCRUD.db.defaultTableModel.getColumnName(table.getSelectedColumn());
                    switch (updateColumn) {
                        // if the column was date_registered, convert date update using a Date
                        case "Date_Registered":
                            sqlDateRegistered = getADate(value);
                            MainCRUD.db.rows.updateDate(updateColumn, (Date) sqlDateRegistered);
                            MainCRUD.db.rows.updateRow();
                            break;
                        default: // otherwise update using a String
                            MainCRUD.db.rows.updateString(updateColumn, value);
                            MainCRUD.db.rows.updateRow();
                            break;
                    }
                    } catch (SQLException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    /**
     * Method used to set the column widths of the JTable being displayed.
     *
     * @param columns the Object array of column names.
     * @param widths the specified widths to set the columns to.
     */
    public void setColumnWidths(Object[] columns, int... widths) {
        TableColumn column;
        for (int i = 0; i < columns.length; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
        }
    }

    /**
     * Used to set the message on the errorPanel.
     *
     * @param message the message to display.
     */
    public void setErrorMessage(String message) {
        errorMessage.setText(message);
    }

    /**
     * Converts a date into one that can be recorded into the database.
     *
     * @param dateRegistered the date that the user inputs in the Date
     * Registered field.
     * @return the newly converted date.
     */
    public java.util.Date getADate(String dateRegistered) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            dateDateRegistered = dateFormatter.parse(dateRegistered);
            sqlDateRegistered = new java.sql.Date(dateDateRegistered.getTime());
        } catch (ParseException e1) {
            System.out.println(e1.getMessage());
            if (e1.getMessage().toString().startsWith("Unparseable date:")) {
                this.setErrorMessage("The date should be in the following format: YYYY-MM-DD");
            }
        }
        return sqlDateRegistered;
    }
}
