package bank.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MiniStatement extends JFrame {
    private JTextField cardField;
    private JPasswordField pinField;
    private JButton fetchBtn, backButton;
    private JTable transactionTable;


    public MiniStatement(String cardNumber, String pin) {
        this();
        cardField.setText(cardNumber);
        cardField.setEditable(false);
        pinField.setText(pin);
        pinField.setEditable(false);
        fetchBtn.doClick();
    }


    public MiniStatement() {
        setTitle("Mini Statement");
        setLayout(null);

        JLabel heading = new JLabel("Enter Card Number & PIN");
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setBounds(140, 20, 300, 30);
        add(heading);

        JLabel cardLabel = new JLabel("Card Number:");
        cardLabel.setBounds(80, 70, 100, 30);
        add(cardLabel);

        cardField = new JTextField();
        cardField.setBounds(180, 70, 200, 30);
        add(cardField);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setBounds(80, 110, 100, 30);
        add(pinLabel);

        pinField = new JPasswordField();
        pinField.setBounds(180, 110, 200, 30);
        add(pinField);

        fetchBtn = new JButton("Show Statement");
        fetchBtn.setBounds(180, 150, 200, 30);
        add(fetchBtn);

        String[] columns = {"Date", "Type", "Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBounds(50, 200, 450, 150);
        add(scrollPane);

        backButton = new JButton("Back");
        backButton.setBounds(220, 360, 100, 30);
        add(backButton);

        setSize(560, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setVisible(true);

        // Event Listeners
        fetchBtn.addActionListener(e -> validateAndLoad(model));
        backButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        // Enter key flow
        cardField.addActionListener(e -> pinField.requestFocus());
        pinField.addActionListener(e -> fetchBtn.doClick());
    }

    private void validateAndLoad(DefaultTableModel model) {
        String cardNumber = cardField.getText().trim();
        String pin = new String(pinField.getPassword()).trim();

        if (cardNumber.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter card number and PIN.");
            return;
        }

        // Verify card and PIN
        String query = "SELECT * FROM logggins WHERE card_number = ? AND pin = ?";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smartbanksystem", "root", "Parmarsatyam09@#");
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, cardNumber);
            pst.setString(2, pin);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {

                loadTransactions(model, pin);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid card number or PIN.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTransactions(DefaultTableModel model, String pin) {
        model.setRowCount(0);

        String query = "SELECT date, type, amount FROM bankss WHERE pin = ? ORDER BY date DESC LIMIT 10";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smartbanksystem", "root", "Parmarsatyam09@#");
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, pin);
            ResultSet rs = pst.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                model.addRow(new Object[]{
                        rs.getString("date"),
                        rs.getString("type"),
                        "₹" + rs.getString("amount")
                });
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No transactions found.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        new MiniStatement("123456789012", "1234");
    }
}
