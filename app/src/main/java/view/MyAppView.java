package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hansysu.mylauncher.AppInfo;
import hansysu.mylauncher.R;


public class MyAppView extends RelativeLayout{
    public interface OnSelectLister{
        void Onselect(AppInfo info,MyAppView v);
    }
    private ImageView image;
    private TextView tv;
    private AppInfo info;
    private OnSelectLister ls;
    public void setAppInfo( AppInfo info){
        this.info = info;
        image.setImageDrawable(info.getImage());
        tv.setText(info.getLabel());
    }
    public AppInfo getInfo(){
        return info;
    }
    public MyAppView(Context context) {
        super(context);
        initView(context);
    }
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.list_item, this);
        image = (ImageView)findViewById(R.id.app_icon);
        tv = (TextView)findViewById(R.id.app_name);
    }
    public MyAppView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setOnSelectListener(OnSelectLister ls){
        this.ls = ls;
    }

    public MyAppView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public MyAppView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                if (ls != null) {
                    ls.Onselect(info,this);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

}
