//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//Not.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import helper.*;
import symboltable.Variable;

public class Not extends Exp 
{
  public Exp e;
  public Variable t;
  
  public Not(Exp ae) 
  {
    e=ae; 
  }

  public void accept(Visitor v) 
  {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) 
  {
    return v.visit(this);
  }
  
  public Variable generateTAC()
  {
	if(t == null)
	{
		Temporary temp = new Temporary();
		t = new Variable(temp.toString(), "temporary");
	}
	
	return t;
  }
}
