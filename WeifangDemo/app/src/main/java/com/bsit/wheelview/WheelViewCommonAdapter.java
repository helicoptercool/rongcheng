package com.bsit.wheelview;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bsit.pboard.R;
import java.util.List;

public class WheelViewCommonAdapter extends BaseAdapter {

    private List<String> listData;
    private Context context;
    private int selectPosition;
    private int nomalColor;
    private int checkColor;
    private int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    private int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
//	private int mHeight = 40;

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    public WheelViewCommonAdapter(Context context, List<String> listData) {
        this.context = context;
        this.listData = listData;
        nomalColor = context.getResources().getColor(R.color.sum_text2);
        checkColor = context.getResources().getColor(R.color.color_3e84cc);
//		mHeight = (int) pixelToDp(context, mHeight);
    }

    public static float pixelToDp(Context context, float val) {
        float density = context.getResources().getDisplayMetrics().density;
        return val * density;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;

        if (null == convertView) {
            convertView = new TextView(context);
            convertView.setLayoutParams(new TosGallery.LayoutParams(mWidth,
                    mHeight));
            textView = (TextView) convertView;
            textView.setGravity(Gravity.CENTER);
            textView.setSingleLine();
            textView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            textView.setTextColor(nomalColor);
        }

        if (null == textView) {
            textView = (TextView) convertView;
        }

        textView.setText(listData.get(position));

        if (selectPosition == position) {
            textView.setTextColor(checkColor);
        } else {
            textView.setTextColor(nomalColor);
        }

        return convertView;
    }

}
