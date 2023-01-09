package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;

public interface CompensationService {

    /**
     * Method to create a compensation record in the database.
     * This function throws a BadRequestException if at least one of the employeeId and the employee objects are not passed.
     * Also in case when the employeeId is passed, it checks if the employee exists in the database.
     * @param compensation object to be created
     * user does not need to send the whole employee details just employeeId is enough.
     * employee details will always be fetched from the database and set to the compensation object.
     * @return the created compensation object
     */
    Compensation create(Compensation compensation);

    /**
     * Method to read a compensation record from the database.
     * Throws RuntimeException if employee does not exist in the database.
     * @param employeeId for which the compensation record is to be fetched
     * @return the compensation object
     */
    Compensation read(String employeeId);
}
