package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.util.DataUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    CompensationService compensationService;

    @PostMapping("/compensation")
    public Compensation create(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for employee id [{}]", compensation.getEmployee().getEmployeeId());

        compensation.setEmployee(DataUtil.createEmployeeReference(compensation.getEmployee().getEmployeeId()));
        Compensation compensationCreated = compensationService.create(compensation);

        return compensationCreated;
    }


    @GetMapping("/compensation/{employeeId}")
    public Compensation read(@PathVariable String employeeId) {
        LOG.debug("Received compensation read request for employee id [{}]", employeeId);

        return compensationService.read(employeeId);
    }

}
