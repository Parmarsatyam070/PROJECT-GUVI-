package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Transactions extends JFrame implements ActionListener {
    private JButton depositButton, withdrawButton, fastCashButton, miniStatementButton;
    private JButton pinChangeButton, balanceEnquiryButton, exitButton;
    private String pin, cardNumber;

    public Transactions(String pin) {
        if (pin == null || pin.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Invalid PIN provided. Please login again.",
                    "Invalid PIN",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Empty or null PIN passed to Transactions screen.");
            dispose();
            return;
        }

        this.pin = pin;
        fetchCardNumberFromDB(pin);

        setTitle("Transaction Menu");
        setLayout(null);

        JLabel heading = new JLabel("Please Select Your Transaction");
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBounds(100, 30, 400, 30);
        add(heading);

        depositButton = createButton("Deposit", 100, 100);
        withdrawButton = createButton("Withdraw", 300, 100);
        fastCashButton = createButton("Fast Cash", 100, 160);
        miniStatementButton = createButton("Mini Statement", 300, 160);
        pinChangeButton = createButton("PIN Change", 100, 220);
        balanceEnquiryButton = createButton("Balance Enquiry", 300, 220);
        exitButton = createButton("Exit", 200, 280);

        setupEnterKeyNavigation();

        getContentPane().setBackground(new Color(245, 245, 245));
        setSize(550, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fetchCardNumberFromDB(String pin) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/smartbanksystem", "root", "Parmarsatyam09@#");
             PreparedStatement pst = conn.prepareStatement(
                     "SELECT card_number FROM logggins WHERE pin = ?")) {
            pst.setString(1, pin);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                cardNumber = rs.getString("card_number");
            } else {
                JOptionPane.showMessageDialog(this, "No matching card found for given PIN.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private JButton createButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 140, 40);
        btn.setBackground(new Color(0, 102, 204));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.addActionListener(this);
        add(btn);
        return btn;
    }

    private void setupEnterKeyNavigation() {
        JButton[] buttons = {
                depositButton,
                withdrawButton,
                fastCashButton,
                miniStatementButton,
                pinChangeButton,
                balanceEnquiryButton,
                exitButton
        };

        for (int i = 0; i < buttons.length; i++) {
            JButton currentButton = buttons[i];
            int nextIndex = (i + 1) % buttons.length;

            InputMap im = currentButton.getInputMap(JComponent.WHEN_FOCUSED);
            ActionMap am = currentButton.getActionMap();

            String enterPressed = "enterPressed";
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), enterPressed);

            am.put(enterPressed, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentButton == exitButton) {
                        currentButton.doClick();
                    } else {
                        buttons[nextIndex].requestFocusInWindow();
                    }
                }
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            Object src = ae.getSource();

            if (src == depositButton) {
                navigateTo(new Deposit(pin));
            } else if (src == withdrawButton) {
                navigateTo(new Withdrawl(pin));
            } else if (src == fastCashButton) {
                navigateTo(new FastCash(pin));
            } else if (src == miniStatementButton) {
                new MiniStatement(cardNumber, pin);
            } else if (src == pinChangeButton) {
                navigateTo(new PinChange(pin));
            } else if (src == balanceEnquiryButton) {
                navigateTo(new BalanceEnquiry(pin));
            } else if (src == exitButton) {
                int confirm = JOptionPane.showConfirmDialog(
                        this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unknown action source detected.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + e.getMessage(),
                    "Transaction Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void navigateTo(JFrame frame) {
        if (frame != null) {
            setVisible(false);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Navigation failed: Target screen is unavailable.",
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Transactions("1234")); // Test PIN
    }
}
