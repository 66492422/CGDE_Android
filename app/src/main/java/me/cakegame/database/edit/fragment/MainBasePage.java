package me.cakegame.database.edit.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;

import java.util.List;

import me.cakegame.database.edit.R;
import me.cakegame.database.edit.adapter.UserAttrListAdapter;
import me.cakegame.database.edit.database.CGDataBase;
import me.cakegame.database.edit.util.Calculate;

public class MainBasePage extends Fragment {
    private View mRootView = null;
    private GridView mAttrList = null, mBackpackList = null;
    private Spinner mUsrList = null;
    private String mCurrentUser = null;

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
                    mCurrentUser = null;
                    updateAttrAdapter(null);
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

        //注册本地广播，用于接收数据库打开或关闭事件
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
        //根据数据是否已打开设置隐藏或显示遮罩
        view.findViewById(R.id.tvUnopenedDatabase).setVisibility(CGDataBase.CGDB!=null?View.GONE:View.VISIBLE);

        //findByView
        mAttrList = view.findViewById(R.id.attrList);
        mBackpackList = view.findViewById(R.id.backpackList);
        mUsrList = view.findViewById(R.id.usrList);

        //UserID下拉列表被选中事件
        mUsrList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentUser = (String) mUsrList.getAdapter().getItem(position);
                updateAttrAdapter(CGDataBase.getUserAttr(mCurrentUser));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //用户属性表格被点击事件
        mAttrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                /*
                * 用户点击属性，进入修改属性
                * */
                Adapter adapter = mAttrList.getAdapter();
                //计算用户点击的项目行开始的位置
                final int row_start = Calculate.GridRowPosition(position, 3);

                //取得用户点击的属性类型
                final String node = (String) adapter.getItem(row_start);
                final String item = (String) adapter.getItem(row_start + 1);

                //修改对话视图创建
                final View dialog_view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_text, null);
                final EditText inputData = dialog_view.findViewById(R.id.inputData);

                //设置默认数据
                inputData.setText((String) adapter.getItem(row_start + 2));

                //构造数据修改对话
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialog_view);
                builder.setTitle(CGDataBase.attrTranslation(node)+"-"+CGDataBase.attrTranslation(item));
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String changeData = inputData.getText().toString();
                        CGDataBase.updateUserAttr(mCurrentUser, node, item, changeData);
                        ((UserAttrListAdapter) mAttrList.getAdapter()).setItemData(row_start + 2, changeData);
                    }
                });

                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();

            }
        });

        if (CGDataBase.CGDB != null)
        {
            initData();
        }
    }

    /*
    * 初始化数据
    * */
    private void initData() {
        List<String> ids = CGDataBase.getAllUserID();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ids);
        mUsrList.setAdapter(arrayAdapter);
        if (ids.size() > 0) {
            mUsrList.setSelection(0);
        }
    }

    private void updateAttrAdapter(List<String> data) {
        mAttrList.setAdapter(new UserAttrListAdapter(getContext(), data));
    }

}
