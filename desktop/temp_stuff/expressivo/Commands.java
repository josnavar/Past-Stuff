/* Copyright (c) 2015 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import java.util.Map;

/**
 * String-based commands provided by the expression system.
 * 
 * <p>PS3 instructions: this is a required class.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 */
public class Commands {
    // PS3 instructions: the Commands class should contain only static methods,
    // not any instance methods, instance fields, or static fields.
    
    /**
     * Differentiate an expression with respect to a variable.
     * @param expression the expression to differentiate
     * @param variable the variable to differentiate by, a case-sensitive nonempty string of letters.
     * @return expression's derivative with respect to variable.  Must be a valid expression equal
     *         to the derivative, but doesn't need to be in simplest or canonical form.
     * @throws IllegalArgumentException if the expression or variable is invalid
     */
    public static String differentiate(String expression, String variable) {
        try{
            Expression expressionRep=Expression.parse(expression);
            Expression withRespectTo=Expression.parse(variable);
            Expression differentiated=expressionRep.differentiate(withRespectTo);
            return differentiated.toString();
        } catch(IllegalArgumentException error)
        {
            throw error;
        }
        
        
    }
    
    /**
     * Simplify an expression.
     * @param expression the expression to simplify
     * @param environment maps variables to values.  Variables are required to be case-sensitive nonempty 
     *         strings of letters.  The set of variables in environment is allowed to be different than the 
     *         set of variables actually found in expression.
     * @return an expression equal to the input, but after substituting every variable v that appears in both
     *         the expression and the environment with its value, environment.get(v).  Additional simplifications 
     *         to the expression may be done at the implementor's discretion.
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static String simplify(String expression, Map<String,Double> environment) {
        try{
            Expression expression1=Expression.parse(expression);
            return expression1.simplify(environment).toString();
        } catch(IllegalArgumentException error)
        {
            throw error;
        }
    }
    
    
    
    
}
