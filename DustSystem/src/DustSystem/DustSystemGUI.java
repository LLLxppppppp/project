package DustSystem;

import org.jfree.chart.*;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class DustSystemGUI extends JFrame implements ActionListener {
    private JScrollPane realtime_scrollPane;
    private JFrame monitor_worker_window,monitor_sensor_window,monitor_realtime_window,
    search_sum_window,dust_analyse_window,statistics_and_analyse_window,check_path_window,
    screen_data_window,screen_data_window2,sum_dust_graph_window,show_data_window,show_sensor_data_table_window,camera_window;
    private JButton monitor_sensor_but,monitor_worker_but,monitor_realtime_but,worker_dust_sum_but,dust_predict_but,statistics_analyse_but;
    private JTextField input_date_box,input_date_box2,input_density_box,input_temperature_box,input_humidity_box,input_wind_box;
    private JComboBox choose_sensorNumber_box,choose_big_or_not_box,screen_box1,screen_box2;
    private JTable worker_data_table,sensor_data_table,realtime_data_table=null,sum_dust_data_table,screen_data_table;
    private JScrollPane worker_scrollPane;
    private int sensorNum;
    private String queryDate,startDate,endDate;
    private String [][] screen_tableValues;
    private Double[][] sum_tableValues;
    private freechartTest d1,d2,d3,d4;
    
    //调数据库
    Database myDatabase=new Database();
    //四个传感器的固定坐标
    final private int sen1X = 35, sen1Y = 0;
    final private int sen2X = 70, sen2Y = 25;
    final private int sen3X = 35, sen3Y = 50;
    final private int sen4X = 0, sen4Y = 25;
    
    
    public DustSystemGUI(){
        //main window
        //todo
        this.setIconImage(Toolkit.getDefaultToolkit().createImage("F:\\DustSystem\\src\\DustSystem\\2.png"));//窗体图标
        this.setBounds(500,100,800,530);  //设置坐标和大小
        this.setResizable(false);  // 缩放为不能缩放

        ImageIcon icon=new ImageIcon("F:\\DustSystem\\src\\DustSystem\\3.jpg");//背景图
        JLabel background = new JLabel(icon);//往一个标签中加入图片
        background.setBounds(0, 0, this.getWidth(), this.getHeight());//设置标签位置大小，大小要和窗口一样大
        icon.setImage(icon.getImage().getScaledInstance(background.getWidth(), background.getHeight(), Image.SCALE_DEFAULT));//图片自适应标签大小
        this.getLayeredPane().add(background, Integer.valueOf(Integer.MIN_VALUE));//标签添加到层面板

        //获取jf的顶层容器,并设置为透明
        JPanel j=(JPanel)this.getContentPane();
        j.setOpaque(false);

        JLabel jp = new JLabel();
        jp.setLayout(null);
        jp.setBorder(new EmptyBorder(5, 5, 5, 5));//设置边界
        jp.setOpaque(false);
        this.add(jp);
        //todo
        this.setTitle("粉尘预警系统");
        this.setSize(600,520);
        //this.setBackground(new Color(183, 237, 242));
        //this.getContentPane().setBackground(new Color(255, 255, 255));
        //myFrame frame1 = new myFrame();
        //  this.setLocationRelativeTo(null);
        this.setLocation(400,60);

        this.setResizable(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.setExtendedState(this.ICONIFIED); //最小化
        this.setExtendedState(this.NORMAL);
        //




        //



        // 最大化或正常状态
//        System.exit(0);
        //关闭窗口
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int value=JOptionPane.showConfirmDialog(null, "确定要关闭吗？");

                if (value==JOptionPane.OK_OPTION) {
                    System.exit(0);
                }
            }
        });
        //add button to the window
        initButton();
        this.add(monitor_sensor_but);
        this.add(monitor_worker_but);
        this.add(monitor_realtime_but);
        this.add(worker_dust_sum_but);
        this.add(dust_predict_but);
        this.add(statistics_analyse_but);

        //listener
        initListener();
    }
  

    /*
    init the style of the button
     */
    private void initButton(){
        //button
        monitor_sensor_but=new MyButton("监控传感器");
        monitor_sensor_but.setBackground(new Color(175, 225, 187));
        monitor_sensor_but.setBounds(122,55,0,0);
        monitor_sensor_but.setFont(new Font("宋体",Font.BOLD,14));

        monitor_worker_but=new MyButton("监控工人");
        monitor_worker_but.setBackground(new Color(225,204,219));
        monitor_worker_but.setBounds(122,55,120,50);
        monitor_worker_but.setFont(new Font("宋体",Font.PLAIN,14));

        monitor_realtime_but=new MyButton("实时监测");
        monitor_realtime_but.setBackground(new Color(225,204,219));
        monitor_realtime_but.setBounds(242,220,120,50);
        monitor_realtime_but.setFont(new Font("宋体",Font.PLAIN,14));

        worker_dust_sum_but=new MyButton("查询累积接尘");
        worker_dust_sum_but.setBackground(new Color(225,204,219));
        worker_dust_sum_but.setBounds(122,390,0,0);
        worker_dust_sum_but.setFont(new Font("宋体",Font.PLAIN,14));

        dust_predict_but=new MyButton("接尘量分析");
        dust_predict_but.setBackground(new Color(225,204,219));
        dust_predict_but.setBounds(350,390,120,50);
        dust_predict_but.setFont(new Font("宋体",Font.PLAIN,14));

        statistics_analyse_but=new MyButton("");
        statistics_analyse_but.setBackground(new Color(225,204,219));
        statistics_analyse_but.setBounds(530,205,0,0);
        statistics_analyse_but.setFont(new Font("宋体",Font.PLAIN,14));
    }

    private void initListener(){
        // add listener to button
        ButtonListener bListener = new ButtonListener();
        monitor_sensor_but.addActionListener(bListener);
        monitor_worker_but.addActionListener(bListener);
        monitor_realtime_but.addActionListener(bListener);
        worker_dust_sum_but.addActionListener(bListener);
        dust_predict_but.addActionListener(bListener);
        statistics_analyse_but.addActionListener(bListener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String args[]){
        DustSystemGUI dust_system=new DustSystemGUI();
        dust_system.setVisible(true);
    }

    private class ButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String buttonName = e.getActionCommand();
            try{
                if(buttonName=="监控传感器"){
                    monitorSensor();
                }
                else if(buttonName=="监控工人"){
                    monitorWorker();
                }
                else if(buttonName=="实时监测"){
                    monitorRealtimeData();
                }
                else if(buttonName==" 确认 "){
                    choose_date_submit();
                }
                else if(buttonName=="确认"){
                    choose_date_number_submit();
                }
                else if(buttonName=="查询累积接尘"){
                    searchSumOfDust();
                }
                else if(buttonName=="确认查询"){
                    search_sum_submit();
                }
                else if(buttonName=="接尘量分析"){
                    dustAnalyse();
                }
                //todo确认分析
                else if(buttonName=="开始分析"){
                    analyse_sum_dust();
                }
                else if(buttonName=="多维统计分析"){
                    statisticsAndAnalyse();
                }
                else if(buttonName=="模拟路径"){
                    checkPath();
                }
                else if(buttonName=="查询"){
                    screenData();
                }
                else if(buttonName=="确认筛选"){
                    refreshTable();
                }
                else if(buttonName=="camera") {
//                	open_sensor_video();
                	openCamera();
                }
            }
            catch (Exception e1){

            }
        }
    }
    
    //method of monitor the four sensors
    public void monitorSensor(){
        monitor_sensor_window=new JFrame("监控传感器数据");
        //todo
        monitor_sensor_window.setIconImage(Toolkit.getDefaultToolkit().createImage("F:\\DustSystem\\src\\DustSystem\\2.png"));//窗体图标
        monitor_sensor_window.setBounds(500,100,1000,530);  //设置坐标和大小
        //monitor_sensor_window.setResizable(false);  // 缩放为不能缩放

        ImageIcon icon=new ImageIcon("F:\\DustSystem\\src\\DustSystem\\3.jpg");//背景图
        JLabel background = new JLabel(icon);//往一个标签中加入图片
        background.setBounds(0, 0, monitor_sensor_window.getWidth(), monitor_sensor_window.getHeight());//设置标签位置大小，大小要和窗口一样大
        icon.setImage(icon.getImage().getScaledInstance(background.getWidth(), background.getHeight(), Image.SCALE_DEFAULT));//图片自适应标签大小
        monitor_sensor_window.getLayeredPane().add(background, Integer.valueOf(Integer.MIN_VALUE));//标签添加到层面板

        //获取jf的顶层容器,并设置为透明
        JPanel j=(JPanel)monitor_sensor_window.getContentPane();
        j.setOpaque(false);

        JLabel jpp = new JLabel();
        jpp.setLayout(null);
        //jpp.setBorder(new EmptyBorder(5, 5, 5, 5));//设置边界
        jpp.setOpaque(false);
        monitor_sensor_window.add(jpp);
        //todo
        monitor_sensor_window.setLayout(null);
        monitor_sensor_window.setSize(430,350);
        monitor_sensor_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        monitor_sensor_window.setVisible(true);
        monitor_sensor_window.setLocation(368,30);

        //日期输入栏及日期选择器
        input_date_box = new JTextField();
        input_date_box.setBounds(30,30,120,30);
        CalendarPanel p = new CalendarPanel(input_date_box, "yyyy-MM-dd");
        p.initCalendarPanel();

        //序号选择
        JPanel jp=new JPanel();    //创建面板
        jp.setBounds(150,10,160,50);

        //标签及combobox
        JLabel label1=new JLabel("传感器序号：");    //创建标签
        label1.setFont(new Font("宋体",Font.PLAIN,12));

        choose_sensorNumber_box=new JComboBox();    //创建JComboBox
        choose_sensorNumber_box.setFont(new Font("宋体",Font.PLAIN,12));
        choose_sensorNumber_box.addItem("--请选择--");    //向下拉列表中添加一项
        choose_sensorNumber_box.addItem("1");
        choose_sensorNumber_box.addItem("2");
        choose_sensorNumber_box.addItem("3");
        choose_sensorNumber_box.addItem("4");
        jp.add(label1);
        jp.add(choose_sensorNumber_box);

        //确认按钮
        JButton sensor_submit_but=new JButton("确认");
        sensor_submit_but.setBackground(new Color(225,204,219));
        sensor_submit_but.setBounds(310,30,70,30);
        sensor_submit_but.setFont(new Font("宋体",Font.PLAIN,12));
        //给确认按钮增加监听
        ButtonListener buttonListener=new ButtonListener();
        sensor_submit_but.addActionListener(buttonListener);

        //添加组件
        JLabel l = new JLabel("日历面板");
        p.add(l);
        monitor_sensor_window.add(sensor_submit_but);
        monitor_sensor_window.add(jp);
        monitor_sensor_window.getContentPane().add(p);
        monitor_sensor_window.getContentPane().add(input_date_box);

    }

    public int getSensorId()
    {
        int id = choose_sensorNumber_box.getSelectedIndex();
        if (id == 0)
        {
            id = 1;
        }
        return id;
    }

    //method of monitor the worker
    public void monitorWorker(){
        //弹窗选择想要查看的日期
        monitor_worker_window=new JFrame("监控工人数据");
        //todo
        monitor_worker_window.setIconImage(Toolkit.getDefaultToolkit().createImage("F:\\DustSystem\\src\\DustSystem\\2.png"));//窗体图标
        monitor_worker_window.setBounds(500,100,800,530);  //设置坐标和大小
        monitor_worker_window.setResizable(false);  // 缩放为不能缩放

        ImageIcon icon=new ImageIcon("F:\\DustSystem\\src\\DustSystem\\3.jpg");//背景图
        JLabel background = new JLabel(icon);//往一个标签中加入图片
        background.setBounds(0, 0, monitor_worker_window.getWidth(), monitor_worker_window.getHeight());//设置标签位置大小，大小要和窗口一样大
        icon.setImage(icon.getImage().getScaledInstance(background.getWidth(), background.getHeight(), Image.SCALE_DEFAULT));//图片自适应标签大小
        monitor_worker_window.getLayeredPane().add(background, Integer.valueOf(Integer.MIN_VALUE));//标签添加到层面板

        //获取jf的顶层容器,并设置为透明
        JPanel j=(JPanel)monitor_worker_window.getContentPane();
        j.setOpaque(false);

        JLabel jp = new JLabel();
        jp.setLayout(null);
        jp.setBorder(new EmptyBorder(5, 5, 5, 5));//设置边界
        jp.setOpaque(false);
        monitor_worker_window.add(jp);
        //todo
        monitor_worker_window.setLayout(null);
        monitor_worker_window.setSize(400,350);
        monitor_worker_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        monitor_worker_window.setVisible(true);
        monitor_worker_window.setLocation(0,60);

        //todo
        JPanel i2=new JPanel();
        JLabel l2=new JLabel("日期查询");
        i2.setBackground(new Color(255, 255, 255));
        l2.setFont(new Font("宋体",Font.PLAIN,12));
        i2.setBounds(150,70,100,20);
        i2.add(l2);
        monitor_worker_window.add(i2);
        //todo
        //日期输入栏及日期选择器
        input_date_box = new JTextField();
        //todo
        input_date_box.setText("请选择日期");
        input_date_box.setForeground(Color.lightGray);
        //todo
        input_date_box.setBounds(125,110,150,30);
        CalendarPanel p = new CalendarPanel(input_date_box, "yyyy-MM-dd");
        p.initCalendarPanel();

        //确认按钮
        button submit_but=new button(" 确认 ");
        submit_but.setBackground(new Color(225,204,219));
        submit_but.setBounds(154,170,100,30);
        submit_but.setFont(new Font("宋体",Font.PLAIN,12));
        //给确认按钮增加监听
        ButtonListener buttonListener=new ButtonListener();
        submit_but.addActionListener(buttonListener);

        JLabel l = new JLabel("日历面板");
        p.add(l);
        monitor_worker_window.getContentPane().add(p);
        monitor_worker_window.add(submit_but);
        monitor_worker_window.getContentPane().add(input_date_box);
    }

    public void choose_date_submit(){
        //得到用户输入的日期
        queryDate=input_date_box.getText();
        //获取数据库工人信息
//        Database myDatabase=new Database();
        ArrayList<PathVertex> apv=myDatabase.getWorkerByDate(queryDate);

        //显示数据的表格
        String[] columnNames = { "日期", "时间","X","Y"};//表头
//        String[][] tableValues={{"0","0","0","0"}};
        String[][] tableValues=new String[apv.size()][4];//表中数据：来自数据库

        for(int i=0;i<apv.size();i++) {
            PathVertex pv=apv.get(i);
            tableValues[i][0]=pv.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            tableValues[i][1]=pv.getTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            tableValues[i][2]=Integer.toString(pv.getX());
            tableValues[i][3]=Integer.toString(pv.getY());
        }

        show_data_table_worker(tableValues,columnNames,queryDate);
    }

    public void show_data_table_worker(String[][] tableValues,String[] columnNames,String queryDate) {
        //弹窗选择想要查看的日期
    	show_data_window=new JFrame("监控李铉鹏于"+queryDate+"数据");
        //todo
        show_data_window.setIconImage(Toolkit.getDefaultToolkit().createImage("F:\\DustSystem\\src\\DustSystem\\2.png"));//窗体图标
        show_data_window.setBounds(0,180,800,530);  //设置坐标和大小
        show_data_window.setResizable(false);  // 缩放为不能缩放

        ImageIcon icon=new ImageIcon("F:\\DustSystem\\src\\DustSystem\\3.jpg");//背景图
        JLabel background = new JLabel(icon);//往一个标签中加入图片
        background.setBounds(0, 0, show_data_window.getWidth(), show_data_window.getHeight());//设置标签位置大小，大小要和窗口一样大
        icon.setImage(icon.getImage().getScaledInstance(background.getWidth(), background.getHeight(), Image.SCALE_DEFAULT));//图片自适应标签大小
        show_data_window.getLayeredPane().add(background, Integer.valueOf(Integer.MIN_VALUE));//标签添加到层面板

        //获取jf的顶层容器,并设置为透明
        JPanel j=(JPanel)show_data_window.getContentPane();
        j.setOpaque(false);

        JLabel jp = new JLabel();
        jp.setLayout(null);
        jp.setBorder(new EmptyBorder(5, 5, 5, 5));//设置边界
        jp.setOpaque(false);
        show_data_window.add(jp);
        //todo
    	show_data_window.setLayout(null);
    	show_data_window.setSize(470,560);
    	show_data_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    	show_data_window.setVisible(true);
    	show_data_window.setLocation(0,300);
    	 //文字说明
        JPanel identify=new JPanel();
        JLabel l1=new JLabel("name：李铉鹏         workdate："+queryDate);
        identify.setBackground(new Color(163, 196, 225));
        l1.setFont(new Font("宋体",Font.PLAIN,12));
        identify.setBounds(30,23,400,20);
        identify.add(l1);
        show_data_window.add(identify);

        DefaultTableModel model; //列表默认TableModel
        worker_data_table=new JTable(new DefaultTableModel(tableValues,columnNames));
        DefaultTableCellRenderer r=new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        worker_data_table.setDefaultRenderer(Object.class,r);
        worker_data_table.getTableHeader().setBackground(new Color(163, 196, 225));
        worker_data_table.getTableHeader().setFont(new Font("宋体",Font.PLAIN,12));
        worker_data_table.setFont(new Font("宋体",Font.PLAIN,12));
        worker_data_table.setBackground(new Color(170, 211, 211));
        worker_data_table.setSelectionBackground(new Color(209, 225, 220));

        worker_scrollPane = new JScrollPane(worker_data_table);
        JScrollBar bar = worker_scrollPane.getHorizontalScrollBar();

        worker_scrollPane.setBounds(40,60,380,400);
        worker_scrollPane.setFont(new Font("宋体",Font.PLAIN,12));

        show_data_window.add(worker_scrollPane);

        //查看轨迹按钮
        button check_path=new button("模拟路径");
        check_path.setBackground(new Color(163, 196, 225));
        check_path.setBounds(342,475,100,30);
        check_path.setFont(new Font("宋体",Font.PLAIN,12));
        //给确认按钮增加监听
        ButtonListener buttonListener=new ButtonListener();
        check_path.addActionListener(buttonListener);

        show_data_window.add(check_path);
//        monitor_worker_window.repaint();
    	
    }
    
    public void checkPath(){
        //获得日期
        queryDate=input_date_box.getText();       
        ArrayList<PathVertex> allPathVertex=myDatabase.getWorkerByDate(queryDate);
        GeneratePath gp = new GeneratePath(allPathVertex);

    }

