package me.cakegame.database.edit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.cakegame.database.edit.adapter.MainPageAdapter;
import me.cakegame.database.edit.database.CGDataBase;
import me.cakegame.database.edit.dialog.FileSelectorComplete;
import me.cakegame.database.edit.dialog.FileSelectorDialog;
import me.cakegame.database.edit.fragment.MainBasePage;
import me.cakegame.database.edit.fragment.MainManagementPage;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mDrawerNav;
    private BottomNavigationView mBottomNav;
    private ViewPager mPager;

    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragment();
        initView();

    }

    private void initFragment() {
        mFragments.add(new MainBasePage());
        mFragments.add(new MainManagementPage());
    }

    private void initView() {
        /*
        * findView
        * */
        mDrawer = findViewById(R.id.mDrawer);
        mToolbar = findViewById(R.id.main_toolbar);
        mDrawerNav = findViewById(R.id.main_drawer_nav);
        mBottomNav = findViewById(R.id.main_bottom_nav);
        mPager = findViewById(R.id.main_pager);

        /*
        * ActionBar配置
        * */
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open, R.string.close);
        actionBarDrawerToggle.syncState();
        mDrawer.addDrawerListener(actionBarDrawerToggle);

        /*
        * DrawerNav配置
        * */
        mDrawerNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.open_database:
                        openDataBase();
                        break;

                    case R.id.close_database:
                        if (CGDataBase.CGDB == null)
                        {
                            Toast.makeText(MainActivity.this, "数据库未打开", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            CGDataBase.CGDB.close();
                            CGDataBase.CGDB = null;
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("database.close"));
                            Toast.makeText(MainActivity.this, "数据库已关闭", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

                mDrawer.closeDrawers();
                return false;
            }
        });

        /*
        * 底部导航配置
        * */
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.base:
                        mPager.setCurrentItem(0);
                        break;

                    case R.id.management:
                        mPager.setCurrentItem(1);
                        break;
                }
                return false;
            }
        });


        mPager.setAdapter(new MainPageAdapter(getSupportFragmentManager(), mFragments));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mBottomNav.getMenu().getItem(i).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void openDataBase(){
        (new FileSelectorDialog.Build(FileSelectorDialog.getSDPath())).build().show(this, new FileSelectorComplete() {
            @Override
            public void onSelect(File file) {
                Log.d("open database", file.getPath());
                if (CGDataBase.CGDB != null)
                {
                    CGDataBase.CGDB.close();
                }
                if (!CGDataBase.openDataBase(MainActivity.this, file.getPath()))
                {
                    Toast.makeText(MainActivity.this, "数据库打开失败", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("database.success.open"));
                    Toast.makeText(MainActivity.this, "成功打开：" + file.getPath(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
