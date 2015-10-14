package humesis.apps.humesisdirectionapp.utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import humesis.apps.humesisdirectionapp.R;

/**
 * Created by dhanraj on 13/10/15.
 */
public class ListItem extends LinearLayout {

    String title,subTitle;
    @DrawableRes int icon;
    TextView titleTV,subTitleTV;
    ImageView iconIV;

    public ListItem(Context context) {
        super(context);
    }

    public ListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ListItem, 0, 0);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_row, this, true);

        icon = a.getResourceId(R.styleable.ListItem_li_icon, R.drawable.ic_person);
        title = a.getString(R.styleable.ListItem_li_title);
        subTitle = a.getString(R.styleable.ListItem_li_desc);

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        iconIV = (ImageView) findViewById(R.id.icon);
        titleTV = (TextView) findViewById(R.id.name);
        subTitleTV = (TextView) findViewById(R.id.description);

        iconIV.setImageResource(icon);
        titleTV.setText(title);
        subTitleTV.setText(subTitle);
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

}
