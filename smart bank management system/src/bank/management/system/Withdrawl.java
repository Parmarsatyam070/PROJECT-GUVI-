package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Withdrawl extends JFrame implements ActionListener {

    private JTextField amountField;
    private JButton withdrawButton, backButton;
    private String pin;

    public Withdrawl(String pin) {
        this.pin = pin;

        setTitle("Withdraw Money");
        setLayout(null);

        JLabel heading = new JLabel("Withdraw Funds");
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBounds(200, 40, 300, 30);
        add(heading);

        JLabel amountLabel = new JLabel("Enter Amount:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        amountLabel.setBounds(100, 120, 150, 30);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));
        amountField.setBounds(250, 120, 200, 30);
        add(amountField);

        withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(250, 180, 100, 30);
        withdrawButton.setBackground(Color.BLACK);
        withdrawButton.setForeground(Color.WHITE);
        withdrawButton.addActionListener(this);
        add(withdrawButton);

        backButton = new JButton("Back");
        backButton.setBounds(360, 180, 90, 30);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);

        getContentPane().setBackground(Color.WHITE);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);


        amountField.addActionListener(e -> withdrawButton.doClick());
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == withdrawButton) {

            String enteredPin = JOptionPane.showInputDialog(this, "Please enter your PIN to confirm withdrawal:", "PIN Confirmation", JOptionPane.PLAIN_MESSAGE);

            if (enteredPin == null) {

                return;
            }

            if (!enteredPin.equals(pin)) {
                JOptionPane.showMessageDialog(this, "Incorrect PIN entered. Withdrawal cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String amountText = amountField.getText().trim();

            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int withdrawAmount;
            try {
                withdrawAmount = Integer.parseInt(amountText);
                if (withdrawAmount <= 0) {
                    JOptionPane.showMessageDialog(this, "Enter a valid positive amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a numeric amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/smartbanksystem", "root", "Parmarsatyam09@#");


                String query = "SELECT type, amount FROM bankss WHERE pin = ?";
                ps = conn.prepareStatement(query);
                ps.setString(1, pin);
                rs = ps.executeQuery();

                int balance = 0;
                while (rs.next()) {
                    String type = rs.getString("type");
                    int amt = rs.getInt("amount");
                    if (type.equalsIgnoreCase("Deposit")) {
                        balance += amt;
                    } else if (type.equalsIgnoreCase("Withdraw")) {
                        balance -= amt;
                    }
                }

                if (withdrawAmount > balance) {
                    JOptionPane.showMessageDialog(this, "Insufficient Balance.", "Transaction Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                String insertQuery = "INSERT INTO bankss(pin, date, type, amount) VALUES (?, NOW(), ?, ?)";
                ps.close();
                ps = conn.prepareStatement(insertQuery);
                ps.setString(1, pin);
                ps.setString(2, "Withdraw");
                ps.setInt(3, withdrawAmount);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "₹" + withdrawAmount + " withdrawn successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

                amountField.setText("");

            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                try { if (rs != null) rs.close(); } catch (Exception e) {}
                try { if (ps != null) ps.close(); } catch (Exception e) {}
                try { if (conn != null) conn.close(); } catch (Exception e) {}
            }

        } else if (ae.getSource() == backButton) {
            this.setVisible(false);

        }
    }

    public static void main(String[] args) {
        new Withdrawl("1234");
    }
}
