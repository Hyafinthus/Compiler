package GUI;


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

public class Frame extends JFrame implements ActionListener {
  private static final long serialVersionUID = -4956743638930220924L;

  JButton jb1, jb2, jb3, jb4; // 按钮
  JPanel jp1, jp2, jp3; // 画布
  JLabel jl1, jl2; // 文本
  JTextField jtf1, jtf2; // 文本框（显示）
  JTextArea jta1, jta2, jta3, jta4; // 定义文本域
  JScrollPane jsp1, jsp2, jsp3, jsp4, jsp5;// 定义文本域对应的滚动条
  File FA, text; // 文件
  JTable jt1, jt2, jt3; // 定义表格
  JTextPane jtp1;// 专门显示文本的文本域
  ResourceManager resourceManager = new ResourceManager();

  public Frame() {

    // Frame初始化设置
    this.setLayout(null);
    this.setTitle("词法分析");
    this.setSize(1600, 768);
    this.setLocation(100, 100);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);

    // this.add(card);
    this.setVisible(true);

    // button设置
    this.jb1 = new JButton("...");
    this.jb1.setBounds(1330, 0, 60, 30);
    this.jb1.addActionListener(this);
    this.jb2 = new JButton("...");
    this.jb2.setBounds(1330, 50, 60, 30);
    this.jb2.addActionListener(this);
    this.jb3 = new JButton("NFA分析表转换");
    this.jb3.addActionListener(this);
    this.jb3.setBounds(580, 100, 150, 30);
    this.jb4 = new JButton("DFA分析表转换");
    this.jb4.addActionListener(this);
    this.jb4.setBounds(780, 100, 150, 30);

    // 文本设置
    this.jl1 = new JLabel("代码文件路径");
    this.jl1.setBounds(180, 0, 100, 40);
    this.jl2 = new JLabel("FA转换表路径");
    this.jl2.setBounds(180, 50, 100, 40);

    // 文本框设置
    this.jtf1 = new JTextField();
    this.jtf1.setEditable(false);
    this.jtf1.setBounds(300, 0, 1000, 30);
    // this.jtf1.setPreferredSize(new Dimension(1000, 30));
    this.jtf2 = new JTextField();
    this.jtf2.setEditable(false);
    this.jtf2.setBounds(300, 50, 1000, 30);
    // this.jtf2.setPreferredSize(new Dimension(1000, 30));

    // 画布初始化
    this.jp1 = new JPanel();
    this.jp1.setLayout(null);

    this.jp1.setSize(1600, 768);
    this.jp1.setLocation(20, 40);

