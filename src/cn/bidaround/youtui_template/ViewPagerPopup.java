package cn.bidaround.youtui_template;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import cn.bidaround.point.PointActivity;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.util.Util;


/**
 * viewpager+gridview 黑色网格样式分享样式
 * @author youtui
 * @since 14/4/25
 */
public class ViewPagerPopup extends YTBasePopupWindow implements OnClickListener, OnItemClickListener, OnPageChangeListener {
	/**判断该分享页面是否正在运行*/
	public static boolean isShowing;
	private GridView pagerOne_gridView, pagerTwo_gridView;
	private ShareGridAdapter pagerOne_gridAdapter, pagerTwo_gridAdapter;
	private View sharepopup_indicator_linelay;
	private ImageView zeroIamge, oneIamge;
	private ArrayList<String> enList;
	private ViewPager viewPager;
	private YtTemplate template;
	private ShareData shareData;

	public ViewPagerPopup(Activity act, int showStyle,boolean hasAct,YtTemplate template,ShareData shareData) {
		super(act,hasAct);
		this.showStyle = showStyle;
		this.template = template;
		this.shareData = shareData;
		instance = this;
	}

	/**
	 * 显示分享界面
	 */
	@SuppressWarnings("deprecation")
	public void show() {
		View view = LayoutInflater.from(act).inflate(YtCore.res.getIdentifier("popup_viewpager", "layout", YtCore.packName), null);
		initButton(view);
		initViewPager(view);
		// 设置popupwindow的属�?
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		setWidth(act.getWindowManager().getDefaultDisplay().getWidth());
		setHeight(Util.dip2px(act, 310));
		setAnimationStyle(YtCore.res.getIdentifier("YtSharePopupAnim", "style", YtCore.packName));
		showAtLocation(getContentView(), Gravity.BOTTOM, 0, 0);
		isShowing = true;
	}

	/**
	 * 初始化积分按钮
	 * 
	 * @param view
	 */

	private void initButton(View view) {
		zeroIamge = (ImageView) view.findViewById(YtCore.res.getIdentifier("sharepopup_zero_iv", "id", YtCore.packName));
		oneIamge = (ImageView) view.findViewById(YtCore.res.getIdentifier("sharepopup_one_iv", "id", YtCore.packName));
		Button cancelBt = (Button) view.findViewById(YtCore.res.getIdentifier("cancel_bt", "id", YtCore.packName));
		if(hasAct){
			cancelBt.setText("我已分享，参与抽奖");
		}else{
			cancelBt.setText("取消");
		}
		cancelBt.setOnClickListener(this);
	}

