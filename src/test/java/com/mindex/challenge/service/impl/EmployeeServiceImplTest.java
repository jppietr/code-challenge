package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.util.DataUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;

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
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    @Test
    public void testGetReportingStructure() {
        Employee employee1 = restTemplate.postForEntity(employeeUrl, generateRandomEmployee(), Employee.class).getBody();
        ReportingStructure employee1ReportingStructure = restTemplate.getForEntity(employeeIdUrl + "/reportingStructure", ReportingStructure.class, employee1.getEmployeeId()).getBody();
        assertEmployeeEquivalence(employee1, employee1ReportingStructure.getEmployee());
        assertEquals(0, employee1ReportingStructure.getNumberOfReports());

        Employee employee2 = restTemplate.postForEntity(employeeUrl, generateRandomEmployee(), Employee.class).getBody();
        ReportingStructure employee2ReportingStructure = restTemplate.getForEntity(employeeIdUrl + "/reportingStructure", ReportingStructure.class, employee2.getEmployeeId()).getBody();
        assertEmployeeEquivalence(employee2, employee2ReportingStructure.getEmployee());
        assertEquals(0, employee2ReportingStructure.getNumberOfReports());

        Employee testManager = generateRandomEmployee();
        testManager.setDirectReports(Arrays.asList(DataUtil.createEmployeeReference(employee1.getEmployeeId()), DataUtil.createEmployeeReference(employee2.getEmployeeId())));
        Employee manager = restTemplate.postForEntity(employeeUrl, testManager, Employee.class).getBody();
        ReportingStructure managerReportingStructure = restTemplate.getForEntity(employeeIdUrl + "/reportingStructure", ReportingStructure.class, manager.getEmployeeId()).getBody();
        assertEmployeeEquivalence(manager, managerReportingStructure.getEmployee());
        assertEquals(2, managerReportingStructure.getNumberOfReports());

        Employee testCeo = generateRandomEmployee();
        testCeo.setDirectReports(Arrays.asList(DataUtil.createEmployeeReference(manager.getEmployeeId())));
        Employee ceo = restTemplate.postForEntity(employeeUrl, testCeo, Employee.class).getBody();
        ReportingStructure ceoReportingStructure = restTemplate.getForEntity(employeeIdUrl + "/reportingStructure", ReportingStructure.class, ceo.getEmployeeId()).getBody();
        assertEmployeeEquivalence(ceo, ceoReportingStructure.getEmployee());
        assertEquals(3, ceoReportingStructure.getNumberOfReports());
    }

    private Employee generateRandomEmployee() {
        Employee employee = new Employee();
        employee.setFirstName(UUID.randomUUID().toString());
        employee.setLastName(UUID.randomUUID().toString());
        employee.setDepartment(UUID.randomUUID().toString());
        employee.setPosition(UUID.randomUUID().toString());
        return employee;
    }
}
