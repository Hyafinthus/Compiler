package syntax;

import java.util.ArrayList;
import java.util.List;

public class Node {
  public String data;
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

  public void addChild(Node node) {
    this.children.add(node);
  }
}