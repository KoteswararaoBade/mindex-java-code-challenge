package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;

    private String employeeReportUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeReportUrl = "http://localhost:" + port + "/reportingStructure/employee/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);

        // read updated employee
        Employee readUpdatedEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, updatedEmployee.getEmployeeId()).getBody();
        assertEquals(updatedEmployee.getEmployeeId(), readUpdatedEmployee.getEmployeeId());
    }

    @Test
    public void testReportingStructureForZeroDirectReports() {
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDepartment("Engineering");
        employee.setPosition("Developer");

        // Zero direct reports
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
        assertNotNull(createdEmployee.getEmployeeId());

        ReportingStructure reportingStructure = restTemplate.getForEntity(employeeReportUrl, ReportingStructure.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(0, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testReportingStructureForMoreDirectReports() {
        String employeeId1 = createEmployeeId("Pete", "Best", "Developer II", "Engineering",
                null, employeeUrl, restTemplate);
        String employeeId2 = createEmployeeId("George", "Harrison", "Developer III", "Engineering",
                null, employeeUrl, restTemplate);

        List<Employee> directReportsForEmployee3 = new ArrayList<>();
        Employee directReport1 = new Employee();
        directReport1.setEmployeeId(employeeId1);
        directReportsForEmployee3.add(directReport1);
        Employee directReport2 = new Employee();
        directReport2.setEmployeeId(employeeId2);
        directReportsForEmployee3.add(directReport2);
        String employeeId3 = createEmployeeId("Ringo", "Starr", "Developer V", "Engineering",
                directReportsForEmployee3, employeeUrl, restTemplate);

        String employeeId4 = createEmployeeId("Paul", "McCartney", "Developer I", "Engineering",
                null, employeeUrl, restTemplate);

        List<Employee> directReportsForEmployee5 = new ArrayList<>();
        Employee directReport3 = new Employee();
        directReport3.setEmployeeId(employeeId3);
        directReportsForEmployee5.add(directReport3);
        Employee directReport4 = new Employee();
        directReport4.setEmployeeId(employeeId4);
        directReportsForEmployee5.add(directReport4);
        String employeeId5 = createEmployeeId("John", "Lennon", "Developer IV", "Engineering",
                directReportsForEmployee5, employeeUrl, restTemplate);

        ReportingStructure reportingStructure1 = restTemplate.getForEntity(employeeReportUrl, ReportingStructure.class, employeeId1).getBody();
        assertEquals(0, reportingStructure1.getNumberOfReports());

        ReportingStructure reportingStructure2 = restTemplate.getForEntity(employeeReportUrl, ReportingStructure.class, employeeId2).getBody();
        assertEquals(0, reportingStructure2.getNumberOfReports());

        ReportingStructure reportingStructure3 = restTemplate.getForEntity(employeeReportUrl, ReportingStructure.class, employeeId3).getBody();
        assertEquals(2, reportingStructure3.getNumberOfReports());

        ReportingStructure reportingStructure4 = restTemplate.getForEntity(employeeReportUrl, ReportingStructure.class, employeeId4).getBody();
        assertEquals(0, reportingStructure4.getNumberOfReports());

        ReportingStructure reportingStructure5 = restTemplate.getForEntity(employeeReportUrl, ReportingStructure.class, employeeId5).getBody();
        assertEquals(4, reportingStructure5.getNumberOfReports());

        // create a new employee and add that employee to the direct reports of employee 1
        String employeeId6 = createEmployeeId("John", "Doe", "Developer", "Engineering",
                null, employeeUrl, restTemplate);

        Employee employeePet = new Employee();
        employeePet.setEmployeeId(employeeId1);
        employeePet.setFirstName("Pete");
        employeePet.setLastName("Best");
        employeePet.setPosition("Developer II");
        employeePet.setDepartment("Engineering");
        List<Employee> directReportsForEmployeePet = new ArrayList<>();
        Employee directReport5 = new Employee();
        directReport5.setEmployeeId(employeeId6);
        directReportsForEmployeePet.add(directReport5);
        employeePet.setDirectReports(directReportsForEmployeePet);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(employeePet, headers),
                        Employee.class,
                        employeePet.getEmployeeId()).getBody();

        assertEquals(employeePet.getEmployeeId(), updatedEmployee.getEmployeeId());

        ReportingStructure reportingStructure6 = restTemplate.getForEntity(employeeReportUrl, ReportingStructure.class, employeeId1).getBody();
        assertEquals(1, reportingStructure6.getNumberOfReports());

        reportingStructure3 = restTemplate.getForEntity(employeeReportUrl, ReportingStructure.class, employeeId3).getBody();
        assertEquals(3, reportingStructure3.getNumberOfReports());

        reportingStructure5 = restTemplate.getForEntity(employeeReportUrl, ReportingStructure.class, employeeId5).getBody();
        assertEquals(5, reportingStructure5.getNumberOfReports());
    }

    private static String createEmployeeId(String firstName, String lastName,
                                           String position, String department,
                                           List<Employee> directReports, String employeeUrl, TestRestTemplate restTemplate) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setDirectReports(directReports);

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
        return createdEmployee.getEmployeeId();
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
