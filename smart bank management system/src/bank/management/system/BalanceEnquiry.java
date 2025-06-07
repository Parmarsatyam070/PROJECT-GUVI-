package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BalanceEnquiry extends JFrame implements ActionListener {

    private JButton backButton;
    private JLabel balanceLabel;
    private JPasswordField pinInputField;
    private String pinNumber;

    public BalanceEnquiry(String pinNumber) {
        this.pinNumber = pinNumber;

        setTitle("Balance Enquiry");
        setLayout(null);

        JLabel heading = new JLabel("Enter Your PIN to Check Balance");
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setBounds(120, 40, 350, 30);
        add(heading);

        pinInputField = new JPasswordField();
        pinInputField.setBounds(180, 90, 180, 30);
        add(pinInputField);

        balanceLabel = new JLabel("");
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        balanceLabel.setBounds(100, 140, 400, 30);
        add(balanceLabel);

        backButton = new JButton("Back");
        backButton.setBounds(220, 190, 120, 35);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.addActionListener(this);
        add(backButton);

        getContentPane().setBackground(Color.WHITE);
        setSize(550, 300);
        setLocationRelativeTo(null);
        setVisible(true);


        if (pinNumber != null && !pinNumber.trim().isEmpty()) {
            pinInputField.setText(pinNumber);
            showBalance();
        }


        pinInputField.addActionListener(e -> {
            this.pinNumber = new String(pinInputField.getPassword()).trim();
            if (this.pinNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your PIN.", "Input Required", JOptionPane.WARNING_MESSAGE);
                balanceLabel.setText("");
                return;
            }
            showBalance();
        });
    }

    private void showBalance() {
        if (pinNumber == null || pinNumber.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "PIN not provided.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double balance = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smartbanksystem", "root", "Parmarsatyam09@#");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM bankss WHERE pin = '" + pinNumber + "'");

            boolean found = false;
            while (rs.next()) {
                found = true;
                String type = rs.getString("type");
                double amt = Double.parseDouble(rs.getString("amount"));
                if (type.equalsIgnoreCase("Deposit")) {
                    balance += amt;
                } else if (type.equalsIgnoreCase("Withdraw") || type.equalsIgnoreCase("Withdrawal")) {
                    balance -= amt;
                }
            }

            if (found) {
                balanceLabel.setText("Available Balance: ₹" + String.format("%.2f", balance));
            } else {
                balanceLabel.setText("No transactions found for this PIN.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        setVisible(false);
        new Transactions(pinNumber).setVisible(true);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BalanceEnquiry(null));
    }
}
