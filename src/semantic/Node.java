package semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
  public String data; // 非终结符 终结符token 语义动作字符串
  public String word; // 终结符word 用于输出

  // 是终结符
  public boolean terminal;
  // 已扩展
  public boolean generated = false;

  public Node parrent;
  public List<Node> children = new ArrayList<>();

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // 是语义动作
  public boolean action = false;

  // 节点属性
  public Map<String, String> attr = new HashMap<>();

  public Node(String data) {
    this.data = data;
    this.action = true;
    this.generated = true;
  }
  // ========== ========== ========== ========== ========== ========== ========== ==========

  public Node(String data, boolean terminal) {
    this.data = data;
    this.terminal = terminal;
  }

  public void setWord(String word) {
    this.word = word;
    if (!terminal) {
      System.err.println("出错: 非终结符赋值");
    }
  }

  public void addChild(Node node) {
    this.children.add(node);
  }
}
