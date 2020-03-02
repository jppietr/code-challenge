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

    @Override
    public ReportingStructure getReportingStructure(String employeeId) {
        LOG.debug("Retrieving reporting structure for employee with id [{}]", employeeId);

        Employee employee = read(employeeId);
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(getNumberOfReports(employee));

        return reportingStructure;
    }

    protected int getNumberOfReports(Employee employee) {

        int numOfReports = 0;
        if ((employee == null) || (employee.getDirectReports() == null)) {
            return numOfReports;
        }

        numOfReports = employee.getDirectReports().size();
        for (Employee directReportReference : employee.getDirectReports()) {
            Employee directReport = read(directReportReference.getEmployeeId());
            numOfReports += getNumberOfReports(directReport);
        }

        return numOfReports;
    }
}
