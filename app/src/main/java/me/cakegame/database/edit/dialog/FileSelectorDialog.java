package me.cakegame.database.edit.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.security.Permission;
import java.util.Stack;

import me.cakegame.database.edit.R;

public class FileSelectorDialog {
    private File mFile;
    private Stack<String> mDir;

    private FileSelectorDialog(Build build) {
        mDir = new Stack<>();

        if (build.root_dir != null) {
            String[] dir = build.root_dir.split("/");
            for (String name : dir) {
                Log.d(build.root_dir, name);
                mDir.push(name);
            }
        }

        mFile = new File(synthetic());
    }

    private String synthetic() {
        String path = new String();
        for (String name : mDir)
        {
            path += name + "/";
        }
        return "/" + path;

    }

    public void show(final Context context, final FileSelectorComplete fileSelectorComplete) {
        if (checkPermission(context) == false)
        {
            return;
        }
        if (mDir.size() == 0)
        {
            Toast.makeText(context, "无外部存储设备", Toast.LENGTH_SHORT).show();
            return;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_file_selector, null);

        final TextView tvRootDir = view.findViewById(R.id.tvRootDir);
        final ListView lvFiles = view.findViewById(R.id.lvFiles);
        ImageButton btnBack = view.findViewById(R.id.btnBack);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setTitle(R.string.please_select_file)
                .create();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDir.size() > 0) {
                    String tmp = mDir.pop();
                    mFile = new File(synthetic());
                    if (mFile.listFiles() == null)
                    {
                        mDir.push(tmp);
                        mFile = new File(synthetic());
                        Toast.makeText(context, "禁止访问", Toast.LENGTH_SHORT).show();
                    }
                    lvFiles.setAdapter(new list_adaper(context, mFile.listFiles()));
                    tvRootDir.setText(mFile.getPath());
                }
            }
        });

        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File select_file = (File) lvFiles.getAdapter().getItem(position);
                if (select_file.isDirectory())
                {
                    mDir.push(select_file.getName());
                    mFile = new File(synthetic());
                    lvFiles.setAdapter(new list_adaper(context, mFile.listFiles()));
                    tvRootDir.setText(mFile.getPath());
                }
                else
                {
                    fileSelectorComplete.onSelect(select_file);
                    dialog.cancel();
                }
            }
        });

        tvRootDir.setText(mFile.getPath());

        lvFiles.setAdapter(new list_adaper(context, mFile.listFiles()));

        dialog.show();

    }

    private boolean checkPermission(Context context) {
        //READ_EXTERNAL_STORAGE WRITE_EXTERNAL_STORAGE
        boolean check = (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED);
        if (check == false)
        {
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        return check;
    }

    public static class Build{
        public String root_dir;

        public Build(String root)
        {
            root_dir = root;
        }

        public FileSelectorDialog build(){
            return new FileSelectorDialog(this);
        }
    }

    private class list_adaper extends BaseAdapter{
        private File[] mItems;
        private Context context;

        protected list_adaper(Context context ,File[] items) {
            mItems = items;
            this.context = context;
        }

        @Override
        public File getItem(int position) {
            return mItems[position];
        }

        @Override
        public int getCount() {
            if (mItems == null)
            {
                return 0;
            }
            return mItems.length;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null)
            {
                view = LayoutInflater.from(context).inflate(R.layout.dialog_file_selector_item, null);
            }

            ImageView type_icon = view.findViewById(R.id.type_icon);
            TextView file_name = view.findViewById(R.id.file_name);

            File item_data = getItem(position);
            if (item_data.isDirectory())
            {
                type_icon.setImageResource(R.drawable.ic_dir);
            }
            else
            {
                type_icon.setImageResource(R.drawable.ic_file);
            }

            file_name.setText(item_data.getName());

            return view;
        }

    }

    /**
     * 获取sd卡的绝对路径
     * @return String 如果sd卡存在，返回sd卡的绝对路径，否则返回null
     **/
    public static String getSDPath(){
        String sdcard= Environment.getExternalStorageState();
        if(sdcard.equals(Environment.MEDIA_MOUNTED)){
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }else{
            return null;
        }
    }
}
