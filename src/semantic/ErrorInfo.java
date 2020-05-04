package semantic;

import com.google.common.collect.BiMap;
import java.util.HashMap;
import java.util.Map;
import lexical.Keyword;

public class ErrorInfo {
  public static Map<String, String> message = new HashMap<>();
  static {
    message.put("Program", "语法错误");
    message.put("P", "语法错误");
    message.put("D", "声明语句有误");
    message.put("A", "声明语句有误");
    message.put("M", "数组声明出错");
    message.put("M'", "缺少逗号");
    message.put("T", "数组声明出错");
    message.put("X", "请输入正确的数据类型\"int\"\"float\"或者\"char\"");
    message.put("C", "缺少\"(\"或\"[\"或\"{\"");
    message.put("S", "语法错误");
    message.put("V", "应使用\"++\"或\"--\"");
    message.put("N", "缺少\"case\"");
    message.put("E", "运算式有误");
    message.put("E'", "缺少运算符号");
    message.put("G", "表达式赋值错误");
    message.put("G'", "表达式赋值错误");
    message.put("L", "缺少标识符");
    message.put("L'", "缺少\"(\"或\"[\"或\"{\"");
    message.put("B", "请使用bool值");
    message.put("B'", "请使用bool值");
    message.put("H", "请使用bool值");
    message.put("I", "请使用bool值");
    message.put("equal", "缺少赋值符");
    message.put("relop", "缺少比较符");
    message.put("Elist", "函数调用有误");
    message.put("Elist'", "缺少逗号");
  }

  public static BiMap<String, String> operations = Keyword.species.inverse();
  static {
    operations.put("IDN", "标识符");
    operations.put("CST", "常数");
    operations.put("FLT", "浮点数");
    operations.put("OCT", "八进制常数");
    operations.put("HEX", "十六进制常数");
    operations.put("CHR", "字符常数");
  }
}
