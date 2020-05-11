package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

public class SemanticGui extends JFrame implements ActionListener {
  private static final long serialVersionUID = -990548592332708520L;

  // 主窗口的所有控件
  JPanel jp1, jp2;
  JButton jb1, jb2; // 按钮
  JLabel jl1; // 文本
  JTextField jtf1; // 路径文本框
  JTextArea jta1, jta2, jta3; // 定义文本域
  JScrollPane jsp1, jsp2, jsp3;// 定义文本域对应的滚动条
  JTable jt1, jt2, jt3; // 定义表格
  JTextPane txtjtp;// 专门显示文本的文本域
  File analysisExcel;

  public SemanticGui(JTextPane jtp0) {
    this.txtjtp = jtp0;

    // Frame初始化设置
    this.setLayout(null);
    this.setTitle("语义分析");
    this.setSize(1068, 768);
    this.setLocation(150, 150);

    this.setResizable(false);
    this.setVisible(true);

    // button设置
    this.jb1 = new JButton("...");
    this.jb1.setBounds(780, 70, 60, 30);
    this.jb1.addActionListener(this);
    this.jb2 = new JButton("分析结果");
    this.jb2.setBounds(880, 70, 100, 30);
    this.jb2.addActionListener(this);

    // 文本设置
    this.jl1 = new JLabel("分析表路径");
    this.jl1.setBounds(80, 70, 100, 40);

    // 文本框设置
    this.jtf1 = new JTextField();
    this.jtf1.setEditable(false);
    this.jtf1.setBounds(180, 70, 600, 30);

    // 画布初始化
    this.jp1 = new JPanel();
    this.jp1.setLayout(null);
    this.jp1.setSize(1068, 768);
    this.jp1.setLocation(0, 0);

    this.jp1.add(jb1);
    this.jp1.add(jb2);
    this.jp1.add(jl1);
    this.jp1.add(jtf1);
    this.setContentPane(jp1);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource().equals(jb1)) {
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
        this.analysisExcel = chooser.getSelectedFile();// f为选择到的文件
        this.jtf1.setText(analysisExcel.getAbsolutePath());

        // 设置显示三个JTable
        readAnalysisExcel();
      }
    } else if (e.getSource().equals(jb2)) {
      ResourceManager.semanticAnalysis(this.txtjtp.getText());
      @SuppressWarnings("unused")
      SemanticResultGui semanticResultGui = new SemanticResultGui();
    }
  }

  private void readAnalysisExcel() {
    this.jp2 = new JPanel();
    this.jp2.setSize(800, 600);
    this.jp2.setLocation(0, 0);
    this.jp2.setLayout(null);

    // 读入分析表文件，修改资源管理中的数据内容
    ResourceManager.semantic_LLexcel_reader(analysisExcel);

    // first,follow集界面设置
    this.jt1 = new JTable(ResourceManager.FirstFollowdata, ResourceManager.FirstFollowdataTitle);
    setTableFormat(jt1);
    this.jta1 = new JTextArea();
    this.jsp1 = new JScrollPane(jta1);
    this.jsp1.setBounds(20, 150, 320, 550);
    this.jsp1.setViewportView(jt1);

    // 产生式select集界面设置
    this.jt2 = new JTable(ResourceManager.Selectdata, ResourceManager.SelectdataTitle);
    setTableFormat(jt2);
    this.jta2 = new JTextArea();
    this.jsp2 = new JScrollPane(jta2);
    this.jsp2.setBounds(350, 150, 320, 550);
    this.jsp2.setViewportView(jt2);

    // 分析表界面设置
    this.jt3 = new JTable(ResourceManager.LLanalysisdata, ResourceManager.LLanalysisdataTitle);
    setTableFormat(jt3);
    this.jta3 = new JTextArea();
    this.jsp3 = new JScrollPane(jta3);
    this.jsp3.setBounds(680, 150, 360, 550);
    this.jsp3.setViewportView(jt3);

    // 显示画布2
    this.jp2 = new JPanel();
    this.jp2.setSize(1068, 768);
    this.jp2.setLocation(0, 0);
    this.jp2.setLayout(null);

    this.jp2.add(jb1);
    this.jp2.add(jb2);
    this.jp2.add(jl1);
    this.jp2.add(jtf1);
    this.jp2.add(jsp1);
    this.jp2.add(jsp2);
    this.jp2.add(jsp3);
    this.setContentPane(jp2);
  }

  private void setTableFormat(JTable table) {
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setRowHeight(30);
    // 表格的列模型
    TableColumnModel cm = table.getColumnModel();
    // 对每个列设置宽度
    for (int i = 0; i < cm.getColumnCount(); i++) {
      TableColumn column = cm.getColumn(i);
      if (table.equals(jt1)) {
        column.setPreferredWidth(100);
        column.setMinWidth(100);
      } else if (table.equals(jt2)) {
        column.setPreferredWidth(150);
        column.setMinWidth(150);
      } else {
        column.setPreferredWidth(60);
        column.setMinWidth(60);
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
}
