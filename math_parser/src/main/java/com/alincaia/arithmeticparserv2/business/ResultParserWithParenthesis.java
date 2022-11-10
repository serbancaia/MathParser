package com.alincaia.arithmeticparserv2.business;

import com.alincaia.arithmeticparserv2.exceptions.DivisionByZero;
import com.alincaia.arithmeticparserv2.exceptions.InvalidInputQueueString;
import com.alincaia.arithmeticparserv2.exceptions.NonBinaryExpression;
import com.alincaia.arithmeticparserv2.exceptions.NonMatchingParenthesis;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * This class is used to convert an infixQueue into a postfixQueue and to
 * evaluate a postfixQueue
 *
 * @author Alin Caia
 * @version 12/6/2020
 */
public class ResultParserWithParenthesis {

    private final static int FIRST_OPERAND_INDEX = 0;
    private final static int OPERATOR_INDEX = 1;
    private final static int SECOND_OPERAND_INDEX = 2;

    private final static int EXPONENT_LEVEL = 3;
    private final static int MULTIPLICATION_DIVISION_LEVEL = 2;
    private final static int PLUS_MINUS_LEVEL = 1;

    private final static int INFIX_OPERATOR_HIGHER_PRECEDENCE = 1;
    private final static int INFIX_OPERATOR_LOWER_PRECEDENCE = -1;

    /**
     * This method converts an infixQueue to a postfixQueue
     *
     * @param infixQueue
     * @return infixQueue converted to postfixQueue
     * @throws NonMatchingParenthesis
     * @throws InvalidInputQueueString
     * @throws NonBinaryExpression
     */
    public Queue<String> convertInfixToPostfix(Queue<String> infixQueue) throws NonMatchingParenthesis, InvalidInputQueueString, NonBinaryExpression {

        if (!checkInfixQueueParenthesesMatch(infixQueue)) {
            throw new NonMatchingParenthesis("The infixQueue's parentheses didn't match");
        }

        if (!checkInvalidInputQueueString(infixQueue)) {
            throw new InvalidInputQueueString("The infixQueue had invalid input");
        }

        if (!checkInfixQueueIsBinaryExpr(infixQueue)) {
            throw new NonBinaryExpression("The infixQueue wasn't a binary expression");
        }

        Queue<String> postfixQueue = new ArrayDeque<>();
        Stack operatorStack = new Stack();

        String lastToken = "";

        for (String token : infixQueue) {
            if (checkStringIsRealNumber(token)) {
                postfixQueue.add(token);
            } else if (token.equals("-(")) {
                postfixQueue.add("-1");
                operatorStack.push("-(");
            } else if (token.equals("(")) {
                if (checkStringIsRealNumber(lastToken)) {
                    if (!operatorStack.isEmpty() && getPrecedence(operatorStack.peek().toString()) > PLUS_MINUS_LEVEL) {
                        postfixQueue.add(operatorStack.pop().toString());
                    }
                }
                operatorStack.push("(");
                if (checkStringIsRealNumber(lastToken) || lastToken.equals(")")) {
                    operatorStack.push(")*(");
                }
            } else if (token.equals("+(")) {
                operatorStack.push("(");
            } else if (token.equals(")")) {
                postfixQueue.addAll(popParenthesisOperators(operatorStack));
            } else if (token.equals("^") || token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-")) {
                if (operatorStack.isEmpty() || operatorStack.peek().toString().equals("(") || operatorStack.peek().toString().equals("-(") || operatorStack.peek().toString().equals(")*(")) {
                    operatorStack.push(token);
                } else {
                    while (!operatorStack.isEmpty() && !operatorStack.peek().toString().equals("(") && !operatorStack.peek().toString().equals("-(") && !operatorStack.peek().toString().equals(")*(") && compareOperators(token, operatorStack.peek().toString()) == INFIX_OPERATOR_LOWER_PRECEDENCE) {
                        postfixQueue.add(operatorStack.pop().toString());
                    }
                    operatorStack.push(token);
                }
            }
            lastToken = token;
        }

        if (!operatorStack.isEmpty()) {
            for (int i = 0; i <= operatorStack.size(); i++) {
                postfixQueue.add(operatorStack.pop().toString());
            }
        }

        return postfixQueue;
    }

