package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import resource.FileFilter;
import resource.ResourceManager;

public class LexicalGui extends JFrame implements ActionListener {
  private static final long serialVersionUID = -4956743638930220924L;

  JButton jb2, jb3, jb4; // 按钮
  JPanel jp1, jp2, jp3; // 画布
  JLabel jl2; // 文本
  JTextField jtf2; // 文本框（显示）
  JTextArea jta1, jta2, jta3, jta4; // 定义文本域
  JScrollPane jsp1, jsp2, jsp3, jsp4;// 定义文本域对应的滚动条
  File FA, text; // 文件
  JTable jt1, jt2, jt3; // 定义表格
  JTextPane txtjtp;
  ResourceManager resourceManager = new ResourceManager();

  public LexicalGui(JTextPane jtp0) {

	this.txtjtp = jtp0;  
	  
    // Frame初始化设置
    this.setLayout(null);
    this.setTitle("词法分析");
    this.setSize(1068, 768);
    this.setLocation(100, 100);
    
    this.setResizable(false);

    // this.add(card);
    this.setVisible(true);

    // button设置
    this.jb2 = new JButton("...");
    this.jb2.setBounds(900, 70, 60, 30);
    this.jb2.addActionListener(this);
    this.jb3 = new JButton("NFA分析表转换");
    this.jb3.addActionListener(this);
    this.jb3.setBounds(360, 140, 150, 30);
    this.jb4 = new JButton("DFA分析表转换");
    this.jb4.addActionListener(this);
    this.jb4.setBounds(560, 140, 150, 30);

    // 文本设置
    this.jl2 = new JLabel("FA转换表路径");
    this.jl2.setBounds(100, 70, 100, 40);

    // 文本框设置
    this.jtf2 = new JTextField();
    this.jtf2.setEditable(false);
    this.jtf2.setBounds(200, 70, 700, 30);
    // this.jtf2.setPreferredSize(new Dimension(1000, 30));

    // 画布初始化
    this.jp1 = new JPanel();
    this.jp1.setLayout(null);

    this.jp1.setSize(1068, 768);
    this.jp1.setLocation(0, 0);

    paneSet(jp1);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // 按键一
    if (e.getSource().equals(jb2)) {
      ArrayList<String> list = new ArrayList<String>();
      list.add(".xls");
      FileFilter xls_filter = new FileFilter(list);
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(0);// 设定只能选择到文件
      chooser.setFileFilter(xls_filter);
      int state = chooser.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
      if (state == 1) {
        return;// 撤销则返回
      } else {
        this.FA = chooser.getSelectedFile();// f为选择到的文件
        this.jtf2.setText(FA.getAbsolutePath());
      }
    } else if (e.getSource().equals(jb3)) {
      // 读取excel文件到资源管理器中
      ResourceManager.NFAexcel_reader(FA);
      ResourceManager.analysis(this.txtjtp.getText());

      // DFA转换表显示界面设置
      this.jt1 = new JTable(ResourceManager.DFAdata, ResourceManager.DFAdataTitle);
      setTableFormat(jt1);
      this.jta1 = new JTextArea();
      this.jsp1 = new JScrollPane(jta1);
      this.jsp1.setBounds(20, 200, 400, 500);
      this.jsp1.setViewportView(jt1);

      // Token显示界面设置
      this.jt2 = new JTable(ResourceManager.Tokendata, ResourceManager.TokendataTitle);
      setTableFormat(jt2);
      this.jta2 = new JTextArea();
      this.jsp2 = new JScrollPane(jta2);
      this.jsp2.setBounds(430, 200, 280, 500);
      this.jsp2.setViewportView(jt2);

      // 错误信息界面设置
      this.jt3 = new JTable(ResourceManager.Errordata, ResourceManager.ErrordataTitle);
      setTableFormat(jt3);
      this.jta3 = new JTextArea();
      this.jsp3 = new JScrollPane(jta3);
      this.jsp3.setBounds(720, 200, 320, 500);
      this.jsp3.setViewportView(jt3);

      // 显示画布2
      this.jp2 = new JPanel();
      this.jp2.setSize(1068, 768);
      this.jp2.setLocation(0, 0);
      this.jp2.setLayout(null);
      paneSet(jp2);

    } else if (e.getSource().equals(jb4)) {
      // 读取excel文件到资源管理器中
      ResourceManager.DFAexcel_reader(FA);
      ResourceManager.analysis(this.txtjtp.getText());

      // DFA转换表显示界面设置
      this.jt1 = new JTable(ResourceManager.DFAdata, ResourceManager.DFAdataTitle);
      setTableFormat(jt1);
      this.jta1 = new JTextArea();
      this.jsp1 = new JScrollPane(jta1);
      this.jsp1.setBounds(20, 200, 400, 500);
      this.jsp1.setViewportView(jt1);

      // Token显示界面设置
      this.jt2 = new JTable(ResourceManager.Tokendata, ResourceManager.TokendataTitle);
      setTableFormat(jt2);
      this.jta2 = new JTextArea();
      this.jsp2 = new JScrollPane(jta2);
      this.jsp2.setBounds(430, 200, 280, 500);
      this.jsp2.setViewportView(jt2);

      // 错误信息界面设置
      this.jt3 = new JTable(ResourceManager.Errordata, ResourceManager.ErrordataTitle);
      setTableFormat(jt3);
      this.jta3 = new JTextArea();
      this.jsp3 = new JScrollPane(jta3);
      this.jsp3.setBounds(720, 200, 320, 500);
      this.jsp3.setViewportView(jt3);

      // 显示画布2
      this.jp2 = new JPanel();
      this.jp2.setSize(1068, 768);
      this.jp2.setLocation(0, 0);
      this.jp2.setLayout(null);
      paneSet(jp2);
    }
  }

