package gui;

import javax.swing.*;

import resource.FileFilter;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import resource.ResourceManager;

public class MainInterface extends JFrame implements ActionListener{

	private static final long serialVersionUID = -6977141133138401619L;
	
	//主窗口的所有控件
	JPanel jp1, jp2;
	JButton jb1, jb2, jb3, jb4; // 按钮
	JTextField jtf1;
	JScrollPane jsp1;
	JTextPane jtp1;// 专门显示文本的文本域
	
	public MainInterface() {
		// Frame初始化设置
	    this.setLayout(null);
	    this.setTitle("编译总界面");
	    this.setSize(800, 600);
	    this.setLocation(100, 100);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setResizable(false);
	    
	    this.setVisible(true);
	    
	    //button设置
	    this.jb1 = new JButton("...");
	    this.jb1.setBounds(700, 40, 60, 30);
	    this.jb1.addActionListener(this);
	    this.jb2 = new JButton("词法分析");
	    this.jb2.setBounds(580, 160, 160, 60);
	    this.jb2.addActionListener(this);
	    this.jb3 = new JButton("语法分析");
	    this.jb3.addActionListener(this);
	    this.jb3.setBounds(580, 300, 160, 60);
	    this.jb4 = new JButton("语义分析");
	    this.jb4.addActionListener(this);
	    this.jb4.setBounds(580, 440, 160, 60);
	    
	    //文本框设置
	    this.jtf1 = new JTextField();
	    this.jtf1.setEditable(false);
	    this.jtf1.setBounds(50, 40, 620, 30);
	    
	    this.jp1 = new JPanel();
	    this.jp1.setLayout(null);
	    
	    this.jp1.setBounds(0, 0, 800, 600);
	    
	    this.jp1.add(jb1);
	    this.jp1.add(jtf1);
	    
	    this.setContentPane(jp1);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
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
		        ResourceManager.text = chooser.getSelectedFile();// f为选择到的文件
		        this.jtf1.setText(ResourceManager.text.getAbsolutePath());
		        readtext();
		      }
		    } else if (e.getSource().equals(jb2)) {
		    	LexicalGui frame1 = new LexicalGui(this.jtp1);
		    } else if (e.getSource().equals(jb3)) {
		    	GrammaticalGui frame2 = new GrammaticalGui(this.jtp1);
		    } else if (e.getSource().equals(jb4)) {
		    	
		    }
		
	}

	// 将text内容读到文本域上供修改
	  private void readtext() {
	    // 设置画板2
	    this.jp2 = new JPanel();
	    this.jp2.setSize(800, 600);
	    this.jp2.setLocation(0, 0);
	    this.jp2.setLayout(null);

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
	      FileInputStream fis = new FileInputStream(ResourceManager.text);
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
	      this.jsp1 = new JScrollPane(jtp1);
	      this.jsp1.setBounds(50, 100, 500, 440);
	      
	      this.jp2.add(jb1);
		  this.jp2.add(jb2);
		  this.jp2.add(jb3);
		  this.jp2.add(jb4);
		  this.jp2.add(jtf1);
		  this.jp2.add(jsp1);
		  this.setContentPane(jp2);
	      
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }
}
