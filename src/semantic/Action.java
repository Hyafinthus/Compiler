package semantic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

// 静态类 每个语义动作对应一个函数
public class Action {
  public static int offset; // 偏移量
  public static int index = 1; // 三地址序号

  // 符号表: lineIndex idn type offset
  public static Vector<Vector<String>> symbol = new Vector<>();
  // 中间代码: lineIndex index three four
  public static Vector<Vector<String>> intermediate = new Vector<>();

  public static Map<String, Method> function = new HashMap<>(); // String -> Method
  static {
    try {
      function.put("init", Action.class.getMethod("init", SemanticNode.class));
      function.put("var_decl", Action.class.getMethod("varDecl", SemanticNode.class));
      function.put("var_type1", Action.class.getMethod("varType1", SemanticNode.class));
      function.put("var_type2", Action.class.getMethod("varType2", SemanticNode.class));
      function.put("var_cont_decl", Action.class.getMethod("varContDecl", SemanticNode.class));
      function.put("var_decl_assi", Action.class.getMethod("varDeclAssi", SemanticNode.class));
      function.put("var_int", Action.class.getMethod("varInt", SemanticNode.class));
      function.put("var_float", Action.class.getMethod("varFloat", SemanticNode.class));
      function.put("var_char", Action.class.getMethod("varChar", SemanticNode.class));
      function.put("var_array", Action.class.getMethod("varArray", SemanticNode.class));
      function.put("var_end", Action.class.getMethod("varEnd", SemanticNode.class));
    } catch (NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
    }
  }

  public static String t; // 临时变量
  public static String w; // 临时变量
  public static SemanticNode T; // 临时存储变量类型
  public static List<SemanticNode> idn = new ArrayList<>(); // 临时存储名

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== 程序入口
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // 程序入口初始化
  // Program -> {offset=0} P
  public static void init(SemanticNode node) {
    offset = 0;
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== 变量声明
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // 生成一条符号表记录
  private static void enter(String lineIndex, String word, String type) {
    Vector<String> enter = new Vector<>();
    enter.add(lineIndex);
    enter.add(word);
    enter.add(type);
    enter.add(String.valueOf(offset));
    symbol.add(enter);
  }

  // 生成一条三地址四元式
  private static void gen(String lineIndex, String idn, String addr) {
    Vector<String> gen = new Vector<>();
    gen.add(lineIndex);
    gen.add(String.valueOf(index));
    String three = idn + " = " + addr;
    gen.add(three);
    String four = "(=, " + addr + ", _, " + idn + ")";
    gen.add(four);
    intermediate.add(gen);
    index++;
  }

  // 变量声明
  // D -> T idn {enter(idn.word,T.type,offset); offset=offset+T.width} A ;
  public static void varDecl(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode T = parent.children.get(0);
    SemanticNode idn = parent.children.get(1);

    enter(idn.lineIndex, idn.word, T.attr.get("type"));
    Action.idn.add(idn); // *最近的idn存储temp
    offset += Integer.valueOf(T.attr.get("width"));
  }

  // 变量类型
  // T -> X {t=X.type; w=X.width} C {T.type=C.type; T.width=C.width}
  public static void varType1(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode X = parent.children.get(0);

    Action.t = X.attr.get("type");
    Action.w = X.attr.get("width");
  }

  public static void varType2(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode C = parent.children.get(2);

    parent.attr.put("type", C.attr.get("type"));
    parent.attr.put("width", C.attr.get("width"));

    // *T需要用temp存
    Action.T = parent;
  }

  // 变量连续声明
  // A -> , idn {enter(idn.word,T.type,offset); offset=offset+T.width)} A
  // *T需要用temp存
  public static void varContDecl(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode idn = parent.children.get(1);
    SemanticNode T = Action.T;

    enter(idn.lineIndex, idn.word, T.attr.get("type"));
    Action.idn.add(idn); // *最近的idn存储temp
    offset += Integer.valueOf(T.attr.get("width"));
  }

  // 变量声明赋值
  // A -> = G {gen(idn'='G.addr)}
  // *idn需要用temp存
  public static void varDeclAssi(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode G = parent.children.get(0);

    for (SemanticNode idn : Action.idn) {
      gen(idn.lineIndex, idn.word, G.attr.get("addr"));
    }
    idn.clear();
  }

  // 基本变量类型 int
  // X -> int {X.type=int; X.width=4}
  public static void varInt(SemanticNode node) {
    SemanticNode parent = node.parrent;

    parent.attr.put("type", "int");
    parent.attr.put("width", "4");
  }

  // 基本变量类型 float
  // X -> float {X.type=float; X.width=8}
  public static void varFloat(SemanticNode node) {
    SemanticNode parent = node.parrent;

    parent.attr.put("type", "float");
    parent.attr.put("width", "8");
  }

  // 基本变量类型 char
  // X -> char {X.type=char; X.width=1}
  public static void varChar(SemanticNode node) {
    SemanticNode parent = node.parrent;

    parent.attr.put("type", "char");
    parent.attr.put("width", "1");
  }

  // 声明数组类型
  // C -> [ cst ] C {C.type=array(cst.word,C.type); C.width=cst.word*C.width}
  public static void varArray(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode cst = parent.children.get(1);
    SemanticNode C = parent.children.get(3);

    parent.attr.put("type", "array(" + cst.word + "," + C.attr.get("type") + ")");
    parent.attr.put("width",
        String.valueOf(Integer.valueOf(cst.word) * Integer.valueOf(C.attr.get("width"))));
  }

  // 变量声明结束
  // C -> ε {C.type=t; C.width=w}
  public static void varEnd(SemanticNode node) {
    SemanticNode parent = node.parrent;

    parent.attr.put("type", Action.t);
    parent.attr.put("width", Action.w);
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== hanghang
  // ========== ========== ========== ========== ========== ========== ========== ==========

}
