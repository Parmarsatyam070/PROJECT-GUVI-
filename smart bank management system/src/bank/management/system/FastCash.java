package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FastCash extends JFrame implements ActionListener {
    private JButton[] amountButtons;
    private JButton backButton;
    private String pinNumber;
    private final String[] amounts = {"₹100", "₹500", "₹1000", "₹2000", "₹5000", "₹10000"};

    public FastCash(String pinNumber) {
        this.pinNumber = pinNumber;


        String enteredPin = JOptionPane.showInputDialog(this, "Please enter your PIN:", "PIN Verification", JOptionPane.PLAIN_MESSAGE);
        if (enteredPin == null || enteredPin.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "PIN entry is required. Exiting.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        if (!enteredPin.equals(pinNumber)) {
            JOptionPane.showMessageDialog(this, "Incorrect PIN entered. Exiting.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        setTitle("Fast Cash");
        setLayout(null);

        JLabel heading = new JLabel("Select Withdrawal Amount");
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBounds(140, 30, 400, 30);
        add(heading);

        amountButtons = new JButton[amounts.length];
        int x = 100, y = 100;

        for (int i = 0; i < amounts.length; i++) {
            amountButtons[i] = new JButton(amounts[i]);
            amountButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            amountButtons[i].setBounds(x, y, 150, 40);
            amountButtons[i].setBackground(new Color(0, 128, 255));
            amountButtons[i].setForeground(Color.WHITE);
            amountButtons[i].addActionListener(this);


            amountButtons[i].addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        ((JButton)e.getSource()).doClick();
                    }
                }
            });

            add(amountButtons[i]);

            if (i % 2 == 1) {
                x = 100;
                y += 60;
            } else {
                x = 300;
            }
        }

        backButton = new JButton("Back");
        backButton.setBounds(200, y + 60, 150, 40);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);


        backButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    backButton.doClick();
                }
            }
        });

        add(backButton);

        getContentPane().setBackground(Color.WHITE);
        setSize(600, 450);
        setLocation(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();

        if (action.equals("Back")) {

            setVisible(false);
            new Transactions(pinNumber);
            return;
        }

        double withdrawAmount = 0;
        try {

            String numericAmount = action.replaceAll("[^0-9]", "");
            withdrawAmount = Double.parseDouble(numericAmount);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you want to withdraw ₹" + withdrawAmount + "?",
                "Confirm Withdrawal",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }


        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smartbanksystem", "root", "Parmarsatyam09@#")) {

            double balance = 0;
            String balanceQuery = "SELECT type, amount FROM bankss WHERE pin = ?";

            try (PreparedStatement pst = conn.prepareStatement(balanceQuery)) {
                pst.setString(1, pinNumber);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        String type = rs.getString("type");
                        double amt = rs.getDouble("amount");
                        if ("Deposit".equalsIgnoreCase(type)) {
                            balance += amt;
                        } else if ("Withdraw".equalsIgnoreCase(type)) {
                            balance -= amt;
                        }
                    }
                }
            }

            if (withdrawAmount > balance) {
                JOptionPane.showMessageDialog(this, "Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String insertQuery = "INSERT INTO bankss(pin, date, type, amount) VALUES (?, NOW(), ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(insertQuery)) {
                pst.setString(1, pinNumber);
                pst.setString(2, "Withdraw");
                pst.setDouble(3, withdrawAmount);
                int rows = pst.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "₹" + withdrawAmount + " withdrawn successfully.");
                    setVisible(false);
                    new Transactions(pinNumber);
                } else {
                    JOptionPane.showMessageDialog(this, "Withdrawal failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Transaction Failed: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FastCash("1234"));
    }
}
