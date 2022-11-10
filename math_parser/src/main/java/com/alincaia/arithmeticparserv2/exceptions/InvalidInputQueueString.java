package com.alincaia.arithmeticparserv2.exceptions;

/**
 * Named exception for invalid strings in the Input Queue. Only operands,
 * operators and parenthesis are allowed
 *
 * @author Ken Fogel
 */
public class InvalidInputQueueString extends Exception {

    public InvalidInputQueueString(String msg) {
        super(msg);
    }

}
