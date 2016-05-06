package pri.h.uitool;
//package uitool;
//
//import httptransfer.creatPoi;
//import httptransfer.deletePoi;
//import httptransfer.httpManager;
//import httptransfer.updatePoi;
//
//public class handleBaiduDate implements Runnable {
//
//	creatPoi creatpoi = null;
//	updatePoi updatepoi = null;
//	deletePoi deletepoi = null;
//	
//	private httpManager httpmanager = new httpManager();
//	
//	public handleBaiduDate(creatPoi creatpoi) {
//		this.creatpoi = creatpoi;
//	}
//
//	public handleBaiduDate(updatePoi updatepoi)
//	{
//		this.updatepoi = updatepoi;
//	}
//	
//	public handleBaiduDate(deletePoi deletepoi)
//	{
//		this.deletepoi = deletepoi;
//	}
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		if(creatpoi != null)
//		{
//			httpmanager.sendMessageToBDStore(creatpoi,msg);
//		}
//		if(updatepoi != null)
//		{
//			httpmanager.sendMessageToBDStore(updatepoi);
//		}
//		if(deletepoi != null)
//		{
//			httpmanager.sendMessageToBDStore(deletepoi);
//		}
//	}
//
//}
