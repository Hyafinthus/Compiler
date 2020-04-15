package syntax;

import java.util.List;

public class Node {
  public String data;
  public boolean terminal;
  public Node parrent;
  public List<Node> children;

  public Node(String data, boolean terminal) {
    this.data = data;
    this.terminal = terminal;
  }

  public void addChild(Node node) {
    this.children.add(node);
  }



}
