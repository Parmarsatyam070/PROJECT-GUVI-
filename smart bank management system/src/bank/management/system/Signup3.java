package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;

public class Signup3 extends JFrame implements ActionListener {

    JRadioButton saving, current, fd, rd;
    JCheckBox atmCard, internetBanking, mobileBanking, alerts, chequeBook, eStatement, declaration;
    JButton submitBtn;
    String formNo;

    public Signup3(String formNo) {
        this.formNo = formNo;
        setTitle("Sign Up - Page 3");
        setLayout(null);

        JLabel title = new JLabel("Account Details");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setBounds(150, 30, 250, 30);
        add(title);

        addLabel("Account Type:", 80);
        saving = new JRadioButton("Saving Account");
        current = new JRadioButton("Current Account");
        fd = new JRadioButton("Fixed Deposit Account");
        rd = new JRadioButton("Recurring Deposit Account");
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(saving);
        typeGroup.add(current);
        typeGroup.add(fd);
        typeGroup.add(rd);

        saving.setBounds(200, 80, 200, 25);
        current.setBounds(200, 110, 200, 25);
        fd.setBounds(200, 140, 250, 25);
        rd.setBounds(200, 170, 250, 25);
        add(saving); add(current); add(fd); add(rd);

        addLabel("Services Required:", 210);
        atmCard = new JCheckBox("ATM Card");
        internetBanking = new JCheckBox("Internet Banking");
        mobileBanking = new JCheckBox("Mobile Banking");
        alerts = new JCheckBox("Email & SMS Alerts");
        chequeBook = new JCheckBox("Cheque Book");
        eStatement = new JCheckBox("E-Statement");
        declaration = new JCheckBox("I hereby declare that the above details are correct.");

        int y = 240;
        JCheckBox[] checkboxes = {atmCard, internetBanking, mobileBanking, alerts, chequeBook, eStatement};
        for (JCheckBox box : checkboxes) {
            box.setForeground(Color.WHITE);
            box.setBounds(200, y, 250, 25);
            add(box);
            y += 30;
        }

        declaration.setForeground(Color.WHITE);
        declaration.setBounds(50, y + 10, 400, 25);
        add(declaration);

        submitBtn = new JButton("Submit");
        submitBtn.setBounds(200, y + 50, 100, 30);
        submitBtn.addActionListener(this);
        add(submitBtn);

        getContentPane().setBackground(new Color(30, 30, 40));
        setSize(500, y + 140);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Enter Key Navigation Setup:
        saving.addKeyListener(new EnterKeyAdapter(current));
        current.addKeyListener(new EnterKeyAdapter(fd));
        fd.addKeyListener(new EnterKeyAdapter(rd));
        rd.addKeyListener(new EnterKeyAdapter(atmCard));

        atmCard.addKeyListener(new EnterKeyAdapter(internetBanking));
        internetBanking.addKeyListener(new EnterKeyAdapter(mobileBanking));
        mobileBanking.addKeyListener(new EnterKeyAdapter(alerts));
        alerts.addKeyListener(new EnterKeyAdapter(chequeBook));
        chequeBook.addKeyListener(new EnterKeyAdapter(eStatement));
        eStatement.addKeyListener(new EnterKeyAdapter(declaration));
        declaration.addKeyListener(new EnterKeyAdapter(submitBtn));
    }

    private class EnterKeyAdapter extends KeyAdapter {
        Component next;
        EnterKeyAdapter(Component next) {
            this.next = next;
        }
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                next.requestFocus();
            }
        }
    }

    private void addLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setBounds(50, y, 150, 25);
        add(label);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!declaration.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please agree to the declaration.");
            return;
        }

        String accountType = null;
        if (saving.isSelected()) accountType = "Saving";
        else if (current.isSelected()) accountType = "Current";
        else if (fd.isSelected()) accountType = "Fixed Deposit";
        else if (rd.isSelected()) accountType = "Recurring Deposit";

        if (accountType == null) {
            JOptionPane.showMessageDialog(this, "Please select an Account Type.");
            return;
        }

        Random rand = new Random();
        String cardNumber = String.valueOf(Math.abs(rand.nextLong()) % 90000000L + 5040936000000000L);
        String pin = String.valueOf(Math.abs(rand.nextInt() % 9000) + 1000);

        StringBuilder servicesBuilder = new StringBuilder();
        if (atmCard.isSelected()) servicesBuilder.append("ATM Card ");
        if (internetBanking.isSelected()) servicesBuilder.append("Internet Banking ");
        if (mobileBanking.isSelected()) servicesBuilder.append("Mobile Banking ");
        if (alerts.isSelected()) servicesBuilder.append("Email & SMS Alerts ");
        if (chequeBook.isSelected()) servicesBuilder.append("Cheque Book ");
        if (eStatement.isSelected()) servicesBuilder.append("E-Statement ");
        String services = servicesBuilder.toString().trim();

        String insertSignup3 = "INSERT INTO signuup3 (form_no, account_type, card_number, pin, services) VALUES (?, ?, ?, ?, ?)";
        String insertLogin = "INSERT INTO logggins (card_number, pin) VALUES (?, ?)";

        try (
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/smartbanksystem", "root", "Parmarsatyam09@#");
                PreparedStatement pstSignup3 = conn.prepareStatement(insertSignup3);
                PreparedStatement pstLogin = conn.prepareStatement(insertLogin);
        ) {
            pstSignup3.setString(1, formNo);
            pstSignup3.setString(2, accountType);
            pstSignup3.setString(3, cardNumber);
            pstSignup3.setString(4, pin);
            pstSignup3.setString(5, services);
            pstSignup3.executeUpdate();

            pstLogin.setString(1, cardNumber);
            pstLogin.setString(2, pin);
            pstLogin.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Account Created Successfully!\nCard Number: " + cardNumber + "\nPIN: " + pin);

            setVisible(false);
            new Deposit(pin).setVisible(true);

        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "This account already exists or card number conflict.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unexpected Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Signup3("1234567890"));
    }
}
