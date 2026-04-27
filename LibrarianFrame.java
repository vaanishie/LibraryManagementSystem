import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.table.*;
import org.jdesktop.swingx.JXDatePicker;
import org.mindrot.jbcrypt.BCrypt;

public class LibrarianFrame {

    public static void show() {

        JFrame librarianFrame = new JFrame("Librarian Functions");

        // ===== VIEW BOOKS =====
        JButton view_books_btn = new JButton("View Books");
        view_books_btn.setBackground(new Color(51, 35, 85));
        view_books_btn.setForeground(Color.white);
        // FIX: duplicate code extracted to BookTableHelper
        view_books_btn.addActionListener(e -> BookTableHelper.showBooksFrame());

        // ===== VIEW USERS =====
        JButton view_users_btn = new JButton("View Users");
        view_users_btn.setBackground(new Color(51, 35, 85));
        view_users_btn.setForeground(Color.white);
        view_users_btn.addActionListener(e -> {
            JFrame viewUsersFrame = new JFrame("Users List");
            try (Connection con = DatabaseConnectivity.connect()) {
                PreparedStatement ps = con.prepareStatement("SELECT UID, USERNAME, USER_TYPE FROM users");
                ResultSet rs = ps.executeQuery();

                String[] cols = {"User ID","Username","User Type"};
                DefaultTableModel model = new DefaultTableModel();
                model.setColumnIdentifiers(cols);
                JTable table = new JTable(model);
                table.setBackground(new Color(51, 35, 85));
                table.setForeground(Color.white);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.setFillsViewportHeight(true);

                while (rs.next()) {
                    int uid = rs.getInt("UID");
                    String username = rs.getString("USERNAME");
                    int userType = rs.getInt("USER_TYPE");
                    model.addRow(new Object[]{uid, username, userType == 1 ? "ADMIN" : "USER"});
                }

                viewUsersFrame.add(new JScrollPane(table));
                viewUsersFrame.setSize(800, 400);
                viewUsersFrame.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        // ===== VIEW ISSUED BOOKS =====
        JButton view_issued_books_btn = new JButton("View Issued Books");
        view_issued_books_btn.setBackground(new Color(51, 35, 85));
        view_issued_books_btn.setForeground(Color.white);
        view_issued_books_btn.addActionListener(e -> {
            JFrame frame = new JFrame("Issued Books List");
            try (Connection con = DatabaseConnectivity.connect()) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM issued_books");
                ResultSet rs = ps.executeQuery();

                String[] cols = {"Issue ID","User ID","Book ID","Issue Date","Period"};
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
                        rs.getInt("IID"), rs.getInt("UID"),
                        rs.getInt("BID"), rs.getString("ISSUED_DATE"), rs.getInt("PERIOD")
                    });
                }

                frame.add(new JScrollPane(table));
                frame.setSize(800, 400);
                frame.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        // ===== VIEW RETURNED BOOKS =====
        JButton view_returned_books_btn = new JButton("View Returned Books");
        view_returned_books_btn.setBackground(new Color(51, 35, 85));
        view_returned_books_btn.setForeground(Color.white);
        view_returned_books_btn.addActionListener(e -> {
            JFrame frame = new JFrame("Returned Books List");
            try (Connection con = DatabaseConnectivity.connect()) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM returned_books");
                ResultSet rs = ps.executeQuery();

                String[] cols = {"Return ID","Book ID","User ID","Return Date","Fine"};
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
                        rs.getInt("rid"), rs.getInt("bid"),
                        rs.getInt("uid"), rs.getString("return_date"), rs.getInt("fine")
                    });
                }

                frame.add(new JScrollPane(table));
                frame.setSize(800, 400);
                frame.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        // ===== ADD USER =====
        JButton add_user_btn = new JButton("Add User");
        add_user_btn.setBackground(new Color(51, 35, 85));
        add_user_btn.setForeground(Color.white);
        add_user_btn.addActionListener(e -> {
            JFrame add_user_frame = new JFrame("Enter User Details");

            JLabel l1 = new JLabel("Username", SwingConstants.CENTER);
            l1.setOpaque(true); l1.setBackground(new Color(51, 35, 85)); l1.setForeground(Color.white);
            JLabel l2 = new JLabel("Password", SwingConstants.CENTER);
            l2.setOpaque(true); l2.setBackground(new Color(51, 35, 85)); l2.setForeground(Color.white);

            JTextField add_username_tf = new JTextField();
            add_username_tf.setBackground(new Color(51, 35, 85)); add_username_tf.setForeground(Color.white);
            JPasswordField add_password_tf = new JPasswordField();
            add_password_tf.setBackground(new Color(51, 35, 85)); add_password_tf.setForeground(Color.white);

            JRadioButton user_type_radio1 = new JRadioButton("Admin");
            user_type_radio1.setHorizontalAlignment(SwingConstants.CENTER);
            user_type_radio1.setBackground(new Color(51, 35, 85)); user_type_radio1.setForeground(Color.white);
            JRadioButton user_type_radio2 = new JRadioButton("User");
            user_type_radio2.setHorizontalAlignment(SwingConstants.CENTER);
            user_type_radio2.setBackground(new Color(51, 35, 85)); user_type_radio2.setForeground(Color.white);
            ButtonGroup grp = new ButtonGroup();
            grp.add(user_type_radio1); grp.add(user_type_radio2);

            JButton create_btn = new JButton("Create");
            create_btn.setBackground(new Color(124, 85, 227)); create_btn.setForeground(Color.white);
            JButton cancel_btn = new JButton("Cancel");
            cancel_btn.setBackground(new Color(124, 85, 227)); cancel_btn.setForeground(Color.white);

            create_btn.addActionListener(ev -> {
                String username = add_username_tf.getText().trim();
                String password = new String(add_password_tf.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Username and password are required");
                    return;
                }

                // Hash the password before storing
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                int userType = user_type_radio1.isSelected() ? 1 : 0;

                try (Connection con = DatabaseConnectivity.connect()) {
                    // FIX: PreparedStatement prevents SQL injection
                    PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO users (USERNAME, PASSWORD, USER_TYPE) VALUES (?, ?, ?)");
                    ps.setString(1, username);
                    ps.setString(2, hashedPassword);
                    ps.setInt(3, userType);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, userType == 1 ? "Admin added!" : "User added!");
                    add_user_frame.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            });

            cancel_btn.addActionListener(ev -> add_user_frame.dispose());

            add_user_frame.add(l1); add_user_frame.add(add_username_tf);
            add_user_frame.add(l2); add_user_frame.add(add_password_tf);
            add_user_frame.add(user_type_radio1); add_user_frame.add(user_type_radio2);
            add_user_frame.add(create_btn); add_user_frame.add(cancel_btn);
            add_user_frame.setSize(350, 200);
            add_user_frame.setLayout(new GridLayout(4, 2));
            add_user_frame.setVisible(true);
            add_user_frame.setResizable(false);
        });

        // ===== ADD BOOK =====
        JButton add_book_btn = new JButton("Add Book");
        add_book_btn.setBackground(new Color(51, 35, 85));
        add_book_btn.setForeground(Color.white);
        add_book_btn.addActionListener(e -> {
            JFrame book_frame = new JFrame("Enter Book Details");

            JLabel l1 = new JLabel("ISBN", SwingConstants.CENTER);
            l1.setOpaque(true); l1.setBackground(new Color(51, 35, 85)); l1.setForeground(Color.white);
            JLabel l2 = new JLabel("Name", SwingConstants.CENTER);
            l2.setOpaque(true); l2.setBackground(new Color(51, 35, 85)); l2.setForeground(Color.white);
            JLabel l3 = new JLabel("Publisher", SwingConstants.CENTER);
            l3.setOpaque(true); l3.setBackground(new Color(51, 35, 85)); l3.setForeground(Color.white);
            JLabel l4 = new JLabel("Edition", SwingConstants.CENTER);
            l4.setOpaque(true); l4.setBackground(new Color(51, 35, 85)); l4.setForeground(Color.white);
            JLabel l5 = new JLabel("Genre", SwingConstants.CENTER);
            l5.setOpaque(true); l5.setBackground(new Color(51, 35, 85)); l5.setForeground(Color.white);
            JLabel l6 = new JLabel("Price", SwingConstants.CENTER);
            l6.setOpaque(true); l6.setBackground(new Color(51, 35, 85)); l6.setForeground(Color.white);
            JLabel l7 = new JLabel("Pages", SwingConstants.CENTER);
            l7.setOpaque(true); l7.setBackground(new Color(51, 35, 85)); l7.setForeground(Color.white);

            JTextField isbn_tf    = new JTextField(); isbn_tf.setBackground(new Color(51,35,85));    isbn_tf.setForeground(Color.white);
            JTextField name_tf    = new JTextField(); name_tf.setBackground(new Color(51,35,85));    name_tf.setForeground(Color.white);
            JTextField pub_tf     = new JTextField(); pub_tf.setBackground(new Color(51,35,85));     pub_tf.setForeground(Color.white);
            JTextField edition_tf = new JTextField(); edition_tf.setBackground(new Color(51,35,85)); edition_tf.setForeground(Color.white);
            JTextField genre_tf   = new JTextField(); genre_tf.setBackground(new Color(51,35,85));   genre_tf.setForeground(Color.white);
            JTextField price_tf   = new JTextField(); price_tf.setBackground(new Color(51,35,85));   price_tf.setForeground(Color.white);
            JTextField pages_tf   = new JTextField(); pages_tf.setBackground(new Color(51,35,85));   pages_tf.setForeground(Color.white);

            JButton submit_btn = new JButton("Submit");
            submit_btn.setBackground(new Color(124, 85, 227)); submit_btn.setForeground(Color.white);
            JButton cancel_btn = new JButton("Cancel");
            cancel_btn.setBackground(new Color(124, 85, 227)); cancel_btn.setForeground(Color.white);

            submit_btn.addActionListener(ev -> {
                try (Connection con = DatabaseConnectivity.connect()) {
                    // FIX: PreparedStatement prevents SQL injection
                    PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO books (book_isbn, book_name, book_publisher, book_edition, book_genre, book_price, book_pages) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    ps.setString(1, isbn_tf.getText());
                    ps.setString(2, name_tf.getText());
                    ps.setString(3, pub_tf.getText());
                    ps.setString(4, edition_tf.getText());
                    ps.setString(5, genre_tf.getText());
                    ps.setDouble(6, Double.parseDouble(price_tf.getText()));
                    ps.setInt(7, Integer.parseInt(pages_tf.getText()));
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Book added!");
                    book_frame.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            });

            cancel_btn.addActionListener(ev -> book_frame.dispose());

            book_frame.add(l1); book_frame.add(isbn_tf);
            book_frame.add(l2); book_frame.add(name_tf);
            book_frame.add(l3); book_frame.add(pub_tf);
            book_frame.add(l4); book_frame.add(edition_tf);
            book_frame.add(l5); book_frame.add(genre_tf);
            book_frame.add(l6); book_frame.add(price_tf);
            book_frame.add(l7); book_frame.add(pages_tf);
            book_frame.add(submit_btn); book_frame.add(cancel_btn);
            book_frame.setSize(800, 500);
            book_frame.setLayout(new GridLayout(8, 2));
            book_frame.setVisible(true);
            book_frame.setResizable(false);
        });

        // ===== ISSUE BOOK =====
        JButton add_issue_book_btn = new JButton("Issue Book");
        add_issue_book_btn.setBackground(new Color(51, 35, 85));
        add_issue_book_btn.setForeground(Color.white);
        add_issue_book_btn.addActionListener(e -> {
            JFrame issue_book_frame = new JFrame("Enter Details");

            JPanel pickerPanel = new JPanel();
            JXDatePicker picker = new JXDatePicker();
            picker.setDate(Calendar.getInstance().getTime());
            picker.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
            pickerPanel.add(picker);
            pickerPanel.setBackground(new Color(51, 35, 85));
            pickerPanel.setForeground(Color.white);

            JLabel l1 = new JLabel("Book ID", SwingConstants.CENTER);
            l1.setOpaque(true); l1.setBackground(new Color(51,35,85)); l1.setForeground(Color.white);
            JLabel l2 = new JLabel("User/Student ID", SwingConstants.CENTER);
            l2.setOpaque(true); l2.setBackground(new Color(51,35,85)); l2.setForeground(Color.white);
            JLabel l3 = new JLabel("Period (days)", SwingConstants.CENTER);
            l3.setOpaque(true); l3.setBackground(new Color(51,35,85)); l3.setForeground(Color.white);
            JLabel l4 = new JLabel("Issued Date", SwingConstants.CENTER);
            l4.setOpaque(true); l4.setBackground(new Color(51,35,85)); l4.setForeground(Color.white);

            JTextField bid_tf    = new JTextField(); bid_tf.setBackground(new Color(51,35,85));    bid_tf.setForeground(Color.white);
            JTextField uid_tf    = new JTextField(); uid_tf.setBackground(new Color(51,35,85));    uid_tf.setForeground(Color.white);
            JTextField period_tf = new JTextField(); period_tf.setBackground(new Color(51,35,85)); period_tf.setForeground(Color.white);

            JButton submit_btn = new JButton("Submit");
            submit_btn.setBackground(new Color(124, 85, 227)); submit_btn.setForeground(Color.white);
            JButton cancel_btn = new JButton("Cancel");
            cancel_btn.setBackground(new Color(124, 85, 227)); cancel_btn.setForeground(Color.white);

            submit_btn.addActionListener(ev -> {
                try {
                    int bid = Integer.parseInt(bid_tf.getText());
                    int uid = Integer.parseInt(uid_tf.getText());
                    int period = Integer.parseInt(period_tf.getText());
                    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd"); // MySQL DATE format
                    String issued_date = fmt.format(picker.getDate());

                    try (Connection con = DatabaseConnectivity.connect()) {
                        // FIX: PreparedStatement prevents SQL injection
                        PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO issued_books (UID, BID, ISSUED_DATE, PERIOD) VALUES (?, ?, ?, ?)");
                        ps.setInt(1, uid);
                        ps.setInt(2, bid);
                        ps.setString(3, issued_date);
                        ps.setInt(4, period);
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Book Issued!");
                        issue_book_frame.dispose();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            });

            cancel_btn.addActionListener(ev -> issue_book_frame.dispose());

            issue_book_frame.add(l1); issue_book_frame.add(bid_tf);
            issue_book_frame.add(l2); issue_book_frame.add(uid_tf);
            issue_book_frame.add(l3); issue_book_frame.add(period_tf);
            issue_book_frame.add(l4); issue_book_frame.getContentPane().add(pickerPanel);
            issue_book_frame.add(submit_btn); issue_book_frame.add(cancel_btn);
            issue_book_frame.setSize(600, 300);
            issue_book_frame.setLayout(new GridLayout(5, 2));
            issue_book_frame.setVisible(true);
            issue_book_frame.setResizable(false);
        });

        // ===== RETURN BOOK =====
        JButton add_return_book_btn = new JButton("Return Book");
        add_return_book_btn.setBackground(new Color(51, 35, 85));
        add_return_book_btn.setForeground(Color.white);
        add_return_book_btn.addActionListener(e -> {
            JFrame returnBookFrame = new JFrame("Enter Details");

            JLabel l1 = new JLabel("Book ID", SwingConstants.CENTER);
            l1.setOpaque(true); l1.setBackground(new Color(51,35,85)); l1.setForeground(Color.white);
            JLabel l2 = new JLabel("User ID", SwingConstants.CENTER);
            l2.setOpaque(true); l2.setBackground(new Color(51,35,85)); l2.setForeground(Color.white);
            JLabel l3 = new JLabel("Return Date", SwingConstants.CENTER);
            l3.setOpaque(true); l3.setBackground(new Color(51,35,85)); l3.setForeground(Color.white);

            JTextField bid_tf = new JTextField(); bid_tf.setBackground(new Color(51,35,85)); bid_tf.setForeground(Color.white);
            JTextField uid_tf = new JTextField(); uid_tf.setBackground(new Color(51,35,85)); uid_tf.setForeground(Color.white);

            JPanel pickerPanel = new JPanel();
            JXDatePicker picker = new JXDatePicker();
            picker.setDate(Calendar.getInstance().getTime());
            picker.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
            pickerPanel.add(picker);
            pickerPanel.setBackground(new Color(51,35,85));

            JButton return_btn = new JButton("Return");
            return_btn.setBackground(new Color(124, 85, 227)); return_btn.setForeground(Color.white);
            JButton cancel_btn = new JButton("Cancel");
            cancel_btn.setBackground(new Color(124, 85, 227)); cancel_btn.setForeground(Color.white);

            return_btn.addActionListener(ev -> {
                try {
                    int bid = Integer.parseInt(bid_tf.getText());
                    int uid = Integer.parseInt(uid_tf.getText());
                    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd"); // MySQL DATE format
                    String return_date = fmt.format(picker.getDate());

                    try (Connection con = DatabaseConnectivity.connect()) {
                        // FIX: PreparedStatement prevents SQL injection
                        // FIX: fine column removed — the DB trigger calculates it automatically
                        PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO returned_books (bid, uid, return_date) VALUES (?, ?, ?)");
                        ps.setInt(1, bid);
                        ps.setInt(2, uid);
                        ps.setString(3, return_date);
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Book Returned!");
                        returnBookFrame.dispose();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            });

            cancel_btn.addActionListener(ev -> returnBookFrame.dispose());

            // Fine field removed — trigger handles it automatically
            returnBookFrame.add(l1); returnBookFrame.add(bid_tf);
            returnBookFrame.add(l2); returnBookFrame.add(uid_tf);
            returnBookFrame.add(l3); returnBookFrame.getContentPane().add(pickerPanel);
            returnBookFrame.add(return_btn); returnBookFrame.add(cancel_btn);
            returnBookFrame.setSize(600, 250);
            returnBookFrame.setLayout(new GridLayout(4, 2));
            returnBookFrame.setVisible(true);
            returnBookFrame.setResizable(false);
        });

        // ===== MAIN FRAME =====
        librarianFrame.setLayout(new GridLayout(2, 4));
        librarianFrame.add(add_user_btn);
        librarianFrame.add(add_book_btn);
        librarianFrame.add(add_issue_book_btn);
        librarianFrame.add(add_return_book_btn);
        librarianFrame.add(view_users_btn);
        librarianFrame.add(view_books_btn);
        librarianFrame.add(view_issued_books_btn);
        librarianFrame.add(view_returned_books_btn);
        librarianFrame.setSize(800, 200);
        librarianFrame.setVisible(true);
        librarianFrame.setResizable(false);
    }
}
