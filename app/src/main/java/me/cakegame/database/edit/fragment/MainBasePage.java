package me.cakegame.database.edit.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.cakegame.database.edit.R;
import me.cakegame.database.edit.database.CGDataBase;

public class MainBasePage extends Fragment {
    private View mRootView = null;
    private GridView mAttrList = null, mBackpackList = null;
    private Spinner mUsrList = null;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction())
            {
                case "database.success.open":
                    mRootView.findViewById(R.id.tvUnopenedDatabase).setVisibility(CGDataBase.CGDB!=null?View.GONE:View.VISIBLE);
                    initData();
                    break;

                case "database.close":
                    mRootView.findViewById(R.id.tvUnopenedDatabase).setVisibility(CGDataBase.CGDB!=null?View.GONE:View.VISIBLE);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //加载Fragment布局
        if(null != mRootView){
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            parent.removeView(mRootView);
        }else{
            mRootView = inflater.inflate(R.layout.fragment_base, null);
        }


        initView(mRootView);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("database.success.open");
        intentFilter.addAction("database.close");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadcastReceiver, intentFilter);

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBroadcastReceiver);
    }

    private void initView(View view) {
        view.findViewById(R.id.tvUnopenedDatabase).setVisibility(CGDataBase.CGDB!=null?View.GONE:View.VISIBLE);
        mAttrList = view.findViewById(R.id.attrList);
        mBackpackList = view.findViewById(R.id.backpackList);
        mUsrList = view.findViewById(R.id.usrList);

        mUsrList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAttrList.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, CGDataBase.getUserAttr((String) mUsrList.getAdapter().getItem(position))));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (CGDataBase.CGDB != null)
        {
            initData();
        }
    }

    private void initData() {
        List<String> ids = CGDataBase.getAllUserID();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ids);
        mUsrList.setAdapter(arrayAdapter);
        if (ids.size() > 0) {
            mUsrList.setSelection(0);
        }
    }

}
