package regalloc;

import IR.Quadruple;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import regalloc.graph.Node;
import symboltable.Variable;

public class Liveness {

    List<Node> flowGraph;
    List<BitSet> liveIn;
    List<BitSet> liveOut;

    public Liveness(List<Node> fG) {
        flowGraph = fG;
        liveIn = new ArrayList<BitSet>();
        liveOut = new ArrayList<BitSet>();

    }

    public void calculateLive() {
        List<Variable> listVar = getAllVariables();
	System.out.println("List of Vars ");
        for (int i = 0; i < listVar.size(); i++) {
            System.out.print(i+"-"+listVar.get(i).getName()+ " ");
        }
	System.out.println(" ");
        printUseDef(listVar);
        System.out.println(" ");
       	List<BitSet> auxListIn = new ArrayList<BitSet>();
        List<BitSet> auxListOut = new ArrayList<BitSet>();;

        for (int j = 0; j < flowGraph.size(); j++) {
            BitSet bitIn = new BitSet(listVar.size());
            liveIn.add(j,bitIn);
	    auxListIn.add(j,bitIn);
            BitSet bitOut = new BitSet(listVar.size());
            auxListOut.add(j,bitOut);
	    liveOut.add(j,bitOut);
        }
        
        do {
	    System.out.println("here");
            for (int i = 0; i < flowGraph.size(); i++) {

		
                Node n = flowGraph.get(i);
		System.out.println(i);
                auxListIn.set(i, liveIn.get(i));
                auxListOut.set(i, liveOut.get(i));

                //or - union , and - intersection, andNot - difference
                BitSet bitUse = n.calculateUse(listVar);
                BitSet bitDef = n.calculateDef(listVar);
                
		BitSet aux = liveOut.get(i);
                aux.andNot(bitDef);
                bitUse.or(aux);
                liveIn.set(i, bitUse);
                //out[n] = union over successors
                List<Node> nextNodes = n.nextNode();
                //walk the nexts to get the number of the instruction to see it liveIn
                BitSet bitNext = new BitSet(listVar.size());
                for (int j = 0; j < nextNodes.size(); j++) {
                    BitSet bitNextAux = liveIn.get(nextNodes.get(j).getNum());
                    bitNext.or(bitNext);
                }
                liveOut.set(i, bitNext);

            }

        } while (!allEqual(liveIn, auxListIn, liveOut, auxListOut));
	printLiveInOut();
    }

    // function to keep in a list all variables and temporaries that the program has
    public List<Variable> getAllVariables() {
        List<Variable> listVar = new ArrayList<Variable>();
        for (int i = 0; i < flowGraph.size(); i++) {
            Node n = flowGraph.get(i);
            Quadruple instr = n.getInstr();
            if (instr.getArg1() != null) {
                if ((instr.getArg1()) instanceof Variable) {
                    Variable arg1 = (Variable) instr.getArg1();
                    if (!arg1.getType().equals("constant")) {
                        if (!hasVariable(arg1, listVar)) {
                            listVar.add(arg1);
                        }
                    }
                }
            }
            if (instr.getArg2() != null) {
                if ((instr.getArg2()) instanceof Variable) {
                    Variable arg2 = (Variable) instr.getArg2();
                    if (!arg2.getType().equals("constant")) {
                        if (!hasVariable(arg2, listVar)) {
                            listVar.add(arg2);
                        }
                    }
                }
            }
            if (instr.getResult() != null) {
                if ((instr.getResult()) instanceof Variable) {
                    Variable result = (Variable) instr.getResult();
                    if (!hasVariable(result, listVar)) {
                        listVar.add(result);
                    }
                }
            }

        }
        return listVar;

    }

    //function helper that returns true if the Variable is already in the list
    private boolean hasVariable(Variable var, List<Variable> listVar) {
        for (int i = 0; i < listVar.size(); i++) {
            if (listVar.contains(var)) {
                return true;
            }
        }
        return false;
    }


	private boolean allEqual(List<BitSet> liveIn, List<BitSet> auxIn, List<BitSet> liveOut, List<BitSet> auxOut) {
            if(liveIn.size() != auxIn.size() && liveOut.size() != auxOut.size()){
                return false;
            }
            else{
                for (int i = 0; i < liveIn.size(); i++) {
                        BitSet bitIn = liveIn.get(i);
                        BitSet bitAuxIn = auxIn.get(i);
                        bitIn.xor(bitAuxIn);
                        if (!bitIn.isEmpty()) {
                                return false;
                        }
                }
                for (int i = 0; i < liveIn.size(); i++) {
                        
                        BitSet bitOut = liveOut.get(i);
                        BitSet bitAuxOut = auxOut.get(i);
                        
                        bitOut.xor(bitAuxOut);
                        if (!bitOut.isEmpty()) {
                                return false;
                        }
                }
            }
            return true;
        }



    public List<BitSet> getLiveOut() {
        return liveOut;

    }

    private void printUseDef(List<Variable> listVar) {
         for (int i = 0; i < flowGraph.size(); i++) {
             Node n = flowGraph.get(i);
             System.out.print(i+" Def: "+n.calculateDef(listVar));
             System.out.println(" Use "+n.calculateUse(listVar));
         }
         
    }
    
     private void printLiveInOut() {
        System.out.println("LiveIn:");
        for (int i = 0; i < liveIn.size(); i++) {
            System.out.print(i+" "+liveIn.get(i)+" ");
        }
	System.out.println();
        System.out.println("LiveOut:");
        for (int i = 0; i < liveOut.size(); i++) {
            System.out.print(i+" "+liveOut.get(i)+" ");
        }
	System.out.println();

    }
    
   

}

