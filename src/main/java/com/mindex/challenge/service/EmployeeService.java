package com.mindex.challenge.service;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;

public interface EmployeeService {
    Employee create(Employee employee);
    Employee read(String id);
    Employee update(Employee employee);

    /**
     * This method is used to read the reporting structure for an employee
     * @param employeeId
     * @return
     */
    ReportingStructure readReportingStructure(String employeeId);
}
