package syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class Parser2Tree {
  // 预测分析表
  private SyntaxConverter syntaxConverter;

  // Node栈
  private Stack<Node> stack = new Stack<>();

  // 输入Token
  private Vector<Vector<String>> tokenData;
  private int index = 0;

  // 输出Tree
  private Node root;
  private Node pointer;

  // 错误信息
  private Vector<Vector<String>> errorData = new Vector<>();

  public Parser2Tree(SyntaxConverter syntaxConverter, Vector<Vector<String>> tokenData) {
    this.syntaxConverter = syntaxConverter;
    this.tokenData = tokenData;

    this.root = new Node("Program", false);
    this.pointer = this.root;

    Node end = new Node("$", false);
    this.stack.push(end);
    this.stack.push(this.root);
  }

  public void analysis() {
    String top = this.stack.peek().data;
    while (!top.equals("$") && this.index < this.tokenData.size()) {
      String token = this.tokenData.get(index).get(2);
      if (top.equals(token)) { // 栈顶终结符与输入相同
        Node terminal = this.stack.pop();

        terminal.generated = true; // 修改: 终结符也需扩展
        pointer2Next(); // 改变指针

        terminal.setWord(this.tokenData.get(index).get(1)); // 为终结符赋值
        this.index++;
      } else if (!this.syntaxConverter.nonterminals.contains(top)) {
        error(0); // 栈顶终结符与输入不符
      } else {
        System.err.println(top);
        int rowIndex = this.syntaxConverter.nonterminalIndex.get(top);
        // System.err.println(rowIndex);
        System.err.println(token);
        int columnIndex = this.syntaxConverter.analysisTitle.indexOf(token);
        // System.err.println(columnIndex);

        String production = this.syntaxConverter.analysisData.get(rowIndex).get(columnIndex).trim();
        if (production.equals("synch")) {
          error(1);
        } else if (production.equals("")) {
          if (token.equals("$")) {
            error(3);
            this.index++;
          } else {
            error(2);
          }
        } else if (production.equals("ε")) {
          Node node = new Node("ε", true);
          node.generated = true;

          this.pointer.children.add(node);
          node.parrent = this.pointer;

          // 已扩展
          this.pointer.generated = true;
          // 改变指针
          pointer2Next();
          // 弹栈
          this.stack.pop();
        } else { // 正确
          // 存产生式
          String[] symbols = production.split(" ");
          // 存该生成式扩展的Node
          List<Node> temp = new ArrayList<>();

          // 存树
          for (String symbol : symbols) {
            Node node;
            if (this.syntaxConverter.nonterminals.contains(symbol)) {
              node = new Node(symbol, false);
            } else {
              node = new Node(symbol, true);
            }
            this.pointer.children.add(node);
            node.parrent = this.pointer;
            temp.add(node);
          }

          // 已扩展
          this.pointer.generated = true;
          // 改变指针
          pointer2Next();

          // 弹栈
          this.stack.pop();
          // 压栈
          for (int i = temp.size() - 1; i >= 0; i--) {
            this.stack.push(temp.get(i));
          }
        }
      }
      top = this.stack.peek().data;
    }
    System.out.println("分析完成");

  }

  // 指针指向下一个可扩展节点
  private void pointer2Next() {
    // 子节点有非终结符 可扩展
    for (Node node : this.pointer.children) {
      // if (!node.terminal && !node.generated) {
      if (!node.generated) { // 修改: 终结符也需扩展
        this.pointer = node;
        return;
      }
    }

    // 子节点无非终结符 寻找最近最深未扩展叶节点
    // 本质为树的左遍历
    // 能保证从根出发的第一个未扩展非终结符为所需指针
    traverse(this.root);
  }

  private boolean traverse(Node parrent) {
    // if (!parrent.terminal && !parrent.generated) {
    if (!parrent.generated) { // 修改: 终结符也需扩展 // <NOTICE> 终结符扩展未使用 日后可用于添加新功能
      this.pointer = parrent;
      return true;
    }

    for (Node node : parrent.children) {
      if (traverse(node)) {
        return true;
      }
    }

    return false;
  }

  public void error(int type) {
    Vector<String> errorLine = new Vector<String>();

    Node errorNode;
    String errorInfo;

    if (type == 1) {
      System.err.println("SYNCH: 弹出栈顶非终结符");
      errorNode = this.stack.pop();

      errorLine.add(this.tokenData.get(this.index).get(0)); // 行号
      errorLine.add(this.tokenData.get(this.index).get(1)); // 错误项
      errorInfo = ErrorInfo.message.get(errorNode.data);
    } else if (type == 2) {
      System.err.println("PANIC: 忽略输入符号");
      errorNode = this.stack.peek();

      errorLine.add(this.tokenData.get(this.index).get(0)); // 行号
      errorLine.add(this.tokenData.get(this.index).get(1)); // 错误项
      errorInfo = ErrorInfo.message.get(errorNode.data);

      this.index++;
    } else if (type == 0) {
      System.err.println("ERROR: 弹出栈顶终结符");
      errorNode = this.stack.pop();

      // 已扩展
      errorNode.generated = true;
      // 改变指针
      pointer2Next();

      errorLine.add(this.tokenData.get(this.index - 1).get(0)); // 行号
      errorLine.add(this.tokenData.get(this.index - 1).get(1)); // 错误项
      errorInfo = "缺少终结符: " + ErrorInfo.operations.get(errorNode.data);
    } else { // type == 3
      errorLine.add(this.tokenData.get(this.index - 1).get(0));
      errorLine.add(this.tokenData.get(this.index - 1).get(1));
      errorInfo = "缺少末尾终结符";
    }

    errorLine.add(errorInfo);
    this.errorData.add(errorLine);
  }

  public Node getRoot() {
    return this.root;
  }

  public Vector<Vector<String>> getErrorData() {
    return this.errorData;
  }
}
