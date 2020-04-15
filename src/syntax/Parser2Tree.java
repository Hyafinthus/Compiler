package syntax;

import java.util.Stack;
import java.util.Vector;

public class Parser2Tree {
  // 预测分析表
  private SyntaxConverter syntaxConverter;

  // 栈
  private Stack<String> stack = new Stack<>();

  // 输入Token
  private Vector<Vector<String>> tokenData;
  private int index = 0;

  // 输出Tree
  private Node root;
  private Node pointer;

  public Parser2Tree(SyntaxConverter syntaxConverter, Vector<Vector<String>> tokenData) {
    this.syntaxConverter = syntaxConverter;
    this.tokenData = tokenData;

    this.stack.push("$");
    this.stack.push("Program");

    this.root = new Node("Program", false);
    this.pointer = this.root;
  }

  public void analysis() {
    String top = this.stack.peek();
    while (!top.equals("$")) {
      if (top.equals(this.tokenData.get(index).get(2))) {
        this.stack.pop();
        this.index++;
      } else if (!this.syntaxConverter.nonterminals.contains(top)) {
        error();
      } else if (true) {

      }
    }
  }

  public void error() {

  }
}
