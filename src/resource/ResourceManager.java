package resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Vector;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lexical.Dfa;
import lexical.Dfa2Token;
import lexical.Nfa;
import semantic.Action;
import semantic.SemanticConverter;
import semantic.SemanticNode;
import semantic.SemanticParser2Tree;
import syntax.Node;
import syntax.Parser2Tree;
import syntax.SyntaxConverter;

public class ResourceManager {
  // 储存读入的txt文件
  public static File text;

  // NFA表格读入此处
  public static Vector<Vector<String>> NFAdata = new Vector<Vector<String>>();

  // DFA表格读入此处或者将NFA转换到此处
  public static Vector<Vector<String>> DFAdata = new Vector<Vector<String>>();
  public static Vector<String> DFAdataTitle = new Vector<String>();

  // Token序列写入此处
  public static Vector<Vector<String>> Tokendata = new Vector<Vector<String>>();
  public static Vector<String> TokendataTitle =
      new Vector<String>(Arrays.asList("行号", "单词", "Token"));

  // 错误信息存放此处
  public static Vector<Vector<String>> Errordata = new Vector<Vector<String>>();
  public static Vector<String> ErrordataTitle =
      new Vector<String>(Arrays.asList("行号", "错误项", "错误原因"));

  // first,follow集存放此处
  public static Vector<Vector<String>> FirstFollowdata = new Vector<Vector<String>>();
  public static Vector<String> FirstFollowdataTitle =
      new Vector<String>(Arrays.asList("非终结符", "First集", "Follow集"));

  // 产生式,select集存放此处
  public static Vector<Vector<String>> Selectdata = new Vector<Vector<String>>();
  public static Vector<String> SelectdataTitle =
      new Vector<String>(Arrays.asList("产生式", "Select集"));

  // LL分析表信息存放此处
  public static Vector<Vector<String>> LLanalysisdata = new Vector<Vector<String>>();
  public static Vector<String> LLanalysisdataTitle = new Vector<String>();

  // 语法分析错误存放此处
  public static Vector<Vector<String>> SyntaxErrordata = new Vector<Vector<String>>();
  public static Vector<String> SyntaxErrordataTitle =
      new Vector<String>(Arrays.asList("行号", "错误项", "错误信息"));

  // 语义分析符号表表头
  public static Vector<String> SemanticSymboldataTitle =
      new Vector<String>(Arrays.asList("行号", "符号", "类型", "偏移量"));

  // 语义分析中间产生式表头
  public static Vector<String> intermediatedataTitle =
      new Vector<String>(Arrays.asList("行号", "序号", "三地址指令", "四元式"));

  // 语义分析错误表头
  public static Vector<String> SemanticErrordataTitle =
      new Vector<String>(Arrays.asList("行号", "错误项", "错误信息"));

  // 存储语法分析树结构根节点
  public static Node syntaxRoot;
  public static SemanticNode semanticRoot;

  private static SyntaxConverter syntaxConverter;
  private static SemanticConverter semanticConverter;

