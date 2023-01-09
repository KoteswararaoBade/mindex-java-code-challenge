package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationIdUrl;

    private String employeeUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/employee/{id}";
        employeeUrl = "http://localhost:" + port + "/employee";
    }

    @Test
    public void testReadAndCreate() {

        Compensation compensation = new Compensation();
        compensation.setSalary(100000);
        compensation.setEffectiveDate("2020-01-01");

        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDepartment("Engineering");
        employee.setPosition("Developer");

        // create employee
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
        assertNotNull(createdEmployee.getEmployeeId());
        compensation.setEmployeeId(createdEmployee.getEmployeeId());

        // checking the creation of compensation
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();
        assertNotNull(createdCompensation.getEmployeeId());
        assertEquals(createdCompensation.getEmployeeId(), createdEmployee.getEmployeeId());

        // checking the read of compensation
        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, createdCompensation.getEmployeeId()).getBody();
        assertEquals(createdCompensation.getEmployeeId(), readCompensation.getEmployeeId());
    }

    @Test
    public void testWhenEmployeeIdIsNotPassed () {
        Compensation compensation = new Compensation();
        compensation.setSalary(100000);
        compensation.setEffectiveDate("2020-01-01");
        HttpStatus status = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getStatusCode();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status);
    }

    @Test
    public void testWhenEmployeeIdIsNotPassedInEmployeeObject () {
        Compensation compensation = new Compensation();
        compensation.setSalary(100000);
        compensation.setEffectiveDate("2023-01-01");

        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDepartment("Engineering");
        employee.setPosition("Developer");
        compensation.setEmployee(employee);
        HttpStatus status = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getStatusCode();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status);
    }
}
