package DustSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.PreparableStatement;



//服务器将客户端上传的数据放在数据库
public class Database {
	/**
	 * Database类的属性 共4个
	 */
	//MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/javatest?useSSL=false&serverTimezone=UTC";
    // 数据库的用户名与密码
    static final String USER = "root";
    static final String PASSWORD = "cl960811";
    
	/**
	 * 连接数据库
	 */
	public static Connection accessDB() {
        Connection connection=null;
        // 加载驱动
        try {
        	Class.forName(JDBC_DRIVER);
            System.out.println("Database...");
            // 创建连接
            connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
        	System.out.println("DatabaseError！");
            e.printStackTrace();
        }
		return connection;
	}
	
	/**
	 * 清空sensor表
	 */
	public void clearSensor(int sensorNum) {
		AtomicReference<Connection> conn = new AtomicReference<>(accessDB());
		String updateStr=null;
		updateStr = "delete from sensor"+sensorNum;
    	PreparedStatement st;
    	try {
    		st = conn.get().prepareStatement(updateStr);
        	int i=st.executeUpdate();
            if(i>0){
//    			System.out.println("清空sensor成功！");
    		}else{
    			System.out.println("clear sensor error！");
    		}
            st.close();
            conn.get().close();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
	}
	/**
	 * 清空worker表
	 */
	public void clearWorker() {
		AtomicReference<Connection> conn = new AtomicReference<>(accessDB());
		String updateStr=null;
		updateStr = "delete from worker";
    	PreparedStatement st;
    	try {
    		st = conn.get().prepareStatement(updateStr);
        	int i=st.executeUpdate();
            if(i>0){
//    			System.out.println("清空worker成功！");
    		}else{
    			System.out.println("clear worker error！");
    		}
            st.close();
            conn.get().close();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
	}
	/**
	 * 插入sensor信息
	 */
	public void insertSensor(int sensorNum,SensorData sensorObj) {
		AtomicReference<Connection> connection = new AtomicReference<>(accessDB());
		String updateStr=null;
		updateStr = "INSERT INTO sensor"+sensorNum+" VALUES(?,?,?,?,?,?)";
    	PreparedStatement st;
    	try {
    		st = connection.get().prepareStatement(updateStr);
    		// 日期转string
    		LocalDate date = sensorObj.getDate();
    		String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    		// 时间转string
    		LocalTime time = sensorObj.getTime();
    		String timeStr = time.format(DateTimeFormatter.ofPattern("HH:mm"));    	        
    		st.setObject(1, dateStr);
        	st.setObject(2, timeStr);
        	st.setObject(3, sensorObj.getDensity());
        	st.setObject(4, sensorObj.getTemperature());
        	st.setObject(5, sensorObj.getHumidity());
        	st.setObject(6, sensorObj.getWind());
        	int i=st.executeUpdate();
            if(i>0){
//    			System.out.println("插入成功！");
    		}else{
    			System.out.println("插入失败！");
    		}
            st.close();
            connection.get().close();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
	}
	/**
	 * 插入worker信息
	 */
	public void insertWorker(PathVertex workerObj) {
		AtomicReference<Connection> connection = new AtomicReference<>(accessDB());
		String updateStr = "INSERT INTO worker VALUES(?,?,?,?)";
    	PreparedStatement st;
    	try {
    		st = connection.get().prepareStatement(updateStr);
    		// 日期转string
    		LocalDate date = workerObj.getDate();
    		String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    		// 时间转string
    		LocalTime time = workerObj.getTime();
    		String timeStr = time.format(DateTimeFormatter.ofPattern("HH:mm"));    	 
    		st.setObject(1, dateStr);
        	st.setObject(2, timeStr);
        	st.setObject(3, workerObj.getX());
        	st.setObject(4, workerObj.getY());
        	int i=st.executeUpdate();
            if(i>0){
//    			System.out.println("插入成功！");
    		}else{
    			System.out.println("insert error！");
    		}
            st.close();
            connection.get().close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	/**
	 * 打印sensor全部信息
	 */
	public ArrayList<SensorData> getSensor(int sensorNum) {
		ArrayList<SensorData> allSensorData = new ArrayList<SensorData>();
		AtomicReference<Connection> connection = new AtomicReference<>(accessDB());
		String queryStr = null;
		queryStr = "select * from sensor"+sensorNum;
        PreparedStatement st;
        try {
            st = connection.get().prepareStatement(queryStr);
            ResultSet rs = st.executeQuery();
            int col = rs.getMetaData().getColumnCount();  // col=6
//            System.out.println("日期        \t时间\t粉尘浓度\t温度\t湿度\t风力\t");
            while (rs.next()) {
                LocalDate sensorDate=null;
                LocalTime sensorTime=null;
                Double Density = null;
                Double Temperature = null;
                int Humidity = 0;
                int Wind = 0;
                for (int i = 1; i <= col; i++) {
                	switch(i) {
	                	case 1:{
	                		sensorDate=LocalDate.parse(rs.getString(i), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	                		break;
	                	}
	                	case 2:{
	                		sensorTime=LocalTime.parse(rs.getString(i), DateTimeFormatter.ofPattern("HH:mm"));
	                		break;
	                	}
	                	case 3:{
	                		Density=rs.getDouble(i);
	                		break;
	                	}
	                	case 4:{
	                		Temperature=rs.getDouble(i);
	                		break;
	                	}
	                	case 5:{
	                		Humidity=rs.getInt(i);
	                		break;
	                	}
	                	case 6:{
	                		Wind=rs.getInt(i);
	                		break;
	                	}
	                	default:{
	                		break;
	                	}
                	}
//                    System.out.print(rs.getString(i) + '\t');
                }
                SensorData sd=new SensorData(Density,Temperature,Humidity,Wind,sensorDate,sensorTime);
                allSensorData.add(sd);
//                System.out.println();
            }
            st.close();
            connection.get().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allSensorData;
	}
	/**
	 * 打印worker全部信息
	 */
	public static ArrayList<PathVertex> getWorker() {
		ArrayList<PathVertex> allPathVertex=new ArrayList<PathVertex>();
		AtomicReference<Connection> connection = new AtomicReference<>(accessDB());
        String queryStr = "select * from worker";
        PreparedStatement st;
        try {
            st = connection.get().prepareStatement(queryStr);
            ResultSet rs = st.executeQuery();
            int col = rs.getMetaData().getColumnCount();
//            System.out.println("日期        \t时间\tPosX\tPosY\t");
            while (rs.next()) {
            	LocalDate workerDate = null;
            	LocalTime workerTime = null;
                int posX = 0;
                int posY = 0;
                for (int i = 1; i <= col; i++) {
                	switch(i) {
	                	case 1:{
	                		workerDate=LocalDate.parse(rs.getString(i), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	                		break;
	                	}
	                	case 2:{
							//String i1 = i < 10 ? ("0" + i) : i + "";
	                		workerTime=LocalTime.parse(rs.getString(i),DateTimeFormatter.ofPattern("HH:mm:ss"));
	                		break;
	                	}
	                	case 3:{
	                		posX=rs.getInt(i);
	                		break;
	                	}
	                	case 4:{
	                		posY=rs.getInt(i);
	                		break;
	                	}
	                	default:{
	                		break;
	                	}
                	}
//                    System.out.print(rs.getString(i) + '\t');
                }
                PathVertex pv=new PathVertex(posX,posY,workerDate,workerTime);
                allPathVertex.add(pv);
//                System.out.println();
            }
            st.close();
            connection.get().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return allPathVertex;
	}
	/**
	 * 获取特定日期的sensor
	 */
	public ArrayList<SensorData> getSensorByDate(int sensorNum,String date) {
		ArrayList<SensorData> allSensorData = new ArrayList<SensorData>();
		AtomicReference<Connection> connection = new AtomicReference<>(accessDB());
		String queryStr = null;
		queryStr = "select * from sensor"+sensorNum+" where Date=(?)";
        PreparedStatement st;
        try {
            st = connection.get().prepareStatement(queryStr);
            st.setObject(1, date);
            ResultSet rs = st.executeQuery();
            int col = rs.getMetaData().getColumnCount();  // col=6
//            System.out.println("日期        \t时间\t粉尘浓度\t温度\t湿度\t风力\t");
            while (rs.next()) {
                LocalDate sensorDate=null;
                LocalTime sensorTime=null;
                Double Density = null;
                Double Temperature = null;
                int Humidity = 0;
                int Wind = 0; 
                for (int i = 1; i <= col; i++) {
                	switch(i) {
	                	case 1:{
	                		sensorDate=LocalDate.parse(rs.getString(i), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	                		break;
	                	}
	                	case 2:{
	                		sensorTime=LocalTime.parse(rs.getString(i), DateTimeFormatter.ofPattern("HH:mm:ss"));
	                		break;
	                	}
	                	case 3:{
	                		Density=rs.getDouble(i);
	                		break;
	                	}
	                	case 4:{
	                		Temperature=rs.getDouble(i);
	                		break;
	                	}
	                	case 5:{
	                		Humidity=rs.getInt(i);
	                		break;
	                	}
	                	case 6:{
	                		Wind=rs.getInt(i);
	                		break;
	                	}
	                	default:{
	                		break;
	                	}
                	}
//                    System.out.print(rs.getString(i) + '\t');
                }
                SensorData sd=new SensorData(Density,Temperature,Humidity,Wind,sensorDate,sensorTime);
                allSensorData.add(sd);
//                System.out.println();
            }
            st.close();
            connection.get().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allSensorData;
	}
	/**
	 * 获取特定日期的Worker
	 */
	public static ArrayList<PathVertex> getWorkerByDate(String date) {
		ArrayList<PathVertex> allPathVertex=new ArrayList<PathVertex>();
		AtomicReference<Connection> connection = new AtomicReference<>(accessDB());
        String queryStr = "select * from worker where Date=(?)";
        PreparedStatement st;
        try {
            st = connection.get().prepareStatement(queryStr);
            st.setObject(1, date);
            ResultSet rs = st.executeQuery();
            int col = rs.getMetaData().getColumnCount();
//            System.out.println("日期        \t时间\tPosX\tPosY\t");
            while (rs.next()) {
            	LocalDate workerDate = null;
            	LocalTime workerTime = null;
                int posX = 0;
                int posY = 0;
                for (int i = 1; i <= col; i++) {
                	switch(i) {
	                	case 1:{
	                		workerDate=LocalDate.parse(rs.getString(i), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	                		break;
	                	}
	                	case 2:{
	                		workerTime=LocalTime.parse(rs.getString(i),DateTimeFormatter.ofPattern("HH:mm:ss"));
	                		break;
	                	}
	                	case 3:{
	                		posX=rs.getInt(i);
	                		break;
	                	}
	                	case 4:{
	                		posY=rs.getInt(i);
	                		break;
	                	}
	                	default:{
	                		break;
	                	}
                	}
//                    System.out.print(rs.getString(i) + '\t');
                }
                PathVertex pv=new PathVertex(posX,posY,workerDate,workerTime);
                allPathVertex.add(pv);
//                System.out.println();
            }
            st.close();
            connection.get().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return allPathVertex;
	}
	/**
	 * 查询同一个日期时间 worker的位置 & sensor的浓度
	 * 返回一个ResultSet 包含日期 时间 x y 浓度1 浓度2 浓度3 浓度4
	 * 用于计算接尘量
	 */
	public ResultSet workerDust(String date) {
		AtomicReference<Connection> connection = new AtomicReference<>(accessDB());
		//todo
        String queryStr = "SELECT worker.Date,worker.Time,worker.PosX,worker.PosY,sensor1.density,sensor2.density,sensor3.density,sensor4.density \r\n"
        		+ "FROM sensor1,sensor2,sensor3,sensor4,worker\r\n"
        		+ "WHERE worker.Date=(?) AND sensor1.date=(?) AND sensor2.date=(?) AND sensor3.date=(?) AND sensor4.date=(?)\r\n"
        		+ "AND worker.Time=sensor1.time\r\n"
        		+ "AND worker.Time=sensor2.time\r\n"
        		+ "AND worker.Time=sensor3.time\r\n"
        		+ "AND worker.Time=sensor4.time";
        PreparedStatement st;
        ResultSet rs = null ;
        try {
            st = connection.get().prepareStatement(queryStr);
            st.setObject(1, date);
            st.setObject(2, date);
            st.setObject(3, date);
            st.setObject(4, date);
            st.setObject(5, date);
            rs = st.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return rs;
	}
	/**
	 * 多维统计分析函数 所有日期
	 */
	public ArrayList<SensorData> queryAndSort(int id,double density,double temperature,int humidity,int wind,
			String compare) {
		ArrayList<SensorData> allsData = new ArrayList<SensorData>();
		AtomicReference<Connection> connection = new AtomicReference<>(accessDB());
		// 比较条件是小于时
		if(compare.equals("<")) {
			if(density == -1) {
				density = 10;
			}
			if(temperature == -1) {
				temperature = 40;
			}
			if(humidity == -1) {
				humidity = 70;
			}
			if(wind == -1) {
				wind = 17;
			}
		}
		
        String queryStr = "SELECT * FROM sensor"+ id + " WHERE density " + compare +"(?) AND  temperature" 
        					+ compare + "(?) AND humidity" + compare + "(?) AND wind" + compare +"(?)";
     
        System.out.println(queryStr);
        PreparedStatement st;
        ResultSet rs = null;
        try {
        	// 根据用户输入和选择 执行查询
            st = connection.get().prepareStatement(queryStr);
            st.setObject(1, density);
            st.setObject(2, temperature);
            st.setObject(3, humidity);
            st.setObject(4, wind);
            rs = st.executeQuery();
            
            // 处理查询结果
            LocalDate sensorDate=null;
            LocalTime sensorTime=null;
            Double Density = null;
            Double Temperature = null;
            int Humidity = 0;
            int Wind = 0;
            
            while(rs.next()) {
            	sensorDate=LocalDate.parse(rs.getString(1), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            	sensorTime=LocalTime.parse(rs.getString(2), DateTimeFormatter.ofPattern("HH:mm"));
            	Density=rs.getDouble(3);
            	Temperature=rs.getDouble(4);
            	Humidity=rs.getInt(5);
            	Wind=rs.getInt(6);
            	SensorData sd = new SensorData(Density, Temperature, Humidity, Wind, sensorDate, sensorTime);
            	allsData.add(sd);
            }
            st.close();
            connection.get().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allsData;
	}
	/**
	 * 多维度统计分析函数 特定日期
	 */
	public ArrayList<SensorData> queryAndSortByDate(int id,double density,double temperature,int humidity,int wind,
			String compare, String startDate, String endDate, String sortKey, String sortType) {
		ArrayList<SensorData> allsData = new ArrayList<SensorData>();
		AtomicReference<Connection> connection = new AtomicReference<>(accessDB());
		// 比较条件是小于时
		if(compare.equals("<")) {
			if(density == -1) {
				density = 10;
			}
			if(temperature == -1) {
				temperature = 40;
			}
			if(humidity == -1) {
				humidity = 70;
			}
			if(wind == -1) {
				wind = 17;
			}
		}
		
        String queryStr = "SELECT * FROM sensor"+ id + " WHERE density " + compare +"(?) AND  temperature" 
        					+ compare + "(?) AND humidity" + compare + "(?) AND wind" + compare +"(?)"
        					+ " AND date>=(?)"  + " AND date<=(?)"
        					+ " ORDER BY "+ sortKey + " " + sortType;
     
        System.out.println(queryStr);
        PreparedStatement st;
        ResultSet rs = null;
        try {
        	// 根据用户输入和选择 执行查询
            st = connection.get().prepareStatement(queryStr);
            st.setObject(1, density);
            st.setObject(2, temperature);
            st.setObject(3, humidity);
            st.setObject(4, wind);
            st.setObject(5, startDate);
            st.setObject(6, endDate);
            rs = st.executeQuery();
            
            // 处理查询结果
            LocalDate sensorDate=null;
            LocalTime sensorTime=null;
            Double Density = null;
            Double Temperature = null;
            int Humidity = 0;
            int Wind = 0;
            
            while(rs.next()) {
            	sensorDate=LocalDate.parse(rs.getString(1), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            	sensorTime=LocalTime.parse(rs.getString(2), DateTimeFormatter.ofPattern("HH:mm"));
            	Density=rs.getDouble(3);
            	Temperature=rs.getDouble(4);
            	Humidity=rs.getInt(5);
            	Wind=rs.getInt(6);
            	SensorData sd = new SensorData(Density, Temperature, Humidity, Wind, sensorDate, sensorTime);
            	allsData.add(sd);
            }
            st.close();
            connection.get().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allsData;
	}
	
	/**
	 * 主函数
	 * @param args
	 */
	public static void main(String[] args) {
		Database mydatabase=new Database();
		//to do
		//监控sensor 打印sensor全部信息
//		ArrayList<SensorData> asd=new ArrayList<SensorData>();
//		asd=mydatabase.getSensor(1);
//		for(SensorData sd : asd) {
//			System.out.println(sd);
//		}

//		监控worker 打印worker全部信息
		ArrayList<PathVertex> apv=new ArrayList<PathVertex>();
		apv=mydatabase.getWorker();
		for(PathVertex pv : apv) {
			System.out.println(pv);
		}

//		多维度统计分析
//		ArrayList<SensorData> asd=new ArrayList<SensorData>();
//		asd=mydatabase.queryAndSortByDate(1, -1, -1, -1, -1, ">","2021-01-02", "2021-01-03", "pressure", "ASC");
//		for(SensorData sd : asd) {
//			System.out.println(sd);
//		}
	}
}
