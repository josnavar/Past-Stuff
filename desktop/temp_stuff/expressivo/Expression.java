/* Copyright (c) 2015 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;


import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import expressivo.parser.ExpressionLexer;
import expressivo.parser.ExpressionListener;
import expressivo.parser.ExpressionParser;

/**
 * An immutable data type representing a polynomial expression of:
 *   + and *
 *   nonnegative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 * 
 * <p>PS3 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 */
public interface Expression {
    
    // Datatype definition
    //Expression= Scalar(double)+Variable(String)+ Product(Expression 1,Expression 2)+Sum(Expression 3,Expression 4)
    
    /**
     * @param scalar Takes in a non-negative double that represents a scalar in the expression.
     * @return Expression An expression that consists solely of that scalar double.
     */
    public static Expression makeScalar(double scalar)
    {
        return new Scalar(scalar);
    }
    /**
     * @param string takes in a non-empty string with no whitespace in it and is composed of letters of lower or upper case.
     * @return Expression that consists of string variable. 
     */
    public static Expression makeVariable(String string)
    {
        return new Variable(string);
    }
    
    /**
     * 
     * @param expression1 
     * @param expression2
     * @return an Expression which represents the product: expression1*expression2
     */
    public static Expression makeProduct(Expression expression1,Expression expression2)
    {
        return new Product(expression1,expression2);
    }
    /**
     * 
     * @param expression1
     * @param expression2
     * @return an Expression which represents the sum: expression1+expression2
     */
    public static Expression makeSum(Expression expression1,Expression expression2)
    {
        return new Sum(expression1,expression2);
    }
    public List<Expression> getElements();
    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS3 handout. When using an operator it must connect two expressions.Every Parenthesis needs a pair parenthesis to close.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        try{
            CharStream stream= new ANTLRInputStream(input);
            ExpressionLexer lexer=new ExpressionLexer(stream);
            lexer.reportErrorsAsExceptions();
            TokenStream tokens = new CommonTokenStream(lexer);
            ExpressionParser parser= new ExpressionParser(tokens);
            parser.reportErrorsAsExceptions();
            ParseTree tree =parser.root();
            ParseTreeWalker walker= new ParseTreeWalker();
            CollectTerms listener= new CollectTerms();
            walker.walk(listener,tree); 
            return listener.getResult(); 
        }
        catch (RuntimeException error)
        {
            IllegalArgumentException newError=new IllegalArgumentException();
            throw newError;
        } 
    }
    class CollectTerms implements ExpressionListener
    {
        private Stack<Expression> stack=new Stack<>();
        
        private Expression getResult()
        {
            return stack.get(0);
        }
        @Override
        public void exitSum(ExpressionParser.SumContext ctx)
        {
            List<ExpressionParser.MultiplyContext> multiply=ctx.multiply();
            
            
            for (int x=0;x<multiply.size();x++)
            {
                Expression expression2;
                Expression expression1;
                if (x>0)
                {
                    expression2=stack.pop();
                    expression1=stack.pop();
                    Expression expression=Expression.makeSum(expression1, expression2);
                    stack.push(expression);
                }
            }
        }

        @Override
        public void exitMultiply(ExpressionParser.MultiplyContext ctx)
        {
            List<ExpressionParser.AddendContext> addends=ctx.addend();
            
            for(int x=0;x<addends.size();x++)
            {
                Expression expression2;
                Expression expression1;
                if (x>0)
                {
                    expression2=stack.pop();
                    expression1=stack.pop();
                    Expression expression = Expression.makeProduct(expression1, expression2);
                    stack.push(expression);
                }
            }
        }
        @Override
        public void exitAddend(ExpressionParser.AddendContext ctx)
        {
            if (ctx.NUMBER()!=null)
            {
                Expression expression = Expression.makeScalar(Double.valueOf(ctx.NUMBER().getText()));
                stack.push(expression);
            }
            if (ctx.LETTER()!=null)
            {
                Expression expression=Expression.makeVariable(ctx.LETTER().getText());
                stack.push(expression);
            }
        }

        @Override public void enterMultiply(ExpressionParser.MultiplyContext ctx){}
        @Override public void enterSum(ExpressionParser.SumContext ctx){}
        @Override public void enterAddend(ExpressionParser.AddendContext ctx){}
        //@Override public void enterVariable(ExpressionParser.VariableContext ctx){}
        @Override public void enterRoot(ExpressionParser.RootContext ctx){}
        @Override public void exitRoot(ExpressionParser.RootContext ctx){}
        @Override public void enterEveryRule(ParserRuleContext ctx) { }
        @Override public void exitEveryRule(ParserRuleContext ctx) { }
        @Override public void visitTerminal(TerminalNode node) { }
        @Override public void visitErrorNode(ErrorNode node) { }
    }
    
    /**
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override 
    public String toString();

    /**
     * @param obj any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, as defined in the PS3 handout. A+(B+C)!=(A+B)+C.
     */
    @Override
    public boolean equals(Object thatObject);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();
    
    /**
     * Takes in a valid expression as defined in the PS3 handout, and differentiates the expression.
     * @return Differentiated expression
     */
    public Expression differentiate(Expression withRespectTo);
    /**
     * 
     * @param expression the expression to simplify
     * @param environment maps variables to values.  Variables are required to be case-sensitive nonempty 
     *         strings of letters.  The set of variables in environment is allowed to be different than the 
     *         set of variables actually found in expression.
     * @return A simplified expression in which if VariableSimplify is found in the expression,
     * it simplifies the numerical expression. For variables that are not affected, they remain in
     * the same structure (grouping and operators).
     */
    public Expression simplify(Map<String,Double> environment);
    public boolean isConstant();
    /**
     * Expression must be constant to return an appropriate constant.
     * @return the constant associated with that constant Expression.
     * @throws IllegalArgumentException if the expression is not a constant.
     */
    public double getConstant() throws IllegalArgumentException; 
}
