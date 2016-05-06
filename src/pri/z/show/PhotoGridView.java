package pri.z.show;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 重写GridView，为了嵌套在ScrollView中将内容全部显示
 * 20140829
 * @author zhuzhenke
 *
 */
public class PhotoGridView extends GridView {


	    public PhotoGridView(Context context, AttributeSet attrs) { 
	        super(context, attrs); 
	    } 

	    public PhotoGridView(Context context) { 
	        super(context); 
	    } 

	    public PhotoGridView(Context context, AttributeSet attrs, int defStyle) { 
	        super(context, attrs, defStyle); 
	    } 

	    @Override 
	    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 

	        int expandSpec = MeasureSpec.makeMeasureSpec( 
	                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 
	        super.onMeasure(widthMeasureSpec, expandSpec); 
	    } 

}
