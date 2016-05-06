package pri.h.semap;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pri.z.show.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

	/**
	 * 日期时间选择控件 
	 * 使用方法：
	   private EditText inputDate;//需要设置的日期时间文本编辑框
	   private String initDateTime="2012年9月3日 14:44",//初始日期时间值 
	       在点击事件中使用：
	   inputDate.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				DateTimePickDialogUtil dateTimePicKDialog=new DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
				dateTimePicKDialog.dateTimePicKDialog(inputDate);
				
			}
		});
	   
	 * @author 吴理琪
	 * @version 1.0
	 */
public class DateTimePickDialogUtil implements  OnDateChangedListener,OnTimeChangedListener{
		private DatePicker datePicker;
		private TimePicker timePicker;
		private AlertDialog ad;
		private String dateTime;
		private String initDateTime;
		private Activity activity;
		
		/**
		 * 日期时间弹出选择框构造函数
		 * @param activity：调用的父activity
		 * @param initDateTime 初始日期时间值，作为弹出窗口的标题和日期时间初始值
		 */
		public DateTimePickDialogUtil(Activity activity,String initDateTime)
		{
			this.activity = activity;
			this.initDateTime=initDateTime;
			
		}
		
		public void init(DatePicker datePicker,TimePicker timePicker)
		{
			Calendar calendar= Calendar.getInstance();
			if(!(null==initDateTime||"".equals(initDateTime)))
			{
				calendar = this.getCalendarByInintData(initDateTime);
			}else
			{
				initDateTime=calendar.get(Calendar.YEAR)+"年"+calendar.get(Calendar.MONTH)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日 "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
			}
			
			datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH), this);
			timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		}

		/**
		 * 弹出日期时间选择框方法
		 * @param inputDate:为需要设置的日期时间文本编辑框
		 * @return
		 */
		public AlertDialog dateTimePicKDialog(final EditText inputDate)
		{
			LinearLayout dateTimeLayout  = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.h_common_datetime, null);
			datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.h_datepicker);
			timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.h_timepicker);
			init(datePicker,timePicker);
			timePicker.setIs24HourView(true);
			timePicker.setOnTimeChangedListener(this);
					
			ad = new AlertDialog.Builder(activity).setTitle(initDateTime).setView(dateTimeLayout).setPositiveButton("设置",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int whichButton)
								{
									inputDate.setText(dateTime);
								}
							}).setNegativeButton("取消",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int whichButton)
								{
									inputDate.setText("");
								}
							}).show();
			
			onDateChanged(null, 0, 0, 0);
			return ad;
		}
		

		public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
		{
			onDateChanged(null, 0, 0, 0);
		}

		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth)
		{
			Calendar calendar = Calendar.getInstance();

			calendar.set(datePicker.getYear(), datePicker.getMonth(),
					datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
					timePicker.getCurrentMinute());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			
			dateTime=sdf.format(calendar.getTime());
			ad.setTitle(dateTime);
		}
		
		/**
		 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
		 * @param initDateTime 初始日期时间值 字符串型
		 * @return Calendar
		 */
		private Calendar getCalendarByInintData(String initDateTime) {
			Calendar calendar = Calendar.getInstance();
			
			// 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
			String date = spliteString(initDateTime, "日", "index", "front");
			String time = spliteString(initDateTime, "日", "index", "back");

			String yearStr = spliteString(date, "年", "index", "front");
			String monthAndDay = spliteString(date, "年", "index", "back");

			String monthStr = spliteString(monthAndDay, "月", "index","front");
			String dayStr = spliteString(monthAndDay, "月", "index", "back");

			String hourStr = spliteString(time, ":", "index", "front");
			String minuteStr = spliteString(time, ":", "index", "back");

			int currentYear = Integer.valueOf(yearStr.trim()).intValue();
			int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
			int currentDay = Integer.valueOf(dayStr.trim()).intValue();
			int currentHour = Integer.valueOf(hourStr.trim()).intValue();
			int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

			calendar.set(currentYear, currentMonth, currentDay, currentHour,currentMinute);
			return calendar;
		}

		public static String spliteString(String srcStr,String pattern,String indexOrLast,String frontOrBack)
		{
			String result="";
			int loc=-1;
			if(indexOrLast.equalsIgnoreCase("index"))
			{
				loc=srcStr.indexOf(pattern);
			}else
			{
				loc=srcStr.lastIndexOf(pattern);
			}
			if(frontOrBack.equalsIgnoreCase("front"))
			{
				if(loc!=-1)result=srcStr.substring(0,loc);
			}else
			{
				if(loc!=-1)result=srcStr.substring(loc+1,srcStr.length());
			}
			return result;
		}
		
		
}

