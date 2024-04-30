import java.sql.*;
import java.util.Scanner;

class User {
    protected String name;
    protected String id;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
    }
}

class Admin  {
    protected String name;
    protected String id;
    public Admin(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public GatePass issueGatePass(User user, String purpose) {
        return new GatePass(user, purpose);
    }
}

class Employee extends User {
    public Employee(String name, String id) {
        super(name, id);
    }
}

class Student extends User {
    private String course;
    private int year;

    public Student(String name, String id,  int year, String course) {
        super(name, id);
        this.course = course;
        this.year = year;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\n" +
                "ID: " + id + "\n" +
                "Course: " + course + "\n" +
                "Year: " + year + "\n";
    }
}

class GatePass {
    private User user;
    private String purpose;

    public GatePass(User user, String purpose) {
        this.user = user;
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return "Gate Pass Details:\n" +
                "User: " + user.name + " (ID: " + user.id + ")\n" +
                "Purpose: " + purpose + "\n" ;
    }
}

public class GatePassApplication {
    private static Connection con;

    // Method to establish database connection
    private static void connectDB() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/project";
        String user = "root";
        String password = "Abhilesh13";
        con = DriverManager.getConnection(url, user, password);
    }

    // Method to retrieve user details from the database based on the user ID
    private static User getUserDetails(String userId, String type) throws SQLException {
        String query ;
        //System.out.println(type.equals("Student"));
        if(type.equals("Student")){
            query = "SELECT * FROM Student WHERE id=?";
        }
        else{
            query = "SELECT * FROM EMP WHERE id=?";
        }
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, userId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String name = resultSet.getString("name");
            String id = resultSet.getString("id");
            if(type.equals("Student")){
                int year = resultSet.getInt("year");
                String course = resultSet.getString("course");
                return new Student(name, id, year, course);
            }
            return new Employee(name, id);
        } else {
            return null;
        }
    }

    private static void addStudentToDB(String id, String name, int year, String course) throws SQLException {
        String query = "INSERT INTO Student (id, name, year, course) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, name);
        preparedStatement.setInt(3, year);
        preparedStatement.setString(4, course);
        preparedStatement.executeUpdate();
        System.out.println("User details added to the database.");
    }

    private static void addEmpToDB(String id, String name) throws SQLException {
        String query = "INSERT INTO Emp (id, name) VALUES (?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, name);
        preparedStatement.executeUpdate();
        System.out.println("User details added to the database.");
    }
    /*private static void deleteStudent(String id) throws SQLException {
        String query = "DELETE FROM Student WHERE id=?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, id);
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Student with ID " + id + " deleted from the database.");
        } else {
            System.out.println("Student with ID " + id + " not found in the database.");
        }
    }

    // Method to delete an employee record from the Emp table based on user ID
    private static void deleteEmp(String id) throws SQLException {
        String query = "DELETE FROM Emp WHERE id=?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, id);
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Employee with ID " + id + " deleted from the database.");
        } else {
            System.out.println("Employee with ID " + id + " not found in the database.");
        }
    }*/

    public static void main(String[] args) throws SQLException {
        try {
            connectDB(); // Establish database connection
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database. Exiting...");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        //System.out.println("Welcome to GatePass Application!");
        while (true) {
            System.out.println("\nEnter your ID (or 0 to exit):");
            String id = scanner.next();
            //deleteStudent(id);
            //deleteEmp(id);


            System.out.println("\nEnter your role:\n1. Student\n2. Emp\n");
            int role = scanner.nextInt();

            if (id.equals("0")) {
                break;
            }

            User user;
            try {
                user = getUserDetails(id, (role==1)?"Student":"Emp");
            } catch (SQLException e) {
                System.out.println("Error fetching user details. Please try again.");
                e.printStackTrace();
                continue;
            }

            if (user == null) {
                System.out.println("Invalid user ID. Try again.");
                continue;
            }

            scanner.nextLine(); // Clear the input buffer
            System.out.println("Enter the purpose of the gate pass:");
            String purpose = scanner.nextLine();

            Admin admin = new Admin("Admin Name", "1");
            GatePass gatePass = admin.issueGatePass(user, purpose);
            System.out.println("\nGate pass issued successfully:");
            System.out.println(gatePass);

            if (user instanceof Student) {
                System.out.println("Student Details:");
                System.out.println(user);
            }
        }

        try {
            if (con != null) {
                con.close(); // Close the database connection
            }
        } catch (SQLException e) {
            System.out.println("Error closing the database connection.");
        }

        System.out.println("Exit");
        scanner.close();
    }
}