  private void setTableFormat(JTable table) {
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setRowHeight(30);
    // 表格的列模型
    TableColumnModel cm = table.getColumnModel();
    // 对每个列设置宽度
    for (int i = 0; i < cm.getColumnCount(); i++) {
      TableColumn column = cm.getColumn(i);
      if (table.equals(jt2)) {
        if(i == 2) {
        	column.setPreferredWidth(120);
            column.setMaxWidth(150);
            column.setMinWidth(60);
        }else {
        	column.setPreferredWidth(80);
            column.setMaxWidth(150);
            column.setMinWidth(60);
        }
      } else if (table.equals(jt3)) {
    	  if(i == 2) {
          	column.setPreferredWidth(160);
              column.setMaxWidth(150);
              column.setMinWidth(60);
          }else {
          	column.setPreferredWidth(85);
              column.setMaxWidth(150);
              column.setMinWidth(60);
          }
      } else {
        column.setPreferredWidth(60);
        column.setMaxWidth(100);
        column.setMinWidth(50);
      }
    }

    // 设置表头文字居中显示
    DefaultTableCellRenderer renderer =
        (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
    renderer.setHorizontalAlignment(SwingConstants.CENTER);

    // 设置表格中的数据居中显示
    DefaultTableCellRenderer r = new DefaultTableCellRenderer();
    r.setHorizontalAlignment(JLabel.CENTER);
    table.setDefaultRenderer(Object.class, r);
  }

  private void paneSet(JPanel jp) {
    // 向画布添加控件
    if (jp.equals(this.jp1)) {
      this.jp1.add(jl2);
      this.jp1.add(jtf2);
      this.jp1.add(jb2);
      this.jp1.add(jb3);
      this.jp1.add(jb4);
      this.setContentPane(jp1);
    } else if (jp.equals(this.jp2)) {
      this.jp2.add(jl2);
      this.jp2.add(jtf2);
      this.jp2.add(jb2);
      this.jp2.add(jb3);
      this.jp2.add(jb4);
      this.jp2.add(jsp1);
      this.jp2.add(jsp2);
      this.jp2.add(jsp3);
      this.setContentPane(jp2);
    } else if (jp.equals(this.jp3)) {
      this.jp3.add(jl2);
      this.jp3.add(jtf2);
      this.jp3.add(jb2);
      this.jp3.add(jb3);
      this.jp3.add(jb4);
      this.setContentPane(jp3);
    }
  }
}
