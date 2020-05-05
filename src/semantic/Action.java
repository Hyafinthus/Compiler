package semantic;

import java.util.Vector;

// 静态类 每个动作{}对应一个函数
// 在注释中写清楚对应哪个产生式的哪个{}
public class Action {
  public static int offset;

  public static Vector<Vector<String>> symbol = new Vector<>(); // 符号表
  public static Vector<Vector<String>> intermediate = new Vector<>(); // 中间代码

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== 程序入口
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // 程序入口初始化
  // Program -> {offset=0} P
  public static void init() {
    offset = 0;
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== 变量声明
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // 变量声明
  // D -> T idn {enter(idn.lexeme,T.type,offset); offset=offset+T.width} A ;
  public static void varDecl() {

  }

  // 变量类型
  // T -> X {t=X.type; w=X.width} C {T.type=C.type; T.width=C.width}
  public static void varType1() {

  }

  public static void varType2() {

  }

  // 变量连续声明
  // A -> , idn {enter(idn.lexeme,T.type,offset); offset=offset+T.width)} A
  public static void varContDecl() {

  }

  // 变量声明赋值
  // A -> = G {gen(id'='D.addr)}
  public static void varDeclAssi() {

  }

  // 基本变量类型 int
  // X -> int {X.type=int; X.width=4}
  public static void varInt() {

  }

  // 基本变量类型 int
  // X -> float {X.type=int; X.width=4}
  public static void varFloat() {

  }

  // 基本变量类型 int
  // X -> char {X.type=int; X.width=4}
  public static void varChar() {

  }

  // 声明数组类型
  // C -> [ cst ] C {C.type=array(num.val,C.type); C.width=num.val*C.width}
  public static void varArray() {

  }

  // 变量声明结束
  // C -> ε {C.type=t; C.width=w}
  public static void varEnd() {

  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== hanghang
  // ========== ========== ========== ========== ========== ========== ========== ==========

}
