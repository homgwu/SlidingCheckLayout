package com.homg.slidingchecklayout;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by homgwu on 2018/2/2.
 */

public class MainRvAdapter extends RecyclerView.Adapter<MainRvAdapter.MainViewHolder> {
    private List<MainEntity> mDataList = new ArrayList<>();

    public MainRvAdapter(List<MainEntity> dataList) {
        mDataList = dataList;
    }

    public MainEntity getEntityByPosition(int position) {
        return mDataList.get(position);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_rv, parent, false);
        return new MainViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, final int position) {
        MainEntity entity = mDataList.get(position);
        holder.mNameTv.setText(entity.mName);
        if (entity.isSelect()) {
            holder.itemView.setBackgroundResource(R.color.colorAccent);
            holder.mNameTv.setTextColor(ContextCompat.getColor(holder.mNameTv.getContext(), android.R.color.holo_orange_light));
        } else {
            holder.itemView.setBackgroundResource(R.color.colorPrimaryDark);
            holder.mNameTv.setTextColor(ContextCompat.getColor(holder.mNameTv.getContext(), android.R.color.darker_gray));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainEntity entityC = mDataList.get(position);
                entityC.setSelect(!entityC.isSelect());
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTv;

        public MainViewHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.item_name_tv);
        }
    }
}
