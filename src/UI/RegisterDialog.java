package UI;

import service.AuthenticationService;
import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {
    private final JTextField txtName = new JTextField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JButton btnCreate = new JButton("Create Account");

    private final AuthenticationService creationService;

    public RegisterDialog(JFrame parent, AuthenticationService service) {
        super(parent, "Create New Account", true);
        this.creationService = service;

        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1; add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; add(txtPassword, gbc);

        btnCreate.setBackground(new Color(74, 105, 189));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCreate.setFocusPainted(false);
        btnCreate.setBorderPainted(false);
        btnCreate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreate.setPreferredSize(new Dimension(120, 35));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(btnCreate, gbc);


        //Event
        btnCreate.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                String email = txtEmail.getText().trim();
                String password = new String(txtPassword.getPassword());

                //creationService is an object from the Authentication Service Class: the connection between our
                //UI and the service:
                creationService.registerNewProfile(name, email, password);

                JOptionPane.showMessageDialog(this,
                        "Account successfully created! You can now log in.",
                        "Registration Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Registration Failed: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}