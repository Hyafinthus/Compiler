package Lexical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import Lexical.Dfa;

public class Nfa {
  // NFA表头，即状态转移函数的输入
  // String为表头
  // Integer为该表头在NFA表中列的索引
  public Map<String, Integer> nfaInputIndex = new HashMap<>();

  // NFA状态
  // Integer为状态号，初始状态为0
  // String为空则为非终结状态，不为空为终结状态，记录Token应输出的种别码
  public Map<Integer, String> nfaState = new HashMap<>();

  // NFA转换表，-1为无此转换路径，其他数字为状态号
  // Integer为状态号，初始状态为0
  // List为该状态可转的所有状态，由于NFA接收同一字符可以转到多个状态，因此为两层List嵌套
  public Map<Integer, List<List<Integer>>> nfaTable = new HashMap<>();

  public Nfa(Vector<Vector<String>> nfaData) {
    int length = nfaData.get(0).size();

    // nfaState & nfaTable
    for (Vector<String> line : nfaData) {
      if(nfaData.indexOf(line)==0) {
    	// nfaInputIndex
    	for (int i = 2; i < length; i++) {
    	  nfaInputIndex.put(line.get(i), i - 2);
    	}
      }
      else {
        nfaState.put(Integer.valueOf(line.get(0)), line.get(1));
        List<List<Integer>> nfaTableLine = new ArrayList<>();
        for (int i = 2; i < length; i++) {
    	  List<Integer> stateCell = new ArrayList<>();
    	  String[] cellString = line.get(i).split(",");
    	  int cellUnitNum = cellString.length;
    	  for (int j = 0; j < cellUnitNum; j++) {
            stateCell.add(Integer.valueOf(cellString[j]));
    	  }
        nfaTableLine.add(stateCell);
        }
      nfaTable.put(Integer.valueOf(line.get(0)), nfaTableLine);
      }
    }
  }
  
  //能够从NFA的状态s开始只通过ε转换到达的NFA状态集合
  //没有状态返回一个空列表
  private List<Integer> epsilonClosure(int s) {
	int epsilonIndex = nfaInputIndex.get("ε");
	List<Integer> avaliableState = nfaTable.get(s).get(epsilonIndex);
	if(avaliableState.contains(-1)) {
		return new ArrayList<Integer>();
	}
	else {
		return avaliableState;
	}
  }
  
  //能够从NFA的状态集合stateList开始只通过ε转换到达的NFA状态集合 
  private List<Integer> epsilonClosure(List<Integer> stateList) {
	 Stack<Integer> stateStack = new Stack<Integer>();
	 ArrayList<Integer> avaliableState = new ArrayList<Integer>();
	 stateStack.addAll(stateList);
	 while (stateStack.size() > 0) {
	   int top = stateStack.pop();
	   for(int u: epsilonClosure(top)) {
	     avaliableState.add(u);
	     stateStack.push(u);
	   }
	 }
	 return avaliableState;
  }

  //能够从T中的某个状态s出发通过标号为ch的转换到达的NFA状态的集合
  private List<Integer> move(List<Integer> stateList,String ch) {
	int chIndex = nfaInputIndex.get(ch);
	ArrayList<Integer> avaliableState = new ArrayList<Integer>();
	for(int state:stateList) {
      if(nfaTable.get(state).get(chIndex).contains(-1)) {
    	  continue;
      } else {
    	  avaliableState.addAll(nfaTable.get(state).get(chIndex));
      }
	}
	return avaliableState;
  }
  
//  public Dfa toDfa() {
//	  Vector<String> dfaTitle = new Vector<String>();
//	  Vector<Vector<String>> dfaData = new Vector<Vector<String>>();
//	  Set<String> nfaTitle = nfaInputIndex.keySet();
//	  nfaTitle.remove("ε");
//	  dfaTitle.addAll(nfaTitle);
//  }
  
  // 某状态是否为终结状态
  public boolean isTerminal(Integer state) {
    if (nfaState.get(state).equals("")) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public String toString() {
	return "Nfa [nfaInputIndex=" + nfaInputIndex + ", nfaState=" + nfaState + ", nfaTable=" + nfaTable + "]";
  }
  

}
