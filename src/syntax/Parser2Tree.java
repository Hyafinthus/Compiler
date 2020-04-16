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
      String token = this.tokenData.get(index).get(2);
      if (top.equals(token)) {
        this.stack.pop();
        this.index++;
      } else if (!this.syntaxConverter.nonterminals.contains(top)) {
        error(0);
      } else {
        System.err.println(top);
        int rowIndex = this.syntaxConverter.nonterminalIndex.get(top);
        System.err.println(rowIndex);
        System.err.println(token);
        int columnIndex = this.syntaxConverter.analysisTitle.indexOf(token);
        System.err.println(columnIndex);

        String production = this.syntaxConverter.analysisData.get(rowIndex).get(columnIndex).trim();
        if (production.equals("synch")) {
          error(1);
        } else if (production.equals("")) {
          error(2);
        } else { // 正确
          // 存产生式
          String[] symbols = production.split(" ");

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
          }
          // 已扩展
          this.pointer.generated = true;
          // 改变指针
          pointer2Next();

          // 弹栈
          this.stack.pop();
          // 压栈
          if (!production.equals("ε")) {
            for (int i = symbols.length - 1; i >= 0; i--) {
              this.stack.push(symbols[i]);
            }
          }
        }
      }
      top = this.stack.peek();
    }
    System.out.println("分析完成");
  }

  // 指针指向下一个可扩展节点
  private void pointer2Next() {
    // 子节点有非终结符 可扩展
    for (Node node : this.pointer.children) {
      if (!node.terminal && !node.generated) {
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
    if (!parrent.terminal && !parrent.generated) {
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
    if (type == 0) {
      System.err.println("ERROR:弹出栈顶终结符");
      this.stack.pop();
    } else if (type == 1) {
      System.err.println("SYNCH:弹出栈顶非终结符");
      this.stack.pop();
    } else if (type == 2) {
      System.err.println("恐慌模式:忽略输入符号");
      this.index++;
    }
  }
}
