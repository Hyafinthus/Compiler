package semantic;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

// 静态类 每个动作{}对应一个函数
// 在注释中写清楚对应哪个产生式的哪个{}
public class Action {
  public static int offset;

  public static Vector<Vector<String>> symbol = new Vector<>(); // 符号表
  public static Vector<Vector<String>> intermediate = new Vector<>(); // 中间代码

  public static Map<String, Method> function = new HashMap<>(); // String -> Method
  static {
    try {
      function.put("init", Action.class.getMethod("init", Node.class));
      function.put("varDecl", Action.class.getMethod("varDecl", Node.class));
      function.put("varType1", Action.class.getMethod("varType1", Node.class));
      function.put("varType2", Action.class.getMethod("varType2", Node.class));
      function.put("varContDecl", Action.class.getMethod("varContDecl", Node.class));
      function.put("varDeclAssi", Action.class.getMethod("varDeclAssi", Node.class));
      function.put("varInt", Action.class.getMethod("varInt", Node.class));
      function.put("varFloat", Action.class.getMethod("varFloat", Node.class));
      function.put("varChar", Action.class.getMethod("varChar", Node.class));
      function.put("varArray", Action.class.getMethod("varArray", Node.class));
      function.put("varEnd", Action.class.getMethod("varEnd", Node.class));
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
    }
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== 程序入口
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // 程序入口初始化
  // Program -> {offset=0} P
  public static void init(Node node) {
    offset = 0;
    System.out.println("init");
    System.out.println(node.data);
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== 变量声明
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // 变量声明
  // D -> T idn {enter(idn.lexeme,T.type,offset); offset=offset+T.width} A ;
  public static void varDecl(Node node) {

  }

  // 变量类型
  // T -> X {t=X.type; w=X.width} C {T.type=C.type; T.width=C.width}
  public static void varType1(Node node) {

  }

  public static void varType2(Node node) {

  }

  // 变量连续声明
  // A -> , idn {enter(idn.lexeme,T.type,offset); offset=offset+T.width)} A
  public static void varContDecl(Node node) {

  }

  // 变量声明赋值
  // A -> = G {gen(id'='D.addr)}
  public static void varDeclAssi(Node node) {

  }

  // 基本变量类型 int
  // X -> int {X.type=int; X.width=4}
  public static void varInt(Node node) {

  }

  // 基本变量类型 int
  // X -> float {X.type=int; X.width=4}
  public static void varFloat(Node node) {

  }

  // 基本变量类型 int
  // X -> char {X.type=int; X.width=4}
  public static void varChar(Node node) {

  }

  // 声明数组类型
  // C -> [ cst ] C {C.type=array(num.val,C.type); C.width=num.val*C.width}
  public static void varArray(Node node) {

  }

  // 变量声明结束
  // C -> ε {C.type=t; C.width=w}
  public static void varEnd(Node node) {

  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== hanghang
  // ========== ========== ========== ========== ========== ========== ========== ==========

}
