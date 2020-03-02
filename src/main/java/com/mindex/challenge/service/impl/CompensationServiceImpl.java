package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.util.DataUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        if ((compensation == null) || (compensation.getEmployee() == null) || StringUtils.isEmpty(compensation.getEmployee().getEmployeeId())) {
            throw (new RuntimeException("Invalid compensation to be created"));
        }

        // no requirement to check if employee exists but we could do it here

        Employee employeeReference = DataUtil.createEmployeeReference(compensation.getEmployee().getEmployeeId());
        Compensation existingCompensation = compensationRepository.findByEmployee(employeeReference);
        if (existingCompensation != null) {
            throw (new RuntimeException("Compensation already exists for employee with id " + employeeReference.getEmployeeId()));
        }

        compensation.setEmployee(employeeReference);
        Compensation compensationInserted = compensationRepository.insert(compensation);

        return compensationInserted;
    }


    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Reading compensation for employee with id [{}]", employeeId);

        Compensation compensation = compensationRepository.findByEmployee(DataUtil.createEmployeeReference(employeeId));

        if (compensation == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

        return compensation;
    }
}
