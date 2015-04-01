//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//MethodSymbolTable.java

package symboltable;

import java.util.Hashtable;
import java.util.Set;

public class MethodSymbolTable extends BlockSymbolTable implements Scope
{
	private String name;
	private Scope parent;
	private Hashtable<String, Variable> vars;
	private Hashtable<String, BlockSymbolTable> blocks;
	private Hashtable<String, Variable> args;
	private String returnType;
	
	public MethodSymbolTable(Scope parent, String name, String[] paramNames, String[] paramTypes, String returnType)
	{
		this.name = name;
		this.parent = parent;
		vars = new Hashtable<String, Variable>();
		blocks = new Hashtable<String, BlockSymbolTable>();
		args = new Hashtable<String, Variable>();
		
		for(int i = 0; i < paramNames.length; i++)
		{
			args.put(paramNames[i], new Variable(paramNames[i], paramTypes[i]));
		}
		
		this.returnType = returnType;
	}
	
	public Object[] getParameters()
	{
		return args.values().toArray();
	}
	
	public String getReturnType()
	{
		return returnType;
	}
	
	public String getName()
	{
		return name;
	}
}