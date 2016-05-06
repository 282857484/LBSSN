package pub.myserverjsontool;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pri.z.mydb.PushMessage;
import pri.z.mydb.RelationActivity;
import pri.z.show.MyMessage;
import pri.z.sqlite.ContentValuesChange;
import pri.z.sqlite.InsertObject;
import pri.z.sqlite.SQLInfo;
import pri.z.sqlite.SQLiteProtocol;
import pri.z.sqlite.sqlSecurityThread;
import pri.z.utils.MomentBaseInfo;
import pri.z.utils.getPushDataFromDB;
import pub.application.SEMapApplication;
import pub.infoclass.db.ActivityDiscussSelectData;
import pub.infoclass.db.ActivitySelectData;
import pub.infoclass.db.RelationSelectData;
import pub.infoclass.db.UserInfoSelectData;
import pub.infoclass.myserver.protocol.CheckVersionAndOtherInfo_S;
import pub.infoclass.myserver.protocol.Validate_S;
import pub.infoclass.myserver.protocol.addActivityDiscuss_S;
import pub.infoclass.myserver.protocol.addActivity_S;
import pub.infoclass.myserver.protocol.addRelation_S;
import pub.infoclass.myserver.protocol.addUserFeedback_S;
import pub.infoclass.myserver.protocol.addUser_S;
import pub.infoclass.myserver.protocol.changFileNameOrder_S;
import pub.infoclass.myserver.protocol.changRelation_S;
import pub.infoclass.myserver.protocol.changUserInfo_S;
import pub.infoclass.myserver.protocol.changeCode_S;
import pub.infoclass.myserver.protocol.getActivityDiscussList_S;
import pub.infoclass.myserver.protocol.getActivityInfo_S;
import pub.infoclass.myserver.protocol.getRelaion_S;
import pub.infoclass.myserver.protocol.getUserInfo_S;
import pub.infoclass.myserver.protocol.loginUser_S;
import pub.infoclass.myserver.protocol.protocolfromserver;
import pub.infoclass.myserver.protocol.updateActivityOpinion_independent_S;
import pub.infoclass.pushservice.ACK;
import pub.infoclass.pushservice.h_protocol_pusher;
import pub.netservice.NetService;
import pub.netservice.handleNetSendReceiveThread_TCP;
import pub.netservice.statusRow;
import pub.util.FormatTime;
import pub.util.MsgReplace;
import pub.util.notification;
import android.content.ContentValues;
import android.util.Log;

public class MyJsonTool {

