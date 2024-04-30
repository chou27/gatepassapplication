import java.sql.*;

public class GatePassApplication {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kmit_database";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Sunny1234";

    public static void main(String[] args) {
        try {
            // Register the JDBC driver
            Class.forName("con.mysql.cj.jdbc.Driver");

            // Establish connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            // Create table if not exists
            createTable(connection);

            // Store student and faculty information
            //storeInformation(connection, "BD003", "John Doe", "Student");
            //storeInformation(connection, "BD005", "Jane Smith", "Faculty");
            
            // Check whether a given roll number belongs to KMIT College
            checkRollNumber(connection, "BD001");
            checkRollNumber(connection, "BD004");

            // Close the connection
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS kmit_records (rollno VARCHAR(10) PRIMARY KEY, name VARCHAR(50), type VARCHAR(20))";
        Statement statement = connection.createStatement();
        statement.execute(createTableQuery);
        statement.close();
    }

    private static void storeInformation(Connection connection, String rollno, String name, String type) throws SQLException {
        String insertQuery = "INSERT INTO kmit_records (rollno, name, type) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, rollno);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, type);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private static void checkRollNumber(Connection connection, String rollno) throws SQLException {
        if (rollno.contains("BD")) {
            String selectQuery = "SELECT type FROM kmit_records WHERE rollno = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, rollno);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String type = resultSet.getString("type");
                System.out.println("Roll number " + rollno + " belongs to KMIT College. Type: " + type);
            } else {
                System.out.println("Roll number " + rollno + " does not exist in the database.");
            }
            resultSet.close();
            preparedStatement.close();
        } else {
            System.out.println("Roll number " + rollno + " does not belong to KMIT College.");
        }
    }
}