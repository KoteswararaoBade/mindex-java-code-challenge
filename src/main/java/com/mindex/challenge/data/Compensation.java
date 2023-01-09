package com.mindex.challenge.data;

import org.springframework.data.annotation.Id;

public class Compensation {
    private Employee employee;
    @Id private String employeeId;
    private int salary;
    private String effectiveDate;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "Compensation [employee=" + employee + ", employeeId=" + employeeId + ", salary=" + salary
                + ", effectiveDate=" + effectiveDate + "]";
    }
}