    paneSet(jp1);

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
        this.text = chooser.getSelectedFile();// f为选择到的文件
        this.jtf1.setText(this.text.getAbsolutePath());
        readtext();
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
        this.FA = chooser.getSelectedFile();// f为选择到的文件
        this.jtf2.setText(FA.getAbsolutePath());
      }
    } else if (e.getSource().equals(jb3)) {
      // 读取excel文件到资源管理器中
      resourceManager.NFAexcel_reader(FA);
      resourceManager.analysis(this.jtp1.getText());

      // DFA转换表显示界面设置
      this.jt1 = new JTable(resourceManager.getDFAdata(), resourceManager.getDFAdataTitle());
      setTableFormat(jt1);
      this.jta1 = new JTextArea();
      this.jsp1 = new JScrollPane(jta1);
      this.jsp1.setBounds(500, 160, 400, 500);
      this.jsp1.setViewportView(jt1);

      // Token显示界面设置
      this.jt2 = new JTable(resourceManager.getTokendata(), resourceManager.getTokendataTitle());
      setTableFormat(jt2);
      this.jta2 = new JTextArea();
      this.jsp2 = new JScrollPane(jta2);
      this.jsp2.setBounds(920, 160, 200, 500);
      this.jsp2.setViewportView(jt2);

      // 错误信息界面设置
      this.jt3 = new JTable(resourceManager.getErrordata(), resourceManager.getErrordataTitle());
      setTableFormat(jt3);
      this.jta3 = new JTextArea();
      this.jsp3 = new JScrollPane(jta3);
      this.jsp3.setBounds(1140, 160, 420, 500);
      this.jsp3.setViewportView(jt3);

      // 显示画布2
      this.jp2 = new JPanel();
      this.jp2.setSize(1600, 768);
      this.jp2.setLocation(20, 40);
      this.jp2.setLayout(null);
      paneSet(jp2);

    } else if (e.getSource().equals(jb4)) {
      // 读取excel文件到资源管理器中
      resourceManager.DFAexcel_reader(FA);
      resourceManager.analysis(this.jtp1.getText());

      // DFA转换表显示界面设置
      this.jt1 = new JTable(resourceManager.getDFAdata(), resourceManager.getDFAdataTitle());
      setTableFormat(jt1);
      this.jta1 = new JTextArea();
      this.jsp1 = new JScrollPane(jta1);
      this.jsp1.setBounds(500, 160, 400, 500);
      this.jsp1.setViewportView(jt1);

      // Token显示界面设置
      this.jt2 = new JTable(resourceManager.getTokendata(), resourceManager.getTokendataTitle());
      setTableFormat(jt2);
      this.jta2 = new JTextArea();
      this.jsp2 = new JScrollPane(jta2);
      this.jsp2.setBounds(920, 160, 200, 500);
      this.jsp2.setViewportView(jt2);

      // 错误信息界面设置
      this.jt3 = new JTable(resourceManager.getErrordata(), resourceManager.getErrordataTitle());
      setTableFormat(jt3);
      this.jta3 = new JTextArea();
      this.jsp3 = new JScrollPane(jta3);
      this.jsp3.setBounds(1140, 160, 420, 500);
      this.jsp3.setViewportView(jt3);

      // 显示画布2
      this.jp2 = new JPanel();
      this.jp2.setSize(1600, 768);
      this.jp2.setLocation(20, 40);
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
      if (table.equals(jt2) && (i == 2)) {
        column.setPreferredWidth(80);
        column.setMaxWidth(150);
        column.setMinWidth(60);
      } else if (table.equals(jt3) && (i == 2)) {
        column.setPreferredWidth(300);
        column.setMinWidth(300);
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

  // 将text内容读到文本域上供修改
  private void readtext() {
    // 设置画板5
    this.jp3 = new JPanel();
    this.jp3.setSize(1600, 768);
    this.jp3.setLocation(20, 40);
    this.jp3.setLayout(null);

    // 设置文本域,同时禁用JTextPane的自动换行功能
    this.jtp1 = new JTextPane() {
      private static final long serialVersionUID = 4854670851066293168L;

      public boolean getScrollableTracksViewportWidth() {
        return false;
      }

      public void setSize(Dimension d) {
        if (d.width < getParent().getSize().width) {
          d.width = getParent().getSize().width;
        }
        d.width += 100;
        super.setSize(d);
      }
    };

    // 将text读入文本域中
    try {
      FileInputStream fis = new FileInputStream(text);
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));// 转换成字符流，有readline方法可以直接读取一行的数据，方便文本的读取
      StringBuffer str = new StringBuffer("");// 读取第一行
      String tempstr = bufferedReader.readLine();
      // 读取所有文本，放到StringBuffer 中，用来在文本域中展示
      while (tempstr != null) {
        str.append(tempstr + "\n");
        tempstr = bufferedReader.readLine();// 读取下一行
      }
      bufferedReader.close();// 关闭输入流
      // 设置文本域及画板显示
      this.jtp1.setFont(new Font("Consolas", Font.PLAIN, 20));
      this.jtp1.setText(str.toString());
      this.jsp5 = new JScrollPane(jtp1);
      this.jsp5.setBounds(0, 160, 480, 500);
      paneSet(jp3);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void paneSet(JPanel jp) {
    // 向画布添加控件
    if (jp.equals(this.jp1)) {
      this.jp1.add(jl1);
      this.jp1.add(jtf1);
      this.jp1.add(jb1);
      this.jp1.add(jl2);
      this.jp1.add(jtf2);
      this.jp1.add(jb2);
      this.jp1.add(jb3);
      this.jp1.add(jb4);
      this.setContentPane(jp1);
    } else if (jp.equals(this.jp2)) {
      this.jp2.add(jl1);
      this.jp2.add(jtf1);
      this.jp2.add(jb1);
      this.jp2.add(jl2);
      this.jp2.add(jtf2);
      this.jp2.add(jb2);
      this.jp2.add(jb3);
      this.jp2.add(jb4);
      this.jp2.add(jsp1);
      this.jp2.add(jsp2);
      this.jp2.add(jsp3);
      this.jp2.add(jsp5);
      this.setContentPane(jp2);
    } else if (jp.equals(this.jp3)) {
      this.jp3.add(jl1);
      this.jp3.add(jtf1);
      this.jp3.add(jb1);
      this.jp3.add(jl2);
      this.jp3.add(jtf2);
      this.jp3.add(jb2);
      this.jp3.add(jb3);
      this.jp3.add(jb4);
      this.jp3.add(jsp5);
      this.setContentPane(jp3);
    }
  }
}
