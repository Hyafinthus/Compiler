package semantic;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class SemanticParser2Tree {
  // 预测分析表
  private SemanticConverter semanticConverter;

  // Node栈
  private Stack<SemanticNode> stack = new Stack<>();

  // 输入Token
  private Vector<Vector<String>> tokenData;
  private int index = 0;

  // 输出Tree
  private SemanticNode root;
  private SemanticNode pointer;

  // 错误信息
  private Vector<Vector<String>> errorData = new Vector<>();

  public SemanticParser2Tree(SemanticConverter semanticConverter, Vector<Vector<String>> tokenData) {
    this.semanticConverter = semanticConverter;
    this.tokenData = tokenData;

    this.root = new SemanticNode("Program", false);
    this.pointer = this.root;

    SemanticNode end = new SemanticNode("$", false);
    this.stack.push(end);
    this.stack.push(this.root);
  }

  public void analysis() {
    String top = this.stack.peek().data;
    while (!top.equals("$") && this.index < this.tokenData.size()) {
      // ========== ========== ========== ========== ========== ========== ========== ==========
      // 栈顶节点是语义动作节点
      if (this.stack.peek().action) {
        SemanticNode action = this.stack.pop();
        String function = action.data.replaceAll("[{}]", "");
        try {
          Action.function.get(function).invoke(Action.class, action);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          e.printStackTrace();
        }
        continue;
      }
      // ========== ========== ========== ========== ========== ========== ========== ==========

      String token = this.tokenData.get(index).get(2);
      if (top.equals(token)) { // 栈顶终结符与输入相同
        SemanticNode terminal = this.stack.pop();

        terminal.generated = true; // 修改: 终结符也需扩展
        pointer2Next(); // 改变指针

        terminal.setWord(this.tokenData.get(index).get(1)); // 为终结符赋值
        this.index++;
      } else if (!this.semanticConverter.nonterminals.contains(top)) {
        error(0); // 栈顶终结符与输入不符
      } else {
        System.err.println(top);
        int rowIndex = this.semanticConverter.nonterminalIndex.get(top);
        // System.err.println(rowIndex);
        System.err.println(token);
        int columnIndex = this.semanticConverter.analysisTitle.indexOf(token);
        // System.err.println(columnIndex);

        String production =
            this.semanticConverter.analysisData.get(rowIndex).get(columnIndex).trim();
        if (production.equals("synch")) {
          error(1);
        } else if (production.equals("")) {
          if (token.equals("$")) {
            error(3);
          } else {
            error(2);
          }
        } else if (production.equals("ε")) {
          SemanticNode node = new SemanticNode("ε", true);
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
          List<SemanticNode> temp = new ArrayList<>();

          // 存树
          for (String symbol : symbols) {
            SemanticNode node;
            // ========== ========== ========== ========== ========== ========== ==========
            // 生成语义动作节点
            if (symbol.contains("{") && symbol.contains("}")) {
              node = new SemanticNode(symbol);
            }
            // ========== ========== ========== ========== ========== ========== ==========
            else if (this.semanticConverter.nonterminals.contains(symbol)) {
              node = new SemanticNode(symbol, false);
            } else {
              node = new SemanticNode(symbol, true);
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
    for (SemanticNode node : this.pointer.children) {
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

  private boolean traverse(SemanticNode parrent) {
    // if (!parrent.terminal && !parrent.generated) {
    if (!parrent.generated) { // 修改: 终结符也需扩展 // <NOTICE> 终结符扩展未使用 日后可用于添加新功能
      this.pointer = parrent;
      return true;
    }

    for (SemanticNode node : parrent.children) {
      if (traverse(node)) {
        return true;
      }
    }

    return false;
  }

  public void error(int type) {
    Vector<String> errorLine = new Vector<String>();

    SemanticNode errorNode;
    String errorInfo;

    if (type == 1) {
      System.err.println("SYNCH: 弹出栈顶非终结符");
      errorNode = this.stack.pop();

      // 已扩展
      errorNode.generated = true;
      // 改变指针
      pointer2Next();

      errorLine.add(this.tokenData.get(this.index).get(0)); // 行号
      errorLine.add(this.tokenData.get(this.index).get(1)); // 错误项
      errorInfo = SemanticErrorInfo.message.get(errorNode.data);
    } else if (type == 2) {
      System.err.println("PANIC: 忽略输入符号");
      errorNode = this.stack.peek();

      errorLine.add(this.tokenData.get(this.index).get(0)); // 行号
      errorLine.add(this.tokenData.get(this.index).get(1)); // 错误项
      errorInfo = SemanticErrorInfo.message.get(errorNode.data);

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
      errorInfo = "缺少终结符: " + (SemanticErrorInfo.operations.get(errorNode.data) == null ? errorNode.data
          : SemanticErrorInfo.operations.get(errorNode.data));
    } else { // type == 3
      errorLine.add(this.tokenData.get(this.index - 1).get(0));
      errorLine.add(this.tokenData.get(this.index - 1).get(1));
      errorInfo = "缺少末尾终结符";

      this.index++;
    }

    errorLine.add(errorInfo);
    this.errorData.add(errorLine);
  }

  public SemanticNode getRoot() {
    return this.root;
  }

  public Vector<Vector<String>> getErrorData() {
    return this.errorData;
  }
}
