package cn.airizu.custom_control.ratingbar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.airizu.activity.R;
import cn.airizu.toolutils.DebugLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class CustomRatingBar extends LinearLayout {
	private final String TAG = this.getClass().getSimpleName();
	
	private CustomRatingBar(Context context) {
		super(context);
	}
	
	public CustomRatingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private Map<String, Object> valueMap = new HashMap<String, Object>();
	
	public static enum StyleEnum {
		// 为 "写评论" 界面准备
		FOR_WRITE_REVIEW,
		// 为 "租客点评" 界面准备
		FOR_TENANT_REVIEWS
	}
	
	/**
	 * 设置数据源, 并且初始化控件(必须调用)
	 * 
	 * @param dataSource
	 * @param styleEnum
	 */
	public void setDataSourceAndInitialize(final Map<String, String> dataSource, final StyleEnum styleEnum) {
		
		do {
			if (dataSource == null || dataSource.size() <= 0) {
				break;
			}
			
			LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int resourceIdForLayoutItem = -1;
			if (styleEnum == StyleEnum.FOR_WRITE_REVIEW) {
				resourceIdForLayoutItem = R.layout.rating_bar_item_for_write_review;
				
				for (Entry<String, String> itemEntry : dataSource.entrySet()) {
					View view = layoutInflater.inflate(resourceIdForLayoutItem, null);
					// 评论项名称
					TextView reviewItemNameTextView = (TextView) view.findViewById(R.id.review_item_name_TextView);
					reviewItemNameTextView.setText(itemEntry.getValue());
					// 评论项具体得分
					TextView reviewTtemScoreTextView = (TextView) view.findViewById(R.id.review_item_score_TextView);
					reviewTtemScoreTextView.setText("5");
					// 星级滑块控件
					RatingBar ratingBar = (RatingBar) view.findViewById(R.id.score_RatingBar);
					ratingBar.setOnRatingBarChangeListener(ratingBarChangeListener);
					//
					ratingBar.setTag(reviewTtemScoreTextView);
					
					valueMap.put(itemEntry.getKey(), reviewTtemScoreTextView);
					this.addView(view);
				}
			} else if (styleEnum == StyleEnum.FOR_TENANT_REVIEWS) {
				resourceIdForLayoutItem = R.layout.rating_bar_item_for_tenant_review;
				
				for (Entry<String, String> itemEntry : dataSource.entrySet()) {
					View view = layoutInflater.inflate(resourceIdForLayoutItem, null);
					// 评论项名称
					TextView reviewItemNameTextView = (TextView) view.findViewById(R.id.review_item_name_TextView);
					reviewItemNameTextView.setText(itemEntry.getKey());
					// 评论项具体得分
					TextView reviewTtemScoreTextView = (TextView) view.findViewById(R.id.review_item_score_TextView);
					reviewTtemScoreTextView.setText(itemEntry.getValue());
					// 星级滑块控件
					RatingBar ratingBar = (RatingBar) view.findViewById(R.id.score_RatingBar);
					ratingBar.setOnRatingBarChangeListener(ratingBarChangeListener);
					try {
						float f = Float.parseFloat(itemEntry.getValue());
						// 不知道为啥这里会报空指针异常
						ratingBar.setRating(f);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//
					ratingBar.setTag(reviewTtemScoreTextView);
					
					valueMap.put(itemEntry.getKey(), reviewTtemScoreTextView);
					this.addView(view);
				}
			} else {
				break;
			}
			
			return;
		} while (false);
		
		DebugLog.i(TAG, "入参错误 ! ");
		return;
	}
	
	private RatingBar.OnRatingBarChangeListener ratingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
		
		@Override
		public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
			TextView reviewTtemScoreTextView = (TextView) ratingBar.getTag();
			reviewTtemScoreTextView.setText(String.valueOf(Float.valueOf(rating).intValue()));
		}
	};
	
	public Map<String, String> getValue() {
		Map<String, String> realValueMap = new HashMap<String, String>();
		for (Entry<String, Object> valueEntry : valueMap.entrySet()) {
			TextView reviewTtemScoreTextView = (TextView) valueEntry.getValue();
			realValueMap.put(valueEntry.getKey(), reviewTtemScoreTextView.getText().toString());
		}
		return realValueMap;
	}
}
