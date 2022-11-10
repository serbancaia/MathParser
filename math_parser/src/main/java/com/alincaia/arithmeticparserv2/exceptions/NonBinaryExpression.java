package com.alincaia.arithmeticparserv2.exceptions;

/**
 * Pattern of operand, operator, operand is not respected
 *
 * @author Ken Fogel
 */
public class NonBinaryExpression extends Exception {

    public NonBinaryExpression(String msg) {
        super(msg);
    }

}
