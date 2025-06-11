import java.util.Scanner;
import java.sql.*;

public class PasswordManager {
    private static final String URL = "jdbc:mysql://localhost:3306/pwd_db";
    private static final String USER = "root";
    private static final String PASS = "amitav";

    private Connection conn;

    public PasswordManager() {
        try{
            conn = DriverManager.getConnection(URL, USER, PASS);
            Scanner sc = new Scanner(System.in);
        }
        catch (SQLException e){
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
            PreparedStatement stmt = conn.PreparedStatement(sql);
            stmt.setString(1, website);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.executeUpdate();

            System.out.println("Credential added successfully ");
        }
        catch (Exception e)
            System.out.println("Error adding credential" +e.getMessage);
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
                String password = rs.getString("password");

                System.out.printf("Website: %s | Username: %s | Password: %s", website, username, password);
            }
        }
        catch(Exception e) {
            System.out.println("Error retrieving credentials: " +e.getMessage());
        }
    }
}
