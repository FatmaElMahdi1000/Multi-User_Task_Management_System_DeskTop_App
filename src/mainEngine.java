import UI.MainApplication;
import UI.LoginFrame;
import service.TaskService;
import service.AuthenticationService;
import Model.UserProfile;
import javax.swing.*;

public class mainEngine {

    public static void main(String[] args) {

        // Make Swing use the operating system's   look -> windows for me
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Create the Service layer.
        // These contain the application's business logic.
        TaskService taskService;
        AuthenticationService authService;

        try {

            // Task management operations
            taskService = new TaskService();

            // Login and authentication operations
            authService = new AuthenticationService();

        } catch (Exception e) {

            // If a service fails to initialize,
            // avoid crashing  .
            System.err.println("Service initialization failed: " + e.getMessage());

            taskService = null;
            authService = null;

        }


        final TaskService finalTaskService = taskService;
        final AuthenticationService finalAuthService = authService;

        // Start the GUI on Swing's Event
        // Swing components should   be created here.
        SwingUtilities.invokeLater(() -> {

            try {
                //  Login window INSTANCE and injecting the Authentication Service  --- Dependency injection example
                LoginFrame loginPortal = new LoginFrame(finalAuthService);

                // Register a callback that runs ONLY after a successful login.
                loginPortal.setOnLoginSuccessCallback((UserProfile authenticatedUser) -> {
                    System.out.println("Access granted.");

                    loginPortal.dispose(); //an order for the Login Window to  close
                    // TO Open the application dashboard.
                    MainApplication app =
                            new MainApplication(finalTaskService, authenticatedUser);

                    // Displaying the dashboard.
                    app.setVisible(true);
                });

                // Displaying the login window.
                loginPortal.setVisible(true);

            } catch (Exception ex) {

                // FOR  unexpected UI errors
                System.err.println("UI initialization failed: " + ex.getMessage());
                ex.printStackTrace();
            }

        });
    }
}