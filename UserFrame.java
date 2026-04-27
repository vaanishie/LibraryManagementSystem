import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserFrame {

    public static void show(String UID) {

        JFrame studentFrame = new JFrame("Student Functions");

        JButton view_books_btn = new JButton("View Books");
        JButton view_user_issued_books_btn = new JButton("Issued Books");
        JButton view_user_returned_books_btn = new JButton("My Returned Books");

        // ===== VIEW BOOKS =====
        // FIX: duplicate code extracted to BookTableHelper
        view_books_btn.addActionListener(e -> BookTableHelper.showBooksFrame());

        // ===== ISSUED BOOKS =====
        view_user_issued_books_btn.addActionListener(e -> {
            JFrame frame = new JFrame("My Issued Books");
            try (Connection con = DatabaseConnectivity.connect()) {
                PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM issued_books WHERE UID = ?");
                ps.setString(1, UID);
                ResultSet rs = ps.executeQuery();

                String[] cols = {"Issue ID", "User ID", "Book ID", "Issued Date", "Period"};
                DefaultTableModel model = new DefaultTableModel();
                model.setColumnIdentifiers(cols);
                JTable table = new JTable(model);

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("IID"),
                        rs.getInt("UID"),
                        rs.getInt("BID"),
                        rs.getString("ISSUED_DATE"),
                        rs.getInt("PERIOD")
                    });
                }

                frame.add(new JScrollPane(table));
                frame.setSize(800, 400);
                frame.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        // ===== RETURNED BOOKS =====
        view_user_returned_books_btn.addActionListener(e -> {
            JFrame frame = new JFrame("My Returned Books");
            try (Connection con = DatabaseConnectivity.connect()) {
                PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM returned_books WHERE uid = ?");
                ps.setString(1, UID);
                ResultSet rs = ps.executeQuery();

                String[] cols = {"Return ID", "User ID", "Book ID", "Return Date", "Fine"};
                DefaultTableModel model = new DefaultTableModel();
                model.setColumnIdentifiers(cols);
                JTable table = new JTable(model);

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("rid"),
                        rs.getInt("uid"),
                        rs.getInt("bid"),
                        rs.getString("return_date"),
                        rs.getInt("fine")
                    });
                }

                frame.add(new JScrollPane(table));
                frame.setSize(800, 400);
                frame.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        // ===== MAIN FRAME =====
        studentFrame.setLayout(new GridLayout(3, 1));
        studentFrame.add(view_books_btn);
        studentFrame.add(view_user_issued_books_btn);
        studentFrame.add(view_user_returned_books_btn);
        studentFrame.setSize(400, 300);
        studentFrame.setLocationRelativeTo(null);
        studentFrame.setVisible(true);
        studentFrame.setResizable(false);
    }
}