    /**
     * Calculates the result of the postfixQueue and returns it as a String
     *
     * @param postfixQueue
     * @return result of postfixQueue as String
     * @throws DivisionByZero
     */
    public String evaluatePostfix(Queue<String> postfixQueue) throws DivisionByZero {

        Stack operands = new Stack();
        List<String> expressionArray = new ArrayList<>();

        for (String token : postfixQueue) {
            if (checkStringIsRealNumber(token)) {
                operands.push(token);
            } else if (token.equals("^") || token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-")) {
                String secondOperand = operands.pop().toString();
                String firstOperand = operands.pop().toString();

                if (token.equals("/") && Double.parseDouble(secondOperand) == 0.0) {
                    throw new DivisionByZero("The postfixQueue tried to divide by 0");
                }

                expressionArray.add(firstOperand);
                expressionArray.add(token);
                expressionArray.add(secondOperand);

                operands.push(calculateExpressionArray(expressionArray));

                expressionArray.clear();
            }
        }

        return operands.pop().toString();
    }

    /**
     * Pops all the operators in the operator stack that are after the last
     * opening parenthesis in the inputted operator stack
     *
     * @param operatorStack
     * @return all operators above last opening parenthesis in operator stack
     */
    private Collection<? extends String> popParenthesisOperators(Stack operatorStack) {

        List<String> stackTokens = new ArrayList<>();
        String token = operatorStack.peek().toString();

        while (!token.equals("(") && !token.equals("-(")) {
            if (token.equals(")*(")) {
                operatorStack.pop();
                stackTokens.add("*");
            } else {
                stackTokens.add(operatorStack.pop().toString());
            }
            token = operatorStack.peek().toString();
        }

        if (token.equals("-(")) {
            stackTokens.add("*");
        }

        operatorStack.pop();

        return stackTokens;
    }

    /**
     * Compares the precedence level of two given operators
     *
     * @param infixOperator
     * @param stackOperator
     * @return 1 if infixOperator has higher precedence, or -1 if not
     */
    private int compareOperators(String infixOperator, String stackOperator) {
        if (getPrecedence(infixOperator) == EXPONENT_LEVEL) {
            return INFIX_OPERATOR_HIGHER_PRECEDENCE;
        } else if (getPrecedence(infixOperator) == getPrecedence(stackOperator)) {
            return INFIX_OPERATOR_LOWER_PRECEDENCE;
        } else if (getPrecedence(infixOperator) == MULTIPLICATION_DIVISION_LEVEL) {
            return INFIX_OPERATOR_HIGHER_PRECEDENCE;
        } else {
            return INFIX_OPERATOR_LOWER_PRECEDENCE;
        }
    }

    /**
     * Gets the precedence of a given operator_INDEX (PLUS_MINUS_LEVEL being the
     * lowest level and EXPONENT_LEVEL being the highest)
     *
     * @param operator
     * @return operator's precedence
     */
    private int getPrecedence(String operator) {
        return switch (operator) {
            case "^" ->
                EXPONENT_LEVEL;
            case "*", "/" ->
                MULTIPLICATION_DIVISION_LEVEL;
            default ->
                PLUS_MINUS_LEVEL;
        };
    }

