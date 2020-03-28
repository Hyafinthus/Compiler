package Lexical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class Dfa {
  // DFA表头，即状态转移函数的输入
  // String为表头
  // Integer为该表头在DFA表中列的索引
  public Map<String, Integer> dfaInputIndex = new HashMap<>();

  // DFA状态
  // Integer为状态号，初始状态为0
  // String为空则为非终结状态，不为空为终结状态，记录Token应输出的种别码
  public Map<Integer, String> dfaState = new HashMap<>();

  // DFA转换表，-1为无此转换路径，其他数字为状态号
  // Integer为状态号，初始状态为0
  // List为该状态可转的所有状态，即每一行
  public Map<Integer, List<Integer>> dfaTable = new HashMap<>();

  public Dfa(Vector<String> dfaTitle, Vector<Vector<String>> dfaData) {
    int length = dfaTitle.size();

    // dfaInputIndex
    for (int i = 2; i < length; i++) {
      dfaInputIndex.put(dfaTitle.get(i), i - 2);
    }

    // dfaState & dfaTable
    for (Vector<String> line : dfaData) {
      dfaState.put(Integer.valueOf(line.get(0)), line.get(1));

      List<Integer> dfaTableLine = new ArrayList<>();
      for (int i = 2; i < length; i++) {
        dfaTableLine.add(Integer.valueOf(line.get(i)));
      }

      dfaTable.put(Integer.valueOf(line.get(0)), dfaTableLine);
    }
  }
  
  //返回能够被resourceManager处理的dfa头类型
  public Vector<String> getDfaTitle(){
    Vector<String> dfaTitle = new Vector<String>();
    dfaTitle.add("");
    dfaTitle.add("");
    List<Map.Entry<String, Integer>> list = new ArrayList<>(dfaInputIndex.entrySet());
    Collections.sort(list,new Comparator<Map.Entry<String,Integer>>(){
    	public int compare(Entry<String, Integer> o1,Entry<String, Integer> o2) {
    		return o1.getValue()-o2.getValue();
    	}
    });
    for(Map.Entry<String, Integer> mapping:list) {
    	dfaTitle.add(mapping.getKey());
    }
    return dfaTitle;
  }
  
//返回能够被resourceManager处理的dfa数据类型
  public Vector<Vector<String>> getDfaData(){
	  Vector<Vector<String>> dfaData = new Vector<Vector<String>>();
	  Set<Integer> keySet = dfaState.keySet();
	  Object[] stateArray = keySet.toArray();
	  Arrays.sort(stateArray);
	  for(Object state:stateArray) {
		Vector<String> dfaDataLine = new Vector<String>();
		dfaDataLine.add(state.toString());
		dfaDataLine.add(dfaState.get(state));
		for(int aState:dfaTable.get(state)) {
			dfaDataLine.add(String.valueOf(aState));
		}
		dfaData.add(dfaDataLine);
	  }
	  return dfaData;
  }
  
  public Dfa(HashMap<String, Integer> dfaInputIndex,HashMap<Integer, String> dfaState,HashMap<Integer, List<Integer>> dfaTable) {
	  this.dfaInputIndex.putAll(dfaInputIndex);
	  this.dfaState.putAll(dfaState);
	  this.dfaTable.putAll(dfaTable);
  }

  @Override
  public String toString() {
	return "Dfa [dfaInputIndex=" + dfaInputIndex + ", dfaState=" + dfaState + ", dfaTable=" + dfaTable + "]";
  }

  // 某状态是否为终结状态
  public boolean isTerminal(Integer state) {
    if (dfaState.get(state).equals("")) {
      return false;
    } else {
      return true;
    }
  }
}
