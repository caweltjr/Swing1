package model;

import java.io.*;
import java.sql.*;
import java.util.*;

public class Database {

    private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private String DATABASE_URL = "jdbc:mysql://localhost:%d/swingtest?useSSL=false";
    private List<Person> people;
    private Connection con;
    private int port;
    private String user;
    private String password;


    public Database() {
        // create as Linked List, because Linked Lists are optimized for adding/removing
        // items in the middle of the list, whereas Array Lists are optimized for
        // adding/removing items at the beginning and end of list *** DNKT(Didn't know that,
        // in so many words) - call it List everywhere else; only need to call it Linked List
        // when you create it
        people = new LinkedList<Person>();
    }
    public void configure(int port, String user, String password) throws Exception {

        this.port = port;
        this.user = user;
        this.password = password;

        if(con != null) {
            disconnect();
            connect();
        }
    }
    public void connect() throws Exception {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new Exception("Driver Not Found");
        }
        String url = String.format(DATABASE_URL, port);
        con = DriverManager.getConnection(url, user, password);
    }
    public void disconnect(){
        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Can't Close Connection");
            }
        }
    }
    public void save() throws SQLException {

        String checkSql = "select count(*) as count from people where id=?";
        PreparedStatement checkStmt = con.prepareStatement(checkSql);

        String insertSql = "insert into people (id, name, age, employment_status, tax_id, us_citizen, gender, occupation) values (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement insertStatement = con.prepareStatement(insertSql);

        String updateSql = "update people set name=?, age=?, employment_status=?, tax_id=?, us_citizen=?, gender=?, occupation=? where id=?";
        PreparedStatement updateStatement = con.prepareStatement(updateSql);

        for (Person person : people) {
            int id = person.getId();
            String name = person.getName();
            String occupation = person.getOccupation();
            AgeCategory age = person.getAgeCategory();
            EmploymentCategory emp = person.getEmploymentCategory();
            String tax = person.getTaxId();
            boolean isUs = person.isUsCitizen();
            Gender gender = person.getGender();

            checkStmt.setInt(1, id);

            ResultSet checkResult = checkStmt.executeQuery();
            checkResult.next();

            int count = checkResult.getInt(1);

            if (count == 0) {
                int col = 1;
                insertStatement.setInt(col++, id);
                insertStatement.setString(col++, name);
                insertStatement.setString(col++, age.name());
                insertStatement.setString(col++, emp.name());
                insertStatement.setString(col++, tax);
                insertStatement.setBoolean(col++, isUs);
                insertStatement.setString(col++, gender.name());
                insertStatement.setString(col,occupation);

                insertStatement.executeUpdate();
            } else {
                int col = 1;
                updateStatement.setString(col++, name);
                updateStatement.setString(col++, age.name());
                updateStatement.setString(col++, emp.name());
                updateStatement.setString(col++, tax);
                updateStatement.setBoolean(col++, isUs);
                updateStatement.setString(col++, gender.name());
                updateStatement.setString(col++, occupation);
                updateStatement.setInt(col, id);

                updateStatement.executeUpdate();
            }
        }

        insertStatement.close();
        checkStmt.close();
    }

    public void load() throws SQLException {
        people.clear();
        String sql = "select id, name, age, employment_status, tax_id, us_citizen, gender, occupation from people"
                + " order by name";
        Statement selectStatement = con.createStatement();
        ResultSet results = selectStatement.executeQuery(sql);
        while(results.next()){
            int id = results.getInt("id");
            String name = results.getString("name");
            String age = results.getString("age");
            String emp = results.getString("employment_status");
            String taxId = results.getString("tax_id");
            boolean isUs = results.getBoolean("us_citizen");
            String gender = results.getString("gender");
            String occ = results.getString("occupation");
            Person person = new Person(id, name, occ, AgeCategory.valueOf(age), EmploymentCategory.valueOf(emp),
                    taxId, isUs, Gender.valueOf(gender));
            people.add(person);
        }

        results.close();
        selectStatement.close();
    }

    public void addPerson(Person person) {
        people.add(person);
    }
    public void removePerson(int row) {
        people.remove(row);
    }
    public List<Person> getPeople() {
        return Collections.unmodifiableList(people); // prevents other classes from modifying list
    }
    public void saveToFile(File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        Person[] persons = people.toArray(new Person[0]);
        objectOutputStream.writeObject(persons);
        objectOutputStream.close();
    }
    public void loadFromFile(File file) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Person[] persons = (Person[]) objectInputStream.readObject();
        people.clear();
        people.addAll(Arrays.asList(persons));
        objectInputStream.close();
    }
}
