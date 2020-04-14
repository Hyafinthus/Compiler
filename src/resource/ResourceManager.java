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
  // 将NFA表格读入此处
  private Vector<Vector<String>> NFAdata = new Vector<Vector<String>>();

  // DFA表格读入此处或者将NFA转换到此处
  private Vector<Vector<String>> DFAdata = new Vector<Vector<String>>();
  private Vector<String> DFAdataTitle = new Vector<String>();

  // Token序列写入此处
  private Vector<Vector<String>> Tokendata = new Vector<Vector<String>>();
  private Vector<String> TokendataTitle = new Vector<String>(Arrays.asList("行号", "单词", "Token"));

  // 错误信息存放此处
  private Vector<Vector<String>> Errordata = new Vector<Vector<String>>();
  private Vector<String> ErrordataTitle = new Vector<String>(Arrays.asList("行号", "错误项", "错误原因"));

  public void NFAexcel_reader(File excel) {
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

    this.NFAtoDFA();
  }

  public void DFAexcel_reader(File excel) {
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

  public Vector<Vector<String>> getDFAdata() {
    return this.DFAdata;
  }

  public Vector<String> getDFAdataTitle() {
    return this.DFAdataTitle;
  }

  public Vector<Vector<String>> getTokendata() {
    return this.Tokendata;
  }

  public Vector<String> getTokendataTitle() {
    return this.TokendataTitle;
  }

  public Vector<Vector<String>> getErrordata() {
    return this.Errordata;
  }

  public Vector<String> getErrordataTitle() {
    return this.ErrordataTitle;
  }

  // NFA转换为DFA
  private void NFAtoDFA() {
    Nfa nfa = new Nfa(this.NFAdata);
    Dfa dfa = nfa.toDfa();
    this.DFAdata = dfa.getDfaData();
    this.DFAdataTitle = dfa.getDfaTitle();
  }

  // 根据已有的DFA转换表和输入文件得出Token序列结果并存放
  public void analysis(String text) {
    // 创建Dfa对象 传入DFA信息
    Dfa dfa = new Dfa(this.getDFAdataTitle(), this.getDFAdata());
    String[] lines = text.split("\n");

    // 创建Lexical中对象 传入text
    try {
      Dfa2Token dfa2Token = new Dfa2Token(dfa, lines);
      dfa2Token.analysis();
      this.Tokendata = dfa2Token.getTokenData();
      this.Errordata = dfa2Token.getErrorData();
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
  }
}
