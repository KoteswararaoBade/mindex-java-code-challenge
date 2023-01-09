package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    /**
     * This method is used to calculate the number of direct reports for an employee
     * @param employeeId
     * @return
     */
    @Override
    public ReportingStructure readReportingStructure(String employeeId) {
        LOG.debug("Creating reporting structure for employee with id [{}]", employeeId);

        Employee employee = employeeRepository.findByEmployeeId(employeeId);

        // check if employee exists
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

        ReportingStructure reportingStructure = new ReportingStructure();
        // filling up the details of the employee
        fillDetailsOfDirectReports(employee);
        reportingStructure.setEmployee(employee);

        // calculating the number of direct reports
        reportingStructure.setNumberOfReports(getNumberOfReports(employee));

        return reportingStructure;
    }

    /**
     * This method is used to recursively fill the details of direct reports for an employee
     * @param employee
     * @return
     */
    private void fillDetailsOfDirectReports(Employee employee) {
        if (employee.getDirectReports() != null) {
            for (Employee directReport : employee.getDirectReports()) {
                // get the details of direct report
                Employee returned = employeeRepository.findByEmployeeId(directReport.getEmployeeId());
                directReport.setFirstName(returned.getFirstName());
                directReport.setLastName(returned.getLastName());
                directReport.setPosition(returned.getPosition());
                directReport.setDepartment(returned.getDepartment());
                directReport.setDirectReports(returned.getDirectReports());
                // recursively fill the details with direct reports of direct reports
                fillDetailsOfDirectReports(directReport);
            }
        }
    }

    /**
     * This method is used to recursively calculate the number of direct reports for an employee
     * @param employee
     * @return
     */
    public int getNumberOfReports(Employee employee) {
        int numberOfReports = 0;
        if (employee.getDirectReports() != null) {
            numberOfReports = employee.getDirectReports().size();
            for (Employee directReport : employee.getDirectReports()) {
                String id = directReport.getEmployeeId();
                Employee fullStructureOfDirectReport = employeeRepository.findByEmployeeId(id);

                // recursively calculate the number of direct reports
                numberOfReports += getNumberOfReports(fullStructureOfDirectReport);
            }
        }
        return numberOfReports;
    }
}
