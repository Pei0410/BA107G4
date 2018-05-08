package timeCount;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;

import com.com.model.ComDaoJdbc;
import com.com.model.ComVO;

public class TIMETEST {
   public static void main(String args[]){
	   ComDaoJdbc cdj=new ComDaoJdbc();
	  
	   ComVO cv=cdj.select("COM001");
//        System.out.println(cv.getEdt());
	     TimeCountTool a1=new TimeCountTool();
	     TimeObject to= a1.caculateLeftTime(121311,300000000);
	     System.out.println(to.getLeftTime());
//	     System.out.println(cv.getCom_id()+to.getDay()+"天"+to.getHour()+"小時,"+to.getMinute()+"分");
//         TimeCountTool tct=new TimeCountTool();
//         new Timer().schedule(tct,1000,60000);
	      
	 
   }
}
