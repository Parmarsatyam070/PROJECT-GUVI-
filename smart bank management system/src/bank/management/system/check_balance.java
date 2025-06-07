package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class check_balance extends JFrame implements ActionListener {

    private final String pin;
    private JButton backBtn;
    private JLabel balanceLabel;

    public check_balance(String pin) {
        this.pin = pin;

        setTitle("Check Account Balance");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(null);


        JLabel heading = new JLabel("Your Current Balance");
        heading.setBounds(100, 30, 250, 25);
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        add(heading);


        balanceLabel = new JLabel("Fetching...");
        balanceLabel.setBounds(100, 70, 250, 25);
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(balanceLabel);


        backBtn = new JButton("Back");
        backBtn.setBounds(140, 130, 100, 30);
        backBtn.addActionListener(this);
        add(backBtn);


        backBtn.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    backBtn.doClick();
                }
            }
        });


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        check_balance.this,
                        "Are you sure you want to exit?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });

        getContentPane().setBackground(new Color(230, 240, 255));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

       
        getBalanceFromDB();

        setVisible(true);
    }

    private void getBalanceFromDB() {
        String url = "jdbc:mysql://localhost:3306/smartbanksystem";
        String user = "root";
        String pass = "Parmarsatyam09@#";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String queryCard = "SELECT card_number FROM logggins WHERE pin = ?";
            try (PreparedStatement pstCard = conn.prepareStatement(queryCard)) {
                pstCard.setString(1, pin);
                try (ResultSet rsCard = pstCard.executeQuery()) {
                    if (!rsCard.next()) {
                        balanceLabel.setText("Invalid PIN. Please login again.");
                        return;
                    }

                    String cardNumber = rsCard.getString("card_number");
                    if (cardNumber == null || cardNumber.isEmpty()) {
                        balanceLabel.setText("Card number not found.");
                        return;
                    }

                    String queryBalance = "SELECT balance FROM balances WHERE card_number = ?";
                    try (PreparedStatement pstBal = conn.prepareStatement(queryBalance)) {
                        pstBal.setString(1, cardNumber);
                        try (ResultSet rsBal = pstBal.executeQuery()) {
                            if (rsBal.next()) {
                                String balance = rsBal.getString("balance");
                                if (balance == null) balance = "0";
                                balanceLabel.setText("₹ " + balance);
                            } else {
                                balanceLabel.setText("No balance record found.");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            balanceLabel.setText("Database error. Please try again later.");
            JOptionPane.showMessageDialog(this,
                    "Error connecting to database:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backBtn) {
            setVisible(false);
            new main_class(pin).setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new check_balance("1234"));
    }
}
