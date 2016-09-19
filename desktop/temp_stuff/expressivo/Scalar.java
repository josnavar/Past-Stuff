package expressivo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;





public class Scalar implements Expression
{
    //Abstract Function
    // Maps a scalar to a scalar expression.
    //Representation Invariant
    // scalar must be non-negative.
    //Safety from Rep Exposure
    // The field scalar is immutable and initiated as final. The observer getElements creates a new 
    // List to return in order to avoid rep exposure.
    private final double scalar;
    
    private void checkRep(){
        assert scalar>=0;
    }
    /**
     * @param scalar Takes in a non-negative double that represents a scalar in the expression.
     */
    public Scalar(double scalar)
    {
        this.scalar=scalar;
        checkRep();
    }

    @Override 
    public String toString(){
        checkRep();
        return Double.toString(scalar);
    }
    @Override
    public List<Expression> getElements(){
        List<Expression> result= new ArrayList<Expression>();
        result.add(this);
        checkRep();
        return result;
    }

    @Override
    public boolean equals(Object thatObject)
    {
        if (!(thatObject instanceof Scalar)) return false;

        Scalar thatScalar=(Scalar)thatObject;
        checkRep();
        return scalar==thatScalar.scalar;
 
    }
    @Override
    public int hashCode()
    {
        return Double.valueOf(scalar).hashCode();
    }  
    @Override
    public Expression differentiate(Expression withRespectTo)
    {
        Expression expression=Expression.makeScalar(0);
        checkRep();
        return expression;
    }
    @Override
    public Expression simplify(Map<String,Double> environment)
    {
        checkRep();
        return this;
    }
    @Override
    public boolean isConstant(){
        checkRep();
        return true;
    }
    @Override
    public double getConstant() throws IllegalArgumentException{
        checkRep();
        return scalar;
    }
}

