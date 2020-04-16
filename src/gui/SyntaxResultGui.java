package gui;

import syntax.Node;

import java.util.List;

import javax.swing.*;
import javax.swing.tree.*;

import resource.ResourceManager;

public class SyntaxResultGui extends JFrame{

	private static final long serialVersionUID = -2760753019517809093L;

	JTextArea jta1; // 定义文本域
	JScrollPane jsp1, jsp2;// 定义文本域对应的滚动条
	JTable jt1;//错误信息表格
	JTree jTree;
	
	DefaultTreeModel newModel;
	DefaultMutableTreeNode Node;
	DefaultMutableTreeNode temp;
	
	public SyntaxResultGui() {
		// Frame初始化设置
	    this.setLayout(null);
	    this.setTitle("语法分析结果");
	    this.setSize(1068, 768);
	    this.setLocation(200, 200);

	    this.setResizable(false);
	    this.setVisible(true);
	    
	    Node=traverseTree(ResourceManager.treeRoot);
		newModel=new DefaultTreeModel(Node);
		jTree=new JTree(newModel);
		
		this.jsp1 = new JScrollPane(jTree);
		this.jsp1.setBounds(30, 30, 500, 700);
		this.add(jsp1);
	}
	
	private DefaultMutableTreeNode traverseTree(Node node) {
		DefaultMutableTreeNode fujiedian = new DefaultMutableTreeNode(node.data);
       
        if (node != null) {
        	List<Node> children = node.children;
            if (node.terminal) {//如果是叶节点（终结符）
                DefaultMutableTreeNode dn=new DefaultMutableTreeNode(node.data);
                return dn;
            }else{//如果是非叶节点（非终结符）
                for (Node tempNode : children) {
                    fujiedian.add(traverseTree(tempNode));
                }
            }
        } 
        
		return fujiedian;
    }

}
