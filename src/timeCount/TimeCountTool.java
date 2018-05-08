package timeCount;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.com.model.ComDaoJdbc;
import com.com.model.ComService;
import com.com.model.ComVO;
import com.combid.model.ComBidService;
import com.comnof.controller.NofWS;
import com.comnof.model.ComNofService;
import com.comnof.model.ComNofVO;
import com.compant.model.ComPantLsService;
import com.compant.model.ComPantLsVO;

public class TimeCountTool extends TimerTask implements ServletContextListener {
	private static final long SEC=1000;
	private static final long MIN=60000;
	private static final long HOUR=3600000;
	private static final long DAY=86400000;
	private static TimeCounter timecounter;
    private int count;
    private static Timer timer; 
    ComService comservice=new ComService();
    NofWS nofws=new NofWS();
    ComPantLsService cps=new ComPantLsService();
    ComBidService cbs=new ComBidService();
	ComNofService cns=new ComNofService();
    TimeWs tw=new TimeWs();
    
	public static TimeCounter getTimecounter() {
		return timecounter;
	}

	public void setTimecounter(TimeCounter timecounter) {
		this.timecounter = timecounter;
	}

	
	
	public TimeCountTool(){
		if(timecounter==null){
			timecounter=TimeCounter.getInstance();
		}
	}
	
	public TimeObject caculateLeftTime(long beginTime,long endTime){
		TimeObject tob=new TimeObject();
		long interval=(endTime)-(beginTime);
		tob.setLeftTime(interval);
		//剩餘天數
		return tob;
	}
	
	public boolean minusTime(TimeObject timeObject){
	long now=new Date().getTime();

		if((timeObject.getLeftTime()-now)<=0){
			
			return true;
		}
		else{
		
			return false;
		}
	}
	
	//加入方式
	public void add(ComVO cv){
	    TimeObject to=this.caculateLeftTime(cv.getCom_e_dt().getTime(), cv.getCom_s_dt().getTime());
	    if(this.minusTime(to)){
	    	timecounter.getCountDownMap().put(cv.getCom_id(), cv);
	    }
	}
	//刪除方式
	public void remove(ComVO cv){
		String com_id=cv.getCom_id();
		if(timecounter.getCountDownMap().containsKey(com_id)){
			timecounter.getCountDownMap().remove(com_id);	
		}
	}
	
