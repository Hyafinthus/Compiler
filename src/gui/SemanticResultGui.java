package gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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
    this.setTitle("语义分析结果");
    this.setSize(1600, 768);
    this.setLocation(200, 200);

    this.setResizable(false);
    this.setVisible(true);

    
    this.jt1 = new JTable(Action.symbol, ResourceManager.SemanticSymboldataTitle);
    setTableFormat(this.jt1);
    this.jta1 = new JTextArea();
    this.jsp1 = new JScrollPane(jta1);
    this.jsp1.setBounds(25, 30, 468, 700);
    this.jsp1.setViewportView(jt1);
    this.add(jsp1);
    
    this.jt2 = new JTable(Action.intermediate, ResourceManager.intermediatedataTitle);
    setTableFormat(this.jt2);
    this.jta2 = new JTextArea();
    this.jsp2 = new JScrollPane(jta2);
    this.jsp2.setBounds(503, 30, 612, 700);
    this.jsp2.setViewportView(jt2);
    this.add(jsp2);
    
    this.jt3 = new JTable(Action.errorData, ResourceManager.SemanticErrordataTitle);
    setTableFormat(this.jt3);
    this.jta3 = new JTextArea();
    this.jsp3 = new JScrollPane(jta3);
    this.jsp3.setBounds(1125, 30, 450, 700);
    this.jsp3.setViewportView(jt3);
    this.add(jsp3);
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
	    if(i == 0 || i == 1) {
	      column.setPreferredWidth(90);
	      column.setMinWidth(90);
	    }else {
	      column.setPreferredWidth(216);
	      column.setMinWidth(216);
	    }
	  } else if (table.equals(jt3)) {
	    if(i == 0) {
	      column.setPreferredWidth(90);
	      column.setMinWidth(90);
	    }else if(i == 1){
	      column.setPreferredWidth(144);
		  column.setMinWidth(144);
	    }else {
	      column.setPreferredWidth(216);
	      column.setMinWidth(216);
	    }
	  } else {
		if(i == 1 || i == 2) {
		  column.setPreferredWidth(144);
		  column.setMinWidth(144);
		}else {
		  column.setPreferredWidth(90);
		  column.setMinWidth(90);
		}
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