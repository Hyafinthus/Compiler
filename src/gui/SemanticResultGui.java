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

public class SemanticResultGui extends JFrame {

  private static final long serialVersionUID = -2760753019517809093L;

  JTextArea jta2; // 定义文本域
  JScrollPane jsp1, jsp2;// 定义文本域对应的滚动条
  JTable jt2;// 错误信息表格
  JTree jTree;

  DefaultTreeModel newModel;
  DefaultMutableTreeNode Node;
  DefaultMutableTreeNode temp;

  public SemanticResultGui() {
    // Frame初始化设置
    this.setLayout(null);
    this.setTitle("语法分析结果");
    this.setSize(1068, 768);
    this.setLocation(200, 200);

    this.setResizable(false);
    this.setVisible(true);

    Node = traverseTree(ResourceManager.semanticRoot);
    newModel = new DefaultTreeModel(Node);
    jTree = new JTree(newModel);

    this.jsp1 = new JScrollPane(jTree);
    this.jsp1.setBounds(30, 30, 500, 700);
    this.add(jsp1);
    
    this.jt2 = new JTable(ResourceManager.SemanticErrordata, ResourceManager.SemanticErrordataTitle);
    setTableFormat(this.jt2);
    this.jta2 = new JTextArea();
    this.jsp2 = new JScrollPane(jta2);
    this.jsp2.setBounds(550, 30, 500, 700);
    this.jsp2.setViewportView(jt2);
    this.add(jsp2);
  }

  private DefaultMutableTreeNode traverseTree(semantic.SemanticNode node) {
    DefaultMutableTreeNode parrent = new DefaultMutableTreeNode(node.data);

    if (node != null) {
      List<semantic.SemanticNode> children = node.children;
      if (node.terminal) { // 如果是叶节点（终结符）
        DefaultMutableTreeNode dn = new DefaultMutableTreeNode(
            node.data.equals("ε") ? node.data : (node.data + ": " + node.word));
        return dn;
      } else { // 如果是非叶节点（非终结符）
        for (semantic.SemanticNode tempNode : children) {
          parrent.add(traverseTree(tempNode));
        }
      }
    }

    return parrent;
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