package bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Signup2 extends JFrame implements ActionListener {
    JComboBox<String> religionBox, categoryBox, incomeBox, educationBox, occupationBox;
    JTextField panField, aadharField;
    JRadioButton yesSenior, noSenior, yesAccount, noAccount;
    JButton nextBtn;
    String formNo;

    public Signup2(String formNo) {
        this.formNo = formNo;

        setTitle("Sign Up - Page 2");
        setLayout(null);

        JLabel title = new JLabel("Application Form - Page 2");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(100, 30, 400, 30);
        add(title);

        addLabel("Religion:", 80);
        religionBox = addCombo(new String[]{"Hindu", "Muslim", "Christian", "Sikh", "Other"}, 80);

        addLabel("Category:", 120);
        categoryBox = addCombo(new String[]{"General", "OBC", "SC", "ST", "Other"}, 120);

        addLabel("Income:", 160);
        incomeBox = addCombo(new String[]{"<1 Lakh", "1-5 Lakh", "5-10 Lakh", "10+ Lakh"}, 160);

        addLabel("Education:", 200);
        educationBox = addCombo(new String[]{"High School", "Graduate", "Postgraduate", "PhD", "Other"}, 200);

        addLabel("Occupation:", 240);
        occupationBox = addCombo(new String[]{"Salaried", "Self-Employed", "Business", "Student", "Retired", "Other"}, 240);

        addLabel("PAN No:", 280);
        panField = addField(280);

        addLabel("Aadhar No:", 320);
        aadharField = addField(320);

        addLabel("Senior Citizen:", 360);
        yesSenior = new JRadioButton("Yes");
        noSenior = new JRadioButton("No");
        yesSenior.setBounds(200, 360, 80, 25);
        noSenior.setBounds(290, 360, 80, 25);
        ButtonGroup seniorGroup = new ButtonGroup();
        seniorGroup.add(yesSenior);
        seniorGroup.add(noSenior);
        add(yesSenior);
        add(noSenior);

        addLabel("Existing Account:", 400);
        yesAccount = new JRadioButton("Yes");
        noAccount = new JRadioButton("No");
        yesAccount.setBounds(200, 400, 80, 25);
        noAccount.setBounds(290, 400, 80, 25);
        ButtonGroup accountGroup = new ButtonGroup();
        accountGroup.add(yesAccount);
        accountGroup.add(noAccount);
        add(yesAccount);
        add(noAccount);

        nextBtn = new JButton("Next");
        nextBtn.setBounds(200, 450, 100, 30);
        nextBtn.addActionListener(this);
        add(nextBtn);

        getContentPane().setBackground(new Color(30, 30, 40));
        setSize(500, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 🔽 Enter key focus chain:
        religionBox.addActionListener(e -> categoryBox.requestFocus());
        categoryBox.addActionListener(e -> incomeBox.requestFocus());
        incomeBox.addActionListener(e -> educationBox.requestFocus());
        educationBox.addActionListener(e -> occupationBox.requestFocus());
        occupationBox.addActionListener(e -> panField.requestFocus());
        panField.addActionListener(e -> aadharField.requestFocus());
        aadharField.addActionListener(e -> nextBtn.doClick());

        setVisible(true);
    }

    private void addLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setBounds(50, y, 150, 25);
        add(label);
    }

    private JComboBox<String> addCombo(String[] items, int y) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setBounds(200, y, 200, 25);
        add(box);
        return box;
    }

    private JTextField addField(int y) {
        JTextField field = new JTextField();
        field.setBounds(200, y, 200, 25);
        add(field);
        return field;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextBtn) {
            String religion = (String) religionBox.getSelectedItem();
            String category = (String) categoryBox.getSelectedItem();
            String income = (String) incomeBox.getSelectedItem();
            String education = (String) educationBox.getSelectedItem();
            String occupation = (String) occupationBox.getSelectedItem();
            String pan = panField.getText().trim();
            String aadhar = aadharField.getText().trim();
            String senior = yesSenior.isSelected() ? "Yes" : noSenior.isSelected() ? "No" : "";
            String account = yesAccount.isSelected() ? "Yes" : noAccount.isSelected() ? "No" : "";

            if (pan.isEmpty() || aadhar.isEmpty()) {
                JOptionPane.showMessageDialog(this, "PAN and Aadhar fields cannot be empty.");
                return;
            }

            if (!pan.matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
                JOptionPane.showMessageDialog(this, "Invalid PAN format. Example: ABCDE1234F");
                return;
            }

            if (!aadhar.matches("\\d{12}")) {
                JOptionPane.showMessageDialog(this, "Aadhar must be exactly 12 digits.");
                return;
            }

            if (senior.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select Senior Citizen status.");
                return;
            }

            if (account.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select Existing Account status.");
                return;
            }

            String url = "jdbc:mysql://localhost:3306/smartbanksystem";
            String user = "root";
            String password = "Parmarsatyam09@#";

            String query = "INSERT INTO ssignup2 (form_no, religion, category, income, education, occupation, pan, aadhar, senior_citizen, existing_account) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pst = conn.prepareStatement(query)) {

                pst.setString(1, formNo);
                pst.setString(2, religion);
                pst.setString(3, category);
                pst.setString(4, income);
                pst.setString(5, education);
                pst.setString(6, occupation);
                pst.setString(7, pan);
                pst.setString(8, aadhar);
                pst.setString(9, senior);
                pst.setString(10, account);

                int rowsInserted = pst.executeUpdate();

                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Details saved successfully!");
                    setVisible(false);
                    new Signup3(formNo).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save details.");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Signup2("1234"));
    }
}
