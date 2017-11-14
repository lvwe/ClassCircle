package com.example.administrator.classcircle.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.RadioGroup;

import com.example.administrator.classcircle.R;
import com.example.administrator.classcircle.activity.BaseActivity;
import com.example.administrator.classcircle.fragment.Fragment01;
import com.example.administrator.classcircle.fragment.Fragment02;
import com.example.administrator.classcircle.fragment.Fragment03;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {


    private RadioGroup mRadioGroup;
    //    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private long mExitTime;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        initViewPager();
        mRadioGroup = (RadioGroup) findViewById(R.id.id_main_radio_group);
//        initToolBar();

    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.id_main_view_pager);
        final List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new Fragment01());
        fragmentList.add(new Fragment02());
        fragmentList.add(new Fragment03());
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
    }

    @Override
    protected void initListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.id_main_radio_button01:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.id_main_radio_button02:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.id_main_radio_button03:
                        mViewPager.setCurrentItem(2);
                        break;
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mRadioGroup.check(R.id.id_main_radio_button01);
                        break;
                    case 1:
                        mRadioGroup.check(R.id.id_main_radio_button02);
                        break;
                    case 2:
                        mRadioGroup.check(R.id.id_main_radio_button03);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logout();
    }

    private void logout() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            showToast("再按一次退出");
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}
