package syntax;

import java.util.ArrayList;
import java.util.List;

public class Node {
  public String data; // 非终结符 终结符token
  public String word; // 终结符word 用于输出

  // 是终结符
  public boolean terminal;
  // 已扩展
  public boolean generated = false;

  public Node parrent;
  public List<Node> children = new ArrayList<>();

  public Node(String data, boolean terminal) {
    this.data = data;
    this.terminal = terminal;
  }

  public void setWord(String word) {
    this.word = word;
    if (!terminal) {
      // System.err.println("出错: 非终结符赋值");
    }
  }

  public void addChild(Node node) {
    this.children.add(node);
  }
}
