package semantic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 静态类 每个语义动作对应一个函数
public class Action {
  public static int offset = 0; // 偏移量
  public static int index = 0; // 三地址序号

  // 符号表: lineIndex idn type offset
  public static Vector<Vector<String>> symbol = new Vector<>();
  // 中间代码: lineIndex index three four
  public static Vector<Vector<String>> intermediate = new Vector<>();
  // 错误信息: lineIndex idn info
  public static Vector<Vector<String>> errorData = new Vector<>();

  // addr 声明变量:变量名 临时变量:t+index
  // 所有声明变量 对应符号表索引
  public static Map<String, Integer> declVar = new HashMap<>();
  // 所有赋值变量(可能是临时变量) 对应中间代码序号
  public static Map<String, Integer> idn2Index = new HashMap<>();

  // procIdn对应信息
  public static Map<String, Map<String, String>> procIdnInfo = new HashMap<>();

  public static Map<String, Method> function = new HashMap<>(); // String -> Method
  static {
    try {
      // 程序入口
      function.put("init", Action.class.getMethod("init", SemanticNode.class));

      // 变量声明
      function.put("var_decl", Action.class.getMethod("varDecl", SemanticNode.class));
      function.put("var_record", Action.class.getMethod("varRecord", SemanticNode.class));
      function.put("var_proc", Action.class.getMethod("varProc", SemanticNode.class));
      function.put("return_type_dp", Action.class.getMethod("returnTypeDp", SemanticNode.class));
      function.put("var_param", Action.class.getMethod("varParam", SemanticNode.class));
      function.put("var_param_type", Action.class.getMethod("varParamType", SemanticNode.class));
      function.put("var_cont_param", Action.class.getMethod("varContParam", SemanticNode.class));
      function.put("var_cont_param_type",
          Action.class.getMethod("varContParamType", SemanticNode.class));
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

      // 控制流
      function.put("back_m", Action.class.getMethod("backM", SemanticNode.class));
      function.put("back_n", Action.class.getMethod("backN", SemanticNode.class));
      function.put("ctrl_testS", Action.class.getMethod("ctrlTestS", SemanticNode.class));
      function.put("ctrl_testBt", Action.class.getMethod("ctrlTestBt", SemanticNode.class));
      function.put("ctrl_testBf", Action.class.getMethod("ctrlTestBf", SemanticNode.class));
      function.put("ctrl_if", Action.class.getMethod("ctrlIf", SemanticNode.class));
      function.put("ctrl_p", Action.class.getMethod("ctrlP", SemanticNode.class));
      function.put("ctrl_np", Action.class.getMethod("ctrlNp", SemanticNode.class));
      function.put("ctrl_while", Action.class.getMethod("ctrlWhile", SemanticNode.class));
      function.put("ctrl_vplus", Action.class.getMethod("ctrlVplus", SemanticNode.class));
      function.put("ctrl_vminus", Action.class.getMethod("ctrlVminus", SemanticNode.class));
      function.put("ctrl_for1", Action.class.getMethod("ctrlFor1", SemanticNode.class));
      function.put("ctrl_for2", Action.class.getMethod("ctrlFor2", SemanticNode.class));
      function.put("ctrl_switch1", Action.class.getMethod("ctrlSwitch1", SemanticNode.class));
      function.put("ctrl_switch2", Action.class.getMethod("ctrlSwitch2", SemanticNode.class));
      function.put("ctrl_switch3", Action.class.getMethod("ctrlSwitch3", SemanticNode.class));
      function.put("ctrl_case1", Action.class.getMethod("ctrlCase1", SemanticNode.class));
      function.put("ctrl_case2", Action.class.getMethod("ctrlCase2", SemanticNode.class));
      function.put("ctrl_casen", Action.class.getMethod("ctrlCasen", SemanticNode.class));

      // 函数声明检测
      function.put("return_type_p", Action.class.getMethod("returnTypeP", SemanticNode.class));
      function.put("return_type_sp", Action.class.getMethod("returnTypeSp", SemanticNode.class));
      function.put("check_return_type",
          Action.class.getMethod("checkReturnType", SemanticNode.class));

      // 布尔表达式
      function.put("inherit_H_node1", Action.class.getMethod("inheritHNode1", SemanticNode.class));
      function.put("inherit_H_node2", Action.class.getMethod("inheritHNode2", SemanticNode.class));
      function.put("get_list", Action.class.getMethod("getList", SemanticNode.class));
      function.put("bool_not", Action.class.getMethod("boolNot", SemanticNode.class));
      function.put("bool_or", Action.class.getMethod("boolOr", SemanticNode.class));
      function.put("bool_and", Action.class.getMethod("boolAnd", SemanticNode.class));
      function.put("bool_null", Action.class.getMethod("boolNull", SemanticNode.class));
      function.put("add_parentheses", Action.class.getMethod("addParentheses", SemanticNode.class));
      function.put("make_relop_list", Action.class.getMethod("makeRelopList", SemanticNode.class));
      function.put("make_true_list", Action.class.getMethod("makeTrueList", SemanticNode.class));
      function.put("make_false_list", Action.class.getMethod("makeFalseList", SemanticNode.class));

      // 函数调用
      function.put("call_function", Action.class.getMethod("callFunction", SemanticNode.class));
      function.put("call_function_return",
          Action.class.getMethod("callFunctionReturn", SemanticNode.class));
      function.put("initialize_queue",
          Action.class.getMethod("initializeQueue", SemanticNode.class));
      function.put("add_parameter", Action.class.getMethod("addParameter", SemanticNode.class));
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
    if (opr.equals("proc")) { // f 函数名 s 参数个数 idn 赋值
      three = idn + " = call " + first + ", " + second;
      four = "( call , " + first + " , " + second + " , " + idn + " )";
    } else if (opr.equals("")) {
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
    Action.idn.clear();
    Action.idn.add(idn); // *最近的idn存储temp
    offset += Integer.valueOf(T.attr.get("width"));
  }

  // 变量类型record
  // D -> record idn {enter(idn.word,record,offset)} { P }
  public static void varRecord(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode record = parent.children.get(0);
    SemanticNode idn = parent.children.get(1);

    enter(record.lineIndex, idn.word, "record");
  }

  // 变量类型proc
  // D -> proc X idn {enter(idn.word,record:X.type,offset)} ( M ) { {P.return=X.type} P }
  public static void varProc(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode proc = parent.children.get(0);
    SemanticNode X = parent.children.get(1);
    SemanticNode idn = parent.children.get(2);

    enter(proc.lineIndex, idn.word, "proc:" + X.attr.get("type"));
  }

  public static void returnTypeDp(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode X = parent.children.get(1);
    SemanticNode idn = parent.children.get(2);
    SemanticNode M = parent.children.get(5);
    SemanticNode P = parent.children.get(9);

    P.attr.put("return", X.attr.get("type"));

    Map<String, String> idnInfo = new HashMap<>();
    idnInfo.put("type", "proc");
    idnInfo.put("return", X.attr.get("type"));
    idnInfo.put("param", M.attr.get("param"));

    procIdnInfo.put(idn.word, idnInfo);
  }

  // 参数声明
  // M -> X idn {enter(idn.word,X.type,offset); offset=offset+X.width} M'
  // {M.param=X.type","M'.param}
  public static void varParam(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode X = parent.children.get(0);
    SemanticNode idn = parent.children.get(1);

    enter(idn.lineIndex, idn.word, X.attr.get("type"));
    offset += Integer.valueOf(X.attr.get("width"));
  }

  public static void varParamType(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode X = parent.children.get(0);
    SemanticNode Mp = parent.children.get(3);

    if (Mp.attr.containsKey("param")) {
      parent.attr.put("param", X.attr.get("type") + "," + Mp.attr.get("param"));
    } else {
      parent.attr.put("param", X.attr.get("type"));
    }
  }

  // 参数连续声明
  // M' -> , X idn {enter(idn.word,X.type,offset); offset=offset+X.width} M1'
  // {M'.param=X.type","M1'.param}
  public static void varContParam(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode X = parent.children.get(1);
    SemanticNode idn = parent.children.get(2);

    enter(idn.lineIndex, idn.word, X.attr.get("type"));
    offset += Integer.valueOf(X.attr.get("width"));
  }

  public static void varContParamType(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode X = parent.children.get(1);
    SemanticNode Mp = parent.children.get(4);

    if (Mp.attr.containsKey("param")) {
      parent.attr.put("param", X.attr.get("type") + "," + Mp.attr.get("param"));
    } else {
      parent.attr.put("param", X.attr.get("type"));
    }
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

  // 寻找变量的addr 变量使用时必须是声明过的
  // 返回变量的type
  private static String lookup(String idn) {
    if (declVar.keySet().contains(idn)) {
      return symbol.get(declVar.get(idn)).get(2);
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
  // S -> L equal E ; {checktype(L,E); S.nextlist=null; gen(L.addr'='E.addr) 可能是连等于}
  public static void assignEnd(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode L = parent.children.get(0);
    SemanticNode equal = parent.children.get(1);
    SemanticNode E = parent.children.get(2);
    SemanticNode SEM = parent.children.get(3);

    parent.attr.put("nextlist", "");

    if (L.attr.get("type").equals(E.attr.get("type"))) { // 类型匹配
      // 函数返回值赋值
      if (E.attr.get("addr").equals("proc")) {
        genAssign(SEM.lineIndex, L.attr.get("addr"), E.children.get(1).word, "proc",
            E.attr.get("num"));
      }

      // 算术表达式赋值
      else {
        if (equal.children.get(0).word.length() == 1) {
          genAssign(SEM.lineIndex, L.attr.get("addr"), E.attr.get("addr"), "", "");
        } else {
          String Laddr = "t" + String.valueOf(index);
          genAssign(SEM.lineIndex, Laddr, L.attr.get("addr"),
              String.valueOf(equal.children.get(0).word.charAt(0)), E.attr.get("addr"));
          genAssign(SEM.lineIndex, L.attr.get("addr"), Laddr, "", "");
        }
      }
    } else {
      // TODO 错误处理 类型不匹配 强制类型转换
      System.out.println("------------------------不匹配");
    }
  }

  // 赋值左部 变量或数组引用
  // L -> idn {L.type=idn.type; L.addr=loopkup(idn.word)} L' {L.addr=idn[getOffset(L'.type)]}
  public static void assignVar(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode idn = parent.children.get(0);

    String addr = lookup(idn.word);
    if (addr == null) {
      // TODO 错误处理 未声明变量引用
    } else {
      parent.attr.put("addr", idn.word);
      parent.attr.put("type", lookup(idn.word));
    }
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
  // E -> G E' {E.type=G.type; E.addr=G.addr 'E'.opr' E'.addr 可能为空}
  public static void assignExp(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode G = parent.children.get(0);
    SemanticNode Ep = parent.children.get(1);

    String Eaddr = "t" + String.valueOf(index);
    parent.attr.put("addr", Eaddr);
    parent.attr.put("type", G.attr.get("type"));

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

    parent.attr.put("opr", OPR.word);
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
  // G -> (E) {G.type=E.type; G.addr=E.addr}
  public static void assignParth(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode E = parent.children.get(1);

    parent.attr.put("addr", E.attr.get("addr"));
    parent.attr.put("type", E.attr.get("type"));
  }

  // 赋值右部 基本变量
  // G -> cst | flt | oct | hex | chr {G.type=base.type; G.val=base.word}
  public static void assignBase(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode base = parent.children.get(0);

    parent.attr.put("val", base.word);
    if (base.data.equals("CST") || base.data.equals("OCT") || base.data.equals("HEX")) {
      parent.attr.put("type", "int");
    } else if (base.data.equals("FLT")) {
      parent.attr.put("type", "float");
    } else {
      parent.attr.put("type", "char");
    }
  }

  // 赋值右部 变量
  // G -> L {G.type=L.type; G.addr=L.addr}
  public static void assignSrc(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode L = parent.children.get(0);

    parent.attr.put("addr", L.attr.get("addr"));
    parent.attr.put("type", L.attr.get("type"));
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== 控制流
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // 回填辅助非终结符K（M）的空转移动作
  // K -> ε {K.quad = nextquad;}
  public static void backM(SemanticNode node) {
    SemanticNode KNode = node.parrent;
    int nextQuad = index;
    KNode.attr.put("quad", String.valueOf(nextQuad));
  }

  // 回填辅助非终结符O（N）的空转移动作
  // O -> ε { O.nextlist = makelist(nextquad);gen(‘goto _’);}
  public static void backN(SemanticNode node) {
    SemanticNode ONode = node.parrent;
    ONode.attr.put("nextlist", String.valueOf(index));
    Vector<String> line = new Vector<String>();
    line.add(" ");
    line.add(String.valueOf(index));
    line.add("goto ");
    line.add("(j, _, _, )");
    intermediate.add(line);
    index++;
  }

  // 用于debug的S
  public static void ctrlTestS(SemanticNode node) {
    SemanticNode parent = node.parrent;
    SemanticNode idnNode = parent.children.get(0);
    SemanticNode cstNode = parent.children.get(2);
    genAssign(idnNode.lineIndex, idnNode.word, cstNode.word, "", "");
    parent.attr.put("nextlist", "");
  }

  // 用于debug的B
  public static void ctrlTestBt(SemanticNode node) {
    SemanticNode BNode = node.parrent;
    BNode.attr.put("truelist", String.valueOf(index));
    BNode.attr.put("falselist", "");
    Vector<String> line = new Vector<String>();
    line.add(BNode.children.get(0).lineIndex);
    line.add(String.valueOf(index));
    line.add("goto ");
    line.add("(j, _, _, )");
    intermediate.add(line);
    index++;
  }

  // 用于debug的B
  public static void ctrlTestBf(SemanticNode node) {
    SemanticNode BNode = node.parrent;
    BNode.attr.put("falselist", String.valueOf(index));
    BNode.attr.put("truelist", "");
    Vector<String> line = new Vector<String>();
    line.add(BNode.children.get(0).lineIndex);
    line.add(String.valueOf(index));
    line.add("goto ");
    line.add("(j, _, _, )");
    intermediate.add(line);
    index++;
  }

  // 提取属性中的list序号列表
  public static HashSet<String> ctrlGetList(SemanticNode node, String attr) {
    HashSet<String> result = new HashSet<String>();
    for (String str : node.attr.get(attr).split(",")) {
      result.add(str);
    }
    return result;
  }

  // 对list中的三地址码和四元组进行回填
  public static void ctrlBackPatch(Set<String> list, String quad) {
    for (String str : list) {
      if (str.length() > 0) {
        String tac = intermediate.get(Integer.parseInt(str)).get(2);
        intermediate.get(Integer.parseInt(str)).set(2, tac + quad);
        String fte = intermediate.get(Integer.parseInt(str)).get(3);
        fte = fte.substring(0, fte.length() - 1);
        intermediate.get(Integer.parseInt(str)).set(3, fte + quad + ")");
      }
    }
  }

  // 辅助函数 把标号集合转化成属性中存储的字符串
  public static String ctrlSet2String(Set<String> list) {
    String result = "";
    for (String str : list) {
      result += str + ",";
    }
    result = result.substring(0, result.length() - 1);
    return result;
  }

  // IF控制流
  // S -> if B then K1 S O else K2 S {S.nextlist = merge( merge(S1.nextlist,
  // O.nextlist),S2.nextlist); backpatch(B.truelist,K1.quad); backpatch(B.falselist,K2.quad)}
  public static void ctrlIf(SemanticNode node) {
    SemanticNode SNode = node.parrent;
    SemanticNode BNode = SNode.children.get(1);
    SemanticNode K1Node = SNode.children.get(3);
    SemanticNode K2Node = SNode.children.get(7);
    SemanticNode S1Node = SNode.children.get(4);
    SemanticNode S2Node = SNode.children.get(8);
    SemanticNode ONode = SNode.children.get(5);
    HashSet<String> SnextList = new HashSet<String>();
    HashSet<String> BtrueList = new HashSet<String>();
    HashSet<String> BfalseList = new HashSet<String>();
    SnextList.addAll(ctrlGetList(S1Node, "nextlist"));
    SnextList.addAll(ctrlGetList(ONode, "nextlist"));
    SnextList.addAll(ctrlGetList(S2Node, "nextlist"));
    SNode.attr.put("nextlist", ctrlSet2String(SnextList));

    BtrueList.addAll(ctrlGetList(BNode, "truelist"));
    BfalseList.addAll(ctrlGetList(BNode, "falselist"));
    ctrlBackPatch(BtrueList, K1Node.attr.get("quad"));
    ctrlBackPatch(BfalseList, K2Node.attr.get("quad"));
  }

  // 处理S的递归生成
  // P -> {P1.return = P.return;S.return = P.return;} S K P1
  // {P.nextlist = P1.nextlist; backpatch(S.nextlist,K.quad);}
  public static void ctrlP(SemanticNode node) {
    SemanticNode PNode = node.parrent;
    SemanticNode SNode = PNode.children.get(1);
    SemanticNode KNode = PNode.children.get(2);
    SemanticNode P1Node = PNode.children.get(3);
    String nextListStr = P1Node.attr.get("nextlist");
    HashSet<String> SnextList = new HashSet<String>();
    SnextList.addAll(ctrlGetList(SNode, "nextlist"));
    PNode.attr.put("nextlist", nextListStr);
    ctrlBackPatch(SnextList, KNode.attr.get("quad"));
  }

  // 处理P的空转移事件
  // P -> ε{P.nextlist = null}
  public static void ctrlNp(SemanticNode node) {
    SemanticNode PNode = node.parrent;
    PNode.attr.put("nextlist", "");
  }

  // while控制流
  // S -> while K1 B do K2 S1 {S.nextlist = B.falselist; backpatch(S1.nextlist,K1.quad);
  // backpatch(B.truelist,K2.quad); gen("goto" K1.quad)}
  public static void ctrlWhile(SemanticNode node) {
    SemanticNode SNode = node.parrent;
    SemanticNode K1Node = SNode.children.get(1);
    SemanticNode BNode = SNode.children.get(2);
    SemanticNode K2Node = SNode.children.get(4);
    SemanticNode S1Node = SNode.children.get(5);
    SNode.attr.put("nextlist", BNode.attr.get("falselist"));
    HashSet<String> S1nextList = new HashSet<String>();
    HashSet<String> BtrueList = new HashSet<String>();
    S1nextList.addAll(ctrlGetList(S1Node, "nextlist"));
    BtrueList.addAll(ctrlGetList(BNode, "truelist"));
    ctrlBackPatch(S1nextList, K1Node.attr.get("quad"));
    ctrlBackPatch(BtrueList, K2Node.attr.get("quad"));

    Vector<String> line = new Vector<String>();
    line.add(" ");
    line.add(String.valueOf(index));
    line.add("goto " + K1Node.attr.get("quad"));
    line.add("(j, _, _, " + K1Node.attr.get("quad") + ")");
    intermediate.add(line);
    index++;
  }

  // for循环判断递增
  // V -> PLSPLS {V.type = "add"}
  public static void ctrlVplus(SemanticNode node) {
    SemanticNode VNode = node.parrent;
    VNode.attr.put("type", "add");
  }

  // for循环判断递增
  // V -> MNSMNS {V.type = "minus"}
  public static void ctrlVminus(SemanticNode node) {
    SemanticNode VNode = node.parrent;
    VNode.attr.put("type", "minus");
  }

  // for循环 用于生成++/--语句
  // S -> for ( S1 K1 B ; K2 IDN V {if V.type = "add": gen(IDN = IDN + 1);gen(goto
  // K1.quad);else:gen(IDN = IDN - 1);gen(goto K1.quad);}
  // ) { K3 S2 } {ctrl_for2}
  public static void ctrlFor1(SemanticNode node) {
    SemanticNode SNode = node.parrent;
    SemanticNode K1Node = SNode.children.get(3);
    SemanticNode idnNode = SNode.children.get(7);
    SemanticNode VNode = SNode.children.get(8);
    if (VNode.attr.get("type").equals("add")) {
      Vector<String> line = new Vector<String>();
      line.add(idnNode.lineIndex);
      line.add(String.valueOf(index));
      line.add(idnNode.word + " = " + idnNode.word + " + 1");
      line.add("(+, " + idnNode.word + ", 1, " + idnNode.word + ")");
      intermediate.add(line);
      index++;
    } else {
      Vector<String> line = new Vector<String>();
      line.add(idnNode.lineIndex);
      line.add(String.valueOf(index));
      line.add(idnNode.word + " = " + idnNode.word + " + 1");
      line.add("(-, " + idnNode.word + ", 1, " + idnNode.word + ")");
      intermediate.add(line);
      index++;
    }
    Vector<String> line = new Vector<String>();
    line = new Vector<String>();
    line.add(idnNode.lineIndex);
    line.add(String.valueOf(index));
    line.add("goto " + K1Node.attr.get("quad"));
    line.add("(j, _, _, " + K1Node.attr.get("quad") + ")");
    intermediate.add(line);
    index++;
  }

  // for循环 用于属性赋值与回填
  // S -> for ( S1 K1 B ; K2 IDN V {ctrl_for1}
  // ) { K3 S2 } {S.nextlist =
  // B.falselist;backpatch(S1.nextlist,K1.quad);backpatch(B.truelist,K3.quad)
  // backpatch(S2.nextlist,K2.quad); gen(goto K2.quad)}
  public static void ctrlFor2(SemanticNode node) {
    SemanticNode SNode = node.parrent;
    SemanticNode S1Node = SNode.children.get(2);
    SemanticNode S2Node = SNode.children.get(13);
    SemanticNode BNode = SNode.children.get(4);
    SemanticNode K1Node = SNode.children.get(3);
    SemanticNode K2Node = SNode.children.get(6);
    SemanticNode K3Node = SNode.children.get(12);
    SemanticNode idnNode = SNode.children.get(7);
    SNode.attr.put("nextlist", BNode.attr.get("falselist"));
    HashSet<String> S1nextList = new HashSet<String>();
    HashSet<String> S2nextList = new HashSet<String>();
    HashSet<String> trueList = new HashSet<String>();
    S1nextList.addAll(ctrlGetList(S1Node, "nextlist"));
    S2nextList.addAll(ctrlGetList(S2Node, "nextlist"));
    trueList.addAll(ctrlGetList(BNode, "truelist"));
    ctrlBackPatch(S1nextList, K1Node.attr.get("quad"));
    ctrlBackPatch(trueList, K3Node.attr.get("quad"));
    ctrlBackPatch(S2nextList, K2Node.attr.get("quad"));

    Vector<String> line = new Vector<String>();
    line.add(idnNode.lineIndex);
    line.add(String.valueOf(index));
    line.add("goto " + K2Node.attr.get("quad"));
    line.add("(j, _, _, " + K2Node.attr.get("quad") + ")");
    intermediate.add(line);
    index++;
  }

  public static int ctrlStemp = -1;

  // switch 中的主体
  // S -> switch ( IDN ) { {N.idn = IDN} N default : {label(Ln);} S1 } {S.nextlist = N.nextlist}
  public static void ctrlSwitch1(SemanticNode node) {
    SemanticNode SNode = node.parrent;
    SemanticNode NNode = SNode.children.get(6);
    SemanticNode idnNode = SNode.children.get(2);
    NNode.attr.put("idn", idnNode.word);
  }

  // switch 中的default
  // S -> switch ( IDN ) { {N.idn = IDN} N default : {label(Ln);} S1 } {S.nextlist = N.nextlist}
  public static void ctrlSwitch2(SemanticNode node) {
    if (ctrlStemp != -1) {
      HashSet<String> list = new HashSet<String>();
      list.add(String.valueOf(ctrlStemp));
      ctrlBackPatch(list, String.valueOf(index));
    }
  }

  // switch 中的主体
  // S -> switch ( IDN ) { {N.idn = IDN} N default : {label(Ln);} S1 } {S.nextlist = N.nextlist}
  public static void ctrlSwitch3(SemanticNode node) {
    SemanticNode SNode = node.parrent;
    SemanticNode NNode = SNode.children.get(6);
    SNode.attr.put("nextlist", NNode.attr.get("nextlist"));
    ctrlStemp = -1;
  }

  // switch 中的case语句
  // N -> case cst : {label(Ln-1);Ln = newlabel(); gen("if"IDN.word"!="cst"goto"Ln) }
  // S O N1 {N.nextlist = Merge(Merge(S.nextlist,O.nextlist),N1.nextlist)}
  public static void ctrlCase1(SemanticNode node) {
    SemanticNode NNode = node.parrent;
    SemanticNode N1Node = NNode.children.get(6);
    SemanticNode cstNode = NNode.children.get(1);
    N1Node.attr.put("idn", NNode.attr.get("idn"));
    if (ctrlStemp != -1) {
      HashSet<String> list = new HashSet<String>();
      list.add(String.valueOf(ctrlStemp));
      ctrlBackPatch(list, String.valueOf(index));
    }
    String idn = NNode.attr.get("idn");
    ctrlStemp = index;
    Vector<String> line = new Vector<String>();
    line.add(cstNode.lineIndex);
    line.add(String.valueOf(index));
    line.add("if " + idn + "!= " + cstNode.word + " goto ");
    line.add("(!=, " + idn + ", " + cstNode.word + ", )");
    intermediate.add(line);
    index++;
  }

  // switch 中的case语句
  // N -> case cst : {label(Ln-1);Ln = newlabel(); gen("if"IDN.word"!="cst"goto"Ln) }
  // S O N1 {N.nextlist = Merge(Merge(S.nextlist,O.nextlist),N1.nextlist)}
  public static void ctrlCase2(SemanticNode node) {
    SemanticNode NNode = node.parrent;
    SemanticNode SNode = NNode.children.get(4);
    SemanticNode ONode = NNode.children.get(5);
    SemanticNode N1Node = NNode.children.get(6);
    HashSet<String> NnextList = new HashSet<String>();
    NnextList.addAll(ctrlGetList(SNode, "nextlist"));
    NnextList.addAll(ctrlGetList(ONode, "nextlist"));
    NnextList.addAll(ctrlGetList(N1Node, "nextlist"));
    NNode.attr.put("nextlist", ctrlSet2String(NnextList));
  }

  // switch 中case的空转移
  // N -> ε {N.nextlist = null}
  public static void ctrlCasen(SemanticNode node) {
    SemanticNode NNode = node.parrent;
    NNode.attr.put("nextlist", "");
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== 函数声明中返回类型检测
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // P的函数声明中返回值与声明返回值类型检测
  // P -> {P1.return = P.return} D P1
  public static void returnTypeP(SemanticNode node) {
    SemanticNode PNode = node.parrent;
    SemanticNode P1Node = PNode.children.get(2);
    if (PNode.attr.containsKey("return")) {
      P1Node.attr.put("return", PNode.attr.get("return"));
    }
  }

  // P的函数声明中返回值与声明返回值类型检测
  // P -> {P1.return = P.return;S.return = P.return;} S K P1 {ctrl_p}
  public static void returnTypeSp(SemanticNode node) {
    SemanticNode PNode = node.parrent;
    SemanticNode SNode = PNode.children.get(1);
    SemanticNode P1Node = PNode.children.get(3);
    if (PNode.attr.containsKey("return")) {
      P1Node.attr.put("return", PNode.attr.get("return"));
      SNode.attr.put("return", PNode.attr.get("return"));
    }
  }

  // 函数声明语句中的return语句的翻译与类型检查
  // S -> return E ; {gen(return E.addr) if(S.return != E.type) error}
  public static void checkReturnType(SemanticNode node) {
    SemanticNode SNode = node.parrent;
    SemanticNode returnNode = SNode.children.get(0);
    SemanticNode ENode = SNode.children.get(1);
    SNode.attr.put("nextlist", "");
    Vector<String> line = new Vector<String>();

    System.err.println("-------------------" + SNode.attr.get("return"));
    System.err.println("-------------------" + ENode.attr.get("type"));

    if (ENode.attr.containsKey("type") && SNode.attr.containsKey("return")
        && !SNode.attr.get("return").equals(ENode.attr.get("type"))) {
      line.add(returnNode.lineIndex);
      line.add(ENode.attr.get("addr"));
      line.add("函数声明的返回值与实际返回类型不匹配！");
      System.out.println("=========================不匹配");
    }
    line = new Vector<String>();
    line.add(returnNode.lineIndex);
    line.add(String.valueOf(index));
    line.add("return " + ENode.attr.get("addr"));
    line.add("(return, _, _, " + ENode.attr.get("addr") + ")");
    intermediate.add(line);
    index++;
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== 布尔表达式
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // B'继承兄弟节点H的list
  // B -> H {B'.exttruelist = H.truelist; B'.extfalselist = H.falselist} B' ……
  public static void inheritHNode1(SemanticNode node) {
    SemanticNode H = node.parrent.children.get(0);
    SemanticNode Bskim = node.parrent.children.get(2);
    HashSet<String> BskimExtTrueList = new HashSet<String>();
    HashSet<String> BskimExtFalseList = new HashSet<String>();

    BskimExtTrueList.addAll(ctrlGetList(H, "truelist"));
    BskimExtFalseList.addAll(ctrlGetList(H, "falselist"));
    Bskim.attr.put("exttruelist", ctrlSet2String(BskimExtTrueList));
    Bskim.attr.put("extfalselist", ctrlSet2String(BskimExtFalseList));
  }

  // or,and语句中B'继承兄弟节点H的list
  // B -> LOGORR/LOGAND K H {B'.exttruelist = H.truelist; B'.extfalselist = H.falselist} B' ……
  public static void inheritHNode2(SemanticNode node) {
    SemanticNode H = node.parrent.children.get(2);
    SemanticNode Bskim = node.parrent.children.get(4);
    HashSet<String> BskimExtTrueList = new HashSet<String>();
    HashSet<String> BskimExtFalseList = new HashSet<String>();

    BskimExtTrueList.addAll(ctrlGetList(H, "truelist"));
    BskimExtFalseList.addAll(ctrlGetList(H, "falselist"));
    Bskim.attr.put("exttruelist", ctrlSet2String(BskimExtTrueList));
    Bskim.attr.put("extfalselist", ctrlSet2String(BskimExtFalseList));
  }

  // B获得子节点B'的list
  // B -> H {a} B'{B.truelist = B'.truelist; B.falselist = B'.falselist}
  public static void getList(SemanticNode node) {
    SemanticNode B = node.parrent;
    SemanticNode Bskim = node.parrent.children.get(2);
    HashSet<String> BtrueList = new HashSet<String>();
    HashSet<String> BfalseList = new HashSet<String>();

    BtrueList.addAll(ctrlGetList(Bskim, "truelist"));
    BfalseList.addAll(ctrlGetList(Bskim, "falselist"));
    B.attr.put("truelist", ctrlSet2String(BtrueList));
    B.attr.put("falselist", ctrlSet2String(BfalseList));
  }

  // B为H取反
  // B -> not H{B.truelist = H.falselist; B.falselist = H.truelist}
  public static void boolNot(SemanticNode node) {
    SemanticNode B = node.parrent;
    SemanticNode H = node.parrent.children.get(1);
    HashSet<String> BtrueList = new HashSet<String>();
    HashSet<String> BfalseList = new HashSet<String>();

    BtrueList.addAll(ctrlGetList(H, "truelist"));
    BfalseList.addAll(ctrlGetList(H, "falselist"));
    B.attr.put("truelist", ctrlSet2String(BtrueList));
    B.attr.put("falselist", ctrlSet2String(BfalseList));
  }

  // 布尔语句中or的相关操作
  // B1' -> logorr K H {a} B2'{B1'.truelist = merge(B1'.exttruelist, B2'.truelist); B1'.falselist =
  // B2'.falselist; backpatch(B1'.falselist, K.quad);}
  public static void boolOr(SemanticNode node) {
    SemanticNode B1skim = node.parrent;
    SemanticNode K = node.parrent.children.get(1);
    SemanticNode B2skim = node.parrent.children.get(4);
    HashSet<String> B1skimtrueList = new HashSet<String>();
    HashSet<String> B1skimfalseList = new HashSet<String>();

    HashSet<String> B1skimExtfalseList = ctrlGetList(B1skim, "extfalselist");
    B1skimtrueList.addAll(ctrlGetList(B1skim, "exttruelist"));
    B1skimtrueList.addAll(ctrlGetList(B2skim, "truelist"));
    B1skimfalseList.addAll(ctrlGetList(B2skim, "falselist"));
    B1skim.attr.put("truelist", ctrlSet2String(B1skimtrueList));
    B1skim.attr.put("falselist", ctrlSet2String(B1skimfalseList));

    ctrlBackPatch(B1skimExtfalseList, K.attr.get("quad"));
  }

  // 布尔语句中and的相关操作
  // B1' -> logand K H {a} B2'{B1'.truelist = B2'.truelist; B1'.falselist = merge(B1'extfalselist,
  // B2'.falselist); backpatch(B1'.truelist, K.quad);}
  public static void boolAnd(SemanticNode node) {
    SemanticNode B1skim = node.parrent;
    SemanticNode K = node.parrent.children.get(1);
    SemanticNode B2skim = node.parrent.children.get(4);
    HashSet<String> B1skimtrueList = new HashSet<String>();
    HashSet<String> B1skimfalseList = new HashSet<String>();

    HashSet<String> B1skimExttrueList = ctrlGetList(B1skim, "exttruelist");
    B1skimtrueList.addAll(ctrlGetList(B2skim, "truelist"));
    B1skimfalseList.addAll(ctrlGetList(B1skim, "extfalselist"));
    B1skimfalseList.addAll(ctrlGetList(B2skim, "falselist"));
    B1skim.attr.put("truelist", ctrlSet2String(B1skimtrueList));
    B1skim.attr.put("falselist", ctrlSet2String(B1skimfalseList));

    ctrlBackPatch(B1skimExttrueList, K.attr.get("quad"));
  }

  // B'为空时的操作
  // B' -> ε {B'.truelist = B'.exttruelist; B'.falselist = B'.extfalselist}
  public static void boolNull(SemanticNode node) {
    SemanticNode Bskim = node.parrent;
    HashSet<String> BskimtrueList = new HashSet<String>();
    HashSet<String> BskimfalseList = new HashSet<String>();

    BskimtrueList.addAll(ctrlGetList(Bskim, "exttruelist"));
    BskimfalseList.addAll(ctrlGetList(Bskim, "extfalselist"));
    Bskim.attr.put("truelist", ctrlSet2String(BskimtrueList));
    Bskim.attr.put("falselist", ctrlSet2String(BskimfalseList));
  }

  // 布尔表达式加上左右括号时的list传递
  // H -> SLP B SRP{H.truelist = B.truelist; H.falselist = B.falselist}
  public static void addParentheses(SemanticNode node) {
    SemanticNode H = node.parrent;
    SemanticNode B = node.parrent.children.get(1);
    HashSet<String> HtrueList = new HashSet<String>();
    HashSet<String> HfalseList = new HashSet<String>();

    HtrueList.addAll(ctrlGetList(B, "truelist"));
    HfalseList.addAll(ctrlGetList(B, "falselist"));
    H.attr.put("truelist", ctrlSet2String(HtrueList));
    H.attr.put("falselist", ctrlSet2String(HfalseList));
  }

  // relop表达式的list生成，并传递给H
  // H -> I relop E {H.truelist = makelist(nextquad); H.falselist = makelist(nextquad+1); gen('if'
  // I.addr relop E.addr 'goto _'); gen('goto _');}
  public static void makeRelopList(SemanticNode node) {
    SemanticNode H = node.parrent;
    SemanticNode I = node.parrent.children.get(0);
    SemanticNode r = node.parrent.children.get(1);
    SemanticNode E = node.parrent.children.get(2);
    String Iaddr = I.attr.get("addr");
    String Eaddr = E.attr.get("addr");
    String relop = r.children.get(0).word;

    H.attr.put("truelist", String.valueOf(index));
    Vector<String> line1 = new Vector<String>();
    line1.add(" ");
    line1.add(String.valueOf(index));
    line1.add("'if' " + Iaddr + " " + relop + " " + Eaddr + " 'goto' ");
    line1.add("(" + relop + ", " + Iaddr + ", " + Eaddr + ", )");
    intermediate.add(line1);
    index++;

    H.attr.put("falselist", String.valueOf(index));
    Vector<String> line2 = new Vector<String>();
    line2.add(" ");
    line2.add(String.valueOf(index));
    line2.add("'goto' ");
    line2.add("(goto, _, _, )");
    intermediate.add(line2);
    index++;
  }

  // true的表达式的list生成并传递给H
  // H -> true {H.truelist = makelist(nextquad); gen('goto _');}
  public static void makeTrueList(SemanticNode node) {
    SemanticNode H = node.parrent;
    SemanticNode t = node.parrent.children.get(0);

    H.attr.put("truelist", String.valueOf(index));
    H.attr.put("falselist", "");
    Vector<String> line1 = new Vector<String>();
    line1.add(t.lineIndex);
    line1.add(String.valueOf(index));
    line1.add("'goto' ");
    line1.add("(goto, _, _, )");
    intermediate.add(line1);
    index++;
  }

  // true的表达式的list生成并传递给H
  // H -> false {H.falselist = makelist(nextquad); gen('goto _');}
  public static void makeFalseList(SemanticNode node) {
    SemanticNode H = node.parrent;
    SemanticNode f = node.parrent.children.get(0);

    H.attr.put("truelist", "");
    H.attr.put("falselist", String.valueOf(index));
    Vector<String> line1 = new Vector<String>();
    line1.add(f.lineIndex);
    line1.add(String.valueOf(index));
    line1.add("'goto' ");
    line1.add("(goto, _, _, )");
    intermediate.add(line1);
    index++;
  }

  // ========== ========== ========== ========== ========== ========== ========== ==========
  // ========== ========== ========== ========== ========== ========== ========== 函数调用
  // ========== ========== ========== ========== ========== ========== ========== ==========

  // 参数队列
  public static Queue<SemanticNode> parametersQueue = new LinkedList<SemanticNode>();

  // 调用无返回值的函数
  // S -> call IDN SLP elist SRP SEM{对队列中每个参数t有gen('param' t); gen('call' IDN.addr ',' number)}
  public static void callFunction(SemanticNode node) {
    SemanticNode call = node.parrent.children.get(0);
    SemanticNode idn = node.parrent.children.get(1);
    node.parrent.attr.put("nextlist", "");

    int tempNum = 0;
    int parameterNum = 0;
    int queueSize = parametersQueue.size();

    System.out.println("--" + procIdnInfo.get(idn.word).get("type"));
    boolean parameterFlag = true;
    boolean parameterNumFlag = true;
    Vector<String> parameterList = new Vector<String>();
    for (String str : procIdnInfo.get(idn.word).get("param").split(",")) {
      System.out.println("----" + str);
      parameterList.add(0, str);
      parameterNum++;
    }

    node.parrent.attr.put("num", parameterNum + "");

    if (parameterNum != queueSize) {
      Vector<String> line = new Vector<String>();
      line.add(call.lineIndex);
      line.add(idn.word);
      line.add("参数数量不匹配");
      System.out.println("===================参数数量不匹配");
      errorData.add(line);
      parameterNumFlag = false;
    }

    for (int i = 0; i < queueSize; i++) {
      SemanticNode tempNode = parametersQueue.poll();
      String tempStr = tempNode.attr.get("addr");
      String tempType = tempNode.attr.get("type");

      if (parameterNumFlag && !tempType.equals(parameterList.get(i))) {
        System.out.println(tempStr + "---------" + parameterList.get(i));
        parameterFlag = false;
      }
      Vector<String> line1 = new Vector<String>();
      line1.add(call.lineIndex);
      line1.add(String.valueOf(index));
      line1.add("'param' " + tempStr);
      line1.add("(param, _, _, " + tempStr + ")");
      intermediate.add(line1);
      tempNum++;
      index++;
    }

    Vector<String> line2 = new Vector<String>();
    line2.add(call.lineIndex);
    line2.add(String.valueOf(index));
    line2.add("'call' " + idn.word + " ',' " + tempNum);
    line2.add("(call, " + idn.word + ", " + tempNum + ", _)");
    intermediate.add(line2);
    index++;

    // 错误判断
    // idn是否为函数
    if (!procIdnInfo.get(idn.word).get("type").equals("proc")) {
      Vector<String> line3 = new Vector<String>();
      line3.add(call.lineIndex);
      line3.add(idn.word);
      line3.add("调用了一个非函数");
      System.out.println("===================调用了一个非函数");
      errorData.add(line3);
    }

    // 形参与实参是否匹配
    if (!parameterFlag) {
      Vector<String> line = new Vector<String>();
      line.add(call.lineIndex);
      line.add(idn.word);
      line.add("参数类型不匹配");
      System.out.println("===================参数类型不匹配");
      errorData.add(line);
    }
  }

  // 调用有返回值的函数
  // E -> call IDN SLP elist SRP SEM{对队列中每个参数t有gen('param' t); gen('call' IDN.addr ',' number)}
  public static void callFunctionReturn(SemanticNode node) {
    SemanticNode call = node.parrent.children.get(0);
    SemanticNode idn = node.parrent.children.get(1);
    node.parrent.attr.put("nextlist", "");
    node.parrent.attr.put("addr", "proc");
    node.parrent.attr.put("type", procIdnInfo.get(idn.word).get("return"));

    int parameterNum = 0;
    int queueSize = parametersQueue.size();

    System.out.println("--" + procIdnInfo.get(idn.word).get("type"));
    boolean parameterFlag = true;
    boolean parameterNumFlag = true;
    Vector<String> parameterList = new Vector<String>();
    for (String str : procIdnInfo.get(idn.word).get("param").split(",")) {
      System.out.println("----" + str);
      parameterList.add(0, str);
      parameterNum++;
    }

    node.parrent.attr.put("num", parameterNum + "");

    if (parameterNum != queueSize) {
      Vector<String> line = new Vector<String>();
      line.add(call.lineIndex);
      line.add(idn.word);
      line.add("参数数量不匹配");
      System.out.println("===================参数数量不匹配");
      errorData.add(line);
      parameterNumFlag = false;
    }

    for (int i = 0; i < queueSize; i++) {
      SemanticNode tempNode = parametersQueue.poll();
      String tempStr = tempNode.attr.get("addr");
      String tempType = tempNode.attr.get("type");

      if (parameterNumFlag && !tempType.equals(parameterList.get(i))) {
        System.out.println(tempStr + "---------" + parameterList.get(i));
        parameterFlag = false;
      }
      Vector<String> line1 = new Vector<String>();
      line1.add(call.lineIndex);
      line1.add(String.valueOf(index));
      line1.add("'param' " + tempStr);
      line1.add("(param, _, _, " + tempStr + ")");
      intermediate.add(line1);
      index++;
    }

    // 错误判断
    // idn是否为函数
    if (!procIdnInfo.get(idn.word).get("type").equals("proc")) {
      Vector<String> line3 = new Vector<String>();
      line3.add(call.lineIndex);
      line3.add(idn.word);
      line3.add("调用了一个非函数");
      System.out.println("===================调用了一个非函数");
      errorData.add(line3);
    }

    // 形参与实参是否匹配
    if (!parameterFlag) {
      Vector<String> line = new Vector<String>();
      line.add(call.lineIndex);
      line.add(idn.word);
      line.add("参数类型不匹配");
      System.out.println("===================参数类型不匹配");
      errorData.add(line);
    }
  }

  // 参数队列初始化为只有一个E
  // Elist -> E Elist'{initialize_queue}
  public static void initializeQueue(SemanticNode node) {
    SemanticNode E = node.parrent.children.get(0);
    System.out.println("============" + E.attr.get("addr"));
    if (E.attr.containsKey("addr")) {
      parametersQueue.offer(E);
    }
  }

  // 参数队列追加参数
  // Elist' -> CMA E Elist'{E.addr 添加到队列队尾}
  public static void addParameter(SemanticNode node) {
    SemanticNode E = node.parrent.children.get(1);
    System.out.println("============" + E.attr.get("addr"));
    if (E.attr.containsKey("addr")) {
      parametersQueue.offer(E);
    }
  }
}
