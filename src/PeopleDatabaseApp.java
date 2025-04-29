import java.sql.*;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class PeopleDatabaseApp {
    public static void main(String[] args) {

        Properties props = new Properties();
        try {
            props.load(PeopleDatabaseApp.class.getClassLoader().getResourceAsStream("res.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String url = props.getProperty("res.url");
        String username = props.getProperty("res.username");
        String password = props.getProperty("res.password");

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to database!");

            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter your first name:");
            String firstName = scanner.nextLine();
            System.out.println("Enter your last name");
            String lastName = scanner.nextLine();

            String checkSQL = "SELECT COUNT(*) FROM PEOPLE WHERE FirstName = ? AND LastName = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                checkStmt.setString(1, firstName);
                checkStmt.setString(2, lastName);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                int count = rs.getInt(1);

                if (count > 0) {
                    System.out.println("This person already exists in the database.");
                } else {
                    String insertSQL = "INSERT INTO PEOPLE (FirstName, LastName) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setString(1, firstName);
                        insertStmt.setString(2, lastName);
                        insertStmt.executeUpdate();
                        System.out.println("Person inserted successfully!");
                    }
                }
            }

            String totalSQL = "SELECT COUNT(*) FROM PEOPLE";
            try (PreparedStatement totalStmt = conn.prepareStatement(totalSQL)) {
                ResultSet totalRs = totalStmt.executeQuery();
                if (totalRs.next()) {
                    int totalPeople = totalRs.getInt(1);
                    System.out.println("Total number of people in the database: " + totalPeople);
                }
            }

            String letterSQL = "SELECT LEFT(FirstName, 1) AS FirstLetter, COUNT(*) AS LetterCount FROM PEOPLE GROUP BY LEFT(FirstName, 1) ORDER BY FirstLetter";
            try (PreparedStatement letterStmt = conn.prepareStatement(letterSQL)) {
                ResultSet letterRs = letterStmt.executeQuery();
                while (letterRs.next()) {
                    String firstLetter = letterRs.getString("FirstLetter");
                    int count = letterRs.getInt("LetterCount");
                    System.out.println(firstLetter + ": " + count);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
