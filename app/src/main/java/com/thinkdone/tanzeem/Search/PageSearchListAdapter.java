package com.thinkdone.tanzeem.Search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkdone.tanzeem.Constants;
import com.thinkdone.tanzeem.DB.DBHelper;
import com.thinkdone.tanzeem.DataModels.PageDataModel;
import com.thinkdone.tanzeem.R;

import java.util.ArrayList;
// 	#282117
//FF4D3921
public class PageSearchListAdapter extends RecyclerView.Adapter<PageSearchListAdapter.viewHolder> {
    ArrayList<PageDataModel> arrayList = new ArrayList<>();
    Context context;
    String searchString;
    int totalResults;
    Activity activity;

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public PageSearchListAdapter(Context context, String searchString,Activity activity) {
        this.context = context;
        this.searchString = searchString;
        this.activity = activity;
    }

    public void addPage(PageDataModel pageDataModel) {
        arrayList.add(pageDataModel);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_for_page_list,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        holder.pageNo.setText("صفحہ نمبر: " +arrayList.get(position).getPage_no());
        holder.bookName.setText(arrayList.get(position).getBookName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchPageViewActivity.class);
                intent.putExtra(Constants.BOOK,arrayList.get(position).getBookId());
                intent.putExtra("pagesSize",totalResults);
                intent.putExtra("position",position);
                intent.putExtra(Constants.BOOKNAME,arrayList.get(position).getBookName());
                intent.putExtra(DBHelper.PageEntry.PAGE_NO,arrayList.get(position).getPage_no());
                intent.putExtra(DBHelper.BaabEntry.ID,arrayList.get(position).getPageBaabId());
                intent.putExtra("cursorIndex",arrayList.get(position).getCursorIndex());
                intent.putExtra(Constants.IS_SEARCH,true);
                intent.putExtra(Constants.SEARCH,searchString);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        CardView layout;
        TextView bookName;
        TextView pageNo;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.bmcard);
            bookName = itemView.findViewById(R.id.nametvcat);
            pageNo = itemView.findViewById(R.id.textView4);
        }
    }
}
