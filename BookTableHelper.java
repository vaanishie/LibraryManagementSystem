import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class BookTableHelper {

    /**
     * Opens a new frame showing all books from the database.
     * Used by both LibrarianFrame and UserFrame to avoid duplicate code.
     */
    public static void showBooksFrame() {
        JFrame frame = new JFrame("Books Available");

        try (Connection con = DatabaseConnectivity.connect()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM books");
            ResultSet rs = ps.executeQuery();

            String[] cols = {"ID", "ISBN", "Name", "Publisher", "Edition", "Genre", "Price", "Pages"};
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(cols);

            JTable table = new JTable(model);
            table.setBackground(new Color(51, 35, 85));
            table.setForeground(Color.white);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            table.setFillsViewportHeight(true);
            table.setFocusable(false);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("bid"),
                    rs.getString("book_isbn"),
                    rs.getString("book_name"),
                    rs.getString("book_publisher"),
                    rs.getString("book_edition"),
                    rs.getString("book_genre"),
                    rs.getDouble("book_price"),
                    rs.getInt("book_pages")
                });
            }

            frame.add(new JScrollPane(table));
            frame.setSize(900, 400);
            frame.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
}
