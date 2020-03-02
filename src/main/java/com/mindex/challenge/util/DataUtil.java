package com.mindex.challenge.util;

import com.mindex.challenge.data.Employee;

public class DataUtil {

    public static Employee createEmployeeReference(String employeeId) {
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        return employee;
    }
}
