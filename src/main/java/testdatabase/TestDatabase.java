package testdatabase;

import model.*;

import java.sql.SQLException;

public class TestDatabase {
    public static void main(String[] args) {

        Database db = new Database();
        System.out.println("Running Database Test");
        try {
            db.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.addPerson(new Person("Joe", "lion tamer",
                AgeCategory.adult, EmploymentCategory.employed, "777", true, Gender.male));
        db.addPerson(new Person("Sue", "housewife",
                AgeCategory.senior, EmploymentCategory.selfEmployed, null, false, Gender.female));
        try {
            db.save();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.load();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.disconnect();
    }
}
