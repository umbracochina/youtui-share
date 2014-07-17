package cn.bidaround.youtui_template;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bidaround.point.PointActivity;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.activity.ShareActivity;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.util.Util;

/**
 * 白色列表样式的分享框
 * 
 * @author youtui
 * @since 14/5/4
 */
public class ListPopup extends YTBasePopupWindow implements OnClickListener {
	/** 用于判断分享页面是否正在运行 */
	private ArrayList<String> enList;
	private Button sharelist_knowaction_btn;
	private TextView yt_listpopup_screencap_text;
	private ShareGridAdapter adapter;
	private YtTemplate template;
	private ShareData shareData;

	public ListPopup(Activity act, int showStyle, boolean hasAct, YtTemplate template, ShareData shareData, ArrayList<String> enList) {
		super(act, hasAct);
		this.showStyle = showStyle;
		this.template = template;
		this.shareData = shareData;
		this.enList = enList;
		instance = this;
	}

	/**
	 * 显示分享列表窗口
	 */
	@SuppressWarnings("deprecation")
	public void show() {
		View view = LayoutInflater.from(act).inflate(YtCore.res.getIdentifier("yt_popup_list", "layout", YtCore.packName), null);
		initListView(view);
		initButton(view);
		// 设置popupwindow的属
		setFocusable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(YtCore.res.getDrawable(YtCore.res.getIdentifier("yt_side", "drawable", YtCore.packName)));
		setContentView(view);
		setWidth(act.getWindowManager().getDefaultDisplay().getWidth());
		setHeight(Util.dip2px(act, 350));
		setAnimationStyle(YtCore.res.getIdentifier("YtSharePopupAnim", "style", YtCore.packName));
		// R.style.YtSharePopupAnim
		showAtLocation(getContentView(), Gravity.BOTTOM, 0, 0);
	}

	/**
	 * 初始化ListView
	 * 
	 * @param view
	 */
	private void initListView(View view) {
		ListView listView = (ListView) view.findViewById(YtCore.res.getIdentifier("sharelist_share_list", "id", YtCore.packName));
		adapter = new ShareGridAdapter(act, enList, showStyle);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	/**
	 * 初始化查看和了解积分按钮
	 * 
	 * @param view
	 */
	private void initButton(View view) {
		// 如果么有活动显示取消，如果有活动显示了解活动规则
		sharelist_knowaction_btn = (Button) view.findViewById(YtCore.res.getIdentifier("sharelist_knowaction_btn", "id", YtCore.packName));
		if (hasAct) {
			sharelist_knowaction_btn.setText("积分兑换");
		} else {
			sharelist_knowaction_btn.setText("取  消");
		}

		sharelist_knowaction_btn.setOnClickListener(this);

		yt_listpopup_screencap_text = (TextView) view.findViewById(YtCore.res.getIdentifier("yt_listpopup_screencap_text", "id", YtCore.packName));
		yt_listpopup_screencap_text.setOnClickListener(this);
	}

	/**
	 * 查看和了解积分按钮监听
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == YtCore.res.getIdentifier("sharelist_knowaction_btn", "id", YtCore.packName)) {
			/** 有活动点击显示活动规则，没活动显示抽奖 */
			if (hasAct) {
				Intent it = new Intent(act, PointActivity.class);
				act.startActivity(it);

			} else {
				this.dismiss();
			}
		} else if (v.getId() == YtCore.res.getIdentifier("sharelist_checkpoint_btn", "id", YtCore.packName)) {
			Intent checkIt = new Intent(act, ShareActivity.class);
			checkIt.putExtra("from", "check");
			act.startActivity(checkIt);
		} else if (v.getId() == YtCore.res.getIdentifier("yt_listpopup_screencap_text", "id", YtCore.packName)) {
			//截屏按钮
			TemplateUtil.GetandSaveCurrentImage(act);
			Intent it = new Intent(act, ScreenCapEditActivity.class);
			it.putExtra("viewType", template.getViewType());
			if(shareData.isAppShare){
				it.putExtra("target_url", YtCore.getTargetUrl());
			}else{
				it.putExtra("target_url", shareData.getTarget_url());	
			}
			act.startActivity(it);
			this.dismiss();
		}
	}

	@Override
	/**列表项点击事件*/
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (Util.isNetworkConnected(act)) {
			new YTShare(act).doListShare(position, template, shareData, instance);
		} else {
			Toast.makeText(act, "无网络连接，请查看您的网络情况", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 刷新积分
	 */
	@Override
	public void refresh() {
		adapter.notifyDataSetChanged();
	}

	/**
	 * 关闭分享界面
	 */
	@Override
	public void dismiss() {
		super.dismiss();
	}

	/** 关闭 分享界面 */
	public static void close() {
		if (instance != null) {
			instance.dismiss();
		}
	}

}
