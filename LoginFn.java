import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginFn {

    public static void show() {

        JFrame loginFrame = new JFrame("Login");

        JLabel l1 = new JLabel("Username", SwingConstants.CENTER);
        JLabel l2 = new JLabel("Password", SwingConstants.CENTER);

        JTextField usernameTF = new JTextField();
        JPasswordField passwordTF = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");

        loginBtn.addActionListener(e -> {

            String username = usernameTF.getText();
            String password = new String(passwordTF.getPassword());

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter username");
                return;
            }

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Enter password");
                return;
            }

            Connection con = DatabaseConnectivity.connect();

            if (con == null) {
                JOptionPane.showMessageDialog(null, "Database connection failed");
                return;
            }

            try (con;
                 PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM users WHERE username = ?")) {

                ps.setString(1, username);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null, "Invalid Login");
                        return;
                    }

                    String storedHash = rs.getString("PASSWORD");

                    if (!BCrypt.checkpw(password, storedHash)) {
                        JOptionPane.showMessageDialog(null, "Invalid Login");
                        return;
                    }

                    int userType = rs.getInt("USER_TYPE");
                    String UID = rs.getString("UID");

                    loginFrame.dispose();

                    if (userType == 1) {
                        LibrarianFrame.show();
                    } else {
                        UserFrame.show(UID);
                    }
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> loginFrame.dispose());

        loginFrame.setLayout(new GridLayout(3, 2));
        loginFrame.add(l1);
        loginFrame.add(usernameTF);
        loginFrame.add(l2);
        loginFrame.add(passwordTF);
        loginFrame.add(loginBtn);
        loginFrame.add(cancelBtn);

        loginFrame.setSize(300, 150);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
        loginFrame.setResizable(false);
    }
}
