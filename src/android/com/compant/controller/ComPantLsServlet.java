package android.com.compant.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.combid.controller.ComGroupEditController;
import com.combid.controller.GroupEditWS;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.com.combid.model.ComBidService;
import android.com.combid.model.ComBidVO;
import android.com.compant.model.ComPantLsService;
import android.com.compant.model.ComPantLsVO;
import android.com.member.model.MemberService;
import android.com.member.model.MemberVO;

public class ComPantLsServlet extends HttpServlet {
	private static final String contentType = "text/html;charset=UTF-8";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		ComPantLsService comPantSvc = new ComPantLsService();

		Gson gson = new Gson();
		String outStr = "";

		String action = req.getParameter("action");
		System.out.println("參數指令是：" + action);

		/** 比對動作 **/
		if ("isAlreadyIn".equals(action)) {
			// 比對她是否參加過此案件團購
			String memId = req.getParameter("memId");
			String comId = req.getParameter("comId");
			outStr = String.valueOf(comPantSvc.isAlreadyInPartList(memId, comId));
			System.out.println("輸出物" + outStr);

		} else if ("selectNumOfPeople".equals(action)) {
			// 查看以參加此案件人數
			String comId = req.getParameter("comId");
			Integer NumOfPeople = comPantSvc.selectAllPartList(comId).size();
			outStr = String.valueOf(NumOfPeople);
			System.out.println("輸出物" + outStr);

		} else if ("insertMemInList".equals(action)) {
			// 加入團購！
			ComPantLsVO comPantsvo = gson.fromJson(req.getParameter("comPantLs"), ComPantLsVO.class);
			outStr = String.valueOf(comPantSvc.insert(comPantsvo));
			System.out.println("輸出物" + outStr);

		}else if ("selectAllMemInCase".equals(action)) {
			Gson gson2=new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
			String comId = req.getParameter("comid");
			List<ComPantLsVO> list=comPantSvc.selectAllPartList(comId);
			MemberService memSvc=new MemberService();
			List<MemberVO> memList=new ArrayList();
			for(ComPantLsVO  comP:list) {
			   MemberVO member=memSvc.findById(comP.getMem_id());
			   memList.add(member);
			}
			outStr = gson2.toJson(memList);
		
	
			System.out.println("輸出物" + outStr);

		}else if ("updateSts".equals(action)) {
			String memId = req.getParameter("memId");
			String comId = req.getParameter("comId");
			
				comPantSvc.updateSts(memId, comId);
				JSONObject job=new JSONObject();
				JSONArray joa=new JSONArray();
				try {
					job.put("type", "BuyerCheckReply");
					job.put("reponse","true");
					job.put("mem_id", "123");
					job.put("nof_cnt","111");
					joa.put(job);
					GroupEditWS gew=new GroupEditWS();
					gew.broadcastInSameParty(joa.toString(), comId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		


		}
		res.setContentType(contentType);
		PrintWriter out = res.getWriter();
		System.out.println("結果:" + outStr);
		out.print(outStr);
		out.close();

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

}
