package com.project.indoorlocalization.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import java.util.ArrayList;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.project.indoorlocalization.R;

/**
 * Created by Embers on 2017/6/26.
 */

public class editphoto extends Activity {
    // 声明ListView控件
    private ListView mListView;

    // 声明数组链表，其装载的类型是ListItem(封装了一个Drawable和一个String的类)
    private ArrayList<ListItem> mList;

    // 获取MainListAdapter对象
    MainListViewAdapter adapter = new MainListViewAdapter();
    /**
     * Acitivity的入口方法
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 指定Activity的布局使用activity_main.xml
        setContentView(R.layout.edit_photo);

        ImageView imageView = (ImageView) findViewById(R.id.back);

        // 通过findviewByID获取到ListView对象
        mListView = (ListView) findViewById(R.id.listView1);

        // 获取Resources对象
        Resources res = this.getResources();

        mList = new ArrayList<editphoto.ListItem>();


        ListItem item;
        for (int i = 0; i < Data.imgs.size(); i++) {
            item = new ListItem();
            item.setImage(Data.imgs.get(i));
            mList.add(item);
        }
            // 将MainListAdapter对象传递给ListView视图
            mListView.setAdapter(adapter);
            setOnItemClickListene();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    public void setOnItemClickListene() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(editphoto.this);

                builder.setMessage("确认删除吗");
                builder.setTitle("提示");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        arg0.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        mList.remove(i);
                        adapter.notifyDataSetChanged();
                        arg0.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }



    /**
     * 定义ListView适配器MainListViewAdapter
     */

    class MainListViewAdapter extends BaseAdapter {

        /**
         * 返回item的个数
         */
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList.size();
        }

        /**
         * 返回item的内容
         */
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mList.get(position);
        }

        /**
         * 返回item的id
         */
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        /**
         * 返回item的视图
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItemView listItemView;

            // 初始化item view
            if (convertView == null) {
                // 通过LayoutInflater将xml中定义的视图实例化到一个View中
                convertView = LayoutInflater.from(editphoto.this).inflate(
                        R.layout.photo_store, null);

                // 实例化一个封装类ListItemView，并实例化它的两个域
                listItemView = new ListItemView();
                listItemView.imageView = (ImageView) convertView
                        .findViewById(R.id.image);
                listItemView.textView = (TextView) convertView
                        .findViewById(R.id.title);

                // 将ListItemView对象传递给convertView
                convertView.setTag(listItemView);
            } else {
                // 从converView中获取ListItemView对象
                listItemView = (ListItemView) convertView.getTag();
            }

            // 获取到mList中指定索引位置的资源
            Bitmap img = mList.get(position).getImage();
            String title = mList.get(position).getTitle();

            // 将资源传递给ListItemView的两个域对象
            listItemView.imageView.setImageBitmap(img);
            listItemView.textView.setText(title);

            // 返回convertView对象
            return convertView;
        }

    }

    /**
     * 封装两个视图组件的类
     */
    class ListItemView {
        ImageView imageView;
        TextView textView;
    }

    /**
     * 封装了两个资源的类
     */
    class ListItem {
        private Bitmap image;
        private String title;

        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }



}
