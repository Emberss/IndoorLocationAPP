package com.project.indoorlocalization.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.project.indoorlocalization.R;
import com.project.indoorlocalization.utils.Data;
import com.project.indoorlocalization.utils.Utils;

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

        modify = (Button)findViewById(R.id.modify);
        width = (EditText)findViewById(R.id.width);
        height = (EditText)findViewById(R.id.height);

        ImageView imageView = (ImageView) findViewById(R.id.back);
        imageView.setAlpha(70);
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
            //setOnItemLongClickListene();
            setOnItemClick();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        resizeBitmap();
    }

    public void setOnItemLongClickListene() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
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
                        Data.imgs.remove(index);
                        mList.remove(index);
                        adapter.notifyDataSetChanged();
                        arg0.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }

    public void setOnItemClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog alertDialog = new AlertDialog.Builder(editphoto.this).create();
                View view1 = LayoutInflater.from(editphoto.this).inflate(R.layout.dialog,null);
                ImageView imageView = (ImageView)view1.findViewById(R.id.dialog);
                imageView.setImageBitmap(Data.imgs.get(i));
                alertDialog.setView(view1);
                alertDialog.show();
            }
        });
    }


    private Button modify;
    private EditText width, height;
    private void resizeBitmap() {
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (width.getText().length() == 0 || height.getText().length() == 0) {
                    Utils.setToast(editphoto.this, "null");
                } else {
                    int w = Integer.parseInt(width.getText().toString());
                    int h = Integer.parseInt(height.getText().toString());
                    List<Bitmap> tmp = new ArrayList<Bitmap>();
                    for (int i = 0; i < Data.imgs.size(); ++i) {
                        tmp.add(Utils.resizeBitmap(Data.imgs.get(i), w, h));
                    }
                    Data.imgs = tmp;
                    for (int i = 0; i < Data.imgs.size(); ++i) {
                        String path = Data.getPictureSavePath();
                        String name = (i+1)+".png";
                        Utils.saveBitmap(Data.imgs.get(i), path, name);
                    }
                    Utils.setToast(editphoto.this, "修改成功!");
                }
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


                // 将ListItemView对象传递给convertView
                convertView.setTag(listItemView);
            } else {
                // 从converView中获取ListItemView对象
                listItemView = (ListItemView) convertView.getTag();
            }

            // 获取到mList中指定索引位置的资源
            Bitmap img = mList.get(position).getImage();


            // 将资源传递给ListItemView的两个域对象
            listItemView.imageView.setImageBitmap(img);


            // 返回convertView对象
            return convertView;
        }

    }

    /**
     * 封装两个视图组件的类
     */
    class ListItemView {
        ImageView imageView;

    }

    /**
     * 封装了两个资源的类
     */
    class ListItem {
        private Bitmap image;


        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }



    }



}
