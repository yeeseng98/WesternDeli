package com.yeeseng.westerndeli.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.List_Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
    private List<List_Category>list_category;
    private Context context;
    private static MainListener itemListener;

    public MainAdapter(List<List_Category> list_data, Context context, MainListener itemListener) {
        this.list_category = list_data;
        this.context = context;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_category,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List_Category listData=list_category.get(position);
        Picasso.get()
                .load(listData.getCategoryUrl())
                .into(holder.img);

        holder.title.setText(listData.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return list_category.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.imageview) ImageView img;
        @BindView(R.id.menuTitle) TextView title;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }
    }


}
