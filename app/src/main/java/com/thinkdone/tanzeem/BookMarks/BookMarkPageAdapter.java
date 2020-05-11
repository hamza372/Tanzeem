package com.thinkdone.tanzeem.BookMarks;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkdone.tanzeem.Constants;
import com.thinkdone.tanzeem.DB.BookmarkDBHelper;
import com.thinkdone.tanzeem.DB.DBHelper;
import com.thinkdone.tanzeem.DataModels.PageDataModel;
import com.thinkdone.tanzeem.Pages.PageViewActivity;
import com.thinkdone.tanzeem.R;

import java.util.ArrayList;
// 	#282117
//FF4D3921
public class BookMarkPageAdapter extends RecyclerView.Adapter<BookMarkPageAdapter.viewHolder> {
    ArrayList<PageDataModel> arrayList = new ArrayList<>();
    Context context;
    BookmarkDBHelper bookmarkDBHelper;

    public BookMarkPageAdapter(Context context) {
        this.context = context;
        bookmarkDBHelper = new BookmarkDBHelper(context);
    }

    public void addPage(PageDataModel pageDataModel)
    {
        arrayList.add(pageDataModel);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_for_bookmark_page_list,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int position) {
        holder.pageNo.setText("صفحہ نمبر: " +arrayList.get(position).getPage_no());
        holder.bookName.setText(arrayList.get(position).getBookName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PageViewActivity.class);
                intent.putExtra(Constants.IS_SEARCH,false);
                intent.putExtra(Constants.BOOK,arrayList.get(position).getBookId());
                intent.putExtra(Constants.BOOKNAME,arrayList.get(position).getBookName());
                intent.putExtra(DBHelper.PageEntry.PAGE_NO,arrayList.get(position).getPage_no());
                intent.putExtra(DBHelper.BaabEntry.ID,arrayList.get(position).getPageBaabId());
                intent.putExtra(Constants.IS_SEARCH,false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.fav.setImageResource(R.drawable.forma_1_copy_4);
        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bookmarkDBHelper.isPageFavourite(arrayList.get(position).getPage_id())) {
                    ////Log.d("tryFav",bookDataModel.getBookNameEnglish());
                    bookmarkDBHelper.addPage(arrayList.get(position).getPage_id(), arrayList.get(position).getPage_no(), arrayList.get(position).getBookName(),arrayList.get(position).getBookId(),arrayList.get(position).getPageBaabId(),arrayList.get(position).getBookName());
                    Toast.makeText(context, "پسندیدہ قرار", Toast.LENGTH_SHORT).show();
                    holder.fav.setImageResource(R.drawable.forma_1_copy_4);
                } else {
                    holder.fav.setImageResource(R.drawable.ic_star_border_black_24dp);
                    bookmarkDBHelper.deletePage(arrayList.get(position).getPage_id() );
                    Toast.makeText(context, "ہٹا دیا گیا", Toast.LENGTH_SHORT).show();
                }
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
        ImageView fav;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.bmcard);
            bookName = itemView.findViewById(R.id.nametvcat);
            pageNo = itemView.findViewById(R.id.textView4);
            fav = itemView.findViewById(R.id.imageView6);
        }
    }
}
