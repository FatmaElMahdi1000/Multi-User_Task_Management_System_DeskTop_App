package UI;

import Model.UserProfile;
import service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.function.Consumer;

public class LoginFrame extends JFrame {

    // Connection between the GUI and the Service layer
    private final AuthenticationService authService;
    private final JTextField txtEmail;
    private final JPasswordField txtPassword;
    private final JButton btnLogin;
    private final JButton btnRegister;
    private final JButton btnForgotPass;

    private Consumer<UserProfile> onLoginSuccessCallback;

    // GUI UI Components
    private JPanel mainPanel;
    private JLabel lblHeader;
    private JLabel lblSubHeader;
    private JLabel lblEmail;
    private JLabel lblPassword;

    // Modern Flat Color Palette
    private final Color COLOR_BG = Color.WHITE;
    private final Color COLOR_TEXT_MAIN = new Color(33, 37, 41);
    private final Color COLOR_TEXT_MUTED = new Color(108, 117, 125);
    private final Color COLOR_BORDER = new Color(206, 212, 218);
    private final Color ACCENT_BLUE = new Color(37, 99, 235); // Modern vibrant blue

    // Standardized Fonts
    Font fontHeader = new Font("Segoe UI", Font.BOLD, 24);
    Font fontSub = new Font("Segoe UI", Font.PLAIN, 13);
    Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
    Font fontInput = new Font("Segoe UI", Font.PLAIN, 14);

    //Dependency Injection, Almost Aggregation relationship:
    public LoginFrame(AuthenticationService authService) {
        this.authService = authService;

        // Window Characteristics
        setTitle("Task Management System - Login");
        setSize(450, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel Setup
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        add(mainPanel);

        // GridBagConstraints Base Config
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // **HEADER**
        lblHeader = new JLabel("Welcome Back");
        lblHeader.setFont(fontHeader);
        lblHeader.setForeground(COLOR_TEXT_MAIN);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 4, 0);
        mainPanel.add(lblHeader, gbc);

        // **SUBHEADER**
        lblSubHeader = new JLabel("Sign in to manage your workspace");
        lblSubHeader.setFont(fontSub);
        lblSubHeader.setForeground(COLOR_TEXT_MUTED);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(lblSubHeader, gbc);

        // **EMAIL LABEL**
        lblEmail = new JLabel("Email Address");
        lblEmail.setFont(fontLabel);
        lblEmail.setForeground(COLOR_TEXT_MAIN);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 4, 0);
        mainPanel.add(lblEmail, gbc);

        // **EMAIL FIELD**
        txtEmail = new JTextField();
        txtEmail.setFont(fontInput);
        txtEmail.setPreferredSize(new Dimension(0, 38));
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 12, 0);
        mainPanel.add(txtEmail, gbc);

        // **PASSWORD LABEL**
        lblPassword = new JLabel("Password");
        lblPassword.setFont(fontLabel);
        lblPassword.setForeground(COLOR_TEXT_MAIN);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 4, 0);
        mainPanel.add(lblPassword, gbc);

        // **PASSWORD FIELD**
        txtPassword = new JPasswordField();
        txtPassword.setFont(fontInput);
        txtPassword.setPreferredSize(new Dimension(0, 38));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 24, 0);
        mainPanel.add(txtPassword, gbc);

        // **BUTTON: SIGN IN**
        btnLogin = new JButton("Sign In");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(ACCENT_BLUE);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(0, 40));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 16, 0);
        mainPanel.add(btnLogin, gbc);

        // **BUTTON: REGISTER** (Left Link)
        btnRegister = new JButton("Create an account");
        btnRegister.setFont(fontSub);
        btnRegister.setForeground(ACCENT_BLUE);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(btnRegister, gbc);

        // **BUTTON: FORGOT PASSWORD** (Right Link)
        btnForgotPass = new JButton("Forgot Password?");
        btnForgotPass.setFont(fontSub);
        btnForgotPass.setForeground(COLOR_TEXT_MUTED);
        btnForgotPass.setContentAreaFilled(false);
        btnForgotPass.setBorderPainted(false);
        btnForgotPass.setFocusPainted(false);
        btnForgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(btnForgotPass, gbc);



        //  Action Listeners
        //btnLogin ActionListener
        btnLogin.addActionListener(e->  {

            String EnteredEmail = txtEmail.getText().trim();
            String EnteredPass = new String(txtPassword.getPassword());

            try {

                UserProfile user = authService.loginSystemPortal(EnteredEmail, EnteredPass);
                //CallBack
                if(user != null && onLoginSuccessCallback != null) {
                    onLoginSuccessCallback.accept(user);
                    //Close the Login Frame
                    dispose();
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Authentication Failed: " + ex.getMessage(),
                "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        });

        //BtnRegister ActionListener
        btnRegister.addActionListener(e -> {
            RegisterDialog regDialog = new RegisterDialog(this, authService);
            regDialog.setVisible(true);
        });

        //btnForgotPass ActionListener
        btnForgotPass.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            try {
                String password = authService.recoverPassword(email);
                JOptionPane.showMessageDialog(
                        this,
                        "Your password is: " + password,
                        "Password Recovery",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(), // Displays "Enter your email." or "Email not found."
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Database error: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        setVisible(true);
    }

    public void setOnLoginSuccessCallback(Consumer<UserProfile> callback) {
        this.onLoginSuccessCallback = callback;
    }

}