package com.alincaia.arithmeticparserv2;

import com.alincaia.arithmeticparserv2.exceptions.DivisionByZero;
import com.alincaia.arithmeticparserv2.exceptions.InvalidInputQueueString;
import com.alincaia.arithmeticparserv2.exceptions.NonBinaryExpression;
import com.alincaia.arithmeticparserv2.exceptions.NonMatchingParenthesis;
import com.alincaia.arithmeticparserv2.business.ResultParserWithParenthesis;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Final assignment 420-517
 *
 * @author Ken Fogel
 */
@RunWith(Parameterized.class)
public class ResultParserWithParenthesisTest {

    private final Queue<String> infixQueue;
    private final Queue<String> postfixQueue;
    private final double result;

    /**
     * A static method is required to hold all the data to be tested and the
     * expected results for each test. This data must be stored in a
     * two-dimension array. The 'name' attribute of Parameters is a JUnit 4.11
     * feature
     *
     * @return The list of arrays
     */
    @Parameterized.Parameters(name = "{index} calculation[{0}]={1}]")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {new ArrayDeque<>(Arrays.asList("2", "+", "3", "*", "4")), new ArrayDeque<>(Arrays.asList("2", "3", "4", "*", "+")), 14.0},
            {new ArrayDeque<>(Arrays.asList("(", "2", "+", "3", ")", "*", "4")), new ArrayDeque<>(Arrays.asList("2", "3", "+", "4", "*")), 20.0},
            {new ArrayDeque<>(Arrays.asList("(", "(", "2", "+", "3", ")", "*", "(", "2", "+", "3", ")", ")")), new ArrayDeque<>(Arrays.asList("2", "3", "+", "2", "3", "+", "*")), 25.0},
            {new ArrayDeque<>(Arrays.asList("(", "(", "2", "+", "3", ")", "*", "2", "+", "(", "2", "+", "3", ")", ")")), new ArrayDeque<>(Arrays.asList("2", "3", "+", "2", "*", "2", "3", "+", "+")), 15.0},
            {new ArrayDeque<>(Arrays.asList("(", "(", "2", "+", "3", ")", "*", "2", "+", "(", "2", "+", "3", "*", "3", ")", ")")), new ArrayDeque<>(Arrays.asList("2", "3", "+", "2", "*", "2", "3", "3", "*", "+", "+")), 21.0},
            {new ArrayDeque<>(Arrays.asList("2", "+", "-3.15")), new ArrayDeque<>(Arrays.asList("2", "-3.15", "+")), -1.15},
            {new ArrayDeque<>(Arrays.asList("-(", "2", "+", "3", ")")), new ArrayDeque<>(Arrays.asList("-1", "2", "3", "+", "*")), -5.0},
            {new ArrayDeque<>(Arrays.asList("4", "+", "(", "1", ")", "+", "3")), new ArrayDeque<>(Arrays.asList("4", "1", "+", "3", "+")), 8.0},
            {new ArrayDeque<>(Arrays.asList("+(", "3", "+", "4", ")", "(", "3", "*", "2", ")")), new ArrayDeque<>(Arrays.asList("3", "4", "+", "3", "2", "*", "*")), 42.0},
            {new ArrayDeque<>(Arrays.asList("+(", "3", "+", "4", ")", "(", "3", "*", "2", ")")), new ArrayDeque<>(Arrays.asList("3", "4", "+", "3", "2", "*", "*")), 42.0},
            {new ArrayDeque<>(Arrays.asList("(", "(", "7", "*", "2", "(", "5", "-", "3", ")", "*", "5", ")", "/", "4", ")")), new ArrayDeque<>(Arrays.asList("7", "2", "*", "5", "3", "-", "*", "5", "*", "4", "/")), 35},
            {new ArrayDeque<>(Arrays.asList("9", "*", "8", "*", "7", "*", "6", "*", "5", "*", "4", "*", "3", "/", "2")), new ArrayDeque<>(Arrays.asList("9", "8", "*", "7", "*", "6", "*", "5", "*", "4", "*", "3", "*", "2", "/")), 90720},
            {new ArrayDeque<>(Arrays.asList("5", "(", "5", "*", "5", "/", "12.5", ")", "+", "9")), new ArrayDeque<>(Arrays.asList("5", "5", "5", "*", "12.5", "/", "*", "9", "+")), 19},
            {new ArrayDeque<>(Arrays.asList("-4", "*", "-3", "(", "5", "+", "3", "(", "5", "+", "3", ")", ")")), new ArrayDeque<>(Arrays.asList("-4", "-3", "*", "5", "3", "5", "3", "+", "*", "+", "*")), 348},
            {new ArrayDeque<>(Arrays.asList("(", "9", "+", "2", ")", "(", "5", "+", "3", ")", "(", "66", "/", "2", ")")), new ArrayDeque<>(Arrays.asList("9", "2", "+", "5", "3", "+", "*", "66", "2", "/", "*")), 2904},
            {new ArrayDeque<>(Arrays.asList("75", "*", "2", "(", "75", "/", "3", ")", "(", "12", "*", "3", ")")), new ArrayDeque<>(Arrays.asList("75", "2", "*", "75", "3", "/", "*", "12", "3", "*", "*")), 135000},
            {new ArrayDeque<>(Arrays.asList("10", "*", "9", "/", "8", "*", "7", "/", "6", "*", "5", "/", "4", "*", "3", "/", "2")), new ArrayDeque<>(Arrays.asList("10", "9", "*", "8", "/", "7", "*", "6", "/", "5", "*", "4", "/", "3", "*", "2", "/")), 24.609375},
            {new ArrayDeque<>(Arrays.asList("(", "3", "+", "2", ")", "(", "-3", "-", "3", "-", "3", ")")), new ArrayDeque<>(Arrays.asList("3", "2", "+", "-3", "3", "-", "3", "-", "*")), -45},
            {new ArrayDeque<>(Arrays.asList("99", "-", "98", "-", "97", "-", "96", "(", "-2", "+", "5", ")")), new ArrayDeque<>(Arrays.asList("99", "98", "-", "97", "-", "96", "-2", "5", "+", "*", "-")), -384},
            {new ArrayDeque<>(Arrays.asList("(", "-5", ")", "(", "-5", ")", "/", "5")), new ArrayDeque<>(Arrays.asList("-5", "-5", "*", "5", "/")), 5}
        });
    }

    private ResultParserWithParenthesis rp;

    /**
     * Constructor that receives the parameters for testing
     *
     * @param infixQueue
     * @param postfixQueue
     * @param result
     */
    public ResultParserWithParenthesisTest(Queue<String> infixQueue, Queue<String> postfixQueue, double result) {
        this.infixQueue = infixQueue;
        this.postfixQueue = postfixQueue;
        this.result = result;
    }

    /**
     * Instantiate the class to be tested
     */
    @Before
    public void setUp() {
        rp = new ResultParserWithParenthesis();
    }

    /**
     * Test of evaluation of postfix to result
     *
     * @throws NonMatchingParenthesis
     * @throws InvalidInputQueueString
     * @throws NonBinaryExpression
     * @throws DivisionByZero
     */
    @Test
    public void testResult() throws NonMatchingParenthesis, InvalidInputQueueString, NonBinaryExpression, DivisionByZero {
        assertEquals("Incorrect result", result, Double.parseDouble(rp.evaluatePostfix(postfixQueue)), 0.001);
    }

    /**
     * Test of conversion from infix to postfix
     *
     * @throws NonMatchingParenthesis
     * @throws InvalidInputQueueString
     * @throws NonBinaryExpression
     * @throws DivisionByZero
     */
    @Test
    public void testToPostFix() throws NonMatchingParenthesis, InvalidInputQueueString, NonBinaryExpression, DivisionByZero {
        assertArrayEquals("Incorrect postfix", postfixQueue.toArray(), rp.convertInfixToPostfix(infixQueue).toArray());
    }
}
