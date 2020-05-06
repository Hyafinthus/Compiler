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
      function.put("init", Action.class.getMethod("init", SemanticNode.class));
      function.put("varDecl", Action.class.getMethod("varDecl", SemanticNode.class));
      function.put("varType1", Action.class.getMethod("varType1", SemanticNode.class));
      function.put("varType2", Action.class.getMethod("varType2", SemanticNode.class));
      function.put("varContDecl", Action.class.getMethod("varContDecl", SemanticNode.class));
      function.put("varDeclAssi", Action.class.getMethod("varDeclAssi", SemanticNode.class));
      function.put("varInt", Action.class.getMethod("varInt", SemanticNode.class));
      function.put("varFloat", Action.class.getMethod("varFloat", SemanticNode.class));
      function.put("varChar", Action.class.getMethod("varChar", SemanticNode.class));
      function.put("varArray", Action.class.getMethod("varArray", SemanticNode.class));
      function.put("varEnd", Action.class.getMethod("varEnd", SemanticNode.class));
      function.put("backM", Action.class.getMethod("backM", SemanticNode.class));
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
    }
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== 程序入口
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // 程序入口初始化
  // Program -> {offset=0} P
  public static void init(SemanticNode node) {
    offset = 0;
    System.out.println("init");
    System.out.println(node.data);
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== 变量声明
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // 变量声明
  // D -> T idn {enter(idn.lexeme,T.type,offset); offset=offset+T.width} A ;
  public static void varDecl(SemanticNode node) {

  }

  // 变量类型
  // T -> X {t=X.type; w=X.width} C {T.type=C.type; T.width=C.width}
  public static void varType1(SemanticNode node) {

  }

  public static void varType2(SemanticNode node) {

  }

  // 变量连续声明
  // A -> , idn {enter(idn.lexeme,T.type,offset); offset=offset+T.width)} A
  public static void varContDecl(SemanticNode node) {

  }

  // 变量声明赋值
  // A -> = G {gen(id'='D.addr)}
  public static void varDeclAssi(SemanticNode node) {

  }

  // 基本变量类型 int
  // X -> int {X.type=int; X.width=4}
  public static void varInt(SemanticNode node) {

  }

  // 基本变量类型 int
  // X -> float {X.type=int; X.width=4}
  public static void varFloat(SemanticNode node) {

  }

  // 基本变量类型 int
  // X -> char {X.type=int; X.width=4}
  public static void varChar(SemanticNode node) {

  }

  // 声明数组类型
  // C -> [ cst ] C {C.type=array(num.val,C.type); C.width=num.val*C.width}
  public static void varArray(SemanticNode node) {

  }

  // 变量声明结束
  // C -> ε {C.type=t; C.width=w}
  public static void varEnd(SemanticNode node) {

  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== hanghang
  // ========== ========== ========== ========== ========== ========== ========== ==========
  
  // 回填辅助非终结符K（M）的空转移动作
  // K -> ε {K.quad = nextquad;}
  public static void backM(SemanticNode node) {
    int nextQuad = intermediate.size()-1;
    node.attr.put("quad",String.valueOf(nextQuad));
  }
  
 // 回填辅助非终结符O（N）的空转移动作
 // O -> ε { O.nextlist = makelist(nextquad);gen(‘goto _’);}

 public static void backN(SemanticNode node) {
   int nextQuad = intermediate.size()-1;
   node.attr.put("nextlist",String.valueOf(nextQuad));
   Vector<String> line = new Vector<String>();
   line.add(String.valueOf(nextQuad));
   line.add("goto ");
   line.add("(j, _, _, )");
 }
  
}
