package com.alincaia.arithmeticparserv2.exceptions;

/**
 * In division the divisor equals zero
 *
 * @author Ken Fogel
 */
public class DivisionByZero extends Exception {
    
    public DivisionByZero(String msg) {
        super(msg);
    }

}
