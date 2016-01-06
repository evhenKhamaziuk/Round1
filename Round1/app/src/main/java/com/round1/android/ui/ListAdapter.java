package com.round1.android.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.round1.android.R;
import com.round1.android.model.WebSiteModel;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    private ArrayList<WebSiteModel> dataList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ListAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (dataList == null || dataList.get(position) == null){
            return;
        }

        final WebSiteModel data = dataList.get(position);

        holder.tvTitle.setText(data.name);
        holder.rlMainContainer.setOnClickListener(onItemListClickListener);
        holder.rlMainContainer.setTag(R.id.data_id, data);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public void swapData(ArrayList<WebSiteModel> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTitle;
        public RelativeLayout rlMainContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.item_list_tv_title);
            rlMainContainer = (RelativeLayout) itemView.findViewById(R.id.item_list_rl_main_container);
        }
    }

    private View.OnClickListener onItemListClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(view);
        }
    };

    public interface OnItemClickListener{
        void onItemClick(View view);
    }
}
