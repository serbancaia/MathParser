package com.alincaia.arithmeticparserv2.exceptions;

/**
 * Non matching parenthesis such as an opening without a closing and vice versa
 *
 * @author Ken Fogel
 */
public class NonMatchingParenthesis extends Exception {

    public NonMatchingParenthesis(String msg) {
        super(msg);
    }

}