    /**
     * Calculates the result of an expression and returns it as a String
     *
     * @param expressionArray
     * @return result of expression as String
     */
    private String calculateExpressionArray(List<String> expressionArray) {
        switch (expressionArray.get(OPERATOR_INDEX)) {
            case "^" -> {
                Double result = Double.parseDouble(expressionArray.get(FIRST_OPERAND_INDEX));

                for (int i = 0; i < Double.parseDouble(expressionArray.get(SECOND_OPERAND_INDEX)); i++) {
                    result *= Double.parseDouble(expressionArray.get(FIRST_OPERAND_INDEX));
                }

                return String.valueOf(result);
            }
            case "*" -> {
                return String.valueOf(Double.parseDouble(expressionArray.get(FIRST_OPERAND_INDEX)) * Double.parseDouble(expressionArray.get(SECOND_OPERAND_INDEX)));
            }
            case "/" -> {
                return String.valueOf(Double.parseDouble(expressionArray.get(FIRST_OPERAND_INDEX)) / Double.parseDouble(expressionArray.get(SECOND_OPERAND_INDEX)));
            }
            case "+" -> {
                return String.valueOf(Double.parseDouble(expressionArray.get(FIRST_OPERAND_INDEX)) + Double.parseDouble(expressionArray.get(SECOND_OPERAND_INDEX)));
            }
            default -> {
                return String.valueOf(Double.parseDouble(expressionArray.get(FIRST_OPERAND_INDEX)) - Double.parseDouble(expressionArray.get(SECOND_OPERAND_INDEX)));
            }
        }
    }

    /**
     * Checks whether given string number is a real number
     *
     * @param realNumberString
     * @return true if real number, false if not
     */
    private boolean checkStringIsRealNumber(String realNumberString) {
        try {
            Double.parseDouble(realNumberString);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    /**
     * Checks whether the given infixQueue contains matching parentheses
     *
     * @param infixQueue
     * @return true if parentheses match, false if not
     */
    private boolean checkInfixQueueParenthesesMatch(Queue<String> infixQueue) {

        int nbOpeningParentheses = 0;
        int nbClosingParentheses = 0;

        for (String token : infixQueue) {
            if (token.equals("(") || token.equals("+(") || token.equals("-(")) {
                nbOpeningParentheses++;
            } else if (token.equals(")")) {
                nbClosingParentheses++;
            }
        }

        return nbOpeningParentheses == nbClosingParentheses;
    }

    /**
     * Checks whether the given infixQueue has valid input
     *
     * @param infixQueue
     * @return true if infixQueue has invalid input, false if not
     */
    private boolean checkInvalidInputQueueString(Queue<String> infixQueue) {

        if (!infixQueue.stream().noneMatch(token -> (!checkStringIsRealNumber(token) && !token.equals("^") && !token.equals("*") && !token.equals("/") && !token.equals("+") && !token.equals("-") && !token.equals(")") && !token.equals("(") && !token.equals("+(") && !token.equals("-(")))) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether the given infixQueue is a binary expression
     *
     * @param infixQueue
     * @return true if binary expression, false if not
     */
    private boolean checkInfixQueueIsBinaryExpr(Queue<String> infixQueue) {

        String lastToken = "";

        if (infixQueue.peek().equals("^") || infixQueue.peek().equals("*") || infixQueue.peek().equals("/") || infixQueue.peek().equals("+") || infixQueue.peek().equals("-")) {
            return false;
        }

        for (String token : infixQueue) {

            if (token.equals(")") && (lastToken.equals("(") || lastToken.equals("+(") || lastToken.equals("-("))) {
                return false;
            } else if ((token.equals("^") || token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-")) && (lastToken.equals("(") || lastToken.equals("+(") || lastToken.equals("-("))) {
                return false;
            } else if (token.equals(")") && (lastToken.equals("^") || lastToken.equals("*") || lastToken.equals("/") || lastToken.equals("+") || lastToken.equals("-"))) {
                return false;
            } else if ((token.equals("^") || token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-")) && (lastToken.equals("^") || lastToken.equals("*") || lastToken.equals("/") || lastToken.equals("+") || lastToken.equals("-"))) {
                return false;
            } else if (checkStringIsRealNumber(token) && checkStringIsRealNumber(lastToken)) {
                return false;
            }

            lastToken = token;
        }

        if (lastToken.equals("^") || lastToken.equals("*") || lastToken.equals("/") || lastToken.equals("+") || lastToken.equals("-")) {
            return false;
        }

        return true;
    }
}
