import java.util.Scanner;
import java.sql.*;

public class PasswordManager {
    private static final String URL = "jdbc:mysql://localhost:3306/pwd_db";
    private static final String USER = "root";
    private static final String PASS = "amitav";

    private Connection conn;
    private Scanner sc;

    public PasswordManager() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            sc = new Scanner(System.in);
        }
        catch (SQLException | ClassNotFoundException e){
            System.out.println("DB Connection failed: " +e.getMessage());
            System.exit(1);
        }
    }

    public void run() {
        while(true) {
            System.out.println("\n Password Manager ");
            System.out.println("1. Add credential");
            System.out.println("2. View all credentials");
            System.out.println("3. Search by website");
            System.out.println("4. Exit");
            System.out.println("Choose: ");

            String choice = sc.nextLine();

            switch(choice) {
                case "1":
                    addCredential();
                    break;
                case "2":
                    viewCredentials();
                    break;
                case "3":
                    searchByWebsite();
                    break;
                case "4":
                    close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice");


            }
        }
    }

    private void addCredential() {
        try {
            System.out.print("Enter website: ");
            String website = sc.nextLine();
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            String encryptedPassword = EncryptionUtil.encrypt(password);

            String sql = "insert into credentials (website, username, password) values (?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, website);
            stmt.setString(2, username);
            stmt.setString(3, encryptedPassword);
            stmt.executeUpdate();

            System.out.println("Credential added successfully ");
        }
        catch (Exception e) {
            System.out.println("Error adding credential: " +e.getMessage());
        }
    }

    private void viewCredentials() {
        try {
            String sql = "select * from credentials";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n Stored Credentials: ");
            while(rs.next()) {
                String website = rs.getString("website");
                String username = rs.getString("username");
                String encryptedPassword = rs.getString("password");
                // String decryptedPassword = EncryptionUtil.decrypt(encryptedPassword);

                System.out.printf("Website: %s | Username: %s | Password: %s", website, username, encryptedPassword);
            }
        }
        catch(Exception e) {
            System.out.println("Error retrieving credentials: " +e.getMessage());
        }
    }

    private void searchByWebsite() {
        try {
            System.out.println("Enter website to search: ");
            String webQuery = sc.nextLine();

            String sql = "select * from credentials where website like ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + webQuery + "%");
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while(rs.next()) {
                found = true;
                String website = rs.getString(1);
                String username = rs.getString(2);
                String password = rs.getString(3);

                System.out.printf("Website: %s | Username: %s | Password: %s", website, username, password);

            }

            if(!found)
                System.out.println("No credentials found for that website");
        }
        catch(Exception e) {
            System.out.println("Error searchign credentials: " +e.getMessage());
        }
    }
    
    public void close() {
        try {
            if(conn != null)
                conn.close();
            if(sc != null)
                sc.close();
        }
        catch(SQLException e){
            System.out.println("Error closing resources: " +e.getMessage());
        }
    }

    public static void main(String[] args) {
        PasswordManager pm = new PasswordManager();
        pm.run();
    }
}
