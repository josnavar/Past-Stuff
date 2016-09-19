package expressivo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



class Sum implements Expression
{
  //Abstract Function
    // Maps two expressions to the sum of these expressions.
    //Representation Invariant
    // No rep invariant since it's recursive.
    //Safety from Rep Exposure
    // The list returned comes from defensive copying. 
    private final List<Expression> elements= new ArrayList<Expression>();
    private final Expression expression1;
    private final Expression expression2;
    /**
     * 
     * @param expression1
     * @param expression2
     */
    public Sum(Expression expression1,Expression expression2)
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
        result=result+expression1+"+"+expression2;
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
        if (!(thatObject instanceof Sum)) return false;

        Sum thatSum= (Sum)thatObject;
        return (elements.equals(thatSum.getElements()));
      
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
        
        Expression expressionResult=Expression.makeSum(expressionDiff1, expressionDiff2);
        
        return expressionResult;
    }
    @Override
    public Expression simplify(Map<String,Double> environment)
    {
        Expression expressionSimple1=expression1.simplify(environment);
        Expression expressionSimple2=expression2.simplify(environment);
        if (expressionSimple1.isConstant() && expressionSimple2.isConstant())
        {
            double result=expressionSimple1.getConstant()+expressionSimple2.getConstant();
            Expression expressionResult=Expression.makeScalar(result);
            return expressionResult;
        }
        else{
            Expression expressionPartial=Expression.makeSum(expressionSimple1, expressionSimple2);
            return expressionPartial;
        }
    }
    @Override
    public boolean isConstant(){
        return (expression1.isConstant() && expression2.isConstant());
    }
    @Override
    public double getConstant(){
        double a1=expression1.getConstant();
        double a2=expression2.getConstant();
        double result=a1+a2;
        return result;
    }
}