	/**
	 * 这里只能处理与我们本地服务器相配套的JSON数据格式
	 * 
	 * @param dataString
	 * @param statusMap
	 * @return 相应的对象
	 */
	public static List<ObjAndWhat> dealJSON(String dataString) {
		// TODO Auto-generated method stub
		List<ObjAndWhat> owList = new ArrayList<ObjAndWhat>();
		try {
			Log.e("MyJsonTool", dataString);
			Log.e("MyJsonTool","MyJsonTool");
			JSONArray jsonArray = new JSONArray(dataString);
			Log.e("JSONOARRAY", jsonArray.toString());
			int jsonArraySize = jsonArray.length();
			for (int fence = 0; fence < jsonArraySize; fence++) {
				JSONObject jsonObj = (JSONObject) jsonArray.get(fence); 
				
				int p = jsonObj.getInt("p");
				
				ObjAndWhat ow = new ObjAndWhat();
				switch (p) {
				case protocolfromserver.Validate_S:
					Validate_S vs = new Validate_S();
					vs.setUploadTime(jsonObj.getString("UploadTime"));
					vs.setMark(jsonObj.getString("Mark"));
					vs.setUserID(jsonObj.getString("UserID"));
					vs.setSendPhone(jsonObj.getString("sendPhone"));
					vs.setSendCode(jsonObj.getString("sendCode"));
					vs.setSendMark(jsonObj.getInt("sendMark"));
					//注册前用户端保存的AccountNumber都为0
					if ("0".equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(vs.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(vs.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(vs.getUploadTime()).isSuccessed = true;

								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(vs.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(vs);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
				case protocolfromserver.addUser_S:
					addUser_S adduser = new addUser_S();
					adduser.setUploadTime(jsonObj.getString("UploadTime"));
					adduser.setMark(jsonObj.getString("Mark"));
					adduser.setUserID(jsonObj.getString("UserID"));
					//注册前用户端保存的AccountNumber都为0
					if ("0".equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(adduser.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(adduser.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(adduser.getUploadTime()).isSuccessed = true;

								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(adduser.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(adduser);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
				break;
				
				case protocolfromserver.loginUser_S:
					loginUser_S ls = new loginUser_S();
					ls.setUploadTime(jsonObj.getString("UploadTime"));
					ls.setMark(jsonObj.getString("Mark"));
					ls.setUserID(jsonObj.getString("UserID"));
					//登录前用户端保存的AccountNumber都为0
					if ("0".equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(ls.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(ls.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(ls.getUploadTime()).isSuccessed = true;

								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(ls.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(ls);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
				
				case protocolfromserver.getUserInfo_S:
					getUserInfo_S us = new getUserInfo_S();
					us.setUploadTime(jsonObj.getString("UploadTime"));
					us.setMark(jsonObj.getString("Mark"));
					us.setUserID(jsonObj.getString("UserID"));
					us.setUserInfoList(jsonObj.getString("UserInfoList"));
					//
					if (us.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(us.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(us.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(us.getUploadTime()).isSuccessed = true;

								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(us.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(us);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
					
				case protocolfromserver.getActivityInfo_S:
					getActivityInfo_S gs = new getActivityInfo_S();
					gs.setUploadTime(jsonObj.getString("UploadTime"));
					gs.setMark(jsonObj.getString("Mark"));
					gs.setActivityInfoList(jsonObj.getString("ActivityInfoList"));
					gs.setUserID(jsonObj.getString("UserID"));
					
					if (gs.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(gs.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(gs.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(gs.getUploadTime()).isSuccessed = true;

//								ow.setReplyTo(pub.netservice.NetService.statusMap
//										.get(gs.getUploadTime())
//										.getMsgReplace().msgReplyTo);
								ow.setReplyTo(SEMapApplication.currentMessenger);
								ow.setObj(gs);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
					
					
				case protocolfromserver.getActivityDiscussList_S:
					getActivityDiscussList_S gds = new getActivityDiscussList_S();
					gds.setUploadTime(jsonObj.getString("UploadTime"));
					gds.setMark(jsonObj.getString("Mark"));
					//用Content判定是否有评论数据
					gds.setContent(jsonObj.getString("Content"));
					gds.setActivityDiscussList(jsonObj.getString("ActivityDiscussList"));
					gds.setUserID(jsonObj.getString("UserID"));
					if (gds.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(gds.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(gds.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(gds.getUploadTime()).isSuccessed = true;

								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(gds.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(gds);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
					
				//	增加活动评论
				case protocolfromserver.addActivityDiscuss_S:
					addActivityDiscuss_S aads = new addActivityDiscuss_S();
					aads.setUploadTime(jsonObj.getString("UploadTime"));
					aads.setMark(jsonObj.getString("Mark"));
					aads.setUserID(jsonObj.getString("UserID"));
					aads.setDiscussID(jsonObj.getString("DiscussID"));
					if (aads.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(aads.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(aads.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(aads.getUploadTime()).isSuccessed = true;

								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(aads.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(aads);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
					//参加/关注活动
				case protocolfromserver.addRelation_S:
					addRelation_S ars = new addRelation_S();
					ars.setUploadTime(jsonObj.getString("UploadTime"));
					ars.setMark(jsonObj.getString("Mark"));
					ars.setStatus(jsonObj.getString("Status"));
					ars.setUserID(jsonObj.getString("UserID"));
					ars.setActivityID(jsonObj.getString("ActivityID"));
//					Log.v("哈哈哈哈", "addRelation_S---   "+ars.toString());
					
					if (ars.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(ars.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(ars.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(ars.getUploadTime()).isSuccessed = true;

								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(ars.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(ars);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
					//活动评分
				case protocolfromserver.updateActivityOpinion_independent_S:
					updateActivityOpinion_independent_S aos = new updateActivityOpinion_independent_S();
					aos.setUploadTime(jsonObj.getString("UploadTime"));
					aos.setMark(jsonObj.getString("Mark"));
					aos.setGotGrade(jsonObj.getString("GotGrade"));
					aos.setTotalFullGrade(jsonObj.getString("TotalFullGrade"));
					aos.setUserID(jsonObj.getString("UserID"));
					if (aos.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(aos.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(aos.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(aos.getUploadTime()).isSuccessed = true;

								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(aos.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(aos);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
					
				case protocolfromserver.getRelaion_S:
					getRelaion_S grs = new getRelaion_S();
					grs.setUploadTime(jsonObj.getString("UploadTime"));
					grs.setMark(jsonObj.getString("Mark"));
					grs.setUserID(jsonObj.getString("UserID"));
					grs.setRelationList(jsonObj.getString("RelationList"));
					grs.setExtraMark(jsonObj.getString("ExtraMark"));
					
//					MyLog.e("哈哈哈哈", "ExtraMark---    "+jsonObj.getString("ExtraMark")
//							+"\nRelationList---    "+jsonObj.getString("RelationList"));
					if(grs.getExtraMark().equals("1")){
						grs.OneUser = jsonObj.getString("OneUser");
						grs.OneActivity = jsonObj.getString("OneActivity");
					}else if(grs.getExtraMark().equals("2")){
						grs.UserIDList = jsonObj.getString("UserIDList");
					}else if(grs.getExtraMark().equals("3")){
						grs.ActivityList = jsonObj.getString("ActivityList");
					}
//					grs.UserIDList = jsonObj.getString("UserIDList");
					
//					pub.netservice.NetService.statusMap.get(grs.getUploadTime()).isSuccessed = true;
//
//			ow.setReplyTo(pub.netservice.NetService.statusMap
//					.get(grs.getUploadTime())
//					.getMsgReplace().msgReplyTo);
//			ow.setObj(grs);
//			ow.setWhat(p);
//			owList.add(ow);
					if (grs.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(grs.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(grs.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(grs.getUploadTime()).isSuccessed = true;

								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(grs.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(grs);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
				case protocolfromserver.changUserInfo_S:
					changUserInfo_S cus = new changUserInfo_S();
					cus.setUploadTime(jsonObj.getString("UploadTime"));
					cus.setUserID(jsonObj.getString("UserID"));
					cus.setMark(jsonObj.getString("Mark"));
					if (cus.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(cus.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(cus.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(cus.getUploadTime()).isSuccessed = true;

								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(cus.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(cus);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
				case protocolfromserver.changeCode_S:
					changeCode_S codes = new changeCode_S();
					codes.setUploadTime(jsonObj.getString("UploadTime"));
					codes.setUserID(jsonObj.getString("UserID"));
					codes.setMark(jsonObj.getString("Mark"));
					if ("0".equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(codes.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(codes.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
								.get(codes.getUploadTime()).isSuccessed = true;
								
								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(codes.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(codes);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
				case protocolfromserver.addUserFeedback_S:
					addUserFeedback_S feed = new addUserFeedback_S();
					feed.setUploadTime(jsonObj.getString("UploadTime"));
					feed.setUserID(jsonObj.getString("UserID"));
					feed.setMark(jsonObj.getString("Mark"));
					if (feed.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(feed.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(feed.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
										.get(feed.getUploadTime()).isSuccessed = true;

								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(feed.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(feed);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
				case protocolfromserver.changRelation_S:
					changRelation_S chanRe = new changRelation_S();
					chanRe.setUploadTime(jsonObj.getString("UploadTime"));
					chanRe.setUserID(jsonObj.getString("UserID"));
					chanRe.setMark(jsonObj.getString("Mark"));
					chanRe.setStatus(jsonObj.getString("Status"));
					chanRe.setActivityID(jsonObj.getString("ActivityID"));
					
//					Log.v("哈哈哈哈", "changRelation_S---   "+chanRe.toString());
					if (chanRe.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(chanRe.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(chanRe.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
								.get(chanRe.getUploadTime()).isSuccessed = true;
								
								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(chanRe.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(chanRe);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;
				case protocolfromserver.CheckVersionAndOtherInfo_S:
		   			CheckVersionAndOtherInfo_S checkVersion =new CheckVersionAndOtherInfo_S();
		   			checkVersion.setUploadTime(jsonObj.getString("UploadTime"));
		   			checkVersion.setUserID(jsonObj.getString("UserID"));
		   			checkVersion.setMark(jsonObj.getString("Mark"));
		   			checkVersion.setVersion(jsonObj.getString("Version"));
		   			checkVersion.setURL(jsonObj.getString("URL"));
		   			checkVersion.setText(jsonObj.getString("Text"));
		   			checkVersion.setUpdateTime(jsonObj.getString("UpdateTime"));
		   			
					if (checkVersion.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(checkVersion.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(checkVersion.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
								.get(checkVersion.getUploadTime()).isSuccessed = true;
								
								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(checkVersion.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(checkVersion);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
		   			break;
				case protocolfromserver.addActivity_S:
					addActivity_S addAct =new addActivity_S();
					addAct.setUploadTime(jsonObj.getString("UploadTime"));
					addAct.setUserID(jsonObj.getString("UserID"));
					addAct.setMark(jsonObj.getString("Mark"));
					addAct.setActivityID(jsonObj.getString("ActivityID"));
		   			
					if (addAct.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(addAct.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(addAct.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
								.get(addAct.getUploadTime()).isSuccessed = true;
								
								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(addAct.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(addAct);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
		   			break;	
				case protocolfromserver.changFileNameOrder_S:
					changFileNameOrder_S chanFile =new changFileNameOrder_S();
					chanFile.setUploadTime(jsonObj.getString("UploadTime"));
					chanFile.setUserID(jsonObj.getString("UserID"));
					chanFile.setMark(jsonObj.getString("Mark"));
					
					if (chanFile.getUserID().equals(
							SEMapApplication.AccountNumber)) {
						if (pub.netservice.NetService.statusMap
								.containsKey(chanFile.getUploadTime()) == true) {
							if (pub.netservice.NetService.statusMap
									.get(chanFile.getUploadTime()).isSuccessed == true) {
								// 相同的包之前已收到
							} else {
								pub.netservice.NetService.statusMap
								.get(chanFile.getUploadTime()).isSuccessed = true;
								
								ow.setReplyTo(pub.netservice.NetService.statusMap
										.get(chanFile.getUploadTime())
										.getMsgReplace().msgReplyTo);
								ow.setObj(chanFile);
								ow.setWhat(p);
								owList.add(ow);
							}
						}
					}
					break;	

				case h_protocol_pusher.PushMessageListObject:
//				case h_protocol_pusher.PushMessageSelectData:
//					Log.e("哈哈哈哈", "MyJsonTool" + jsonObj.toString());
					JSONArray jsonArrayPush = jsonObj.getJSONArray("pmsdList");
//					JSONArray jsonArrayPush = jsonObj.getJSONArray("");
					if(jsonArrayPush == null){
						break;
					}
					if(jsonArrayPush.length() <= 0){
						break;
					}
//					Log.e("哈哈哈哈", "jsonArrayPush.length()----" +jsonArrayPush.length());
					for(int index = 0;index < jsonArrayPush.length();index++){
						JSONObject jsonObjPush = jsonArrayPush.getJSONObject(index);  
						// 判定是否重复
						if (new getPushDataFromDB().IfContainPushID(jsonObjPush
								.getString("PushID"))) {
//							Log.e("哈哈哈哈", "jsonArrayPush.length()--breakbreakbreak--");
							// repeat ,do nothing
							break;
						} else { // 通知---notification
							PushMessage push = new PushMessage();
							//将数据插入数据库中
							String UserID = jsonObjPush.getString("UserID");
//							Log.e("哈哈哈哈", "UserID--- "+UserID);
							String PushMessage = jsonObjPush.getString("PushMessage");
							String PushID = jsonObjPush.getString("PushID");
							push.PushID = PushID;
							push.PushUserID = UserID;
							push.PushMessage = PushMessage;
							push.PushStatue = "1";//开始时默认为未读
							
//							Log.e("哈哈哈哈","PushMessage" + PushMessage);
							
//							Log.e("哈哈哈哈", "jsonArrayPush.length()--elseelseelse--");
							ACK ack = new ACK(SEMapApplication.AccountNumber, PushID);
							List<ACK> ackList = new ArrayList<ACK>();
							ackList.add(ack);
							MsgReplace msgreplace = new MsgReplace(ackList, NetService.myMessenger,h_protocol_pusher.ACK);
							statusRow register = new statusRow(msgreplace);
//							Log.e("哈哈哈哈", "jsonObjPush.toString()---"+"nimanimanima");
							NetService.ReceivePool.execute(new handleNetSendReceiveThread_TCP(
									register, FormatTime.getFormatTime()));
//							Log.e("哈哈哈哈", "jsonObjPush.toString()---"+jsonObjPush.toString());
							
							//入库
							new getPushDataFromDB().insertPushMessage(push);
							
							//加入到推送的Map
							int length = SEMapApplication.MapPushMessage.size();
							SEMapApplication.MapPushMessage.put("MapPushMessage"+length, push);
							
							String deleteGG = PushMessage.replace("\\", "");
							JSONObject pushObj = new JSONObject(deleteGG);
//							Log.e("哈哈哈哈","pushObj : " + pushObj);
							int SEND_p = pushObj.getInt("p");
//							Log.e("哈哈哈哈","SEND_p : " + SEND_p);
							switch (SEND_p) {
							//用户参加/关注申请
							case h_protocol_pusher.SEND_Activity_Join:
								JSONObject rsdjson = pushObj.getJSONObject("rsd");
								JSONObject uisdjson = pushObj.getJSONObject("uisd");
								JSONObject asdjson = pushObj.getJSONObject("asd");
								RelationSelectData rsd = new RelationSelectData(
										rsdjson.getLong("RelationID"),
										rsdjson.getLong("ActivityID"),
										rsdjson.getLong("UserID"),
										rsdjson.getString("UploadTime"),
										rsdjson.getString("Status"));
								
								UserInfoSelectData uisd = new UserInfoSelectData(
										uisdjson.getLong("UserID"),
										uisdjson.getString("UserName"),
										uisdjson.getString("UploadTime"),
										uisdjson.getString("Code"),
										uisdjson.getString("UserPhone"),
										uisdjson.getString("UserJoinActivity"),
										uisdjson.getString("UserAttentionClass"),
										uisdjson.getString("UserQQ"),
										uisdjson.getString("UserWeiChat"),
										uisdjson.getString("UserTags"),
										uisdjson.getString("UserClass"),
										uisdjson.getString("UserDescribe"),
										uisdjson.getString("UserLevel"),
										uisdjson.getString("UserLogo"),
										uisdjson.getString("UserAge"),
										uisdjson.getString("UserSex"),
										uisdjson.getString("School"),
										uisdjson.getString("Profession"),
										uisdjson.getString("Birthday"),
										uisdjson.getString("Home"));
								
								ActivitySelectData asd = new ActivitySelectData(
										asdjson.getLong("ActivityID"),
										asdjson.getLong("BuildActivityUserID"),
										asdjson.getLong("ActivityManagerID"),
										asdjson.getString("UploadTime"),
										asdjson.getString("IsDirectJoinIn"),
										asdjson.getString("ActivityFlag"),
										asdjson.getString("ActivityMemberNumber"),
										asdjson.getString("ActivityMaxMemberNumber"),
										asdjson.getString("ActivityDescribe"),
										asdjson.getString("ActivityName"),
										asdjson.getString("ActivityStartTime"),
										asdjson.getString("ActivityEndTime"),
										asdjson.getString("ActivityHoldPlace"),
										asdjson.getString("HelpPhone"),
										asdjson.getString("ActivityBelongClass"),
										asdjson.getString("ActivityTags"),
										asdjson.getString("ActivityOpinion"),
										asdjson.getString("ActivityAddress"),
										asdjson.getString("ActivityLogo"));
								//消息类通知用户
								notification.showIntentActivityNotify(MyMessage.class , rsd,uisd,asd);
								break;
								
								//活动主办方对用户请求的反馈
							case h_protocol_pusher.SEND_Activity_Join_Checked:
								JSONObject rsdjson_Checked = pushObj.getJSONObject("rsd");
								JSONObject asdjson_Checked = pushObj.getJSONObject("asd");
								RelationSelectData rsd_Checked = new RelationSelectData(
										rsdjson_Checked.getLong("RelationID"),
										rsdjson_Checked.getLong("ActivityID"),
										rsdjson_Checked.getLong("UserID"),
										rsdjson_Checked.getString("UploadTime"),
										rsdjson_Checked.getString("Status"));
								
								ActivitySelectData asd_Checked = new ActivitySelectData(
										asdjson_Checked.getLong("ActivityID"),
										asdjson_Checked.getLong("BuildActivityUserID"),
										asdjson_Checked.getLong("ActivityManagerID"),
										asdjson_Checked.getString("UploadTime"),
										asdjson_Checked.getString("IsDirectJoinIn"),
										asdjson_Checked.getString("ActivityFlag"),
										asdjson_Checked.getString("ActivityMemberNumber"),
										asdjson_Checked.getString("ActivityMaxMemberNumber"),
										asdjson_Checked.getString("ActivityDescribe"),
										asdjson_Checked.getString("ActivityName"),
										asdjson_Checked.getString("ActivityStartTime"),
										asdjson_Checked.getString("ActivityEndTime"),
										asdjson_Checked.getString("ActivityHoldPlace"),
										asdjson_Checked.getString("HelpPhone"),
										asdjson_Checked.getString("ActivityBelongClass"),
										asdjson_Checked.getString("ActivityTags"),
										asdjson_Checked.getString("ActivityOpinion"),
										asdjson_Checked.getString("ActivityAddress"),
										asdjson_Checked.getString("ActivityLogo"));
								if(rsd_Checked.getStatus().equals("1")){//如果主办方通过参加审核
									//保存知道本地
									//得到活动的具体的信息类
									String userPhone = SEMapApplication.AccountNumber;
				   					RelationActivity re = new RelationActivity(0, userPhone, 
				   							asd_Checked.getActivityID()+"", asd_Checked.getActivityName(), 
				   							asd_Checked.getBuildActivityUserID()+"", asd_Checked.getActivityStartTime(), 
				   							asd_Checked.getActivityEndTime(), "1","1","1");
//				   					DBAdapter dbAdapter = new DBAdapter(SEMapApplication.getInstance()
//											.getBaseContext());
//				   					dbAdapter.open();
//				   					dbAdapter.insertRelationActivity(re);
//				   				//关闭数据库连接
//									dbAdapter.close();
				   					
				   					ContentValues newValues = ContentValuesChange
											.contentInsertRelationActivity(re);
									SQLInfo sqlInfo = new SQLInfo();
									sqlInfo.p = SQLiteProtocol.insertCommonData;
									sqlInfo.SQLInfo = new InsertObject(
											ContentValuesChange.RelationActivity_Table, null, newValues);
									sqlSecurityThread.handleSQl(sqlInfo);
									
									//加入到推送的Map
									int lengthRela = SEMapApplication.MapRelationActivity.size();
									SEMapApplication.MapRelationActivity.put("MapRelationActivity"+lengthRela, re);
								}
								notification.showActivity_Join_CheckedNotify(MyMessage.class , rsd_Checked,asd_Checked);
								break;
								
								//用户对活动的评论
							case h_protocol_pusher.SEND_Activity_Leave_word:
								//解析数据
								JSONObject rsdjson_Act_Leave_word;
								ActivityDiscussSelectData rsd_Act_Leave_word = null;
								try {
									rsdjson_Act_Leave_word = pushObj.getJSONObject("adsd");
									rsd_Act_Leave_word = new ActivityDiscussSelectData(
											rsdjson_Act_Leave_word.getLong("DiscussID"),
											rsdjson_Act_Leave_word.getLong("ActivityID"),
											rsdjson_Act_Leave_word.getLong("UserID"),
											rsdjson_Act_Leave_word.getLong("PointDiscussID"),
											rsdjson_Act_Leave_word.getLong("ThisUserID"),
											rsdjson_Act_Leave_word.getString("UploadTime"),
											rsdjson_Act_Leave_word.getString("ActivityName"),
											rsdjson_Act_Leave_word.getString("UserName"),
											rsdjson_Act_Leave_word.getString("DiscussContent"),
											rsdjson_Act_Leave_word.getString("Photo"));
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								notification.showActivity_Leave_wordNotify(MyMessage.class , rsd_Act_Leave_word);
								break;
								
								
							case h_protocol_pusher.SEND_UserOpinion:
								JSONObject uisdjson_UserOpinion = pushObj.getJSONObject("uisd");
								JSONObject asdjson_UserOpinion = pushObj.getJSONObject("asd");
								
								UserInfoSelectData uisd_UserOpinion = new UserInfoSelectData(
										uisdjson_UserOpinion.getLong("UserID"),
										uisdjson_UserOpinion.getString("UserName"),
										uisdjson_UserOpinion.getString("UploadTime"),
										uisdjson_UserOpinion.getString("Code"),
										uisdjson_UserOpinion.getString("UserPhone"),
										uisdjson_UserOpinion.getString("UserJoinActivity"),
										uisdjson_UserOpinion.getString("UserAttentionClass"),
										uisdjson_UserOpinion.getString("UserQQ"),
										uisdjson_UserOpinion.getString("UserWeiChat"),
										uisdjson_UserOpinion.getString("UserTags"),
										uisdjson_UserOpinion.getString("UserClass"),
										uisdjson_UserOpinion.getString("UserDescribe"),
										uisdjson_UserOpinion.getString("UserLevel"),
										uisdjson_UserOpinion.getString("UserLogo"),
										uisdjson_UserOpinion.getString("UserAge"),
										uisdjson_UserOpinion.getString("UserSex"),
										uisdjson_UserOpinion.getString("School"),
										uisdjson_UserOpinion.getString("Profession"),
										uisdjson_UserOpinion.getString("Birthday"),
										uisdjson_UserOpinion.getString("Home"));
								
								ActivitySelectData asd_UserOpinion = new ActivitySelectData(
										asdjson_UserOpinion.getLong("ActivityID"),
										asdjson_UserOpinion.getLong("BuildActivityUserID"),
										asdjson_UserOpinion.getLong("ActivityManagerID"),
										asdjson_UserOpinion.getString("UploadTime"),
										asdjson_UserOpinion.getString("IsDirectJoinIn"),
										asdjson_UserOpinion.getString("ActivityFlag"),
										asdjson_UserOpinion.getString("ActivityMemberNumber"),
										asdjson_UserOpinion.getString("ActivityMaxMemberNumber"),
										asdjson_UserOpinion.getString("ActivityDescribe"),
										asdjson_UserOpinion.getString("ActivityName"),
										asdjson_UserOpinion.getString("ActivityStartTime"),
										asdjson_UserOpinion.getString("ActivityEndTime"),
										asdjson_UserOpinion.getString("ActivityHoldPlace"),
										asdjson_UserOpinion.getString("HelpPhone"),
										asdjson_UserOpinion.getString("ActivityBelongClass"),
										asdjson_UserOpinion.getString("ActivityTags"),
										asdjson_UserOpinion.getString("ActivityOpinion"),
										asdjson_UserOpinion.getString("ActivityAddress"),
										asdjson_UserOpinion.getString("ActivityLogo"));
								//通知栏通知
								notification.showSend_UserOpinionNotify(MyMessage.class ,
										uisd_UserOpinion,asd_UserOpinion);
								break;
							//动态消息推送	
							case h_protocol_pusher.SEND_USER_WANTTO_PUSH:
								JSONObject mbijson = pushObj.getJSONObject("mbi");
//								Log.v("哈哈哈哈", "MBI---"+mbijson.toString());
								MomentBaseInfo mbi = new MomentBaseInfo(
										mbijson.getInt("MomentId"),
										mbijson.getString("MessageId"),
										mbijson.getString("UserId"),
										mbijson.getString("UserName"),
										mbijson.getString("UploadTime"),
										mbijson.getString("MomentContent"),
										mbijson.getString("PraiseNum"),
										mbijson.getString("CritizenNum"),
										mbijson.getString("MyUserId"),
										mbijson.getString("MyUserName"),
										mbijson.getString("HasOrNo"),
										mbijson.getString("Location"),
										mbijson.getString("Sex"),
										mbijson.getString("Age"),
										mbijson.getString("ShowPhotos"));
//								Log.v("哈哈哈哈", "MBI---111  ");
								//通知栏通知
								notification.showMomentCommentNotify(MyMessage.class ,
										mbi);
//								Log.v("哈哈哈哈", "MBI---222 ");
								break;
							case h_protocol_pusher.SEND_Person_Leave_Word:
								//解析数据
								break;
								
							case h_protocol_pusher.SEND_UserFeedback_Response:
								//解析数据
								String feedBackContent = "";
								try {
									feedBackContent = pushObj.getString("Feedback_Response");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								notification.showUserFeedback_ResponseNotify(MyMessage.class , feedBackContent);
								break;
							case h_protocol_pusher.SEND_Text:
								//解析数据
								String commomStr = "";
								try {
									feedBackContent = pushObj.getString("Text");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								notification.showUserCommonTextNotify(MyMessage.class , commomStr);
								break;
								
							case h_protocol_pusher.SEND_Version_Update:
								String url = "";//下载的路径地址
								notification.showVersionUpdateNotify(MyMessage.class , url);
								break;
							default:
								Log.e("MyJsonTool",
										"PushMessageSelectData: unknown packet");
							}
//							notification.showIntentActivityNotify(myMap.class);
							
						}
						
					}
					
				default:
					Log.e("MyJsonTool", "unknown packet");
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return owList;
	}

}
