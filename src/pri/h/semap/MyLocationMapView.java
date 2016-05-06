package pri.h.semap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupOverlay;

/**
 * 继承MapView重写onTouchEvent实现泡泡处理操作
 * 
 * @author 
 * 
 */
class MyLocationMapView extends MapView {
	
	static PopupOverlay pop = null;
//	static PopupOverlay mypop = null;

	public MyLocationMapView(Context context) {
		super(context);
	}

	public MyLocationMapView(Context context , AttributeSet attributeset){
		super(context,attributeset);
	}
	
	public MyLocationMapView(Context context, AttributeSet attributeset,
			int defStyle) {
		super(context, attributeset, defStyle);
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// 消除泡泡
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
//			if (mypop != null && event.getAction() == MotionEvent.ACTION_UP)
//				mypop.hidePop();
		}
		return true;
	}

}