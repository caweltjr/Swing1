package controller;

import gui.FormEvent;
import model.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Controller {
    Database db = new Database();

    public List<Person> getPeople(){
        return db.getPeople();
    }

    public void configure(int port, String user, String password) throws Exception {
        db.configure(port, user, password);
    }
    public void save() throws SQLException {
        db.save();
    }
    public void disconnect() throws SQLException {
        db.disconnect();
    }
    public void connect() throws Exception {
        db.connect();
    }
    public void load() throws Exception {
        db.load();
    }
    public void addPerson(FormEvent ev){
        String name = ev.getName();
        String occupation = ev.getOccupation();
        int ageCat = ev.getAgeCat();
        String empCat = ev.getEmpCat();
        boolean isUS = ev.isUsCitizen();
        String taxId = ev.getTaxId();
        String gender = ev.getGenderText();

        AgeCategory ageCategory;
        switch (ageCat){
            case 0:
                ageCategory = AgeCategory.child;
                break;
            case 1:
                ageCategory = AgeCategory.adult;
                break;
            case 2:
                ageCategory = AgeCategory.senior;
                break;
            default:
                ageCategory = AgeCategory.adult;

        }
        EmploymentCategory employmentCategory;
        if ("employed".equals(empCat)) {
            employmentCategory = EmploymentCategory.employed;
        } else if ("self-employed".equals(empCat)) {
            employmentCategory = EmploymentCategory.selfEmployed;
        } else if ("unemployed".equals(empCat)) {
            employmentCategory = EmploymentCategory.unEmployed;
        } else {
            employmentCategory = EmploymentCategory.other;
        }
        Gender gender1;
        if ("male".equals(gender)) {
            gender1 = Gender.male;
        } else if ("female".equals(gender)) {
            gender1 = Gender.female;
        } else {
            gender1 = Gender.male;
        }
        Person person = new Person(name, occupation, ageCategory,
                employmentCategory, taxId, isUS, gender1);
        db.addPerson(person);
    }
    public void saveToFile(File file) throws IOException {
        db.saveToFile(file);
    }
    public void loadFromFile(File file) throws IOException, ClassNotFoundException {
        db.loadFromFile(file);
    }
    public void removePerson(int row){
        db.removePerson(row);
    }
}
