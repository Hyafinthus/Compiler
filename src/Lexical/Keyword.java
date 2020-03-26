package Lexical;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Keyword {

  public static Set<String> keywords = new HashSet<>(); // 关键字
  public static Map<String, String> species = new HashMap<>(); // 单词到种别码映射
  public static Map<String, String> input = new HashMap<>(); // 单个字符到表头映射

  public Keyword() {
    keywords.addAll(Arrays.asList("int", "float", "bool", "char", "record", "if", "else", "while",
        "do", "break", "continue", "true", "false", "proc", "call", "return"));
    species.put("", "");
    input.put("", "");
  }

}
