package com.thinkdone.tanzeem.PageList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkdone.tanzeem.DataModels.PageDataModel;
import com.thinkdone.tanzeem.Pages.PageViewActivity;
import com.thinkdone.tanzeem.R;

import java.util.ArrayList;

public class PageListAdapter extends RecyclerView.Adapter<PageListAdapter.viewHolder> {
    ArrayList<PageDataModel> arrayList = new ArrayList<>();
    Context context;

    @NonNull
    @Override
    public PageListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_for_page_list,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PageListAdapter.viewHolder holder, int position) {
        holder.pageNo.setText("صفحہ نمبر: " +arrayList.get(position).getPage_no());
        holder.bookName.setText(arrayList.get(position).getBookName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PageViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public class viewHolder extends RecyclerView.ViewHolder
    {

        CardView layout;
        TextView bookName;
        TextView pageNo;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.bmcard);
            bookName = itemView.findViewById(R.id.nametvcat);            pageNo = itemView.findViewById(R.id.textView4);
        }
    }

}
