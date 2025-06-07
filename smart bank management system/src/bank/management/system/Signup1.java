package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;
import java.util.regex.Pattern;

public class Signup1 extends JFrame implements ActionListener {
    JTextField nameField, fnameField, emailField, addressField, cityField, pinField, stateField;
    JRadioButton male, female, other;
    JComboBox<String> maritalBox;
    JFormattedTextField dobField;
    JButton nextBtn;
    String formNo;

    public Signup1() {
        setTitle("Sign Up - Page 1");
        setLayout(null);

        formNo = "" + Math.abs(new Random().nextLong() % 9000 + 1000);

        JLabel formTitle = new JLabel("Application Form No. " + formNo);
        formTitle.setFont(new Font("Arial", Font.BOLD, 20));
        formTitle.setBounds(100, 30, 400, 30);
        formTitle.setForeground(Color.WHITE);
        add(formTitle);

        addLabel("Name:", 60);
        nameField = addField(60);

        addLabel("Father's Name:", 100);
        fnameField = addField(100);

        addLabel("DOB (YYYY-MM-DD):", 140);
        dobField = new JFormattedTextField();
        dobField.setBounds(200, 140, 200, 25);
        add(dobField);

        addLabel("Gender:", 180);
        male = new JRadioButton("Male");
        female = new JRadioButton("Female");
        other = new JRadioButton("Other");
        male.setBounds(200, 180, 70, 25);
        female.setBounds(280, 180, 80, 25);
        other.setBounds(360, 180, 70, 25);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        genderGroup.add(other);
        add(male);
        add(female);
        add(other);

        addLabel("Email:", 220);
        emailField = addField(220);

        addLabel("Marital Status:", 260);
        maritalBox = new JComboBox<>(new String[]{"Single", "Married", "Other"});
        maritalBox.setBounds(200, 260, 200, 25);
        add(maritalBox);

        addLabel("Address:", 300);
        addressField = addField(300);

        addLabel("City:", 340);
        cityField = addField(340);

        addLabel("State:", 380);
        stateField = addField(380);

        addLabel("PIN Code:", 420);
        pinField = addField(420);

        nextBtn = new JButton("Next");
        nextBtn.setBounds(200, 470, 100, 30);
        nextBtn.addActionListener(this);
        add(nextBtn);

  
        nameField.addActionListener(e -> fnameField.requestFocus());
        fnameField.addActionListener(e -> dobField.requestFocus());
        dobField.addActionListener(e -> emailField.requestFocus());
        emailField.addActionListener(e -> maritalBox.requestFocus());
        maritalBox.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addressField.requestFocus();
                }
            }
        });
        addressField.addActionListener(e -> cityField.requestFocus());
        cityField.addActionListener(e -> stateField.requestFocus());
        stateField.addActionListener(e -> pinField.requestFocus());
        pinField.addActionListener(e -> nextBtn.doClick());

        getContentPane().setBackground(new Color(30, 30, 40));
        setSize(500, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void addLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setBounds(50, y, 150, 25);
        add(label);
    }

    private JTextField addField(int y) {
        JTextField field = new JTextField();
        field.setBounds(200, y, 200, 25);
        add(field);
        return field;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidDate(String date) {
        String dateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
        return Pattern.matches(dateRegex, date);
    }

    private boolean isValidPin(String pin) {
        return pin.matches("\\d{6}");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextBtn) {
            String name = nameField.getText().trim();
            String fname = fnameField.getText().trim();
            String dob = dobField.getText().trim();
            String gender = male.isSelected() ? "Male" : (female.isSelected() ? "Female" : other.isSelected() ? "Other" : "");
            String email = emailField.getText().trim();
            String marital = (String) maritalBox.getSelectedItem();
            String address = addressField.getText().trim();
            String city = cityField.getText().trim();
            String pin = pinField.getText().trim();
            String state = stateField.getText().trim();

            if (name.isEmpty() || fname.isEmpty() || dob.isEmpty() || gender.isEmpty() || email.isEmpty()
                    || marital.isEmpty() || address.isEmpty() || city.isEmpty() || pin.isEmpty() || state.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all the fields.");
                return;
            }

            if (!isValidDate(dob)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid date in YYYY-MM-DD format.");
                return;
            }

            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
                return;
            }

            if (!isValidPin(pin)) {
                JOptionPane.showMessageDialog(this, "PIN code must be exactly 6 digits.");
                return;
            }

            String query = "INSERT INTO signupp1 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/smartbanksystem", "root", "Parmarsatyam09@#");
                 PreparedStatement pst = conn.prepareStatement(query)) {

                pst.setString(1, formNo);
                pst.setString(2, name);
                pst.setString(3, fname);
                pst.setString(4, dob);
                pst.setString(5, gender);
                pst.setString(6, email);
                pst.setString(7, marital);
                pst.setString(8, address);
                pst.setString(9, city);
                pst.setString(10, pin);
                pst.setString(11, state);

                int rowsInserted = pst.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Data saved successfully!");
                    setVisible(false);
                    new Signup2(formNo).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save data. Please try again.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new Signup1();
    }
}
