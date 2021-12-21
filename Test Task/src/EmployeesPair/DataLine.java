package EmployeesPair;

import java.io.Serializable;

/*
    Object to hold data from each line in the sample data text file.
    It contains the Employee ID, the Project ID, the Date when the employee started working on the project
    and the Date when the employee stopped working on the project.
* */
class DataLine implements Serializable {
    private String  employeeID;
    private String projectID;
    private String dateStart;
    private String dateEnd;

    public DataLine() {}

    public DataLine(String employeeID, String projectID, String dateStart, String dateEnd) {
        this.employeeID = employeeID;
        this.projectID = projectID;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    @Override
    public String toString() {
        return ("\nEmployee ID: " + this.getEmployeeID() +
                ", Project ID: "+ this.getProjectID() +
                ", Start Date: "+ this.getDateStart() +
                ", End Date: " + this.getDateEnd());
    }
}

