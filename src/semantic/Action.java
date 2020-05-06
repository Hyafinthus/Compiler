package semantic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
      function.put("back_m", Action.class.getMethod("backM", SemanticNode.class));
      function.put("back_n", Action.class.getMethod("backN", SemanticNode.class));
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
  
  // 回填辅助非终结符K（M）的空转移动作
  // K -> ε {K.quad = nextquad;}
  public static void backM(SemanticNode node) {
    SemanticNode KNode = node.parrent;
    int nextQuad = intermediate.size()-1;
    KNode.attr.put("quad",String.valueOf(nextQuad));
  }
  
 // 回填辅助非终结符O（N）的空转移动作
 // O -> ε { O.nextlist = makelist(nextquad);gen(‘goto _’);}

 public static void backN(SemanticNode node) {
   SemanticNode ONode = node.parrent;
   ONode.attr.put("nextlist",String.valueOf(index));
   Vector<String> line = new Vector<String>();
   line.add(" ");
   line.add(String.valueOf(index));
   line.add("goto ");
   line.add("(j, _, _, )");
   intermediate.add(line);   
   index++;
 }
 
 public static void ctrlTestS(SemanticNode node) {
   SemanticNode parent = node.parrent;
   SemanticNode idnNode = parent.children.get(0);
   SemanticNode cstNode = parent.children.get(2);
   gen(idnNode.lineIndex,idnNode.word,cstNode.word);
   parent.attr.put("nextlist","");
 }
 
 public static void ctrlTestBt(SemanticNode node) {
   SemanticNode BNode = node.parrent;
   BNode.attr.put("truelist", String.valueOf(index));
   Vector<String> line = new Vector<String>();
   line.add(BNode.children.get(0).lineIndex);
   line.add(String.valueOf(index));
   line.add("goto ");
   line.add("(j, _, _, )");
   intermediate.add(line);
   index ++;
 }
 
 public static void ctrlTestBf(SemanticNode node) {
   SemanticNode BNode = node.parrent;
   BNode.attr.put("falselist", String.valueOf(index));
   Vector<String> line = new Vector<String>();
   line.add(BNode.children.get(0).lineIndex);
   line.add(String.valueOf(index));
   line.add("goto ");
   line.add("(j, _, _, )");
   intermediate.add(line);
   index ++;
 }
 
 public static HashSet<String> ctrlGetList(SemanticNode node,String attr){
   HashSet<String> result = new HashSet<String>();
   for (String str:node.attr.get(attr).split(",")) {
     result.add(str);
   }
   return result;
 }
 
 public static void ctrlBackPatch(Set<String> list,String quad){
   for (String str:list) {
     String tac = intermediate.get(Integer.parseInt(str)).get(2);
     intermediate.get(Integer.parseInt(str)).set(2,tac+quad);
     String fte = intermediate.get(Integer.parseInt(str)).get(3);
     fte = fte.substring(0,fte.length()-1);
     intermediate.get(Integer.parseInt(str)).set(3,fte+quad+")");
   }
 }
 
 public static String ctrlSet2String(Set<String> list){
   String result = "";
   for (String str:list) {
     result += str + ",";
   }
   result = result.substring(0, result.length()-1);
   return result;
 }
 
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
   SNode.attr.put("nextlist",ctrlSet2String(SnextList));
   
   BtrueList.addAll(ctrlGetList(BNode, "truelist"));
   BfalseList.addAll(ctrlGetList(BNode, "falselist"));
   ctrlBackPatch(BtrueList,K1Node.attr.get("quad"));
   ctrlBackPatch(BfalseList,K2Node.attr.get("quad"));
 }
 
 
  
}
