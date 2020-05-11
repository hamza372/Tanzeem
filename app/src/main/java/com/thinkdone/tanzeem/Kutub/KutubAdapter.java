package com.thinkdone.tanzeem.Kutub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thinkdone.tanzeem.Constants;
import com.thinkdone.tanzeem.DB.DBHelper;
import com.thinkdone.tanzeem.DataModels.KitabDataModel;
import com.thinkdone.tanzeem.Pages.PageViewActivity;
import com.thinkdone.tanzeem.R;

import java.util.ArrayList;

public class KutubAdapter extends RecyclerView.Adapter<KutubAdapter.viewHolder> {
    ArrayList<KitabDataModel> arrayList = new ArrayList<>();
    Context context;
    Activity activity;

    public KutubAdapter(Context context, Activity activity) {

        this.context = context;
        this.activity = activity;
    }

    public void addKitab(KitabDataModel kitabDataModel)
    {
        arrayList.add(kitabDataModel);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_for_book_new,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, final int position) {
        holder.bookName.setText(arrayList.get(position).getBookName());
       //// holder.bookNo.setText((position+1)+"");
        Log.d("tryImage","http://tanzeemdigitallibrary.com/BookImages/"+arrayList.get(position).getBookImage());
        String encodedUrl = "http://tanzeemdigitallibrary.com/BookImages/"+arrayList.get(position).getBookImage();

        encodedUrl=encodedUrl.replaceAll(" ", "%20");
        Log.d("tryUrl",encodedUrl);
        // encodedUrl = URLEncoder.encode("http://tanzeemdigitallibrary.com/BookImages/"+arrayList.get(position).getBookImage(), "utf-8");


        String str = arrayList.get(position).getKitabNameEng();
        Log.d("tryName","k"+str);
        if(str != null){
            str = str.toLowerCase();
            Log.d("tryBName","k"+str);
            str = str.replaceAll(" ","");
            Log.d("tryAName","k"+str);
            if(str.contains(".")){
                str = str.replaceAll(".","");
            }
            if(str.contains("-")){
                str = str.replaceAll("-","");
            }
            if(str.contains(",")){
                str = str.replaceAll(",","");
            }
            Log.d("tryame","k"+str);
        }

        Log.d("tryKName","k"+str);
       // holder.image.setImageResource( context.getResources().getIdentifier(str, "drawable", context.getPackageName()));
        Glide.with(context).load(context.getResources().getIdentifier(str, "drawable", context.getPackageName()))   .into(holder.image);
        if(str.equals("")){
            Glide.with(context).load(encodedUrl).into(holder.image);
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PageViewActivity.class);
                intent.putExtra(Constants.BOOK,arrayList.get(position).getBookId());
                Log.d("tryBName","k"+arrayList.get(position).getBookId()+"");
                intent.putExtra(Constants.BOOKNAME,arrayList.get(position).getBookName());
                intent.putExtra(DBHelper.PageEntry.PAGE_NO,-1);
                intent.putExtra(Constants.IS_SEARCH,false);
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

        ConstraintLayout layout;
        ImageView image;
        TextView bookName;
        //TextView bookNo;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.kcard);
            image = itemView.findViewById(R.id.imageView4);
            bookName = itemView.findViewById(R.id.tvname);
            //   bookNo = itemView.findViewById(R.id.textView9);
        }
    }
}
