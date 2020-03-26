package Lexical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  public List<List<Integer>> dfaTable = new ArrayList<>();

  public Dfa(Vector<String> dfaTitle, Vector<Vector<String>> dfaData) {
    // TODO Auto-generated constructor stub
  }

}
