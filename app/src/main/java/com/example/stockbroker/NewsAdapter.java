package com.example.stockbroker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    class NewsHolder2 extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView source, dateFrom, title;
        public NewsHolder2(@NonNull View itemView) {
            super(itemView);
            this.iv = itemView.findViewById(R.id.imageView2);
            this.source = itemView.findViewById(R.id.source_side);
            this.dateFrom = itemView.findViewById(R.id.date_from_side);
            this.title = itemView.findViewById(R.id.title_side);
        }
    }

    Context c;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }



    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0){
            return 0;
        }
        else {
            return 2;
        }
    }

}
