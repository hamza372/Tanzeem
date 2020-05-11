package com.thinkdone.tanzeem.Categories;

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
import com.thinkdone.tanzeem.DataModels.CategoryDataModel;
import com.thinkdone.tanzeem.Kutub.KutubListActivity;
import com.thinkdone.tanzeem.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewHolder> {

    ArrayList<CategoryDataModel> arrayList = new ArrayList<>();
    Context context;
    Activity activity;

    public void addCategory(CategoryDataModel categoryDataModel)
    {
        arrayList.add(categoryDataModel);
    }
    public CategoryAdapter(Context context,Activity activity) {this.activity = activity;
        this.context = context;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_for_categories,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
         holder.title.setText(arrayList.get(position).getCategoryNameUrdu());
         holder.mainLayout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(context, KutubListActivity.class);
                 intent.putExtra(Constants.CATEGORY,arrayList.get(position).getCategoryId());
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


    public class viewHolder extends RecyclerView.ViewHolder{

        TextView title;
        CardView mainLayout;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = (CardView) itemView.findViewById(R.id.catcard);
            title = (TextView) itemView.findViewById(R.id.nametvcat);
        }
    }
}
