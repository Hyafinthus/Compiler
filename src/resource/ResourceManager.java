package resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Vector;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import lexical.Dfa;
import lexical.Dfa2Token;
import lexical.Nfa;

public class ResourceManager {
  //储存读入的txt文件
  static public File text;
	
  // 将NFA表格读入此处
  static public Vector<Vector<String>> NFAdata = new Vector<Vector<String>>();

  // DFA表格读入此处或者将NFA转换到此处
  static public Vector<Vector<String>> DFAdata = new Vector<Vector<String>>();
  static public Vector<String> DFAdataTitle = new Vector<String>();

  // Token序列写入此处
  static public Vector<Vector<String>> Tokendata = new Vector<Vector<String>>();
  static public Vector<String> TokendataTitle = new Vector<String>(Arrays.asList("行号", "单词", "Token"));

  // 错误信息存放此处
  static public Vector<Vector<String>> Errordata = new Vector<Vector<String>>();
  static public Vector<String> ErrordataTitle = new Vector<String>(Arrays.asList("行号", "错误项", "错误原因"));
  
  //first,follow集存放此处
  static public Vector<Vector<String>> FirstFollowdata = new Vector<Vector<String>>();
  static public Vector<String> FirstFollowdataTitle = new Vector<String>(Arrays.asList("非终结符", "First集", "Follow集"));
 
  //产生式,select集存放此处
  static public Vector<Vector<String>> Selectdata = new Vector<Vector<String>>();
  static public Vector<String> SelectdataTitle = new Vector<String>(Arrays.asList("产生式", "Select集"));

  //LL分析表信息存放此处
  static public Vector<Vector<String>> LLanalysisdata = new Vector<Vector<String>>();
  static public Vector<String> LLanalysisdataTitle = new Vector<String>();

  static public void NFAexcel_reader(File excel) {
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

    NFAtoDFA();
  }

  static public void DFAexcel_reader(File excel) {
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
  
  public static void LLexcel_reader(File excel) {
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
		
  }

  // NFA转换为DFA
  static private void NFAtoDFA() {
    Nfa nfa = new Nfa(NFAdata);
    Dfa dfa = nfa.toDfa();
    DFAdata = dfa.getDfaData();
    DFAdataTitle = dfa.getDfaTitle();
  }

  // 根据已有的DFA转换表和输入文件得出Token序列结果并存放
  static public void analysis(String text) {
    // 创建Dfa对象 传入DFA信息
    Dfa dfa = new Dfa(DFAdataTitle, DFAdata);
    String[] lines = text.split("\n");

    // 创建Lexical中对象 传入text
    try {
      Dfa2Token dfa2Token = new Dfa2Token(dfa, lines);
      dfa2Token.analysis();
      Tokendata = dfa2Token.getTokenData();
      Errordata = dfa2Token.getErrorData();
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
  }


}
