package semantic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 静态类 每个语义动作对应一个函数
public class Action {
  public static int offset = 0; // 偏移量
  public static int index = 1; // 三地址序号

  // 符号表: lineIndex idn type offset
  public static Vector<Vector<String>> symbol = new Vector<>();
  // 中间代码: lineIndex index three four
  public static Vector<Vector<String>> intermediate = new Vector<>();

  // addr 声明变量:变量名 临时变量:t+index
  // 所有声明变量 对应声明Type(数组)
  public static Map<String, Integer> declVar = new HashMap<>();
  // 变量对应序号
  public static Map<String, Integer> idn2Index = new HashMap<>();

  public static Map<String, Method> function = new HashMap<>(); // String -> Method
  static {
    try {
      // 程序入口
      function.put("init", Action.class.getMethod("init", SemanticNode.class));
      // 变量声明
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
      // 变量赋值
      function.put("assign_end", Action.class.getMethod("assignEnd", SemanticNode.class));
      function.put("assign_var", Action.class.getMethod("assignVar", SemanticNode.class));
      function.put("assign_array_end",
          Action.class.getMethod("assignArrayEnd", SemanticNode.class));
      function.put("assign_array", Action.class.getMethod("assignArray", SemanticNode.class));
      function.put("assign_exp", Action.class.getMethod("assignExp", SemanticNode.class));
      function.put("assign_opr", Action.class.getMethod("assignOpr", SemanticNode.class));
      function.put("assign_parth", Action.class.getMethod("assignParth", SemanticNode.class));
      function.put("assign_base", Action.class.getMethod("assignBase", SemanticNode.class));
      function.put("assign_src", Action.class.getMethod("assignSrc", SemanticNode.class));
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

    declVar.put(word, symbol.size() - 1);
  }

  // 生成一条三地址四元式
  private static void genAssign(String lineIndex, String idn, String first, String opr,
      String second) {
    Vector<String> gen = new Vector<>();
    gen.add(lineIndex);
    gen.add(String.valueOf(index));

    String three = "";
    String four = "";
    if (opr.equals("")) {
      three = idn + " = " + first;
      four = "( = , " + first + " , _ , " + idn + " )";
    } else if (second.equals("")) {
      three = idn + " = " + opr + " " + first;
      four = "( " + opr + " , " + first + " , _ , " + idn + " )";
    } else {
      three = idn + " = " + first + " " + opr + " " + second;
      four = "( " + opr + " , " + first + " , " + second + " , " + idn + " )";
    }
    gen.add(three);
    gen.add(four);
    intermediate.add(gen);

    idn2Index.put(idn, index);

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
    SemanticNode G = parent.children.get(1);

    for (SemanticNode idn : Action.idn) {
      if (G.attr.containsKey("val")) {
        genAssign(idn.lineIndex, idn.word, G.attr.get("val"), "", "");
      } else {
        genAssign(idn.lineIndex, idn.word, G.attr.get("addr"), "", "");
      }
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
  // ========== ========== ========== ========== ========== ========== ========== 变量赋值
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // 寻找变量的addr
  private static String lookup(String idn) {
    if (idn2Index.containsKey(idn) || declVar.keySet().contains(idn)) {
      return idn;
    } else {
      return null;
    }
  }


  // 优化: 自上向下传递arraytype 正则提取处理 计算每个L'offset 向上传递相加
  // 数组引用获取偏移量
  private static String getOffset(String idn, String quote) {
    String idnType = symbol.get(declVar.get(idn)).get(2);

    Pattern pattern = Pattern.compile("(array\\()(\\d+)");
    Matcher matcher = pattern.matcher(idnType);
    List<Integer> length = new ArrayList<>();
    while (matcher.find()) {
      length.add(Integer.valueOf(matcher.group().replaceAll("array\\(", "")));
    }

    Pattern patternT = Pattern.compile("([a-z]+)(\\))");
    Matcher matcherT = patternT.matcher(idnType);
    matcherT.find();
    String type = matcherT.group().replaceAll("\\)", "");
    int width = 0;
    switch (type) {
      case "int":
        width = 4;
        break;
      case "float":
        width = 8;
        break;
      case "char":
        width = 1;
        break;
    }

    String[] quotes = quote.split(" ");

    if (length.size() != quotes.length) {
      return null;
    }

    List<String> tempIdns = new ArrayList<>();

    for (int i = 0; i < quotes.length; i++) {
      int temp = width;
      for (int j = i + 1; j < length.size(); j++) {
        temp *= length.get(j);
      }
      String tempIdn = "t" + String.valueOf(index);
      tempIdns.add(tempIdn);
      genAssign("", tempIdn, quotes[i], "*", String.valueOf(temp));
    }

    String offset = tempIdns.get(0);
    for (int i = 1; i < tempIdns.size(); i++) {
      String tempIdn = "t" + String.valueOf(index);
      genAssign("", tempIdn, offset, "+", tempIdns.get(i));
      offset = tempIdn;
    }
    return offset;
  }

  // 赋值语句结束
  // S -> L equal E ; {gen(L.addr'='E.addr) 可能是连等于}
  public static void assignEnd(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode L = parent.children.get(0);
    SemanticNode equal = parent.children.get(1);
    SemanticNode E = parent.children.get(2);
    SemanticNode SEM = parent.children.get(3);

    if (equal.children.get(0).word.length() == 1) {
      genAssign(SEM.lineIndex, L.attr.get("addr"), E.attr.get("addr"), "", "");
    } else {
      String Laddr = "t" + String.valueOf(index);
      genAssign(SEM.lineIndex, Laddr, L.attr.get("addr"),
          String.valueOf(equal.children.get(0).word.charAt(0)), E.attr.get("addr"));
      genAssign(SEM.lineIndex, L.attr.get("addr"), Laddr, "", "");
    }
  }

  // 赋值左部 变量或数组引用
  // L -> idn {L.addr=loopkup(idn.word)} L' {L.addr=idn[getOffset(L'.type)]}
  public static void assignVar(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode idn = parent.children.get(0);

    parent.attr.put("addr", lookup(idn.word));
  }

  public static void assignArrayEnd(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode Lp = parent.children.get(2);

    if (Lp.attr.containsKey("type")) {
      parent.attr.put("addr", parent.attr.get("addr") + "["
          + getOffset(parent.attr.get("addr"), Lp.attr.get("type")) + "]");
    }
  }

  // 赋值 数组应用
  // L' -> [E]L' {L'.type=E.addr L'.type}
  public static void assignArray(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode E = parent.children.get(1);
    SemanticNode Lp = parent.children.get(3);

    if (Lp.attr.containsKey("type")) {
      parent.attr.put("type", E.attr.get("addr") + " " + Lp.attr.get("type"));
    } else {
      parent.attr.put("type", E.attr.get("addr"));
    }
  }

  // 赋值右部 算术表达式
  // E -> G E' {E.addr=G.addr 'E'.opr' E'.addr 可能为空}
  public static void assignExp(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode G = parent.children.get(0);
    SemanticNode Ep = parent.children.get(1);

    String Eaddr = "t" + String.valueOf(index);
    parent.attr.put("addr", Eaddr);

    if (!Ep.attr.containsKey("opr")) {
      if (G.attr.containsKey("val")) {
        parent.attr.put("addr", G.attr.get("val"));
      } else {
        parent.attr.put("addr", G.attr.get("addr"));
      }
    } else {
      if (G.attr.containsKey("val")) {
        genAssign("", Eaddr, G.attr.get("val"), Ep.attr.get("opr"), Ep.attr.get("addr"));
      } else {
        genAssign("", Eaddr, G.attr.get("addr"), Ep.attr.get("opr"), Ep.attr.get("addr"));
      }
    }
  }

  // 增加赋值项
  // E' -> + G E' {E'.opr=+; gen(E'.addr'='G.addr 'E'.opr' E'.addr 可能为空)}
  public static void assignOpr(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode OPR = parent.children.get(0);
    SemanticNode G = parent.children.get(1);
    SemanticNode Ep = parent.children.get(2);

    String EpAddr = "t" + String.valueOf(index);
    parent.attr.put("addr", EpAddr);

    parent.attr.put("opr", "+");
    if (!Ep.attr.containsKey("opr")) {
      if (G.attr.containsKey("val")) {
        parent.attr.put("addr", G.attr.get("val"));
      } else {
        parent.attr.put("addr", G.attr.get("addr"));
      }
    } else {
      if (G.attr.containsKey("val")) {
        genAssign(OPR.lineIndex, EpAddr, G.attr.get("val"), Ep.attr.get("opr"),
            Ep.attr.get("addr"));
      } else {
        genAssign(OPR.lineIndex, EpAddr, G.attr.get("addr"), Ep.attr.get("opr"),
            Ep.attr.get("addr"));
      }
    }
  }

  // 括号优先级
  // G -> (E) {G.addr=E.addr}
  public static void assignParth(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode E = parent.children.get(1);

    parent.attr.put("addr", E.attr.get("addr"));
  }

  // 赋值右部 基本变量
  // G -> cst | flt | oct | hex | chr {G.val=base.word}
  public static void assignBase(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode base = parent.children.get(0);

    parent.attr.put("val", base.word);
  }

  // 赋值右部 变量
  // G -> L {G.addr=L.addr}
  public static void assignSrc(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode L = parent.children.get(0);

    parent.attr.put("addr", L.attr.get("addr"));
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== hanghang
  // ========== ========== ========== ========== ========== ========== ========== ==========

}
