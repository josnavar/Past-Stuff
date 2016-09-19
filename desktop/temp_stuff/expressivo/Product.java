package expressivo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



class Product implements Expression
{
  //Abstract Function
    // Maps two expressions to a product of these two expressions.
    //Representation Invariant
    // Since expressions are recursive, no rep invariant. 
    //Safety from Rep Exposure
    // The rep expressions(1 and 2) are never returned via a method.
    // The final list elements is never returned. getElements uses defensive copying.
    private final List<Expression> elements= new ArrayList<Expression>();
    
    private final Expression expression1;
    private final Expression expression2;
    
    /**
     * 
     * @param expression1 Left hand side of the product
     * @param expression2 Right hand side of the product
     */
    public Product(Expression expression1,Expression expression2)
    {
        this.expression1=expression1;
        this.expression2=expression2; 
        elements.add(expression1);
        elements.add(expression2);
    }
    @Override 
    public String toString()
    {
        String result="";
        result=result+"("+expression1.toString()+")"+"*"+"("+expression2.toString()+")";
        return result;
        
    }

    @Override
    public List<Expression> getElements()
    {
        List<Expression> result= new ArrayList<Expression>();
        result.add(expression1);
        result.add(expression2);
        return result;
    }
    @Override
    public boolean equals(Object thatObject)
    {
        if (!(thatObject instanceof Product)) return false;

        Product thatProduct= (Product)thatObject;
        return (elements.equals(thatProduct.getElements()));
      
    }    
    @Override
    public int hashCode()
    {
        return elements.hashCode();
    }
    @Override
    public Expression differentiate(Expression withRespectTo)
    {
        Expression expressionDiff1=expression1.differentiate(withRespectTo);
        Expression expressionDiff2=expression2.differentiate(withRespectTo);
        
        Expression expression3=Expression.makeProduct(expression1, expressionDiff2);
        Expression expression4=Expression.makeProduct(expression2, expressionDiff1);
        
        Expression expressionResult=Expression.makeSum(expression3, expression4);
        
        return expressionResult;  
    }
    @Override
    public Expression simplify(Map<String,Double> environment)
    {
        Expression expressionSimple1=expression1.simplify(environment);
        
        Expression expressionSimple2=expression2.simplify(environment);
        //System.out.println(expressionSimple1+" "+expressionSimple2);
        if (expressionSimple1.isConstant() && expressionSimple2.isConstant())
        {
            double result=expressionSimple1.getConstant()*expressionSimple2.getConstant();
            Expression expressionResult=Expression.makeScalar(result);
            return expressionResult;
        }
        else{
            Expression expressionPartial=Expression.makeProduct(expressionSimple1,expressionSimple2);
            return expressionPartial;
        }
    }
    @Override
    public boolean isConstant(){
        return (expression1.isConstant() && expression2.isConstant());

    }
    @Override
    public double getConstant() throws IllegalArgumentException{
        double a1=expression1.getConstant();
        double a2=expression2.getConstant();
        double result=a1*a2;
        return result;
    }

}
