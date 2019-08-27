package model;

public enum EmploymentCategory {
    employed("Employed"),
    selfEmployed("Self-Employed"),
    unEmployed("Unemployed"),
    other("Other");

    private String text;

    EmploymentCategory(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
