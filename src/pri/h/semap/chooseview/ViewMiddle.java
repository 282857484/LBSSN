package pri.h.semap.chooseview;

import java.util.ArrayList;
import java.util.LinkedList;

import pri.z.show.R;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ViewMiddle extends LinearLayout implements ViewBaseAction {

	private ListView regionListView;
	private ListView plateListView;
	// 第一列的字符标识
	private ArrayList<String> groups = new ArrayList<String>();
	// 第二列的字符标识
	private LinkedList<String> childrenItem = new LinkedList<String>();
	// 第二列的二维字符标识
	private SparseArray<LinkedList<String>> children = new SparseArray<LinkedList<String>>();
	private TextAdapter plateListViewAdapter;
	private TextAdapter earaListViewAdapter;
	private OnSelectListener mOnSelectListener;
	private int tEaraPosition = 0;
	private int tBlockPosition = 0;
	private String showString = "不限";
	
	private int tagsX,tagsY;

	public ViewMiddle(Context context) {
		super(context);
//		init(context);
		initActivitytype(context);
	}
	public ViewMiddle(Context context , int type) {
		super(context);
		initActivitytype(context);
	}
	

	private void initActivitytype(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.h_view_region, this, true);
		regionListView = (ListView) findViewById(R.id.h_expend_listView);
		plateListView = (ListView) findViewById(R.id.h_expend_listView2);
		setBackgroundResource(R.drawable.h_choosearea_bg_left);

		
		
		groups.add("聚会P");
		LinkedList<String> msItem = new LinkedList<String>();
		msItem.add("读书会");
		msItem.add("节日活动");
		msItem.add("经验交流会");
		msItem.add("行业聚会");
		msItem.add("创业聚会");
		msItem.add("茶话会");
		msItem.add("集市");
//		msItem.add("江浙菜");
//		msItem.add("烧烤烤肉");
		
		children.put(0, msItem);
		
		groups.add("娱乐P");
		LinkedList<String> jdItem = new LinkedList<String>();
		jdItem.add("电子竞技");
		jdItem.add("K歌");
		jdItem.add("街头表演");
		jdItem.add("拼餐");
		jdItem.add("逛街");
		jdItem.add("PotluckParty");
		jdItem.add("桌游");
		jdItem.add("行为艺术");
		
		children.put(1, jdItem);
		
		groups.add("讲座P");
		LinkedList<String> xxItem = new LinkedList<String>();
		xxItem.add("公益");
		xxItem.add("学术");
		xxItem.add("科技");
		xxItem.add("文化");
		xxItem.add("留学");
		xxItem.add("投资");
		xxItem.add("创业");
		xxItem.add("行业讲座");
//		xxItem.add("景点郊游");
//		xxItem.add("主题公园/游乐园");
//		xxItem.add("演出/赛事/展览");
		
		children.put(2, xxItem);
		
		groups.add("运动P");
		LinkedList<String> shItem = new LinkedList<String>();
		shItem.add("网球");
		shItem.add("羽毛球");
		shItem.add("篮球");
		shItem.add("足球");
		shItem.add("登山");
		shItem.add("远足");
		shItem.add("健身分享会");
		shItem.add("减肥分享会");
		shItem.add("骑行");
		
		children.put(3, shItem);
		
		groups.add("游玩P");
		LinkedList<String> mlItem = new LinkedList<String>();
		mlItem.add("结伴自由行");
		mlItem.add("近郊野餐");
		mlItem.add("自驾游");
		mlItem.add("驴友座谈会");
		mlItem.add("摄影同好会");
		mlItem.add("探险");
//		mlItem.add("");
//		mlItem.add("");
//		mlItem.add("");
		
		children.put(4, mlItem);
		
		groups.add("音乐-舞P");
		LinkedList<String> gwItem = new LinkedList<String>();
		gwItem.add("音乐交流会");
		gwItem.add("粉丝见面会");
		gwItem.add("小型音乐会");
		gwItem.add("合唱团");
		
		gwItem.add("快闪");
		gwItem.add("舞会");
		gwItem.add("街舞(PK)");
//		gwItem.add("配饰");
//		gwItem.add("图书音像");
//		gwItem.add("个护化妆");
//		gwItem.add("配饰");
//		gwItem.add("其他");
		
		children.put(5, gwItem);
		
		
		groups.add("电影-戏P");
		LinkedList<String> abItem = new LinkedList<String>();
		abItem.add("微电影拍摄");
		abItem.add("电影分享会");
		abItem.add("电影见面会");
		abItem.add("主题放映会");
		
		abItem.add("业余拼团");
		abItem.add("戏剧交流会");
		abItem.add("话剧");
		abItem.add("音乐剧");
		abItem.add("舞台剧");
//		gwItem.add("个护化妆");
//		gwItem.add("配饰");
//		gwItem.add("其他");
		
		children.put(6, abItem);
		
		groups.add("艺术P");
		LinkedList<String> bcItem = new LinkedList<String>();
		bcItem.add("写生活动");
		bcItem.add("手工艺制作");
		bcItem.add("美学交流会");
		bcItem.add("艺术品鉴赏会");
		
//		bcItem.add("业余拼团");
//		bcItem.add("戏剧交流会");
//		bcItem.add("话剧");
//		bcItem.add("音乐剧");
//		bcItem.add("舞台剧");
//		gwItem.add("个护化妆");
//		gwItem.add("配饰");
//		gwItem.add("其他");
		
		children.put(7, bcItem);
		
		groups.add("博览P");
		LinkedList<String> cdItem = new LinkedList<String>();
		cdItem.add("主题动漫展");
		cdItem.add("摄影展");
		cdItem.add("画展");
		cdItem.add("3D展");
		cdItem.add("服装展");
		
//		cdItem.add("展览交流会");
//		cdItem.add("话剧");
//		cdItem.add("音乐剧");
//		cdItem.add("舞台剧");
//		cdItem.add("个护化妆");
//		cdItem.add("配饰");
//		cdItem.add("其他");
		
		children.put(8, cdItem);
		


		earaListViewAdapter = new TextAdapter(context, groups,
				R.drawable.h_choose_item_selected,
				R.drawable.h_choose_eara_item_selector);
		earaListViewAdapter.setTextSize(17);
		earaListViewAdapter.setSelectedPositionNoNotify(tEaraPosition);
		regionListView.setAdapter(earaListViewAdapter);
		earaListViewAdapter
				.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(View view, int position) {
						// 第一行的监听,可以保存position
						if (position < children.size()) {
							tagsX = position;
							childrenItem.clear();
							childrenItem.addAll(children.get(position));
							plateListViewAdapter.notifyDataSetChanged();
						}
					}
				});
		if (tEaraPosition < children.size())
			childrenItem.addAll(children.get(tEaraPosition));
		plateListViewAdapter = new TextAdapter(context, childrenItem,
				R.drawable.h_choose_item_right,
				R.drawable.h_choose_plate_item_selector);
		plateListViewAdapter.setTextSize(15);
		plateListViewAdapter.setSelectedPositionNoNotify(tBlockPosition);
		plateListView.setAdapter(plateListViewAdapter);
		plateListViewAdapter
				.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(View view, final int position) {
						tagsY = position;
						// 第二行的监听,可以保存position
						showString = childrenItem.get(position);
						if (mOnSelectListener != null) {
							// 回调
							mOnSelectListener.getValue(showString, tagsX, tagsY);
						}

					}
				});
		if (tBlockPosition < childrenItem.size())
			showString = childrenItem.get(tBlockPosition);
		if (showString.contains("不限")) {
			showString = showString.replace("不限", "");
		}
		setDefaultSelect();
	}
	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.h_view_region, this, true);
		regionListView = (ListView) findViewById(R.id.h_expend_listView);
		plateListView = (ListView) findViewById(R.id.h_expend_listView2);
		setBackgroundResource(R.drawable.h_choosearea_bg_left);

		
		
		groups.add("美食");
		LinkedList<String> msItem = new LinkedList<String>();
		msItem.add("自助餐");
		msItem.add("聚餐宴请");
		msItem.add("蛋糕");
		msItem.add("日本料理");
		msItem.add("西餐");
		msItem.add("川湘菜");
		msItem.add("海鲜");
		msItem.add("江浙菜");
		msItem.add("烧烤烤肉");
		
		children.put(0, msItem);
		
		groups.add("酒店");
		LinkedList<String> jdItem = new LinkedList<String>();
		jdItem.add("经济型酒店");
		jdItem.add("豪华酒店");
		jdItem.add("公寓式酒店");
		jdItem.add("主题酒店");
		jdItem.add("度假酒店");
		jdItem.add("客栈");
		jdItem.add("青年旅舍");
		jdItem.add("钟点房");
		
		children.put(1, jdItem);
		
		groups.add("休闲娱乐");
		LinkedList<String> xxItem = new LinkedList<String>();
		xxItem.add("KTV");
		xxItem.add("足疗按摩");
		xxItem.add("温泉");
		xxItem.add("洗浴/汗蒸");
		xxItem.add("游泳/水上运动");
		xxItem.add("运动健身");
		xxItem.add("咖啡/酒吧");
		xxItem.add("桌游/电玩");
		xxItem.add("景点郊游");
		xxItem.add("主题公园/游乐园");
		xxItem.add("演出/赛事/展览");
		
		children.put(2, xxItem);
		
		groups.add("生活服务");
		LinkedList<String> shItem = new LinkedList<String>();
		shItem.add("摄影写真");
		shItem.add("儿童摄影");
		shItem.add("照片冲印");
		shItem.add("汽车服务");
		shItem.add("配镜");
		shItem.add("体检保健");
		shItem.add("母婴亲子");
		shItem.add("培训课程");
		shItem.add("服装定制");
		
		children.put(3, shItem);
		
		groups.add("魅力生活");
		LinkedList<String> mlItem = new LinkedList<String>();
		mlItem.add("美发");
		mlItem.add("美容美体");
		mlItem.add("美甲");
		mlItem.add("瑜伽/舞蹈");
		mlItem.add("个性写真");
		mlItem.add("婚纱摄影");
