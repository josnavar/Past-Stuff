package expressivo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



class Variable implements Expression
{
  //Abstract Function
    // Maps a string to an expression consisting of a variable.
    //Representation Invariant
    // variables, which are case-sensitive nonempty sequences of letters 
    //Safety from Rep Exposure
    //The getList method returns a defensive copying of list in rep, and String is a final variable.
    private final String variable;
    /**
     * 
     * @param string takes in a non-empty string with no whitespace in the body and consisting of lower or upper case characters.  
     */
    private void checkRep(){
        String validInput="[a-zA-Z]+";
        Pattern pattern=Pattern.compile(validInput);
        Matcher matcher= pattern.matcher(variable);
        assert matcher.matches();
    }
    public Variable(String string)
    {
        this.variable=string;
    }

    @Override 
    public String toString()
    {
        checkRep();
        return variable;
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
        if (!(thatObject instanceof Variable)) return false;

        Variable thatVariable= (Variable)thatObject;
        checkRep();
        return variable.equals(thatVariable.variable);
          
    }
    @Override
    public int hashCode()
    {
        checkRep();
        return variable.hashCode();
    }
    @Override
    public Expression differentiate(Expression withRespectTo)
    {
        if(withRespectTo.equals(this))
        {
            Expression expression=Expression.makeScalar(1); 
            checkRep();
            return expression;
        }
        else
        {
            Expression expression=Expression.makeScalar(0); 
            checkRep();
            return expression;
        }   
    }
    @Override
    public Expression simplify(Map<String,Double> environment)
    {
        Iterator<String> iterator=environment.keySet().iterator();
        Expression substitution=this;
        while (iterator.hasNext())
        {
            String key=iterator.next();
            Expression expression=Expression.makeVariable(key);
            if (this.equals(expression))
            {
                substitution= Expression.makeScalar(environment.get(key));
            }

        }
        checkRep();
        return substitution;
    }
    @Override
    public boolean isConstant(){
        checkRep();
        return false;
    }
    @Override
    public double getConstant() throws IllegalArgumentException{
        throw new IllegalArgumentException();
    }
}

