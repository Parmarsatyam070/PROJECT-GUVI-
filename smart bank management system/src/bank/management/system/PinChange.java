package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PinChange extends JFrame implements ActionListener {
    private JPasswordField newPinField, reenterPinField;
    private JButton changeButton, backButton;
    private String pin;

    public PinChange(String pin) {
        this.pin = pin;

        setTitle("Change PIN");
        setLayout(null);

        JLabel heading = new JLabel("Change Your PIN");
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBounds(180, 40, 300, 30);
        add(heading);

        JLabel newPinLabel = new JLabel("New PIN:");
        newPinLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        newPinLabel.setBounds(100, 120, 150, 30);
        add(newPinLabel);

        newPinField = new JPasswordField();
        newPinField.setFont(new Font("Arial", Font.PLAIN, 16));
        newPinField.setBounds(250, 120, 200, 30);
        add(newPinField);

        JLabel reenterPinLabel = new JLabel("Re-Enter New PIN:");
        reenterPinLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        reenterPinLabel.setBounds(100, 170, 180, 30);
        add(reenterPinLabel);

        reenterPinField = new JPasswordField();
        reenterPinField.setFont(new Font("Arial", Font.PLAIN, 16));
        reenterPinField.setBounds(250, 170, 200, 30);
        add(reenterPinField);

        changeButton = new JButton("Change PIN");
        changeButton.setBounds(250, 230, 120, 35);
        changeButton.setBackground(new Color(0, 128, 0));
        changeButton.setForeground(Color.WHITE);
        changeButton.addActionListener(this);
        add(changeButton);

        backButton = new JButton("Back");
        backButton.setBounds(380, 230, 70, 35);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);


        newPinField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    reenterPinField.requestFocus();
                }
            }
        });

        reenterPinField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    changeButton.doClick();
                }
            }
        });

        getContentPane().setBackground(Color.WHITE);
        setSize(600, 350);
        setLocation(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == changeButton) {
            changeButton.setEnabled(false);

            String newPin = new String(newPinField.getPassword()).trim();
            String reenterPin = new String(reenterPinField.getPassword()).trim();

            if (newPin.isEmpty() || reenterPin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter and confirm your new PIN.", "Input Error", JOptionPane.ERROR_MESSAGE);
                changeButton.setEnabled(true);
                return;
            }

            if (!newPin.equals(reenterPin)) {
                JOptionPane.showMessageDialog(this, "Entered PINs do not match.", "Input Error", JOptionPane.ERROR_MESSAGE);
                changeButton.setEnabled(true);
                return;
            }

            if (!newPin.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(this, "PIN must be exactly 4 digits.", "Input Error", JOptionPane.ERROR_MESSAGE);
                changeButton.setEnabled(true);
                return;
            }

            if (newPin.equals(pin)) {
                JOptionPane.showMessageDialog(this, "New PIN cannot be the same as the current PIN.", "Input Error", JOptionPane.ERROR_MESSAGE);
                changeButton.setEnabled(true);
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smartbanksystem", "root", "Parmarsatyam09@#")) {
                conn.setAutoCommit(false);

                String updateBank = "UPDATE bankss SET pin = ? WHERE pin = ?";
                String updateLogin = "UPDATE logggins SET pin = ? WHERE pin = ?";
                String updateSignup3 = "UPDATE signuup3 SET pin = ? WHERE pin = ?";

                try (
                        PreparedStatement pst1 = conn.prepareStatement(updateBank);
                        PreparedStatement pst2 = conn.prepareStatement(updateLogin);
                        PreparedStatement pst3 = conn.prepareStatement(updateSignup3)
                ) {
                    pst1.setString(1, newPin);
                    pst1.setString(2, pin);
                    int updated1 = pst1.executeUpdate();

                    pst2.setString(1, newPin);
                    pst2.setString(2, pin);
                    int updated2 = pst2.executeUpdate();

                    pst3.setString(1, newPin);
                    pst3.setString(2, pin);
                    int updated3 = pst3.executeUpdate();

                    if (updated1 > 0 && updated2 > 0 && updated3 > 0) {
                        conn.commit();
                        JOptionPane.showMessageDialog(this, "PIN changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        setVisible(false);
                        new Transactions(newPin).setVisible(true);
                    } else {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, "PIN change failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                        changeButton.setEnabled(true);
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                changeButton.setEnabled(true);
            }

        } else if (ae.getSource() == backButton) {
            setVisible(false);
            new Transactions(pin).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PinChange("1234"));
    }
}
