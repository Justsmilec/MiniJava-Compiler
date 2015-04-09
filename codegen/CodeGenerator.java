//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//CodeGenerator.java

package codegen;

import java.io.*;
import IR.*;
import helper.Label;
import symboltable.Variable;
import java.util.List;
import java.util.Hashtable;
import regalloc.RegisterAllocator;

public class CodeGenerator
{
	private String output;
	private List<Quadruple> IRList;
	private Hashtable<Quadruple, List<Label>> labels;
	private List<Variable> varList;
	
	public CodeGenerator(List<Quadruple> list, Hashtable<Quadruple, List<Label>> label, List<Variable> vars, String fileName)
	{
		IRList = list;
		output = fileName;
		labels = label;
		varList = vars;
	}
	
	public void generateMIPS()
	{
		try
		{
			FileWriter fw = new FileWriter(output);
			BufferedWriter bw = new BufferedWriter(fw);
			
			//Allocated Variables
			bw.write(".data\n");
			generateDataSeg(varList, bw);
			
			bw.write(".text\n", 0, 6);
			bw.write("main:\n", 0, 6);
			
			//Iterate through the IR instructions
			for(int i = 0; i < IRList.size(); i++)
			{
				Quadruple q = IRList.get(i);
				
				if(q instanceof CallIR)
				{
					functionCall(IRList, i, bw);
				}
			}
			
			//Close output file resources
			if(bw != null)
				bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void generateDataSeg(List<Variable> varList, BufferedWriter bw)
	{
		for(int i = 0; i < varList.size(); i++)
		{
			try
			{
				String name = varList.get(i).getName();
				String type = varList.get(i).getType();
				String value = "";
				
				if(type.equals("int") || type.equals("boolean"))
				{
					type = ".word";
					value = "0"; //Default value of 0d or false
				}
				else //Don't handle objects or int[] yet
				{
					return;
				}
				
				String data = name + ": " + type + " " + value + "\n";
				bw.write(data, 0, data.length());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	private void typeVar(String type){
            
        }
	
	private void functionCall(List<Quadruple>IRList, int index, BufferedWriter bw)
	{
		try
		{
			CallIR instruction = (CallIR)IRList.get(index);
			int paramCount = Integer.parseInt((String)instruction.getArg2());
			
			//Store $ra on stack
			String temp = "addi $sp, $sp, -68\n";  //Make enough space on stack to save all reg
			bw.write(temp, 0, temp.length());
			temp = "sw $ra, 64($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Store $a0 - $a3 on stack
			for(int i = 0; i < 4; i++)
			{
				temp = "sw $a" + i + ", " + (60 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Store $t0-$t9 on the stack
			for(int i = 0; i < 10; i++)
			{
				temp = "sw $t" + i + ", " + (44 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Store $v0-$v1 on the stack
			for(int i = 0; i < 2; i++)
			{
				temp = "sw $v" + i + ", " + (4 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Store the (up to 4) params in register
			if(paramCount > 4)
			{
				System.err.println("Invalid number of parameters.  A max of 4 params per function in our MIPS output.");
				return;
			}
			
			int paramIndex = index - paramCount;
			
			for(int i = 0; i < paramCount; i++)
			{	
				String reg = "$a" + i;
				RegisterAllocator regAll = new RegisterAllocator();
				/*if(IRList.get(paramIndex) instanceof AssignmentIR)
                                {
                                    
                                        AssignmentIR assign = (AssignmentIR) IRList.get(paramIndex);
                                        if(((String)assign.getOp()).equals("+"))
					{
                                                String resName = ((Variable)assign.getResult()).getName();
                                                String resType = ((Variable)assign.getResult()).getType();
                                                String arg1Name = ((Variable)assign.getArg1()).getName();
                                                String arg1Type = ((Variable)assign.getArg2()).getType();
                                                String arg2Name = ((Variable)assign.getArg2()).getName();
                                                String arg2Type = ((Variable)assign.getArg2()).getType();
                                                if(resType.equals("temporary"))
                                                {
                                                    
                                                        //need to check if args are temporary, constant or variable
                                                        if(arg1Type.equals("temporary")) 
                                                        {   
                                                                temp = "add " + regAll.allocateRegister(resName) + ", $zero, " + regAll.allocateRegister(arg1Name) + "\n";
                                                                
                                                        }
                                                        if(arg1Type.equals("constant")) 
                                                        {   
                                                                temp = "addi " + regAll.allocateRegister(resName) + ", $zero, " + arg1Name + "\n";
                                                                
                                                        }
                                                        if(arg1Type.equals("variable")) 
                                                        {   
                                                                temp = "add " + regAll.allocateRegister(resName) + ", $zero, " + arg1Name + "\n";
                                                                
                                                        }
                                                        bw.write(temp, 0, temp.length());
                                                         if(arg2Type.equals("temporary")) 
                                                        {   
                                                                temp = "add " + regAll.allocateRegister(resName) + regAll.allocateRegister(arg1Name) + regAll.allocateRegister(arg1Name) + "\n";
                                                                
                                                        }
                                                        if(arg1Type.equals("constant")) 
                                                        {   
                                                                temp = "addi " + regAll.allocateRegister(resName) +regAll.allocateRegister(arg1Name) + arg1Name + "\n";
                                                                
                                                        }
                                                        if(arg1Type.equals("variable")) 
                                                        {   
                                                                temp = "add " + regAll.allocateRegister(resName) + regAll.allocateRegister(arg1Name) + arg1Name + "\n";
                                                               
                                                        }
                                                        bw.write(temp, 0, temp.length());
                                                        
                                                }
                                                else{
						}


                                        }
                                        if(((String)assign.getOp()).equals("-")){

                                        }
                                        if(((String)assign.getOp()).equals("*")){

                                        }
                                }*/			
	
				ParameterIR param = (ParameterIR)IRList.get(paramIndex);

				String arg1Type = ((Variable)param.getArg1()).getType();
                                String arg1Name = ((Variable)param.getArg1()).getName();
				
				
				if(arg1Type.equals("constant")) 
                                {              
                                        temp = "addi " + reg + ", $zero, " + arg1Name + "\n"; 
                                
                                }
                                if(arg1Type.equals("variable")) 
                                {              
                                        temp = "add " + reg + ", $zero, " + arg1Name + "\n"; 
                                
                                }
                                if(arg1Type.equals("temporary")) 
                                {              
                                        temp = "add " + reg + ", $zero, " + regAll.allocateRegister(arg1Name) + "\n"; 
                                
                                }

				
				bw.write(temp, 0, temp.length());
			}
			
			//Jump to the function
			String function = (String)instruction.getArg1();
			
			if(function.equals("System.out.println"));
			{
				function = "_system_out_println";
			}
			
			temp = "jal " + function + "\n";
			bw.write(temp, 0, temp.length());
			
			//Restore $v0-$v1 from the stack
			for(int i = 1; i >= 0; i--)
			{
				temp = "lw $v" + i + ", " + (4 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Restore $t0-$t9 from the stack
			for(int i = 9; i >= 0; i--)
			{
				temp = "lw $t" + i + ", " + (44 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Restore $a0-$a3 on the stack
			for(int i = 3; i >= 0; i--)
			{
				temp = "lw $a" + i + ", " + (60 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Restore $ra from the stack
			temp = "lw $ra, 64($sp)\n";
			bw.write(temp, 0, temp.length());
			temp = "addi $sp, $sp, 68\n";    //Cleanup space on stack from all saved reg
			bw.write(temp, 0, temp.length());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