//    public void drawPath(String date) {
//		setSize(800,600);
//		
//		setTitle("工人一天活动轨迹图" + date);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);	//窗口设于正中间
//        
//        setVisible(true);
//	}
	
	//调用drawPath()的时候自动调用
	public void drawPath(Graphics g,String date) {
        //整体区域
		int x0=50,y0=50;
        g.setColor(Color.darkGray);
        g.drawRect(x0,y0,700,500);

        //在起点绘制红色十字
        //todo
        g.setColor(Color.green);
        int s=3;
        g.drawLine(x0-s,y0,x0+s,y0);
        g.drawLine(x0,y0-s,x0,y0+s);
        int[] prePos={x0,y0};
        ArrayList<PathVertex> allPathVertex=myDatabase.getWorkerByDate(queryDate);
        for(PathVertex v : allPathVertex){
            int x1 = v.getX();
            int y1 = v.getY();
            //todo
            g.setColor(Color.green);
            g.drawLine(x1-s,y1,x1+s,y1);
            g.drawLine(x1,y1-s,x1,y1+s);

            g.setColor(Color.DARK_GRAY);
            g.drawLine(prePos[0],prePos[1],x1,y1);
            prePos[0] = x1;
            prePos[1] = y1;
        }
    }
	
	
    public void monitorRealtimeData(){
        //窗体设计
        monitor_realtime_window=new JFrame("实时监测");
        //todo
        monitor_realtime_window.setIconImage(Toolkit.getDefaultToolkit().createImage("F:\\DustSystem\\src\\DustSystem\\2.png"));//窗体图标
        monitor_realtime_window.setBounds(1002,60,800,230);  //设置坐标和大小
        monitor_realtime_window.setResizable(false);  // 缩放为不能缩放

        ImageIcon icon=new ImageIcon("F:\\DustSystem\\src\\DustSystem\\3.jpg");//背景图
        JLabel background = new JLabel(icon);//往一个标签中加入图片
        background.setBounds(0, 0, monitor_realtime_window.getWidth(), monitor_realtime_window.getHeight());//设置标签位置大小，大小要和窗口一样大
        icon.setImage(icon.getImage().getScaledInstance(background.getWidth(), background.getHeight(), Image.SCALE_DEFAULT));//图片自适应标签大小
        monitor_realtime_window.getLayeredPane().add(background, Integer.valueOf(Integer.MIN_VALUE));//标签添加到层面板

        //获取jf的顶层容器,并设置为透明
        JPanel j=(JPanel)monitor_realtime_window.getContentPane();
        j.setOpaque(false);

        JLabel jp1 = new JLabel();
        jp1.setLayout(null);
        jp1.setBorder(new EmptyBorder(5, 5, 5, 5));//设置边界
        jp1.setOpaque(false);
        monitor_realtime_window.add(jp1);
        //todo

        monitor_realtime_window.setLayout(null);
        monitor_realtime_window.setSize(500,230);
        monitor_realtime_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        monitor_realtime_window.setLocation(1002,60);
        monitor_realtime_window.setVisible(true);

        //实时数据（初始为0）//表头
        String[][] realtime_tableValues={{"0","0","0","0"}};
        String[] columnNames = { "检测点1", "监测点2","监测点3","监测点4"};
        realtime_data_table=new JTable(realtime_tableValues,columnNames);
        DefaultTableCellRenderer r=new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        realtime_data_table.setDefaultRenderer(Object.class,r);
        realtime_data_table.getTableHeader().setBackground(new Color(163, 196, 225 ));
        realtime_data_table.getTableHeader().setFont(new Font("宋体",Font.PLAIN,12));
        realtime_data_table.setFont(new Font("宋体",Font.PLAIN,12));
        realtime_data_table.setBackground(new Color(170, 211, 211));
        realtime_data_table.setSelectionBackground(new Color(170, 211, 211));
        realtime_data_table.setRowHeight(21);

        realtime_scrollPane = new JScrollPane(realtime_data_table);
        JScrollBar bar = realtime_scrollPane.getHorizontalScrollBar();

        realtime_scrollPane.setBounds(50,50,380,43);
        realtime_scrollPane.setFont(new Font("宋体",Font.PLAIN,12));
        
        //序号选择
        JPanel jp=new JPanel();    //创建面板
        jp.setBounds(50,10,160,30);
        jp.setBackground(new Color(122,187,218));
        //标签及combobox
        JLabel label1=new JLabel("传感器序号：");    //创建标签
        label1.setFont(new Font("宋体",Font.PLAIN,12));

        choose_sensorNumber_box=new JComboBox();    //创建JComboBox
        choose_sensorNumber_box.setFont(new Font("宋体",Font.PLAIN,12));
        choose_sensorNumber_box.addItem("默认：1");    //向下拉列表中添加一项
        choose_sensorNumber_box.addItem("1");
        choose_sensorNumber_box.addItem("2");
        choose_sensorNumber_box.addItem("3");
        choose_sensorNumber_box.addItem("4");
        jp.add(label1);
        jp.add(choose_sensorNumber_box);

        //确认按钮
        button sensor_check_but=new button("camera");
        //sensor_check_but.setBackground(new Color(163, 196, 225 ));
        sensor_check_but.setBounds(197,130,110,30);
        sensor_check_but.setFont(new Font("宋体",Font.PLAIN,12));
        //给确认按钮增加监听
        ButtonListener buttonListener=new ButtonListener();
        sensor_check_but.addActionListener(buttonListener);

        monitor_realtime_window.add(jp);
        monitor_realtime_window.add(sensor_check_but);
        monitor_realtime_window.add(realtime_scrollPane);
        
        d1=new freechartTest();
        d2=new freechartTest();
        d3=new freechartTest();
        d4=new freechartTest();
        d1.setTitle("sensor1");
        d1.setLocation(0, 60);
        d1.setVisible(true);
        d2.setTitle("sensor2");
        d2.setLocation(0,440);
        d2.setVisible(true);
        d3.setTitle("sensor3");
        d3.setLocation(400,60);
        d3.setVisible(true);
        d4.setTitle("sensor4");
        d4.setLocation(400,440);
        d4.setVisible(true);
//        //选择传感器序号
//        JPanel jp=new JPanel();    //创建面板
//        jp.setBounds(5,25,300,150);
//        //标签及combobox
//        JLabel label1=new JLabel("传感器序号：");    //创建标签
//        label1.setFont(new Font("宋体",Font.PLAIN,12));
//
//        choose_sensorNumber_box_realtime=new JComboBox();    //创建JComboBox
//        choose_sensorNumber_box_realtime.setFont(new Font("宋体",Font.PLAIN,12));
//        choose_sensorNumber_box_realtime.addItem("--请选择--");    //向下拉列表中添加一项
//        choose_sensorNumber_box_realtime.addItem("1");
//        choose_sensorNumber_box_realtime.addItem("2");
//        choose_sensorNumber_box_realtime.addItem("3");
//        choose_sensorNumber_box_realtime.addItem("4");
//        jp.add(label1);
//        jp.add(choose_sensorNumber_box_realtime);
//
//        //确认按钮
//        JButton realtime_submit_but=new JButton(" 确认");
//        realtime_submit_but.setBackground(new Color(225,204,219));
//        realtime_submit_but.setBounds(330,27,70,30);
//        realtime_submit_but.setFont(new Font("宋体",Font.PLAIN,12));
//        //给确认按钮增加监听
//        ButtonListener bListener=new ButtonListener();
//        realtime_submit_but.addActionListener(bListener);
//
//        //添加组件
//        monitor_realtime_window.add(jp);
//        monitor_realtime_window.add(realtime_submit_but);

//        change_realtime_data(1,"8");

//        change_realtime_data(2,"5");
    }

    public void searchSumOfDust(){
        //弹窗选择想要查看的日期
        search_sum_window=new JFrame("查询累积接尘");
        search_sum_window.setLayout(null);
        search_sum_window.setSize(500,350);
        search_sum_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        search_sum_window.setVisible(true);
        search_sum_window.setLocation(368,30);

        //日期输入栏及日期选择器
        input_date_box = new JTextField();
        input_date_box.setBounds(30,20,300,30);
        CalendarPanel p = new CalendarPanel(input_date_box, "yyyy-MM-dd");
        p.initCalendarPanel();

        //确认按钮
        JButton submit_but=new JButton("确认查询");
        submit_but.setBackground(new Color(225,204,219));
        submit_but.setBounds(340,20,100,30);
        submit_but.setFont(new Font("宋体",Font.PLAIN,12));
        //给确认按钮增加监听
        ButtonListener buttonListener=new ButtonListener();
        submit_but.addActionListener(buttonListener);

        JLabel l = new JLabel("日历面板");
        p.add(l);
        search_sum_window.add(submit_but);
        search_sum_window.getContentPane().add(p);
        search_sum_window.getContentPane().add(input_date_box);
    }

    public void dustAnalyse(){
        //弹窗选择想要查看的日期
        dust_analyse_window=new JFrame("累计接尘量");
        //todo
        dust_analyse_window.setIconImage(Toolkit.getDefaultToolkit().createImage("F:\\DustSystem\\src\\DustSystem\\2.png"));//窗体图标
        dust_analyse_window.setBounds(500,100,800,530);  //设置坐标和大小
        dust_analyse_window.setResizable(false);  // 缩放为不能缩放

        ImageIcon icon=new ImageIcon("F:\\DustSystem\\src\\DustSystem\\3.jpg");//背景图
        JLabel background = new JLabel(icon);//往一个标签中加入图片
        background.setBounds(0, 0, dust_analyse_window.getWidth(), dust_analyse_window.getHeight());//设置标签位置大小，大小要和窗口一样大
        icon.setImage(icon.getImage().getScaledInstance(background.getWidth(), background.getHeight(), Image.SCALE_DEFAULT));//图片自适应标签大小
        dust_analyse_window.getLayeredPane().add(background, Integer.valueOf(Integer.MIN_VALUE));//标签添加到层面板

        //获取jf的顶层容器,并设置为透明
        JPanel j=(JPanel)dust_analyse_window.getContentPane();
        j.setOpaque(false);

        JLabel jp = new JLabel();
        jp.setLayout(null);
        jp.setBorder(new EmptyBorder(5, 5, 5, 5));//设置边界
        jp.setOpaque(false);
        dust_analyse_window.add(jp);
        //todo
        dust_analyse_window.setLayout(null);
        dust_analyse_window.setSize(500,400);
        dust_analyse_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        dust_analyse_window.setVisible(true);
        dust_analyse_window.setLocation(1002,60);

        //起始日期输入栏及日期选择器
        input_date_box = new JTextField();
        input_date_box.setText("开始日期");
        input_date_box.setForeground(Color.lightGray);
        input_date_box.setBounds(130,30,200,30);
        CalendarPanel p = new CalendarPanel(input_date_box, "yyyy-MM-dd");
        p.initCalendarPanel();
        //结束日期输入栏及日期选择器
        input_date_box2 = new JTextField();
        input_date_box2.setText("终止日期");
        input_date_box2.setForeground(Color.lightGray);
        input_date_box2.setBounds(130,100,200,30);
        CalendarPanel p2 = new CalendarPanel(input_date_box2, "yyyy-MM-dd");
        p2.initCalendarPanel();

        //确认按钮
        button analyse_but=new button("开始分析");
        //analyse_but.setBackground(new Color(225,204,219));
        analyse_but.setBounds(182,160,100,30);
        analyse_but.setFont(new Font("宋体",Font.PLAIN,12));
        //给确认按钮增加监听
        ButtonListener buttonListener=new ButtonListener();
        analyse_but.addActionListener(buttonListener);

        JLabel l = new JLabel("日历面板");
        p.add(l);

        dust_analyse_window.getContentPane().add(p);
        dust_analyse_window.getContentPane().add(p2);
        dust_analyse_window.add(analyse_but);
        dust_analyse_window.getContentPane().add(input_date_box);
        dust_analyse_window.getContentPane().add(input_date_box2);
    }

    //多维度查询与分析
    public void statisticsAndAnalyse(){
        //窗口参数
        statistics_and_analyse_window=new JFrame("多维查询分析");
        statistics_and_analyse_window.setLayout(null);
        statistics_and_analyse_window.setSize(770,500);
        statistics_and_analyse_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        statistics_and_analyse_window.setVisible(true);
        statistics_and_analyse_window.setLocation(368,30);

        //输入框与查询按钮
        JPanel density_area=new JPanel();
        JLabel density_label=new JLabel("浓度：");
        density_label.setFont(new Font("宋体",Font.PLAIN,12));
        density_area.setBounds(20,27,40,20);
        density_area.add(density_label);
        input_density_box = new JTextField();
        input_density_box.setBounds(60,30,40,20);

        JPanel temperature_area=new JPanel();
        JLabel temperature_label=new JLabel("温度：");
        temperature_label.setFont(new Font("宋体",Font.PLAIN,12));
        temperature_area.setBounds(110,27,40,20);
        temperature_area.add(temperature_label);
        input_temperature_box = new JTextField();
        input_temperature_box.setBounds(150,30,40,20);

        JPanel humidity_area=new JPanel();
        JLabel humidity_label=new JLabel("湿度：");
        humidity_label.setFont(new Font("宋体",Font.PLAIN,12));
        humidity_area.setBounds(200,27,40,20);
        humidity_area.add(humidity_label);
        input_humidity_box = new JTextField();
        input_humidity_box.setBounds(240,30,40,20);

        JPanel wind_area=new JPanel();
        JLabel wind_label=new JLabel("风力：");
        wind_label.setFont(new Font("宋体",Font.PLAIN,12));
        wind_area.setBounds(290,27,40,20);
        wind_area.add(wind_label);
        input_wind_box = new JTextField();
        input_wind_box.setBounds(330,30,40,20);

        //序号选择
        JPanel big_or_not_area=new JPanel();    //创建面板
        big_or_not_area.setBounds(380,23,160,100);

        //查询按钮
        JButton search_but=new JButton("查询");
        search_but.setBackground(new Color(225,204,219));
        search_but.setBounds(660,27,70,25);
        search_but.setFont(new Font("宋体",Font.PLAIN,12));
        ButtonListener buttonListener=new ButtonListener();
        search_but.addActionListener(buttonListener);


        choose_big_or_not_box=new JComboBox();    //创建JComboBox
        choose_big_or_not_box.setFont(new Font("宋体",Font.PLAIN,12));
        choose_big_or_not_box.addItem("--请选择--");    //向下拉列表中添加一项
        choose_big_or_not_box.addItem("大于");
        choose_big_or_not_box.addItem("小于");
        big_or_not_area.add(choose_big_or_not_box);
        
        //序号选择
        JPanel jp=new JPanel();    //创建面板
        jp.setBounds(540,10,100,50);
        //标签及combobox
        JLabel label1=new JLabel("传感器序号：");    //创建标签
        label1.setFont(new Font("宋体",Font.PLAIN,12));

        choose_sensorNumber_box=new JComboBox();    //创建JComboBox
        choose_sensorNumber_box.setFont(new Font("宋体",Font.PLAIN,12));
        choose_sensorNumber_box.addItem("--请选择--");    //向下拉列表中添加一项
        choose_sensorNumber_box.addItem("1");
        choose_sensorNumber_box.addItem("2");
        choose_sensorNumber_box.addItem("3");
        choose_sensorNumber_box.addItem("4");
        jp.add(label1);
        jp.add(choose_sensorNumber_box);
        
        JPanel jp1=new JPanel();    //创建面板
        jp1.setBounds(0,95,1000,5);
        jp1.setBackground(new Color(225,204,219));
        JLabel label2=new JLabel("--------------------------------------------------------------------------------------------------------------------------------------------------------");    //创建标签
        label2.setFont(new Font("宋体",Font.PLAIN,12));
        jp1.add(label2);

        statistics_and_analyse_window.add(density_area);
        statistics_and_analyse_window.add(input_density_box);
        statistics_and_analyse_window.add(temperature_area);
        statistics_and_analyse_window.add(input_temperature_box);
        statistics_and_analyse_window.add(humidity_area);
        statistics_and_analyse_window.add(input_humidity_box);
        statistics_and_analyse_window.add(wind_area);
        statistics_and_analyse_window.add(input_wind_box);
        statistics_and_analyse_window.add(big_or_not_area);
        statistics_and_analyse_window.add(jp);
        statistics_and_analyse_window.add(jp1);
        statistics_and_analyse_window.add(search_but);
        
      //起始日期输入栏及日期选择器
        JPanel input_begin_date=new JPanel();
        JLabel input_begin_label=new JLabel("起始日期：");
        input_begin_label.setFont(new Font("宋体",Font.PLAIN,12));
        input_begin_date.setBounds(35,140,100,50);
//        input_begin_date.setBackground(Color.RED);
        input_date_box = new JTextField();
        input_date_box.setText("请选择起始日期");
        input_date_box.setForeground(Color.lightGray);
        input_date_box.setBounds(35,180,10,10);
        CalendarPanel p = new CalendarPanel(input_date_box, "yyyy-MM-dd");
        p.initCalendarPanel();
        input_begin_date.add(input_begin_label);
        input_begin_date.add(input_date_box);

        //结束日期输入栏及日期选择器
        JPanel input_end_date=new JPanel();
        JLabel input_end_label=new JLabel("结束日期：");
        input_end_label.setFont(new Font("宋体",Font.PLAIN,12));
        input_end_date.setBounds(175,140,100,50);
//        input_end_date.setBackground(Color.RED);
        input_date_box2 = new JTextField();
        input_date_box2.setText("请选择结束日期");
        input_date_box2.setForeground(Color.lightGray);
        input_date_box2.setBounds(140,180,10,10);
        CalendarPanel p2 = new CalendarPanel(input_date_box2, "yyyy-MM-dd");
        p2.initCalendarPanel();
        input_end_date.add(input_end_label);
        input_end_date.add(input_date_box2);

        JPanel screen_area1=new JPanel();
        JLabel screen_l1= new JLabel("选择排序方式：");
        screen_l1.setFont(new Font("宋体",Font.PLAIN,12));
        screen_area1.setBounds(300,140,160,50);
        screen_box1=new JComboBox();    //创建JComboBox
        screen_box1.setFont(new Font("宋体",Font.PLAIN,12));
        screen_box1.addItem("--请选择--");    //向下拉列表中添加一项
        screen_box1.addItem("按粉尘浓度排序");
        screen_box1.addItem("按温度排序");
        screen_box1.addItem("按湿度排序");
        screen_box1.addItem("按风力排序");
        screen_box1.addItem("按时间排序");
        screen_area1.add(screen_l1);
        screen_area1.add(screen_box1);

        JPanel screen_area2=new JPanel();
        JLabel screen_l2= new JLabel("选择大小排序方式：");
        screen_l2.setFont(new Font("宋体",Font.PLAIN,12));
        screen_area2.setBounds(460,140,160,50);
        screen_box2=new JComboBox();    //创建JComboBox
        screen_box2.setFont(new Font("宋体",Font.PLAIN,12));
        screen_box2.addItem("--请选择--");    //向下拉列表中添加一项
        screen_box2.addItem("从小到大排序");
        screen_box2.addItem("从大到小排序");
        screen_area2.add(screen_l2);
        screen_area2.add(screen_box2);

        JButton screen_but=new JButton("确认筛选");
        screen_but.setBackground(new Color(225,204,219));
        screen_but.setBounds(640,150,90,30);
        screen_but.setFont(new Font("宋体",Font.PLAIN,12));
        ButtonListener buttonListener1=new ButtonListener();
        screen_but.addActionListener(buttonListener1);

        statistics_and_analyse_window.getContentPane().add(p);
        statistics_and_analyse_window.getContentPane().add(p2);
        statistics_and_analyse_window.add(input_begin_date);
        statistics_and_analyse_window.add(input_end_date);
        statistics_and_analyse_window.add(screen_area1);
        statistics_and_analyse_window.add(screen_area2);
        statistics_and_analyse_window.add(screen_but);
    }

    public void screenData(){
    	screen_data_window=new JFrame("多维查询分析");
    	screen_data_window.setLayout(null);
    	screen_data_window.setSize(770,400);
    	screen_data_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    	screen_data_window.setVisible(true);
    	screen_data_window.setLocation(368,260);
        //表
        // 传感器序号
        int sId=(choose_sensorNumber_box.getSelectedIndex()==0)?1:choose_sensorNumber_box.getSelectedIndex();
        // 查询条件 浓度 温度 湿度 风力
    	// 首先要判断是否输入为空 为空则初始化为-1
    	double density;
    	double temperature;
    	int humidity;
    	int wind;
    	if(input_density_box.getText().length() == 0) {
    		density = -1;
    	}else {
    		density = Double.parseDouble(input_density_box.getText());
    	}
    	if(input_temperature_box.getText().length() == 0) {
    		temperature = -1;
    	}else {
    		temperature = Double.parseDouble(input_temperature_box.getText());
    	}
    	if(input_humidity_box.getText().length() == 0) {
    		humidity = -1;
    	}else {
    		humidity = Integer.parseInt(input_humidity_box.getText());
    	}
    	if(input_wind_box.getText().length() == 0) {
    		wind = -1;
    	}else {
    		wind = Integer.parseInt(input_wind_box.getText());
    	}
    	// 下拉框设置比较方式 默认是大于
        int compareID = choose_big_or_not_box.getSelectedIndex();
        String compare = ">";
        if(compareID == 2) {
        	compare = "<";
        }
        
      // 调数据 调用数据库quaryAndSort方法 获得多维查询结果
        ArrayList<SensorData> asd=new ArrayList<SensorData>();
        asd = myDatabase.queryAndSort(sId, density, temperature, humidity, wind, compare);
        
      //显示传感器表
        String[] columnNames = { "日期", "时间","粉尘浓度","温度","湿度","风力"};//表头
//        String[][] tableValues={{"0","0","0","0","0","0"}};
        String[][] screen_tableValues=new String[asd.size()][6];//表中数据：来自数据库

        for(int i=0;i<asd.size();i++) {
            SensorData pv=asd.get(i);
            screen_tableValues[i][0]=pv.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            screen_tableValues[i][1]=pv.getTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            screen_tableValues[i][2]=Double.toString(pv.getDensity());
            screen_tableValues[i][3]=Double.toString(pv.getTemperature());
            screen_tableValues[i][4]=Integer.toString(pv.getHumidity());
            screen_tableValues[i][5]=Integer.toString(pv.getWind());
        }

        screen_data_table=new JTable(screen_tableValues,columnNames);
        DefaultTableCellRenderer r=new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        screen_data_table.setDefaultRenderer(Object.class,r);
        screen_data_table.getTableHeader().setBackground(new Color(225,204,219));
        screen_data_table.getTableHeader().setFont(new Font("宋体",Font.PLAIN,12));
        screen_data_table.setFont(new Font("宋体",Font.PLAIN,12));
        screen_data_table.setBackground(new Color(245,238,243));
        screen_data_table.setSelectionBackground(new Color(225,204,219));

        JScrollPane sensor_scrollPane = new JScrollPane(screen_data_table);
        JScrollBar bar = sensor_scrollPane.getHorizontalScrollBar();

        sensor_scrollPane.setBounds(40,20,680,320);
        sensor_scrollPane.setFont(new Font("宋体",Font.PLAIN,12));

        screen_data_window.add(sensor_scrollPane);
    }

    //根据筛选条件刷新表格
    public void refreshTable(){
    	screen_data_window2=new JFrame("多维查询分析");
    	screen_data_window2.setLayout(null);
    	screen_data_window2.setSize(770,400);
    	screen_data_window2.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    	screen_data_window2.setVisible(true);
    	screen_data_window2.setLocation(368,260);
    	// 传感器序号
        int sId=(choose_sensorNumber_box.getSelectedIndex()==0)?1:choose_sensorNumber_box.getSelectedIndex();
    	// 查询条件 浓度 温度 湿度 风力
    	// 首先要判断是否输入为空 为空则初始化为-1
    	double density;
    	double temperature;
    	int humidity;
    	int wind;
    	if(input_density_box.getText().length() == 0) {
    		density = -1;
    	}else {
    		density = Double.parseDouble(input_density_box.getText());
    	}
    	if(input_temperature_box.getText().length() == 0) {
    		temperature = -1;
    	}else {
    		temperature = Double.parseDouble(input_temperature_box.getText());
    	}
    	if(input_humidity_box.getText().length() == 0) {
    		humidity = -1;
    	}else {
    		humidity = Integer.parseInt(input_humidity_box.getText());
    	}
    	if(input_wind_box.getText().length() == 0) {
    		wind = -1;
    	}else {
    		wind = Integer.parseInt(input_wind_box.getText());
    	}
    	// 下拉框设置比较方式 默认是大于
        int compareID = choose_big_or_not_box.getSelectedIndex();
        String compare = ">";
        if(compareID == 2) {
        	compare = "<";
        }
        
    	// 得到用户筛选条件
        // 起始日期 结束日期
        String begin_date=null;
        String end_date=null;
        if(input_date_box.getText().length() == 7) {
        	begin_date = "2022-01-01";
    	}else {
    		begin_date=input_date_box.getText();
    	}
        if(input_date_box2.getText().length() == 7) {
        	end_date = "2022-01-01";
    	}else {
    		end_date=input_date_box2.getText();
    	}
        // 按照哪个字段 按升序/降序排列
        int sort_methodID=screen_box1.getSelectedIndex();
        int sort_time_methodID=screen_box2.getSelectedIndex();
        String sort_method = "Date"; // 默认按日期排列
        String sort_time_method = "ASC"; // 默认升序输出
        switch(sort_methodID) {
	        case 1:{
	        	sort_method="density";
	        	break;
	        }
	        case 2:{
	        	sort_method="temperature";
	        	break;
	        }
	        case 3:{
	        	sort_method="humidity";
	        	break;
	        }
	        case 4:{
	        	sort_method="wind";
	        	break;
	        }
	        default:{
	        	sort_method = "Date";
	        	break;
	        }
        }
        switch(sort_time_methodID) {
	        case 1:{
	        	sort_time_method = "ASC";
	        	break;
	        }
	        case 2:{
	        	sort_time_method = "DESC";
	        	break;
	        }
	        default:{
	        	sort_time_method = "ASC";
	        	break;
	        }
        }
        // 调数据 调用数据库quaryAndSort方法 获得多维查询结果
        ArrayList<SensorData> asd=new ArrayList<SensorData>();
        asd = myDatabase.queryAndSortByDate(sId, density, temperature, humidity, wind, compare, begin_date, end_date, sort_method, sort_time_method);
        System.out.println("多维查询结果为");
        for(SensorData sd : asd) {
			System.out.println(sd);
		}
        String[] columnNames = { "日期", "时间","粉尘浓度","温度","湿度","风力"};
        String[][] screen_tableValues=new String[asd.size()][6];
        //更新表格
        for(int i=0;i<asd.size();i++) {
            SensorData pv=asd.get(i);
            screen_tableValues[i][0]=pv.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            screen_tableValues[i][1]=pv.getTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            screen_tableValues[i][2]=Double.toString(pv.getDensity());
            screen_tableValues[i][3]=Double.toString(pv.getTemperature());
            screen_tableValues[i][4]=Integer.toString(pv.getHumidity());
            screen_tableValues[i][5]=Integer.toString(pv.getWind());
        }
//        screen_tableValues=temp;
        screen_data_table=new JTable(screen_tableValues,columnNames);
        DefaultTableCellRenderer r=new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        screen_data_table.setDefaultRenderer(Object.class,r);
        screen_data_table.getTableHeader().setBackground(new Color(225,204,219));
        screen_data_table.getTableHeader().setFont(new Font("宋体",Font.PLAIN,12));
        screen_data_table.setFont(new Font("宋体",Font.PLAIN,12));
        screen_data_table.setBackground(new Color(245,238,243));
        screen_data_table.setSelectionBackground(new Color(225,204,219));

        JScrollPane sensor_scrollPane = new JScrollPane(screen_data_table);
        JScrollBar bar = sensor_scrollPane.getHorizontalScrollBar();

        sensor_scrollPane.setBounds(40,20,680,320);
        sensor_scrollPane.setFont(new Font("宋体",Font.PLAIN,12));

        screen_data_window2.add(sensor_scrollPane);
    }

    public void choose_date_number_submit(){
        //获得用户输入日期
        queryDate=input_date_box.getText();
        //获得用户输入号
        sensorNum=choose_sensorNumber_box.getSelectedIndex();

        //获取数据库传感器信息
        ArrayList<SensorData> asd=myDatabase.getSensorByDate(sensorNum,queryDate);

        //显示传感器表
        String[] columnNames = { "日期", "时间","粉尘浓度","温度","湿度","风力"};//表头
//        String[][] tableValues={{"0","0","0","0","0","0"}};
        String[][] tableValues=new String[asd.size()][6];//表中数据：来自数据库

        for(int i=0;i<asd.size();i++) {
            SensorData pv=asd.get(i);
            tableValues[i][0]=pv.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            tableValues[i][1]=pv.getTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            tableValues[i][2]=Double.toString(pv.getDensity());
            tableValues[i][3]=Double.toString(pv.getTemperature());
            tableValues[i][4]=Integer.toString(pv.getHumidity());
            tableValues[i][5]=Integer.toString(pv.getWind());
        }

        show_sensor_data_table(tableValues,columnNames);
    }
    
    public void show_sensor_data_table(String[][] tableValues,String[] columnNames) {
    	 //弹窗选择想要查看的日期
    	show_sensor_data_table_window=new JFrame("sensor"+choose_sensorNumber_box.getSelectedIndex()+"数据");
    	show_sensor_data_table_window.setLayout(null);
    	show_sensor_data_table_window.setSize(470,560);
    	show_sensor_data_table_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    	show_sensor_data_table_window.setVisible(true);
    	show_sensor_data_table_window.setLocation(787,30);
    	
        sensor_data_table=new JTable(tableValues,columnNames);
        DefaultTableCellRenderer r=new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        sensor_data_table.setDefaultRenderer(Object.class,r);
        sensor_data_table.getTableHeader().setBackground(new Color(225,204,219));
        sensor_data_table.getTableHeader().setFont(new Font("宋体",Font.PLAIN,12));
        sensor_data_table.setFont(new Font("宋体",Font.PLAIN,12));
        sensor_data_table.setBackground(new Color(245,238,243));
        sensor_data_table.setSelectionBackground(new Color(225,204,219));

        JScrollPane sensor_scrollPane = new JScrollPane(sensor_data_table);
        JScrollBar bar = sensor_scrollPane.getHorizontalScrollBar();

        sensor_scrollPane.setBounds(40,40,400,440);
        sensor_scrollPane.setFont(new Font("宋体",Font.PLAIN,12));

        show_sensor_data_table_window.add(sensor_scrollPane);
    }

    //计算工人某天吸入的粉尘量
    private ArrayList<Double> getWorkerDust(String date) {
        Database db = new Database();
        ResultSet rs = db.workerDust(date);
        //System.out.println("aa");
        int wX,wY; //工人坐标
        double den1, den2, den3, den4;
        int dis1, dis2, dis3, dis4;
        int cnt = 0;
        double accumulation = 0.0;
        ArrayList<Double> accDensityOfHour = new ArrayList<Double>();

        try {
            //System.out.println("aa");
            while(rs.next()) {
                System.out.println("aa");
                wX=rs.getInt(3)/10;
                wY=rs.getInt(4)/10;
                den1=rs.getDouble(5);
                den2=rs.getDouble(6);
                den3=rs.getDouble(7);
                den4=rs.getDouble(8);

                dis1 = (int)Math.sqrt((wX-sen1X)*(wX-sen1X) + (wY-sen1Y)*(wY-sen1Y));
                dis2 = (int)Math.sqrt((wX-sen2X)*(wX-sen2X) + (wY-sen2Y)*(wY-sen2Y));
                dis3 = (int)Math.sqrt((wX-sen3X)*(wX-sen3X) + (wY-sen3Y)*(wY-sen3Y));
                dis4 = (int)Math.sqrt((wX-sen4X)*(wX-sen4X) + (wY-sen4Y)*(wY-sen4Y));

                System.out.println(dis1 + "\t" + dis2 + "\t" + dis3 + "\t" + dis4 + "\t");

                double averageDensity = 0.02*(den1/(dis1+1) + den2/(dis2+1) + den3/(dis3+1) + den4/(dis4+1))/4;
                accumulation += averageDensity;
                cnt++;
//                System.out.println(averageDensity + " " + accumulation);
                
                if(cnt%6 == 0) {
                    // 保留两位小数
                    // *0.01
                    accDensityOfHour.add((double)Math.round(accumulation*1000)/1000);
                    accumulation = 0;
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for(Double d : accDensityOfHour) {
            System.out.println(d);
        }
        return accDensityOfHour;
    }

    //计算截至给定日期，工人吸入的总粉尘量
    @SuppressWarnings("deprecation")
    private ArrayList<Double> totalDust(String startDate,String endDate) {
    	ArrayList<Double> totalDustList = new ArrayList<Double>();
        LocalDate startDay = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDay = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int days = (int) startDay.until(endDay,ChronoUnit.DAYS);

        int count = 0;//用于计算天数
        //为了对超过16位有效位的数进行精确的运算，采用BigDecimal
        BigDecimal b = new BigDecimal("9.00");//每天的粉尘浓度为9.00
        BigDecimal k = new BigDecimal("0.1");//工人的自我净化率为90%，后续会随着接尘量的增加而减少
        BigDecimal dustRes = new BigDecimal("0");
        System.out.println("days: " + days);
        int i;
        for(i = 0; i < days; i++) {
            //周末休息,不加新吸尘量,工人自净化积累的粉尘
            if(count == 6 || count == 7) {
            	dustRes = dustRes.multiply(k);
                if(count == 7) {
                    count = 0;
                }
            }else {
                //周一到周五工作日,加新积累量
                dustRes = (dustRes.multiply(k)).add(b);
            }
            dustRes = dustRes.setScale(2, BigDecimal.ROUND_HALF_UP);//保留两位小数
            System.out.println("dustRes="+dustRes);
            totalDustList.add(dustRes.doubleValue());
            count++;
            // k < 0.9
            if(k.compareTo(new BigDecimal("0.95"))  == -1) {
                k = k.add(new BigDecimal("0.01"));//自我净化效果降低
            }else {
            	k = new BigDecimal("0.97");//能力衰弱到几乎没有
            }
        }
        return totalDustList;
    }

    //画工人轨迹图并动态显示吸尘量（按小时）
    private void drawPathAndDust() {
    	//得到用户输入的日期
        queryDate=input_date_box.getText();
        //每小时的吸尘量（不累加）列表
        ArrayList<Double> dustOfHours = getWorkerDust(queryDate);
        
    	Database myDatabase=new Database();
        ArrayList<PathVertex> apv=myDatabase.getWorkerByDate(queryDate);
        int wX, wY; //工人坐标
        int dis1, dis2, dis3, dis4;
        for(int i = 1; i <= apv.size(); i++){
        	wX = apv.get(i).getX();
        	wY = apv.get(i).getY();
        	//画出这个点
        	
        	
        	//整数时刻时给出距离和在那个点的吸尘量
        	if(i%6 == 0) {
        		//得到该整时刻的吸尘量
        		double dustofHour = dustOfHours.get(i/6);
        		
        		//画该点和四个传感器的距离线
        		try {
        			dis1 = (int)Math.sqrt((wX-sen1X)*(wX-sen1X) + (wY-sen1Y)*(wY-sen1Y));
    				dis2 = (int)Math.sqrt((wX-sen2X)*(wX-sen2X) + (wY-sen2Y)*(wY-sen2Y));
    				dis3 = (int)Math.sqrt((wX-sen3X)*(wX-sen3X) + (wY-sen3Y)*(wY-sen3Y));
    				dis4 = (int)Math.sqrt((wX-sen4X)*(wX-sen4X) + (wY-sen4Y)*(wY-sen4Y));
    				
    				System.out.println(dis1 + "\t" + dis2 + "\t" + dis3 + "\t" + dis4 + "\t");
    				
					Thread.sleep(2000); //休息一秒
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
        	}
        	
        }
        
    }
  
    
    public void change_realtime_data(int id,SensorData sData){
    	// 显示粉尘浓度
    	double density = sData.getDensity();
    	if(realtime_data_table == null) {
    		return;
    	}
        if(density>=8){
            DefaultTableCellRenderer dtc=new DefaultTableCellRenderer();
            dtc.setForeground(new Color(191,27,0));
            dtc.setHorizontalAlignment(JLabel.CENTER);
            realtime_data_table.getColumnModel().getColumn(id-1).setCellRenderer(dtc);
            realtime_data_table.setDefaultRenderer(Object.class,dtc);
            
        }
        else {
            DefaultTableCellRenderer dtc=new DefaultTableCellRenderer();
            dtc.setForeground(Color.BLACK);
            dtc.setHorizontalAlignment(JLabel.CENTER);
            realtime_data_table.getColumnModel().getColumn(id-1).setCellRenderer(dtc);
            realtime_data_table.setDefaultRenderer(Object.class,dtc);
        }
        id = getSensorId();
        String s_dendity = Double.toString(density);
        realtime_data_table.setValueAt(s_dendity,0,id-1);
        realtime_scrollPane.validate(); 
        	
        // 实时折线图
        LocalDate sDate = sData.getDate();
        LocalTime sTime = sData.getTime();
        String dateStr = sDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String timeStr = sTime.format(DateTimeFormatter.ofPattern("HH:mm"));
		int year, month, date, hrs, min;
		year = Integer.parseInt(dateStr.substring(0, 4))-1900;
		month = Integer.parseInt(dateStr.substring(5, 7))-1;
		date = Integer.parseInt(dateStr.substring(8, 10));
		hrs = Integer.parseInt(timeStr.substring(0, 2));
		min = Integer.parseInt(timeStr.substring(3, 5));
//		System.out.println(year+" "+month+" "+date);
//		System.out.println(hrs+" "+min);
//		System.out.println(new Date(year-1900,month-1,date,hrs,min));
        if(id==1) {
//        	long t = System.currentTimeMillis();
//        	d1.addData(t, density);
    		d1.addData(year, month, date, hrs, min, density);
    		d1.repaint();
        }
        else if(id==2) {
//        	long t = System.currentTimeMillis();
//        	d1.addData(t, density);
    		d2.addData(year, month, date, hrs, min, density);
    		d2.repaint();      	
        }
        else if(id==3) {
//        	long t = System.currentTimeMillis();
//        	d1.addData(t, density);
    		d3.addData(year, month, date, hrs, min, density);
    		d3.repaint();    	
        }
        else if(id==4) {
//        	long t = System.currentTimeMillis();
//        	d1.addData(t, density);
    		d4.addData(year, month, date, hrs, min,density);
    		d4.repaint();    	
        }
    }

    public void search_sum_submit() throws IOException {
        //获得用户输入日期
        queryDate=input_date_box.getText();

        //获得接尘量数据
        ArrayList<Double> data= getWorkerDust(queryDate);
//        ArrayList<Double> data=new ArrayList<Double>(Arrays.asList(12.5,12.5,12.5,12.5,12.5,12.5,12.5,12.5));

        //绘制表与折线图
        String[] colNames={"10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00"};
        sum_tableValues= new Double[1][8];

        //转换及计算
        for(int i=0;i<data.size();i++){
            sum_tableValues[0][i]=data.get(i);
        }
        for(int j=1;j<8;j++){
            sum_tableValues[0][j]+=sum_tableValues[0][j-1];
            sum_tableValues[0][j]=(double)Math.round(sum_tableValues[0][j]*100)/100;
        }

        //文字说明
        JPanel identify=new JPanel();
        JLabel l1=new JLabel("工人姓名：李铉鹏     年龄：25    工作日期："+queryDate);
        l1.setFont(new Font("宋体",Font.PLAIN,12));
        identify.setBounds(-13,55,400,20);
        identify.add(l1);
        search_sum_window.add(identify);
        //search_sum_window.validate();
        //search_sum_window.setVisible(true);


        //绘制表
        sum_dust_data_table=new JTable(sum_tableValues,colNames);
        DefaultTableCellRenderer r=new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        sum_dust_data_table.setDefaultRenderer(Object.class,r);
        sum_dust_data_table.getTableHeader().setBackground(new Color(225,204,219));
        sum_dust_data_table.getTableHeader().setFont(new Font("宋体",Font.PLAIN,12));
        sum_dust_data_table.setFont(new Font("宋体",Font.PLAIN,12));
        sum_dust_data_table.setBackground(new Color(245,238,243));
        sum_dust_data_table.setSelectionBackground(new Color(225,204,219));
        sum_dust_data_table.setRowHeight(21);

        JScrollPane sum_dust_scrollPane = new JScrollPane(sum_dust_data_table);
        JScrollBar bar = sum_dust_scrollPane.getHorizontalScrollBar();

        sum_dust_scrollPane.setBounds(50,85,380,43);
        sum_dust_scrollPane.setFont(new Font("宋体",Font.PLAIN,12));

        search_sum_window.add(sum_dust_scrollPane);
        draw_sum_dust_graph(sum_tableValues);
    }
    
    public void draw_sum_dust_graph(Double[][] sum_tableValues){

    	//获得用户输入日期
    	queryDate=input_date_box.getText();
    	
    	sum_dust_graph_window=new JFrame("查询李铉鹏在"+queryDate+"接尘量");
    	sum_dust_graph_window.setLayout(null);
    	sum_dust_graph_window.setSize(500,460);
    	sum_dust_graph_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    	sum_dust_graph_window.setVisible(true);
    	sum_dust_graph_window.setLocation(368,190);
        //绘制图像
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(int i=0;i<sum_tableValues[0].length;i++){
            int num=10+i;
            String rowName=Integer.toString(num)+":00";
            dataset.setValue(sum_tableValues[0][i],"某工人",rowName);
        }

        StandardChartTheme standardChartTheme = new StandardChartTheme("CN") {
            public void apply(JFreeChart chart) {
                chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                super.apply(chart);
            }
        };
        // 设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("宋体", Font.BOLD, 11));
        // 设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 11));
        // 设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋体", Font.PLAIN, 11));

        standardChartTheme.setSmallFont(new Font("宋体", Font.PLAIN, 6));
        // 应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);

        JFreeChart chart= ChartFactory.createLineChart(
                "李铉鹏于"+input_date_box.getText()+"累积接尘",  //图表标题
                "时间",  //X轴lable
                "接尘量",  //Y轴lable
                dataset, //数据集
                PlotOrientation.VERTICAL, //图表放置模式水平/垂直
                true, //显示lable
                false,  //显示提示
                false //显示urls
        );


        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinesVisible(true); // 是否显示格子线
        plot.setBackgroundAlpha(0.3f); // 设置背景透明度
        plot.setRangeGridlinePaint(Color.BLUE);
        //todo
        plot.setOutlinePaint(Color.green);

        CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot.getRenderer();

        // 显示数据值
        DecimalFormat decimalformat1 = new DecimalFormat("##.##");// 数据点显示数据值的格式
        lineandshaperenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "{2}", decimalformat1));// 设置数据项标签的生成器
        lineandshaperenderer.setBaseItemLabelsVisible(true);// 基本项标签显示

        chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        chart.setBackgroundPaint(new Color(238,238,238));

        ChartPanel cp=new ChartPanel(chart);
        cp.setBounds(30,150,400,400);
        sum_dust_graph_window.setContentPane(cp);
    }

    public void draw_sum_dust_graph2(Double[][] sum_tableValues){
    	//获得用户输入日期
    	startDate=input_date_box.getText();
    	endDate=input_date_box2.getText();
    	
    	sum_dust_graph_window=new JFrame(startDate+"--"+endDate+"累计接尘情况");
        //todo
        sum_dust_graph_window.setIconImage(Toolkit.getDefaultToolkit().createImage("F:\\DustSystem\\src\\DustSystem\\2.png"));//窗体图标
        sum_dust_graph_window.setBounds(500,100,800,530);  //设置坐标和大小
        sum_dust_graph_window.setResizable(false);  // 缩放为不能缩放

        ImageIcon icon=new ImageIcon("F:\\DustSystem\\src\\DustSystem\\3.jpg");//背景图
        JLabel background = new JLabel(icon);//往一个标签中加入图片
        background.setBounds(0, 0, sum_dust_graph_window.getWidth(), sum_dust_graph_window.getHeight());//设置标签位置大小，大小要和窗口一样大
        icon.setImage(icon.getImage().getScaledInstance(background.getWidth(), background.getHeight(), Image.SCALE_DEFAULT));//图片自适应标签大小
        sum_dust_graph_window.getLayeredPane().add(background, Integer.valueOf(Integer.MIN_VALUE));//标签添加到层面板

        //获取jf的顶层容器,并设置为透明
        JPanel j=(JPanel)sum_dust_graph_window.getContentPane();
        j.setOpaque(false);

        JLabel jp = new JLabel();
        jp.setLayout(null);
        jp.setBorder(new EmptyBorder(5, 5, 5, 5));//设置边界
        jp.setOpaque(false);
        sum_dust_graph_window.add(jp);
        //todo
        sum_dust_graph_window.setResizable(false);
    	sum_dust_graph_window.setLayout(null);
    	sum_dust_graph_window.setSize(500,460);
    	sum_dust_graph_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    	sum_dust_graph_window.setVisible(true);
    	sum_dust_graph_window.setLocation(1002,370);
        //绘制图像
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(int i=0;i<sum_tableValues[0].length;i++){
            int num=10+i;
            String rowName=Integer.toString(num)+":00";
            dataset.setValue(sum_tableValues[0][i],"李铉鹏",rowName);
        }

        StandardChartTheme standardChartTheme = new StandardChartTheme("CN") {
            public void apply(JFreeChart chart) {
                chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                super.apply(chart);
            }
        };
        // 设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("宋体", Font.BOLD, 13));
        // 设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 11));
        // 设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋体", Font.PLAIN, 11));

        standardChartTheme.setSmallFont(new Font("宋体", Font.PLAIN, 6));
        // 应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);

        JFreeChart chart= ChartFactory.createLineChart(
        		startDate+"--"+endDate+"累计接尘预测",  //图表标题
                "时间",  //X轴lable
                "接尘量",  //Y轴lable
                dataset, //数据集
                PlotOrientation.VERTICAL, //图表放置模式水平/垂直
                true, //显示lable
                false,  //显示提示
                false //显示urls
        );


        CategoryPlot plot = chart.getCategoryPlot();
        //TODO
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();// 设置样式
        renderer.setSeriesStroke(0, new BasicStroke(2.0F));//设置折线大小
        renderer.setSeriesPaint(0, Color.GREEN);//绿色
        //TODO
        plot.setRangeGridlinesVisible(true); // 是否显示格子线
        plot.setBackgroundAlpha(0.5f); // 设置背景透明度
        // 设置背景颜色
        plot.setBackgroundPaint(Color.WHITE);
        //todo
        Color c = new Color(44, 177,251);//这里可以设置颜色的rgb
        plot.setRangeGridlinePaint(Color.black);
        plot.setOutlinePaint(c);
        //plot.setOutlinePaint(Color.green);

        CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot.getRenderer();

//        // 显示数据值
//        DecimalFormat decimalformat1 = new DecimalFormat("##.##");// 数据点显示数据值的格式
//        lineandshaperenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
//                "{2}", decimalformat1));// 设置数据项标签的生成器
//        lineandshaperenderer.setBaseItemLabelsVisible(true);// 基本项标签显示

        chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        chart.setBackgroundPaint(new Color(238,238,238));

        ChartPanel cp=new ChartPanel(chart);
        cp.setBounds(30,150,400,400);
        sum_dust_graph_window.setContentPane(cp);
    }

    public void analyse_sum_dust(){

        //获得用户输入日期
    	startDate=input_date_box.getText();
    	endDate=input_date_box2.getText();
        
    	// 天数
        LocalDate startDay = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDay = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int days = (int) startDay.until(endDay,ChronoUnit.DAYS);

        //累积接尘
        ArrayList<Double> accuDustList = totalDust(startDate,endDate);
        Double lastAccuDust=accuDustList.get(accuDustList.size()-1);
        
        Double[][] sum_tableValues  = new Double[1][accuDustList.size()];
        
        for(int i=0;i<accuDustList.size();i++) {
        	sum_tableValues[0][i]=accuDustList.get(i);
        }

        //安全周期
        int willSaveDays = 0;
        if(days < 8524) {
        	willSaveDays = 8524-days;
        }
         
        
        JPanel jp=new JPanel();    //创建面板
        jp.setBounds(100,200,300,200);
        //todo
        Color c = new Color(122,187,218);//这里可以设置颜色的rgb
        jp.setBackground(c);
        //jp.setBackground();
        //todo
        //标签及combobox
        JLabel label1=new JLabel("工作天数："+Integer.toString(days)+"  累积接尘量："+Double.toString(lastAccuDust));    //创建标签
        label1.setFont(new Font("宋体",Font.PLAIN,15));
        jp.add(label1);
        
        dust_analyse_window.add(jp);
        dust_analyse_window.validate();
        draw_sum_dust_graph2(sum_tableValues);    
    }

    public void open_sensor_video() {
    	//获得用户输入查询序号
    	sensorNum=choose_sensorNumber_box.getSelectedIndex();
    	
    	//获得路径
    	String path="";
    	
 	    try {
		    // 打开当前文件
		    Desktop.getDesktop().open(new File(path));
		} catch (IOException e) {
		    e.printStackTrace();
		}
    	
    }
    
    public void openCamera() {
    	Webcam webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(true);
		
    	camera_window=new JFrame("监控摄像头");
    	camera_window.setLocation(820,20);
    	camera_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		camera_window.add(panel);
		camera_window.setResizable(true);
		camera_window.pack();
    	camera_window.setVisible(true);
    	camera_window.validate();
    	
	}



}












