package hansysu.mylauncher;
import android.app.Activity;
import android.app.LauncherActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.AppAdapter;
import adapter.ViewPagerAdapter;
import indicate.CirclePageIndicator;
import view.MyAppView;

public class MyLauncher  extends Activity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,View.OnClickListener{
    private int pageSize = 1;
    private int pageIndex = 0;
    private int appClomnNums = 5;
    private ViewPager vPage;
    private List<GridView> gridList = new ArrayList<>();
    private CirclePageIndicator indicator;
    //正在操作的view的标识
    int OpView;
    private PopupWindow mPopupWindow;
    ArrayList<AppInfo> apps;
    private ArrayList<AppAdapter> appAdapter  = new ArrayList<>();
    private ViewPagerAdapter vPageAdapter;
    private LinearLayout favourite;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout. activity_main );
        LauncherPermission desktop=new LauncherPermission(this);
        desktop.getDefaultHome();
        initView();
        loadApps();
        initDatas();

    }

    private void initDatas() {


    }
    private void initView(){
        vPage = (ViewPager) findViewById(R.id.view_pager );
        vPageAdapter = new ViewPagerAdapter();
        vPage.setAdapter(vPageAdapter);
        //圆点指示器
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setVisibility(View.VISIBLE);
        indicator.setViewPager(vPage);
    }
    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = new ArrayList<>();
        List<ResolveInfo> temp= this.getPackageManager().queryIntentActivities(mainIntent, 0);
        //favourite
        favourite = (LinearLayout)findViewById(R.id.favourite);
        for(int i = 0 ; i < 5 ; i++){
            apps.add(new AppInfo());
            apps.get(i).setAppName(temp.get(i).activityInfo.name);
            apps.get(i).setImage(temp.get(i).loadIcon(this.getPackageManager()));
            apps.get(i).setLabel(temp.get(i).loadLabel(this.getPackageManager()).toString());
            apps.get(i).setPackageName(temp.get(i).activityInfo.packageName);
            MyAppView mView = new MyAppView(this);
            LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1);
            mView.setLayoutParams(LP_WW);
            mView.setAppInfo(apps.get(i));
            mView.setOnSelectListener(new MyAppView.OnSelectLister() {
                @Override
                public void Onselect(AppInfo info,MyAppView v) {
                    ComponentName cn = new ComponentName(info.getPackageName(),info.getAppName());
                    Intent intent1 = new Intent();
                    intent1.setComponent(cn);
                    startActivity(intent1);
                }
            });
            favourite.addView(mView);
        }
        for(int i = 5 ; i < temp.size() ; i++){
            apps.add(new AppInfo());
            apps.get(i).setAppName(temp.get(i).activityInfo.name);
            apps.get(i).setImage(temp.get(i).loadIcon(this.getPackageManager()));
            apps.get(i).setLabel(temp.get(i).loadLabel(this.getPackageManager()).toString());
            apps.get(i).setPackageName(temp.get(i).activityInfo.packageName);
        }
        pageSize = apps.size()/(appClomnNums*6)+ (apps.size()%(appClomnNums*6)==0?0:1);
        for (int i = 0; i < pageSize; i++) {
            AppAdapter app = new AppAdapter(apps, this,i);
            GridView gridView = new GridView(this);
            gridView.setNumColumns(appClomnNums);
            gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            gridView.setScrollBarSize(0);
            gridView.setAdapter(app);
            gridView.setOnItemClickListener(this);
            gridView.setOnItemLongClickListener(this);
            gridList.add(gridView);
            appAdapter.add(app);
        }
        vPageAdapter.add(gridList);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppAdapter adapter = (AppAdapter)parent.getAdapter();
        AppInfo app = (AppInfo) adapter.getItem(position);
        if(app.inBlackList){
            return ;
        }
        ComponentName cn = new ComponentName(app.getPackageName(),app.getAppName());
        Intent intent = new Intent();
        intent.setComponent(cn);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AppAdapter adapter = (AppAdapter)parent.getAdapter();
        AppInfo app = (AppInfo) adapter.getItem(position);
        if(app.inBlackList){
            return false;
        }
        OpView = ((int) adapter.getItemId(position));
        showPop(view);
        return true;
    }

    @Override
    public  boolean onKeyDown(int keyCode, KeyEvent event){
        Log.v("keyCode ===",String.valueOf(keyCode));
        if(keyCode == KeyEvent.KEYCODE_BACK){

            return true;
        }
        return false;
    }

    private void showPop(View v) {
        mPopupWindow = new PopupWindow(this);
        // 设置布局文件
        mPopupWindow.setContentView(LayoutInflater.from(this).inflate(R.layout.pop_select, null));
        // 为了避免部分机型不显示，我们需要重新设置一下宽高
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置pop透明效果
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
        // 设置pop出入动画
//        mPopupWindow.setAnimationStyle(R.style.pop_add);
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPopupWindow.setFocusable(true);
        // 设置pop可点击，为false点击事件无效，默认为true
        mPopupWindow.setTouchable(true);
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        mPopupWindow.setOutsideTouchable(true);
        // 相对于 + 号正下面，同时可以设置偏移量
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-100);
        // 设置pop关闭监听，用于改变背景透明度
        mPopupWindow.getContentView().findViewById(R.id.app_del).setOnClickListener(this);
        mPopupWindow.getContentView().findViewById(R.id.app_black_list).setOnClickListener(this);
        mPopupWindow.getContentView().findViewById(R.id.app_info).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.app_del:
                apps.remove(OpView);
                appAdapter.get(vPage.getCurrentItem()).notifyDataSetChanged();
                mPopupWindow.dismiss();
                break;
            case R.id.app_black_list:
                apps.get(OpView).inBlackList = true;
                appAdapter.get(vPage.getCurrentItem()).notifyDataSetChanged();
                mPopupWindow.dismiss();
                break;
            case R.id.app_info:
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + apps.get(OpView).getPackageName()));
                startActivity(intent);
                mPopupWindow.dismiss();
                break;
            case R.id.app_draw:

                break;
        }
    }

//    private void toggleBright() {
//        // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
//        animUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION);
//        animUtil.addUpdateListener(new AnimUtil.UpdateListener() {
//            @Override
//            public void progress(float progress) {
//                // 此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
//                bgAlpha = bright ? progress : (START_ALPHA + END_ALPHA - progress);
//                backgroundAlpha(bgAlpha);
//            }
//        });
//        animUtil.addEndListner(new AnimUtil.EndListener() {
//            @Override
//            public void endUpdate(Animator animator) {
//                // 在一次动画结束的时候，翻转状态
//                bright = !bright;
//            }
//        });
//        animUtil.startAnimator();
//    }
//
//    /**
//     * 此方法用于改变背景的透明度，从而达到“变暗”的效果
//     */
//    private void backgroundAlpha(float bgAlpha) {
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        // 0.0-1.0
//        lp.alpha = bgAlpha;
//        getWindow().setAttributes(lp);
//        // everything behind this window will be dimmed.
//        // 此方法用来设置浮动层，防止部分手机变暗无效
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//    }

}