	/**
	 * 初始化viewpager
	 */
	private void initViewPager(View view) {
		viewPager = (ViewPager) view.findViewById(YtCore.res.getIdentifier("share_viewpager", "id", YtCore.packName));
		sharepopup_indicator_linelay = view.findViewById(YtCore.res.getIdentifier("sharepopup_indicator_linelay", "id", YtCore.packName));
		ArrayList<View> pagerList = new ArrayList<View>();
		enList = KeyInfo.enList;
		// 如果分享的数目<=6，只放置一页
		if (enList.size() <= 6) {
			View pagerOne = LayoutInflater.from(act).inflate(YtCore.res.getIdentifier("share_pager", "layout", YtCore.packName), null);
			pagerOne_gridView = (GridView) pagerOne.findViewById(YtCore.res.getIdentifier("sharepager_grid", "id", YtCore.packName));
			pagerOne_gridAdapter = new ShareGridAdapter(act, enList, showStyle);
			pagerOne_gridView.setAdapter(pagerOne_gridAdapter);
			pagerOne_gridView.setOnItemClickListener(this);
			pagerList.add(pagerOne);
		} else if (enList.size() > 6 && enList.size() <= 12) {
			// 分享数量7~12之间,放置两页
			ArrayList<String> pagerOneList = new ArrayList<String>();
			for (int i = 0; i < 6; i++) {
				pagerOneList.add(enList.get(i));
			}
			// 初始化第一页
			View pagerOne = LayoutInflater.from(act).inflate(YtCore.res.getIdentifier("share_pager", "layout", YtCore.packName), null);
			pagerOne_gridView = (GridView) pagerOne.findViewById(YtCore.res.getIdentifier("sharepager_grid", "id", YtCore.packName));
			pagerOne_gridAdapter = new ShareGridAdapter(act, pagerOneList, showStyle);
			pagerOne_gridView.setAdapter(pagerOne_gridAdapter);
			pagerOne_gridView.setOnItemClickListener(this);
			pagerList.add(pagerOne);

			ArrayList<String> pagerTwoList = new ArrayList<String>();
			for (int i = 6; i < enList.size(); i++) {
				pagerTwoList.add(enList.get(i));
			}
			// 初始化第二页
			View pagerTwo = LayoutInflater.from(act).inflate(YtCore.res.getIdentifier("share_pager", "layout", YtCore.packName), null);
			pagerTwo_gridView = (GridView) pagerTwo.findViewById(YtCore.res.getIdentifier("sharepager_grid", "id", YtCore.packName));
			pagerTwo_gridAdapter = new ShareGridAdapter(act, pagerTwoList, showStyle);
			pagerTwo_gridView.setAdapter(pagerTwo_gridAdapter);
			pagerTwo_gridView.setOnItemClickListener(this);
			pagerList.add(pagerTwo);
		}

		SharePagerAdapter pagerAdapter = new SharePagerAdapter(pagerList);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(2);
		// 设置滑动下标
		if (enList.size() > 6 && enList.size() <= 12) {
			viewPager.setOnPageChangeListener(this);
		} else if (enList.size() <= 6) {
			if(sharepopup_indicator_linelay!= null){
				sharepopup_indicator_linelay.setVisibility(View.INVISIBLE);
			}		
		}
	}

	/**
	 * 活动按钮事件
	 */
	@Override
	public void onClick(View v) {

		if (v.getId() == YtCore.res.getIdentifier("cancel_bt", "id", YtCore.packName)) {
			/**有活动点击显示活动规则*/
			if(hasAct){
				Intent it = new Intent(act, PointActivity.class);
				act.startActivity(it);
			}else{
				this.dismiss();
			}
						
		} else if (v.getId() == YtCore.res.getIdentifier("share_popup_knowtv", "id", YtCore.packName)) {


		} else if (v.getId() == YtCore.res.getIdentifier("share_popup_checktv", "id", YtCore.packName)) {

		}

	}

	/**
	 * 分享按钮点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
		if (Util.isNetworkConnected(act)) {
			if (adapterView == pagerOne_gridView) {
				new YTShare(act).doGridShare(position, 0,template,shareData);

			} else if (adapterView == pagerTwo_gridView) {
				new YTShare(act).doGridShare(position, 1,template,shareData);
			}
		} else {
			Toast.makeText(act, "无网络连接，请查看您的网络情况", Toast.LENGTH_SHORT).show();
		}
		

	}
	/**
	 * 刷新显示积分
	 */
	@Override
	public void refresh() {
		if (pagerOne_gridAdapter != null) {
			pagerOne_gridAdapter.notifyDataSetChanged();
		}
		if (pagerTwo_gridAdapter != null) {
			pagerTwo_gridAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * viewpager状态变化监听
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	/**
	 * viewpager滑动监听
	 */
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	/**
	 * 页面选择监听，这里用来显示viewpager下标
	 */
	@Override
	public void onPageSelected(int index) {
		// viewpager下标
		switch (index) {
		case 0:
			zeroIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("guide_dot_white", "drawable", YtCore.packName)));
			oneIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("guide_dot_black", "drawable", YtCore.packName)));
			break;
		case 1:
			zeroIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("guide_dot_black", "drawable", YtCore.packName)));
			oneIamge.setImageDrawable(act.getResources().getDrawable(YtCore.res.getIdentifier("guide_dot_white", "drawable", YtCore.packName)));
			break;

		default:
			break;
		}

	}
	public static void close(){
		if(instance!=null){
			instance.dismiss();
		}
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		isShowing = false;
		super.dismiss();
	}
}