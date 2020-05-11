package com.thinkdone.tanzeem.Updates;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thinkdone.tanzeem.DBFiles.KutubDBHelperFile;
import com.thinkdone.tanzeem.DataModels.KitabDataModel;
import com.thinkdone.tanzeem.Database;
import com.thinkdone.tanzeem.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.GONE;

public class BookDownloadAdapter extends RecyclerView.Adapter<BookDownloadAdapter.viewHolder> {
    ArrayList<KitabDataModel> arrayList = new ArrayList<>();
    Context context;
    Activity activity;
    ArrayList<String> existingFiles;
    public BookDownloadAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        existingFiles = KutubDBHelperFile.getDatabasesList();
    }

    public void addKitab(KitabDataModel kitabDataModel)
    {
        arrayList.add(kitabDataModel);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_for_book_download,parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, final int position) {
        holder.download.setImageResource(R.drawable.dbtn);
        holder.del.setImageResource(R.drawable.ic_close_black_24dp);
        if(!existingFiles.contains(arrayList.get(position).getFileName()))
        {
            holder.del.setVisibility(GONE);
        }else{
            holder.download.setVisibility(GONE);
        }
        holder.bookName.setText(arrayList.get(position).getBookName());
        // holder.bookNo.setText((position+1)+"");
        Log.d("tryImage","http://tanzeemdigitallibrary.com/BookImages/"+arrayList.get(position).getBookImage());
        Glide.with(context).load(arrayList.get(position).getBookImage()).into(holder.image);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, PageViewActivity.class);
//                intent.putExtra(Constants.BOOK,arrayList.get(position).getBookId());
//                intent.putExtra(Constants.BOOKNAME,arrayList.get(position).getBookName());
//                intent.putExtra(DBHelper.PageEntry.PAGE_NO,-1);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDatabaseDownloadTask == null) {
                    DB_DOWNLOAD_PATH = arrayList.get(position).getDownloadLink();
                    mDatabaseDownloadTask = new DatabaseDownloadTask(arrayList.get(position));
                    mDatabaseDownloadTask.execute(new Context[]{context});
                }else{
                    Toast.makeText(context, "Already downloading", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle("Alert!!!!");
                dialog.setMessage("Are you want to delete this book");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File f = new File(KutubDBHelperFile.getDatabaseFolder()+"/"+arrayList.get(position).getFileName());
                        if(f.exists())
                        {
                            f.delete();
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

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
        ImageView download;
        ImageView del;
        //TextView bookNo;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            del = itemView.findViewById(R.id.imageView12);
            layout = itemView.findViewById(R.id.kcard);
            image = itemView.findViewById(R.id.imageView4);
            bookName = itemView.findViewById(R.id.textView5);
            download = itemView.findViewById(R.id.imageView11);
            //   bookNo = itemView.findViewById(R.id.textView9);
        }
    }


    private static final String SD_CARD_FOLDER = "MyApp";
    private static String DB_DOWNLOAD_PATH = "https://img.raymond.cc/blog/wp-content/uploads/2011/02/rapidgator_download.png";//"https://www.dropbox.com/s/49yvcios1e7dn55/2018-09-24%2020.10.17.jpg?dl=0";//"https://www.dropbox.com/s/7na222lb2l8ldf7/UrduFatwa.db?dl=0";
    private Database mDB = null;
    private DatabaseDownloadTask mDatabaseDownloadTask = null;

    ProgressDialog mProgressDialog;

    private class DatabaseDownloadTask extends AsyncTask<Context, Integer, Boolean> {

        KitabDataModel model;

        public DatabaseDownloadTask(KitabDataModel model) {
            this.model = model;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setTitle("Starting download "+model.getBookName());
            mProgressDialog.setMessage("It will take some time");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        @Override
        protected Boolean doInBackground(Context... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(DB_DOWNLOAD_PATH);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    /*return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();*/
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(Database.getDatabaseFolder()+model.getFileName());

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }

        }

        protected void onProgressUpdate(Integer... values) {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.setProgress(values[0]);
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mDatabaseDownloadTask = null;
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (result.equals(Boolean.TRUE)) {
                Toast.makeText(context, "Opening", Toast.LENGTH_LONG).show();
//                mDatabaseOpenTask = new DatabaseOpenTask();
//                mDatabaseOpenTask.execute(new Context[] { UpdatesActivity.this });
            }
            else {
                Toast.makeText(context, "Download Failed", Toast.LENGTH_LONG).show();
                //finish();
            }
        }

    }
}