  public static void NFAexcel_reader(File excel) {
    int columnCount;
    int rowCount;
    Sheet sheet;
    Workbook book;
    Cell cell;
    NFAdata = new Vector<Vector<String>>();
    DFAdata = new Vector<Vector<String>>();
    DFAdataTitle = new Vector<String>();
    try {
      book = Workbook.getWorkbook(excel);

      // 获得第一个工作表对象(excel中sheet的编号从0开始,0,1,2,3,....)
      sheet = book.getSheet(0);

      // 获取行数与列数
      columnCount = sheet.getColumns();
      rowCount = sheet.getRows();

      for (int j = 0; j < rowCount; j++) {
        Vector<String> tempRow = new Vector<String>();
        // 循环读取
        for (int k = 0; k < columnCount; k++) {
          cell = sheet.getCell(k, j);
          tempRow.add(cell.getContents());
        }
        NFAdata.add(tempRow);
      }
      book.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    NFA2DFA();
  }

  public static void DFAexcel_reader(File excel) {
    int columnCount;
    int rowCount;
    Sheet sheet;
    Workbook book;
    Cell cell;
    DFAdata = new Vector<Vector<String>>();
    DFAdataTitle = new Vector<String>();
    try {
      book = Workbook.getWorkbook(excel);

      // 获得第一个工作表对象(excel中sheet的编号从0开始,0,1,2,3,....)
      sheet = book.getSheet(0);

      // 获取行数与列数
      columnCount = sheet.getColumns();
      rowCount = sheet.getRows();

      // 得到DFAdataTitle
      for (int i = 0; i < columnCount; i++) {
        cell = sheet.getCell(i, 0);
        DFAdataTitle.add(cell.getContents());
      }

      for (int j = 1; j < rowCount; j++) {
        Vector<String> tempRow = new Vector<String>();
        // 循环读取
        for (int k = 0; k < columnCount; k++) {
          cell = sheet.getCell(k, j);
          tempRow.add(cell.getContents());
        }
        DFAdata.add(tempRow);
      }
      book.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void syntax_LLexcel_reader(File excel) {
    int columnCount;
    int rowCount;
    Sheet sheet;
    Workbook book;
    Cell cell;
    LLanalysisdata = new Vector<Vector<String>>();
    LLanalysisdataTitle = new Vector<String>();
    try {
      book = Workbook.getWorkbook(excel);

      // 获得第一个工作表对象(excel中sheet的编号从0开始,0,1,2,3,....)
      sheet = book.getSheet(0);

      // 获取行数与列数
      columnCount = sheet.getColumns();
      rowCount = sheet.getRows();

      // 得到DFAdataTitle
      for (int i = 0; i < columnCount; i++) {
        cell = sheet.getCell(i, 0);
        LLanalysisdataTitle.add(cell.getContents());
      }

      for (int j = 1; j < rowCount; j++) {
        Vector<String> tempRow = new Vector<String>();
        // 循环读取
        for (int k = 0; k < columnCount; k++) {
          cell = sheet.getCell(k, j);
          tempRow.add(cell.getContents());
        }
        LLanalysisdata.add(tempRow);
      }
      book.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    syntaxConvert();
  }

  public static void semantic_LLexcel_reader(File excel) {
    int columnCount;
    int rowCount;
    Sheet sheet;
    Workbook book;
    Cell cell;
    LLanalysisdata = new Vector<Vector<String>>();
    LLanalysisdataTitle = new Vector<String>();
    try {
      book = Workbook.getWorkbook(excel);

      // 获得第一个工作表对象(excel中sheet的编号从0开始,0,1,2,3,....)
      sheet = book.getSheet(0);

      // 获取行数与列数
      columnCount = sheet.getColumns();
      rowCount = sheet.getRows();

      // 得到DFAdataTitle
      for (int i = 0; i < columnCount; i++) {
        cell = sheet.getCell(i, 0);
        LLanalysisdataTitle.add(cell.getContents());
      }

      for (int j = 1; j < rowCount; j++) {
        Vector<String> tempRow = new Vector<String>();
        // 循环读取
        for (int k = 0; k < columnCount; k++) {
          cell = sheet.getCell(k, j);
          tempRow.add(cell.getContents());
        }
        LLanalysisdata.add(tempRow);
      }
      book.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    semanticConvert();
  }

  private static Vector<Vector<String>> autoLexical(String text) {
    String dfaPath = System.getProperty("user.dir") + "\\res\\DFA.xls";
    File dfaXls = new File(dfaPath);
    DFAexcel_reader(dfaXls);
    Vector<Vector<String>> tokens = lexicalAnalysis(text, true);
    Vector<String> last =
        new Vector<>(Arrays.asList(tokens.get(tokens.size() - 1).get(0), "$", "$"));
    tokens.add(last);
    return tokens;
  }

  // 根据已有的Parser预测分析表和代码文件得出语法树并存放
  public static void syntaxAnalysis(String text) {
    Vector<Vector<String>> tokenData = autoLexical(text);
    Parser2Tree p2t = new Parser2Tree(syntaxConverter, tokenData);
    p2t.analysis();
    syntaxRoot = p2t.getRoot();
    SyntaxErrordata = p2t.getErrorData();
  }

  // Syntax转为Parser
  public static void syntaxConvert() {
    syntaxConverter = new SyntaxConverter(LLanalysisdataTitle, LLanalysisdata);
    FirstFollowdata = syntaxConverter.getFirstFollowData();
    Selectdata = syntaxConverter.getSelectData();
    LLanalysisdataTitle = syntaxConverter.getLLanalysisTitle();
    LLanalysisdata = syntaxConverter.getLLanalysisData();

    exportExcel(FirstFollowdataTitle, FirstFollowdata,
        System.getProperty("user.dir") + "\\out\\syntaxFirstFollow.xls");
    exportExcel(SelectdataTitle, Selectdata,
        System.getProperty("user.dir") + "\\out\\syntaxSelect.xls");
    exportExcel(LLanalysisdataTitle, LLanalysisdata,
        System.getProperty("user.dir") + "\\out\\syntaxParser.xls");
  }

  // 根据已有的Parser预测分析表和代码文件得出语法树并存放
  public static void semanticAnalysis(String text) {
    Vector<Vector<String>> tokenData = autoLexical(text);
    SemanticParser2Tree p2t = new SemanticParser2Tree(semanticConverter, tokenData);

    Action.offset = 0;
    Action.index = 0;
    Action.symbol = new Vector<Vector<String>>();
    Action.intermediate = new Vector<Vector<String>>();
    Action.errorData = new Vector<Vector<String>>();
    Action.declVar.clear();
    Action.idn2Index.clear();
    Action.procIdnInfo.clear();

    p2t.analysis();
    semanticRoot = p2t.getRoot();
    SyntaxErrordata = p2t.getErrorData();
    Action.errorData.addAll(SyntaxErrordata);
  }

  // Syntax转为Parser
  public static void semanticConvert() {
    semanticConverter = new SemanticConverter(LLanalysisdataTitle, LLanalysisdata);
    FirstFollowdata = semanticConverter.getFirstFollowData();
    Selectdata = semanticConverter.getSelectData();
    LLanalysisdataTitle = semanticConverter.getLLanalysisTitle();
    LLanalysisdata = semanticConverter.getLLanalysisData();

    exportExcel(FirstFollowdataTitle, FirstFollowdata,
        System.getProperty("user.dir") + "\\out\\semanticFirstFollow.xls");
    exportExcel(SelectdataTitle, Selectdata,
        System.getProperty("user.dir") + "\\out\\semanticSelect.xls");
    exportExcel(LLanalysisdataTitle, LLanalysisdata,
        System.getProperty("user.dir") + "\\out\\semanticParser.xls");
  }

  // NFA转换为DFA
  private static void NFA2DFA() {
    Nfa nfa = new Nfa(NFAdata);
    Dfa dfa = nfa.toDfa();
    DFAdata = dfa.getDfaData();
    DFAdataTitle = dfa.getDfaTitle();
  }

  // 根据已有的DFA转换表和输入文件得出Token序列结果并存放
  public static Vector<Vector<String>> lexicalAnalysis(String text, boolean forSyntax) {
    // 创建Dfa对象 传入DFA信息
    Dfa dfa = new Dfa(DFAdataTitle, DFAdata);
    String[] lines = text.split("\n");

    // 创建Lexical中对象 传入text
    try {
      Dfa2Token dfa2Token = new Dfa2Token(dfa, lines);
      dfa2Token.analysis();

      Errordata = dfa2Token.getErrorData();
      Tokendata = dfa2Token.getTokenData();

      if (forSyntax) {
        return dfa2Token.getTokenForSyntaxData();
      } else {
        return dfa2Token.getTokenData();
      }
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
      return null;
    }
  }

  // 导出分析的First Follow Select为Excel
  private static void exportExcel(Vector<String> dataTitle, Vector<Vector<String>> data,
      String targetfile) {
    String worksheet = "List"; // 输出的excel文件工作表名

    WritableWorkbook workbook;
    try {
      OutputStream os = new FileOutputStream(targetfile);
      workbook = Workbook.createWorkbook(os);

      WritableSheet sheet = workbook.createSheet(worksheet, 0); // 添加第一个工作表

      jxl.write.Label label;
      for (int i = 0; i < dataTitle.size(); i++) {
        // Label(列号,行号 ,内容 )
        label = new jxl.write.Label(i, 0, dataTitle.get(i)); // put the title in row1
        sheet.addCell(label);
      }
      for (int i = 0; i < data.size(); i++) {
        Vector<String> tempData = data.get(i);
        for (int j = 0; j < tempData.size(); j++) {
          label = new jxl.write.Label(j, i + 1, tempData.get(j)); // put the title in row1
          sheet.addCell(label);
        }
      }

      workbook.write();
      workbook.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
