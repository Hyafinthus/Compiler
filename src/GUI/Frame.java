package GUI;

import Lexical.Dfa;
import Lexical.Dfa2Token;
import Lexical.Nfa;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class Frame extends JFrame implements ActionListener {
  private static final long serialVersionUID = -4956743638930220924L;

  JButton jb1, jb2, jb3, jb4; // 按钮
  JPanel jp1, jp2, jp3, jp4; // 画布
  JLabel jl1, jl2, jl3, jl4; // 文本
  JTextField jtf1, jtf2; // 文本框（显示）
  JTextArea jta1, jta2, jta3; // 定义文本域
  JScrollPane jsp1, jsp2, jsp3;// 定义文本域对应的滚动条
  File text, FA; // 文件
  JTable jt1, jt2, jt3; // 定义表格
  ResourceManager resourceManager = new ResourceManager();

  public Frame() {
    // Frame初始化设置
    this.setLayout(null);
    this.setTitle("词法分析");
    this.setSize(1600, 768);
    this.setLocation(100, 100);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // button设置
    jb1 = new JButton("...");
    jb1.addActionListener(this);
    jb2 = new JButton("...");
    jb2.addActionListener(this);
    jb3 = new JButton("NFA分析表转换");
    jb3.addActionListener(this);
    jb4 = new JButton("DFA分析表转换");
    jb4.addActionListener(this);

    // 文本设置
    jl1 = new JLabel("代码文件路径");
    jl2 = new JLabel("FA转换表路径");
    jl3 = new JLabel("DFA转换表");
    jl3.setBounds(20, 0, 70, 40);
    jl4 = new JLabel("TOKEN序列");
    jl4.setBounds(510, 0, 70, 40);

    // 文本框设置
    jtf1 = new JTextField();
    jtf1.setEditable(false);
    jtf1.setPreferredSize(new Dimension(1200, 30));
    jtf2 = new JTextField();
    jtf2.setEditable(false);
    jtf2.setPreferredSize(new Dimension(1200, 30));

    // 画布初始化
    jp1 = new JPanel();
    jp2 = new JPanel();
    jp3 = new JPanel();

    jp1.setSize(1500, 40);
    jp1.setLocation(0, 50);
    jp2.setSize(1500, 40);
    jp2.setLocation(0, 100);
    jp3.setSize(1600, 40);
    jp3.setLocation(0, 150);

    // 向画布添加控件
    jp1.add(jl1);
    jp1.add(jtf1);
    jp1.add(jb1);
    jp2.add(jl2);
    jp2.add(jtf2);
    jp2.add(jb2);
    jp3.add(jb3);
    jp3.add(jb4);

    this.add(jp1);
    this.add(jp2);
    this.add(jp3);

    this.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // 按键一
    if (e.getSource().equals(jb1)) {
      ArrayList<String> list = new ArrayList<String>();
      list.add(".txt");
      FileFilter txt_filter = new FileFilter(list);
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(0);// 设定只能选择到文件
      chooser.setFileFilter(txt_filter);
      int state = chooser.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
      if (state == 1) {
        return;// 撤销则返回
      } else {
        text = chooser.getSelectedFile();// f为选择到的文件
        jtf1.setText(text.getAbsolutePath());
      }
    } else if (e.getSource().equals(jb2)) {
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
        FA = chooser.getSelectedFile();// f为选择到的文件
        jtf2.setText(FA.getAbsolutePath());
      }
    } else if (e.getSource().equals(jb3)) {
      // 读取excel文件到资源管理器中
      resourceManager.NFAexcel_reader(FA);
      Nfa nfa = new Nfa(resourceManager.getNFAdata());
      Dfa dfa = nfa.toDfa();
      try {
          Dfa2Token dfa2Token = new Dfa2Token(dfa, this.text);
          dfa2Token.analysis();
          System.out.println("11166");
        } catch (FileNotFoundException e1) {
          e1.printStackTrace();
        }
      
      // DFA转换表显示界面设置
      jt1 = new JTable(resourceManager.getDFAdata(), resourceManager.getDFAdataTitle());
      setTableFormat(jt1);
      jta1 = new JTextArea();
      JScrollPane jsp1 = new JScrollPane(jta1);
      jsp1.setBounds(90, 0, 400, 500);
      jsp1.setViewportView(jt1);

      // Token显示界面设置
      jt2 = new JTable(resourceManager.getTokendata(), resourceManager.getTokendataTitle());
      setTableFormat(jt2);
      jta2 = new JTextArea();
      JScrollPane jsp2 = new JScrollPane(jta2);
      jsp2.setBounds(580, 0, 400, 500);
      jsp2.setViewportView(jt2);

      jp4 = new JPanel();
      jp4.setSize(1600, 540);
      jp4.setLocation(0, 200);
      jp4.setLayout(null);
      jp4.add(jl3);
      jp4.add(jsp1);
      jp4.add(jl4);
      jp4.add(jsp2);

      this.setContentPane(jp4);
    } else if (e.getSource().equals(jb4)) {
      // 读取excel文件到资源管理器中
      resourceManager.DFAexcel_reader(FA);

      // 创建Dfa对象 传入DFA信息
      Dfa dfa = new Dfa(resourceManager.getDFAdataTitle(), resourceManager.getDFAdata());
      // 创建Lexical中对象 传入text
      try {
        Dfa2Token dfa2Token = new Dfa2Token(dfa, this.text);
        dfa2Token.analysis();
        System.out.println("123");
      } catch (FileNotFoundException e1) {
        e1.printStackTrace();
      }

      // DFA转换表显示界面设置
      jt1 = new JTable(resourceManager.getDFAdata(), resourceManager.getDFAdataTitle());
      setTableFormat(jt1);
      jta1 = new JTextArea();
      JScrollPane jsp1 = new JScrollPane(jta1);
      jsp1.setBounds(90, 0, 400, 500);
      jsp1.setViewportView(jt1);

      // Token显示界面设置
      jt2 = new JTable(resourceManager.getTokendata(), resourceManager.getTokendataTitle());
      setTableFormat(jt2);
      jta2 = new JTextArea();
      JScrollPane jsp2 = new JScrollPane(jta2);
      jsp2.setBounds(580, 0, 400, 500);
      jsp2.setViewportView(jt2);

      // 错误信息界面设置
      jt3 = new JTable(resourceManager.getErrordata(), resourceManager.getErrordataTitle());
      setTableFormat(jt2);
      TableColumnModel cm = jt3.getColumnModel();
      TableColumn column = cm.getColumn(2);
      column.setPreferredWidth(300);
      jta3 = new JTextArea();
      JScrollPane jsp3 = new JScrollPane(jta3);
      jsp3.setBounds(1000, 0, 560, 500);
      jsp3.setViewportView(jt3);

      // 显示画布4
      jp4 = new JPanel();
      jp4.setSize(1600, 540);
      jp4.setLocation(0, 200);
      jp4.setLayout(null);
      jp4.add(jl3);
      jp4.add(jsp1);
      jp4.add(jl4);
      jp4.add(jsp2);
      jp4.add(jsp3);

      this.setContentPane(jp4);
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
      column.setPreferredWidth(60);
      column.setMaxWidth(100);
      column.setMinWidth(50);
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
}
