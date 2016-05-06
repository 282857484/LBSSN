//package pri.h.uitool;
//
//import java.util.List;
//
//import pri.h.uitool.RadialMenuWidget.RadialMenuEntry;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//
//public class MyLocationNote implements RadialMenuEntry{
//
//	Context context = null;
//	public MyLocationNote(Context context) {
//		// TODO Auto-generated constructor stub
//		this.context = context;
//	}
//
//	@Override
//	public String getName() {
//		// TODO Auto-generated method stub
//		return "MyLocationNote";
//	}
//
//	@Override
//	public String getLabel() {
//		// TODO Auto-generated method stub
//		return "位置笔记";
//	}
//
//	@Override
//	public int getIcon() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public List<RadialMenuEntry> getChildren() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void menuActiviated() {
//		// TODO Auto-generated method stub
//		//本地的位置笔记
////		Intent intent = new Intent(((Activity)context) , NotesListActivity.class);
////		((Activity) context).startActivity(intent);
//	}
//
//}
