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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Final assignment 420-517
 *
 * @author Ken Fogel
 */
@RunWith(Parameterized.class)
public class NonMatchingParenthesisTest {

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
            {new ArrayDeque<>(Arrays.asList("2", "+", "3", "("))},
            {new ArrayDeque<>(Arrays.asList("(", "2", "+", "3", "/", "(", "7", "-", "7"))},
            {new ArrayDeque<>(Arrays.asList("14", "/", "(", "9", "/", "2", "*", "0"))}
        });
    }

    private ResultParserWithParenthesis rp;
    private final Queue<String> infixQueue;

    /**
     * Constructor that receives the parameters for testing
     *
     * @param infixQueue
     */
    public NonMatchingParenthesisTest(Queue<String> infixQueue) {
        this.infixQueue = infixQueue;
    }

    /**
     * Initialize the test object and the input queue
     */
    @Before
    public void setUp() {
        rp = new ResultParserWithParenthesis();
    }

    /**
     * Conversion of infixQueue to postfixQueue should throw a
     * NonMatchingParenthesis exception
     *
     * @throws NonMatchingParenthesis
     * @throws InvalidInputQueueString
     * @throws DivisionByZero
     * @throws NonBinaryExpression
     */
    @Test(expected = NonMatchingParenthesis.class)
    public void testNonMatchingParenthesisTest() throws NonMatchingParenthesis, InvalidInputQueueString, NonBinaryExpression, DivisionByZero {
        rp.convertInfixToPostfix(infixQueue);
        fail("NonMatchingParenthesis Excption was not thrown");
    }
}
