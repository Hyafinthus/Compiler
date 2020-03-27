package Lexical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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

  // 某状态是否为终结状态
  public boolean isTerminal(Integer state) {
    if (nfaState.get(state).equals("")) {
      return false;
    } else {
      return true;
    }
  }

}
