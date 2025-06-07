package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class main_class extends JFrame implements ActionListener {
    String pin;
    JButton depositBtn, withdrawBtn, fastCashBtn, miniStatementBtn;
    JButton balanceEnquiryBtn, pinChangeBtn, transactionsBtn, exitBtn;

    public main_class(String pin) {
        if (pin == null || pin.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid PIN. Please login again.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            new Login().setVisible(true);
            dispose();
            return;
        }

        this.pin = pin;
        setTitle("Smart Bank Dashboard");
        setLayout(new GridLayout(4, 2, 15, 15));
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        depositBtn = createButton("Deposit");
        withdrawBtn = createButton("Withdraw");
        fastCashBtn = createButton("Fast Cash");
        balanceEnquiryBtn = createButton("Balance Enquiry");
        miniStatementBtn = createButton("Mini Statement");
        transactionsBtn = createButton("Transactions");
        pinChangeBtn = createButton("PIN Change");
        exitBtn = createButton("Exit");

        add(depositBtn);
        add(withdrawBtn);
        add(fastCashBtn);
        add(balanceEnquiryBtn);
        add(miniStatementBtn);
        add(transactionsBtn);
        add(pinChangeBtn);
        add(exitBtn);

        getContentPane().setBackground(Color.WHITE);
        setVisible(true);
    }

    private JButton createButton(String title) {
        JButton button = new JButton(title);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.addActionListener(this);
        return button;
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == depositBtn) {
            new Deposit(pin).setVisible(true);
        } else if (src == withdrawBtn) {
            new Withdrawl(pin).setVisible(true);
        } else if (src == fastCashBtn) {
            new FastCash(pin).setVisible(true);
        } else if (src == balanceEnquiryBtn) {
            new BalanceEnquiry(pin).setVisible(true);
        } else if (src == miniStatementBtn) {
            String cardNumber = getCardNumber(pin);
            if (cardNumber != null) {
                new MiniStatement(cardNumber, pin).setVisible(true);
            }
        } else if (src == transactionsBtn) {
            new Transactions(pin).setVisible(true);
        } else if (src == pinChangeBtn) {
            new PinChange(pin).setVisible(true);
        }
            else if (src == balanceEnquiryBtn) {
                new check_balance(pin).setVisible(true);
            }

        else if (src == exitBtn) {
            int confirm = JOptionPane.showConfirmDialog(this, "Exit application?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    private String getCardNumber(String pin) {
        String cardNumber = null;
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smartbanksystem", "root", "Parmarsatyam09@#");
             PreparedStatement pst = conn.prepareStatement("SELECT card_number FROM logggins WHERE pin = ?")) {
            pst.setString(1, pin);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                cardNumber = rs.getString("card_number");
            } else {
                JOptionPane.showMessageDialog(this, "Card not found for this PIN.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
        return cardNumber;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new main_class("1234"));
    }
}
