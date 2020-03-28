package GUI;

import java.io.File;
import java.util.Vector;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import Lexical.Nfa;

public class ResourceManager {
  // 将NFA表格读入此处
  private Vector<Vector<String>> NFAdata = new Vector<Vector<String>>();

  // DFA表格读入此处或者将NFA转换到此处
  private Vector<Vector<String>> DFAdata = new Vector<Vector<String>>();
  private Vector<String> DFAdataTitle = new Vector<String>();

  // Token序列写入此处
  private Vector<Vector<String>> Tokendata = new Vector<Vector<String>>();
  private Vector<String> TokendataTitle = new Vector<String>();

  // 错误信息存放此处
  private Vector<Vector<String>> Errordata = new Vector<Vector<String>>();
  private Vector<String> ErrordataTitle = new Vector<String>();

  public ResourceManager() {
    ErrordataTitle.add("行数");
    ErrordataTitle.add("错误项");
    ErrordataTitle.add("错误信息");
  }

  public void NFAexcel_reader(File excel) {
    int columnCount;
    int rowCount;
    Sheet sheet;
    Workbook book;
    Cell cell;
    try {
      book = Workbook.getWorkbook(excel);

      // 获得第一个工作表对象(ecxel中sheet的编号从0开始,0,1,2,3,....)
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
    
    // 将NFA转换为DFA
    NFAtoDFA();
  }

  public void DFAexcel_reader(File excel) {
    int columnCount;
    int rowCount;
    Sheet sheet;
    Workbook book;
    Cell cell;
    try {
      book = Workbook.getWorkbook(excel);

      // 获得第一个工作表对象(ecxel中sheet的编号从0开始,0,1,2,3,....)
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

  public Vector<Vector<String>> getNFAdata() {
	    return this.NFAdata;
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

  // NFA转换到DFA,航航加油 =v=
  private void NFAtoDFA() {

  }

  // 根据已有的DFA转换表和输入文件得出Token序列结果并存放
  public void analysis(File text) {

  }

}