	@Override
	public void  run() throws RuntimeException{
	try {	
		List<ComVO> TuanGoMap=comservice.selectTuanGoToCount();
		List<ComVO> DaiGOMap=comservice.selectDaiGoToCount();
		
		for(ComVO cv:TuanGoMap) {
			SimpleDateFormat sdf=new SimpleDateFormat();
			Long now=new Date().getTime();
			Long cvDate=cv.getCom_e_dt().getTime();
	
			if(cvDate<=now) {
				
				    System.out.println(cv.getCom_sts());
			      if(TimeWs.OnlineMap!=null) {
					 tw.countDownNotifi2(cv.getCom_id());
			      }
				
			}
		}
		
		for(ComVO cv2:DaiGOMap) {
			Long now=new Date().getTime();
			Long tuangoDate=cv2.getLmt_m_dt().getTime();
			
				if(tuangoDate<now) {
					
				if(TimeWs.OnlineMap!=null) {	
				 tw.notifiToVote(cv2.getCom_id());
				}
				}
			
			}
		
	}catch(Exception e) {
		throw new RuntimeException(e);
	}
		
		
		
		
		
		
		
//		try {
//			ComPantLsService compantlsservice=new ComPantLsService();
//            ComService comservice=new ComService();
//			ComVO cv=null;
//			Map<String,ComVO> map=timecounter.getCountDownMap();
//			Map<String,ComVO> DaiGoCountDownMap=timecounter.getDaiGoCountDownMap();
//			Set<String> set=map.keySet();
//           //團購案件
//			for(String s2:set){
//				String s=s2.toString();
//				boolean a=minusTime(timecounter.getCountDownMap().get(s).getTimeObject());
//		
//				if(!a){
////					System.out.println("減少時間:"+timecounter.getCountDownMap().get(s).getCom_id());
////					System.out.println(timecounter.getCountDownMap().get(s).getTimeObject().getDay()+"天");
////					System.out.println(timecounter.getCountDownMap().get(s).getTimeObject().getHour()+"小時");
////					System.out.println(timecounter.getCountDownMap().get(s).getTimeObject().getMinute()+"分");
//				}else if(a){
//					
////					System.out.println("移除:"+timecounter.getCountDownMap().get(s).getCom_id());
////					System.out.println(timecounter.getCountDownMap().get(s).getTimeObject().getLeftTime());
//					cv=timecounter.getCountDownMap().get(s);
//					if(cv.getCom_min_num()<=compantlsservice.selectAllinCase(s).size()){
//					this.getTimecounter().getVoteMap().put(cv.getCom_id(), cv);
//					TimeWs timews=new TimeWs();
//					timews.countDownNotifi2(s2);
//					cv.setCom_sts("等待主辦人編輯代購訊息");
//					//將剩餘時間切換成代購倒數
//					cv.setLmt_m_dt(cv.getCom_e_dt());
//					timecounter.getCountDownMap().remove(s);
//					timecounter.getComservice().update(cv);
//					
//					}else{
//						//下架通知
//					
//						List<ComPantLsVO> list=cps.selectAllinCase(cv.getCom_id());
//						
//						for(ComPantLsVO cpl:list){
//						 ComNofVO cnv=new ComNofVO();
//						 
//						 cnv.setMem_id(cpl.getMem_id());
//						 cnv.setNof_tit("下架通知");
//						 cnv.setNof_cnt("您參與的案件編號:"+cv.getCom_id()+"因在時間限制內未招募足夠人數,已經被系統下架,請再接再厲^^");
//						 cnv.setNof_sts("未讀");
//						 cns.insert(cnv);
//						 nofws.SendNofToSomeOne(cpl.getMem_id(), cnv);
//						}
//						cv.setLmt_m_dt(cv.getCom_e_dt());
//						cv.setCom_sts("下架");
//						timecounter.getCountDownMap().remove(s);
//						timecounter.getComservice().update(cv);
//					
//					
//					}
//					
//
//				}
//			}
//			//代購案件
//			
//			Set<String> DaiGoCountKeySet=DaiGoCountDownMap.keySet();
//			
//			for(String s:DaiGoCountKeySet) {
//				ComVO DaiGoCV=DaiGoCountDownMap.get(s);
//				//如果回傳負數代表時間到了
//				if(!this.minusTime(DaiGoCV.getTimeObject())) {
////					System.out.println("id:_"+DaiGoCV.getCom_id()+"剩餘時間:_"+DaiGoCV.getTimeObject().getLeftTime());
//				}
//				else {
//					//拿出已經倒數完的案件
//					String DaiGoCV_id=DaiGoCV.getCom_id();
//					//有人參加
//					if(cbs.selectAll(DaiGoCV_id)!=null){
////					System.out.println("已經跑到else,    id:_"+DaiGoCV.getCom_id()+"剩餘時間:_"+DaiGoCV.getTimeObject().getLeftTime());	
//					DaiGoCV.setCom_sts("準備進行投票");
//					
//					TimeWs timews=new TimeWs();
//					timews.notifiToVote(s.toString());
//					
//					List<ComPantLsVO> list=cps.selectAllinCase(DaiGoCV.getCom_id());
//					
//					for(ComPantLsVO cpl:list){
//					 ComNofVO cnv=new ComNofVO();
//					 cnv.setMem_id(cpl.getMem_id());
//					 cnv.setNof_tit("投票通知");
//					 cnv.setNof_cnt("您參與的案件:"+DaiGoCV_id+"已經開始投票了,請快快上線參與投票");
//					 cnv.setNof_sts("未讀");
//					 nofws.SendNofToSomeOne(cpl.getMem_id(), cnv);
//					}
//					
//			
//					}
//					//沒人參加
//					else {
//						DaiGoCV.setCom_sts("下架");
//						List<ComPantLsVO> list=cps.selectAllinCase(DaiGoCV_id);
//						
//						for(ComPantLsVO cpl:list){
//							 ComNofVO cnv=new ComNofVO();
//							 cnv.setMem_id(cpl.getMem_id());
//							 cnv.setNof_tit("投票通知");
//							 cnv.setNof_cnt("您參與的案件:"+DaiGoCV_id+",在投票時限內,無人參與投票,已經進入下架程序,詳情請見F&Q");
//							 cnv.setNof_sts("未讀");
//							 nofws.SendNofToSomeOne(cpl.getMem_id(),cnv);
//							}
//						 
//					}
//					DaiGoCountDownMap.remove(s);
//					new TimeCountTool().getTimecounter().getComservice().update(DaiGoCV);
//				}
//			}
//			
//		}//try
//		catch(Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
		
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
//		HashMap<String, ComVO> map=this.timecounter.getCountDownMap();
//		for(String s:map.keySet()){
//			try{
//				ComService cdj=new ComService();
//				cdj.insert(map.get(s));
//				
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			
//		}
		timer.cancel();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("start");
		timer=new Timer();
		timer.schedule(this,1000, 3000);
		event.getServletContext().setAttribute("TimeCountTool", this);
	}
	
	public List<ComVO> toList(){
		List<ComVO> list=new ArrayList<ComVO>();
		for(Object ob:this.getTimecounter().getCountDownMap().keySet()){
			list.add(this.getTimecounter().getCountDownMap().get(ob));
		}
		return list;
	}
	

}
