package me.cakegame.database.edit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.cakegame.database.edit.database.CGDataBase;

public class UserAttrListAdapter extends BaseAdapter {
    private List<String> mAttrs;
    private Context mContext;

    public UserAttrListAdapter(Context context, List<String> attrs) {
        mAttrs = attrs;
        mContext = context;
    }

    public void setItemData(int position, String data){
        if (mAttrs!=null && mAttrs.size() > position) {
            mAttrs.set(position, data);
        }
    }


    @Override
    public String getItem(int position) {
        return mAttrs.get(position);
    }

    @Override
    public int getCount() {
        if (mAttrs == null)
        {
            return 0;
        }
        return mAttrs.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) convertView;
        if (view == null) {
            view = (TextView) LayoutInflater.from(mContext).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        String text = getItem(position);

        if (position % 3 == 1 || position % 3 == 0)
        {
            text = CGDataBase.attrTranslation(text);
        }

        view.setText(text);

        return view;
    }
}
