package gui;

import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import resource.ResourceManager;
import semantic.Action;

public class SemanticResultGui extends JFrame {

  private static final long serialVersionUID = -2760753019517809093L;

  JTextArea jta1, jta2, jta3; // 定义文本域
  JScrollPane jsp1, jsp2, jsp3;// 定义文本域对应的滚动条
  JTable jt1, jt2, jt3;// 错误信息表格

  public SemanticResultGui() {
    // Frame初始化设置
    this.setLayout(null);
    this.setTitle("语法分析结果");
    this.setSize(1068, 768);
    this.setLocation(200, 200);

    this.setResizable(false);
    this.setVisible(true);

    this.jt1 = new JTable(Action.symbol, ResourceManager.SemanticSymboldataTitle);
    setTableFormat(this.jt1);
    this.jta1 = new JTextArea();
    this.jsp1 = new JScrollPane(jta1);
    this.jsp1.setBounds(20, 30, 330, 700);
    this.jsp1.setViewportView(jt1);
    this.add(jsp1);
    
    this.jt2 = new JTable(Action.intermediate, ResourceManager.intermediatedataTitle);
    setTableFormat(this.jt2);
    this.jta2 = new JTextArea();
    this.jsp2 = new JScrollPane(jta2);
    this.jsp2.setBounds(370, 30, 330, 700);
    this.jsp2.setViewportView(jt2);
    this.add(jsp2);
    
    this.jt3 = new JTable(ResourceManager.SemanticErrordata, ResourceManager.SemanticErrordataTitle);
    setTableFormat(this.jt3);
    this.jta3 = new JTextArea();
    this.jsp3 = new JScrollPane(jta3);
    this.jsp3.setBounds(720, 30, 330, 700);
    this.jsp3.setViewportView(jt3);
    this.add(jsp3);
  }


  private void setTableFormat(JTable table) {
	    table.setRowHeight(25);
	    
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