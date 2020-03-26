package Lexical;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Keyword {

  // 关键字
  public static Set<String> keywords =
      new HashSet<>(Arrays.asList("int", "float", "bool", "char", "record", "if", "else", "while",
          "do", "break", "continue", "true", "false", "proc", "call", "return"));

  // 单词到种别码映射
  public static Map<String, String> species = new HashMap<>();

  public static Set<String> a2d =
      new HashSet<>(Arrays.asList("a", "b", "c", "d", "A", "B", "C", "D"));
  public static Set<String> e = new HashSet<>(Arrays.asList("e", "E"));
  public static Set<String> f = new HashSet<>(Arrays.asList("f", "F"));
  public static Set<String> g2w = new HashSet<>(Arrays.asList("g", "h", "i", "j", "k", "l", "m",
      "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "G", "H", "I", "J", "J", "L", "M", "N", "O",
      "P", "Q", "R", "S", "T", "U", "V", "W"));
  public static Set<String> x = new HashSet<>(Arrays.asList("x", "X"));
  public static Set<String> y2z = new HashSet<>(Arrays.asList("y", "Y", "z", "Z"));

  public static Set<String> d1_7 = new HashSet<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7"));
  public static Set<String> d8_9 = new HashSet<>(Arrays.asList("8", "9"));

  public static Set<String> plusminus = new HashSet<>(Arrays.asList("+", "-"));
  public static Set<String> oper =
      new HashSet<>(Arrays.asList("%", "<", ">", "=", "!", "&", "|", "^"));
  public static Set<String> deli =
      new HashSet<>(Arrays.asList(",", ";", ":", "?", "(", ")", "[", "]", "{", "}"));

  public Keyword() {
    // 算术运算符
    species.put("+", "PLS");
    species.put("-", "MNS");
    species.put("*", "MLT");
    species.put("/", "DVS");
    species.put("%", "MLD");
    species.put("&", "AND");
    species.put("|", "ORR");
    species.put("+=", "PLSEQL");
    species.put("-=", "MNSEQL");
    species.put("*=", "MLTEQL");
    species.put("/=", "DVSEQL");
    species.put("++", "PLSPLS");
    species.put("--", "MNSMNS");

    // 关系运算符
    species.put("<", "LES");
    species.put(">", "MOR");
    species.put("<=", "LESEQL");
    species.put(">=", "MOREQL");
    species.put("!=", "NOTEQL");
    species.put("==", "EQL");
    species.put("=", "ASN");

    // 逻辑运算符
    species.put("&&", "LOGAND");
    species.put("||", "LOGORR");
    species.put("!", "NOT");
    species.put("^", "XOR");

    // 界符
    species.put(",", "CMA");
    species.put(";", "SEM");
    species.put(":", "CLN");
    species.put("?", "QUS");
    species.put("(", "SLP");
    species.put(")", "SRP");
    species.put("[", "MLP");
    species.put("]", "MRP");
    species.put("{", "LLP");
    species.put("}", "LRP");
  }

  // 单个读入字符到表头映射
  public String getInput(String chara) {
    if (a2d.contains(chara)) {
      return "a-d";
    } else if (e.contains(chara)) {
      return "e";
    } else if (f.contains(chara)) {
      return "f";
    } else if (g2w.contains(chara)) {
      return "g-w";
    } else if (x.contains(chara)) {
      return "x";
    } else if (y2z.contains(chara)) {
      return "y-z";
    } else if (d1_7.contains(chara)) {
      return "1-7";
    } else if (d8_9.contains(chara)) {
      return "8-9";
    } else if (plusminus.contains(chara)) {
      return "+-";
    } else if (oper.contains(chara)) {
      return "oper";
    } else if (deli.contains(chara)) {
      return "deli";
    } else {
      return chara;
    }
  }

}
