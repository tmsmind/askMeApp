package com.android.askme.utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.askme.R;


/**
 * Created by dhanraj on 13/10/15.
 */
public class ListItem extends LinearLayout {

    String title,subTitle;
    @DrawableRes int icon;
    TextView titleTV,subTitleTV;
    ImageView iconIV;
    boolean showIcon;

    public ListItem(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_row, this, true);
        View view = inflater.inflate(R.layout.listview_row, this, true);

        iconIV = (ImageView) view.findViewById(R.id.icon);
        titleTV = (TextView) view.findViewById(R.id.name);
        subTitleTV = (TextView) view.findViewById(R.id.description);
    }

    public ListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ListItem, 0, 0);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_row, this, true);

        icon = a.getResourceId(R.styleable.ListItem_li_icon, R.drawable.ic_person);
        title = a.getString(R.styleable.ListItem_li_title);
        subTitle = a.getString(R.styleable.ListItem_li_desc);
        showIcon = a.getBoolean(R.styleable.ListItem_li_show_icon, true);

        iconIV = (ImageView) view.findViewById(R.id.icon);
        titleTV = (TextView) view.findViewById(R.id.name);
        subTitleTV = (TextView) view.findViewById(R.id.description);

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        iconIV.setImageResource(icon);
        titleTV.setText(title);
        subTitleTV.setText(subTitle);
        iconIV.setVisibility(showIcon ? VISIBLE : GONE);
    }

    public void setTitle(String title){
        titleTV.setText(title);
        invalidate();
    }

    public void setSubTitle(String subTitle) {
        subTitleTV.setText(subTitle);
        invalidate();
    }

    public void setIcon(@DrawableRes int icon){
        iconIV.setImageResource(icon);
        invalidate();
    }

    public void showIcon(boolean showIcon){
        iconIV.setVisibility(showIcon ? VISIBLE : GONE);
        invalidate();
    }

    public ImageView getIconIV(){
        return iconIV;
    }

    public void setSingleLine(boolean singleLine){
        titleTV.setSingleLine(singleLine);
        invalidate();
    }

}
