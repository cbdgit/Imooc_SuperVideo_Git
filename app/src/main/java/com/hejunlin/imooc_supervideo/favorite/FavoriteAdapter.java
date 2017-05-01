package com.hejunlin.imooc_supervideo.favorite;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hejunlin.imooc_supervideo.AlbumDetailActivity;
import com.hejunlin.imooc_supervideo.R;
import com.hejunlin.imooc_supervideo.model.Album;
import com.hejunlin.imooc_supervideo.model.AlbumList;
import com.hejunlin.imooc_supervideo.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hejunlin on 17/4/30.
 */

public class FavoriteAdapter extends BaseAdapter {

    private static final int TYPE_COUNT = 2;
    private Context mContext;
    private AlbumList mAlbumList;// 数据list
    private boolean mShowChecked;// item 选中态
    private List<FavoriteAlbum> mFavoriteList;//收藏list

    public FavoriteAdapter(Context context, AlbumList list) {
        mAlbumList = list;
        mContext = context;
        mShowChecked = false;
        mFavoriteList = new ArrayList<>();
        for (Album album : mAlbumList) { //遍历取数据
            mFavoriteList.add(new FavoriteAlbum(album));
        }
    }

    @Override
    public int getCount() {
        return mFavoriteList.size();
    }

    public boolean isSelected() {
        return mShowChecked;
    }

    @Override
    public FavoriteAlbum getItem(int position) {
        return mFavoriteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mShowChecked ? 1: 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FavoriteAlbum favoriteAlbum = getItem(position);
        final Album album = favoriteAlbum.getAlbum();
        final ViewHolder holder;
        if (convertView == null) {
            convertView =  LayoutInflater.from(mContext).inflate(R.layout.favorite_item, null);
            holder = new ViewHolder();
            holder.mAlbumName = (TextView)convertView.findViewById(R.id.tv_album_name);
            holder.mCbButton = (CheckBox) convertView.findViewById(R.id.cb_favorite);
            holder.mAlbumPost = (ImageView) convertView.findViewById(R.id.iv_album_poster);
            holder.mContainer = (RelativeLayout) convertView.findViewById(R.id.favorite_container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mShowChecked) {
            holder.mCbButton.setVisibility(View.VISIBLE);
        } else {
            holder.mCbButton.setVisibility(View.GONE);
        }
        if (mFavoriteList.size() > 0) {
            holder.mAlbumName.setText(album.getTitle());
            //重新计算宽高
            Point point =  ImageUtils.getVerPostSize(mContext, 3);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(point.x, point.y);
            holder.mAlbumPost.setLayoutParams(params);
            if (album.getVerImgUrl()!= null) {
                ImageUtils.disPlayImage(holder.mAlbumPost, album.getVerImgUrl(), point.x, point.y);
            } else if (album.getHorImgUrl() != null) {
                ImageUtils.disPlayImage(holder.mAlbumPost, album.getHorImgUrl(), point.x, point.y);
            }
            holder.mCbButton.setChecked(favoriteAlbum.isIsChecked());
            if (!mShowChecked) {
                holder.mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击跳转详情页
                        AlbumDetailActivity.launch((Activity) mContext, album);
                    }
                });
                //长按可删除
                holder.mContainer.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        favoriteAlbum.setIsChecked(true);//当前item选中
                        setShowChecked(true);
                        mFavoriteList.get(position).setIsChecked(true);
                        holder.mContainer.setVisibility(View.VISIBLE);
                        notifyDataSetChanged();//刷新,相当于调用getview
                        return true;
                    }
                });
            } else { //处理当有一个item被选中时,再选其他item情况
                holder.mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean checked = favoriteAlbum.isIsChecked();
                        favoriteAlbum.setIsChecked(checked);
                        holder.mCbButton.setChecked(!checked);
                        favoriteAlbum.setIsChecked(!checked);
                    }
                });
            }
        }
        return convertView;
    }

    public void setShowChecked(boolean isSelected) {
        this.mShowChecked = isSelected;
    }

    public List<FavoriteAlbum> getFavorAlbmList() {
        return mFavoriteList;
    }

    public void optionCheckedAllItem(boolean isSelected) {
        for (FavoriteAlbum favoriteAlbum : mFavoriteList) {
            favoriteAlbum.setIsChecked(isSelected);
        }
    }

    class ViewHolder {
        ImageView mAlbumPost;
        TextView mAlbumName;
        CheckBox mCbButton;
        RelativeLayout mContainer;
    }

    class FavoriteAlbum {
        private Album mAlbum;
        private boolean mIsChecked;

        public FavoriteAlbum(Album album) {
            mAlbum = album;
            mIsChecked = false;
        }

        public Album getAlbum() {
            return mAlbum;
        }

        public void setAlbum(Album album) {
            this.mAlbum = album;
        }

        public boolean isIsChecked() {
            return mIsChecked;
        }

        public void setIsChecked(boolean isChecked) {
            this.mIsChecked = isChecked;
        }
    }
}
