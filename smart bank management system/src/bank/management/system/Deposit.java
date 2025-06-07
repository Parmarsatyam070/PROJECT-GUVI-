package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Deposit extends JFrame implements ActionListener {

    private JTextField amountField;
    private JButton depositBtn, backBtn;
    private String pin;

    public Deposit(String pin) {

        if (pin == null || pin.trim().isEmpty()) {
            this.pin = JOptionPane.showInputDialog(this, "Please enter your PIN for verification:", "Enter PIN", JOptionPane.PLAIN_MESSAGE);
            if (this.pin == null || this.pin.trim().isEmpty()) {

                JOptionPane.showMessageDialog(this, "PIN is required to proceed.", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }
        } else {
            this.pin = pin;
        }

        setTitle("Deposit");
        setLayout(null);

        JLabel heading = new JLabel("Deposit Money");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setForeground(Color.WHITE);
        heading.setBounds(140, 30, 200, 30);
        add(heading);

        JLabel label = new JLabel("Enter Amount:");
        label.setForeground(Color.WHITE);
        label.setBounds(80, 100, 150, 30);
        add(label);

        amountField = new JTextField();
        amountField.setBounds(200, 100, 180, 30);
        add(amountField);

        depositBtn = new JButton("Deposit");
        depositBtn.setBounds(100, 160, 120, 30);
        depositBtn.addActionListener(this);
        add(depositBtn);

        backBtn = new JButton("Back");
        backBtn.setBounds(240, 160, 120, 30);
        backBtn.addActionListener(this);
        add(backBtn);

        getContentPane().setBackground(new Color(30, 30, 40));
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);


        amountField.addActionListener(e -> depositBtn.doClick());
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getSource() == depositBtn) {
                handleDeposit();
            } else if (ae.getSource() == backBtn) {
                handleBack();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleDeposit() {
        String amountStr = amountField.getText().trim();

        if (amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (amount <= 0) {
            JOptionPane.showMessageDialog(this, "Please enter a positive amount.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smartbanksystem", "root", "Parmarsatyam09@#");
             PreparedStatement pst = conn.prepareStatement(
                     "INSERT INTO bankss(pin, date, type, amount) VALUES (?, NOW(), ?, ?)")) {

            pst.setString(1, pin);
            pst.setString(2, "Deposit");
            pst.setDouble(3, amount);

            int rows = pst.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Rs. " + amount + " deposited successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);
                new main_class(pin).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Deposit failed. Please try again.", "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBack() {
        setVisible(false);
        new main_class(pin).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Deposit(null));
    }
}
