package com.yeeseng.westerndeli.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.Menu_Item;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Menu_Item> list_item;
    private Context context;
    private static MenuItemListener itemListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public MenuItemAdapter(List<Menu_Item> list_data, Context context, MenuItemListener itemListener) {
        this.list_item = list_data;
        this.context = context;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
            return new MenuItemAdapter.ViewHolder(view);
        } else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new MenuItemAdapter.LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {
            populateItemRows((ViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }

    }

    private void populateItemRows(ViewHolder holder, int position) {
        Menu_Item listData=list_item.get(position);

        Picasso.get()
                .load(listData.getItemUrl())
                .into(holder.img);

        holder.name.setText(listData.getItemName());
        holder.cost.setText(" RM" + listData.getItemCost().toString());
        holder.prepTime.setText(" " + listData.getItemPrepTime().toString() + " Minutes");
        if (listData.getChefRecommended()) {
            holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_rated,0 );
        }
    }

    @Override
    public int getItemCount() {
        return list_item == null ? 0 : list_item.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.imageview) ImageView img;
        @BindView(R.id.itemTitle) TextView name;
        @BindView(R.id.itemCost) TextView cost;
        @BindView(R.id.prepTime) TextView prepTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewMenuListClicked(v, this.getLayoutPosition());
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progressBar) ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    public int getItemViewType(int position) {
        return list_item.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void clear() {
        int size = list_item.size();
        list_item.clear();
        notifyItemRangeRemoved(0, size);
    }
}
