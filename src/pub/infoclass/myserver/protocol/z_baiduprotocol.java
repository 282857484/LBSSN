package pub.infoclass.myserver.protocol;

public interface z_baiduprotocol {

	/**
	 * ��̬����
	 */
	int baiduMomentPraise = 100001;
	
	/**
	 * ��̬�Ĳ�
	 */
	int baiduMomentCriticizen = 99999;
	
	/**
	 * ��ȡָ��messageid�Ķ�̬����
	 */
	int baiduMomentDiscuss = 100000;
	
	/**
	 * ��ȡ�û���ȫ����̬
	 */
	int baiduOneUserAllMoments = 100002;
	
	/**
	 * ���Id��ȡĳ��̬����
	 */
	int baiduOneMomentDetail = 100003;
	
	/**
	 * ��ȡ���ֻ��Ϣ
	 */
	int baiduSomeActivitiesOnCreate = 100004;
	int baiduSomeActivitiesOnRefresh = 100005;
	int baiduSomeActivitiesOnLoadmore = 100006;
	/**
	 * ��ȡ���ֻ��Ϣ
	 */
	int baiduSomeMomentsOnCreate = 100007;
	int baiduSomeMomentsOnRefresh = 100008;
	int baiduSomeMomentsOnLoadmore = 100009;
	
	//动态图片上传
	int momentCenterUploadPhoto = 100010;
	
	/**
	 * 动态详情请求/评论请求
	 */
	int baiduMomentDetail= 100011;

	/**
	 * 红点显示
	 */
	int noticeRedPointPro = 100012;
}
