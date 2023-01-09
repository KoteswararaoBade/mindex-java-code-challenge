package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.exception.BadRequestException;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompensationServiceImpl implements CompensationService {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        // check for valid employee details in the request
        if (compensation.getEmployee() == null && compensation.getEmployeeId() == null) {
            throw new RuntimeException("Employee or employeeId is required");
        }

        String employeeId = null;
        if (compensation.getEmployee() == null) {
            employeeId = compensation.getEmployeeId();
        } else if (compensation.getEmployeeId() == null) {
            employeeId = compensation.getEmployee().getEmployeeId();
            if (employeeId == null) {
                throw new BadRequestException("Pass employeeId in employee object");
            }
            compensation.setEmployeeId(employeeId);
        }

        // Check if employee exists
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        if (employee == null) {
            throw new BadRequestException("Employee does not exist. Invalid employeeId: " + employeeId);
        }
        compensation.setEmployee(employee);
        compensationRepository.insert(compensation);

        return compensation;
    }

    @Override
    public Compensation read(String id) {
        LOG.debug("Creating compensation with id [{}]", id);

        Compensation compensation = compensationRepository.findByEmployeeId(id);

        if (compensation == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return compensation;
    }
}
