package adapter;

        import android.app.Activity;
        import android.graphics.Color;
        import android.util.DisplayMetrics;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.WindowManager;
        import android.widget.AbsListView;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import java.util.ArrayList;

        import hansysu.mylauncher.AppInfo;
        import hansysu.mylauncher.R;
        import view.MyAppView;

public class AppAdapter extends BaseAdapter{
    private ArrayList<AppInfo> appinfos;
    private Activity context;
    private int pageIndex;
    private int rows = 6;
    private int cols = 5;
    public AppAdapter(ArrayList<AppInfo> appinfos, Activity context,int pageIndex){
        this.appinfos = appinfos;
        this.context = context;
        this.pageIndex = pageIndex;
    }
    @Override
    public int getCount() {
        int nums = appinfos.size() - pageIndex*cols*rows;
        if(nums>=cols*rows){
            return cols*rows;
        }else{
            return  nums;
        }
    }

    @Override
    public Object getItem(int position) {
        return appinfos.get(position+pageIndex*cols*rows);
    }

    @Override
    public long getItemId(int position) {
        return position+pageIndex*cols*rows;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHoler;
        if (convertView == null) {
            convertView = new MyAppView(context);
            WindowManager manager = context.getWindowManager();
            DisplayMetrics outMetrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(outMetrics);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(outMetrics.widthPixels/cols, (outMetrics.heightPixels-80)/rows);
            convertView.setLayoutParams(param);
            vHoler = new ViewHolder();
            vHoler.image = (ImageView)convertView.findViewById(R.id.app_icon);
            vHoler.tv = (TextView) convertView.findViewById(R.id.app_name);
            convertView.setTag(vHoler);
        }else{
            vHoler = (ViewHolder) convertView.getTag();
        }
        AppInfo info = (AppInfo)getItem(position);
        vHoler.image.setImageDrawable(info.getImage());
        vHoler.tv.setText(info.getLabel());
        if(info.inBlackList){
            convertView.setBackgroundColor(Color.GRAY);
        }else{
            convertView.setBackgroundColor(Color.WHITE           );
        }
        return convertView;
    }
    static class ViewHolder{
        ImageView image;
        TextView tv;
    }
}