//		mlItem.add("");
//		mlItem.add("");
//		mlItem.add("");
		
		children.put(4, mlItem);
		
		groups.add("购物");
		LinkedList<String> gwItem = new LinkedList<String>();
		gwItem.add("服装");
		gwItem.add("鞋类/箱包");
		gwItem.add("食品");
		gwItem.add("家居百货");
		gwItem.add("家纺");
		gwItem.add("电器/数码");
		gwItem.add("玩具/母婴");
		gwItem.add("配饰");
		gwItem.add("图书音像");
		gwItem.add("个护化妆");
		gwItem.add("配饰");
		gwItem.add("其他");
		
		children.put(5, gwItem);
		


		earaListViewAdapter = new TextAdapter(context, groups,
				R.drawable.h_choose_item_selected,
				R.drawable.h_choose_eara_item_selector);
		earaListViewAdapter.setTextSize(17);
		earaListViewAdapter.setSelectedPositionNoNotify(tEaraPosition);
		regionListView.setAdapter(earaListViewAdapter);
		earaListViewAdapter
				.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(View view, int position) {
						// 第一行的监听,可以保存position
						if (position < children.size()) {
							tagsX = position;
							childrenItem.clear();
							childrenItem.addAll(children.get(position));
							plateListViewAdapter.notifyDataSetChanged();
						}
					}
				});
		if (tEaraPosition < children.size())
			childrenItem.addAll(children.get(tEaraPosition));
		plateListViewAdapter = new TextAdapter(context, childrenItem,
				R.drawable.h_choose_item_right,
				R.drawable.h_choose_plate_item_selector);
		plateListViewAdapter.setTextSize(15);
		plateListViewAdapter.setSelectedPositionNoNotify(tBlockPosition);
		plateListView.setAdapter(plateListViewAdapter);
		plateListViewAdapter
				.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(View view, final int position) {
						tagsY = position;
						// 第二行的监听,可以保存position
						showString = childrenItem.get(position);
						if (mOnSelectListener != null) {
							// 回调
							mOnSelectListener.getValue(showString, tagsX, tagsY);
						}

					}
				});
		if (tBlockPosition < childrenItem.size())
			showString = childrenItem.get(tBlockPosition);
		if (showString.contains("不限")) {
			showString = showString.replace("不限", "");
		}
		setDefaultSelect();

	}

	public void setDefaultSelect() {
		regionListView.setSelection(tEaraPosition);
		plateListView.setSelection(tBlockPosition);
	}

	public String getShowText() {
		return showString;
	}

	public void setOnSelectListener(OnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnSelectListener {
		public void getValue(String showText, int tagsX, int tagsY);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}
	
	public String getTags(int tagX , int tagY)
	{
		return groups.get(tagX) + " " + children.get(tagX).get(tagY);
	}
}
