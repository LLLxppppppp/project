package DustSystem;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class GeneratePath extends JFrame{
	private ArrayList<PathVertex> allVertex;
    public GeneratePath(ArrayList<PathVertex> allVertex) {
    	this.allVertex = allVertex;
        setSize(400,300);
        this.setLocation(462,560);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setVisible(true);
        this.setBackground(new Color(122,187,218));

    }


    public void paint(Graphics g) {
        int x0=25,y0=35;
        //整体区域
        g.setColor(Color.darkGray);
        g.drawRect(x0,y0,350,250);
   

        //绘制传感器位置
        g.setColor(Color.BLUE);
        int s=3;
        g.drawLine(200-s, 35, 200+s, 35);
        g.drawLine(200, 35-s, 200, 35+s);
        g.drawLine(25-s, 160, 25+s, 160);
        g.drawLine(25, 160-s, 25, 160+s);
        g.drawLine(200-s, 285, 200+s, 285);
        g.drawLine(200, 285-s, 200, 285+s);
        g.drawLine(375-s, 160, 375+s, 160);
        g.drawLine(375, 160-s, 375, 160+s);
        //在起点绘制红色十字
        g.setColor(Color.RED);
        s=2;
        g.drawLine(x0-s,y0,x0+s,y0);
        g.drawLine(x0,y0-s,x0,y0+s);
        int[] prePos={x0,y0};
//        g.fillOval(x0, y0, 2, 2);


        for(PathVertex v : allVertex){
            int x1 = 25+(v.getX()-50)/2;
            if(x1<25) x1++;
            else if(x1>375) x1--;
            int y1 = 35+(v.getY()-50)/2;
            if(y1<35) y1++;
            else if(y1>285) y1--;
            g.setColor(Color.RED);
            g.drawLine(x1-s,y1,x1+s,y1);
            g.drawLine(x1,y1-s,x1,y1+s);

            g.setColor(new Color(225,204,219));
            g.drawLine(prePos[0],prePos[1],x1,y1);
            prePos[0] = x1;
            prePos[1] = y1;
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //休息一秒
        }
        return;

    }

    public int[] Generate_Path_Data(int x,int y,int leftBound,int rightBound,int topBound,int bottomBound){
        Random r=new Random();
        //边界规定随机数生成范围
        int xx=r.nextInt(400);
        while(x+(xx-200)<leftBound||x+(xx-200)>rightBound) {
            xx = r.nextInt(400);
        }
        int yy=r.nextInt(400);
        while(y+(yy-200)<topBound||y+(yy-200)>bottomBound){
            yy=r.nextInt(400);
        }

        int x1=x+(xx-200);
        int y1=y+(yy-200);
        return new int[]{x1, y1};
    }

    public static void main(String[] args) {
     //   new GeneratePath().setVisible(true);
    }
}

