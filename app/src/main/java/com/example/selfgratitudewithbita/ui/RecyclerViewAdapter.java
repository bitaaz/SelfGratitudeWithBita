package com.example.selfgratitudewithbita.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfgratitudewithbita.R;
import com.example.selfgratitudewithbita.model.Journal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journalList;


    public RecyclerViewAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }


    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =LayoutInflater.from(context).inflate(R.layout.journal_list_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Journal journal = journalList.get(position);
        String imageUrl;

        holder.title.setText(journal.getTitle());
        holder.thought.setText(journal.getThought());
        imageUrl = journal.getImageUrl();

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal.getDateCreated().getSeconds()*1000);
        holder.dateCreated.setText(timeAgo);

        Picasso.get().load(imageUrl).into(holder.journalImage);




    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView journalImage;
        public TextView title;
        public TextView thought;
        public TextView dateCreated;
        public TextView name;
        public ImageButton shareButton;

        public String message;
        public Uri imageUri;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;



            journalImage = itemView.findViewById(R.id.journal_list_image);
            title = itemView.findViewById(R.id.journal_list_title);
            thought = itemView.findViewById(R.id.journal_list_thought);
            dateCreated = itemView.findViewById(R.id.journal_list_timestamp);
            name = itemView.findViewById(R.id.username_journal_list);
            shareButton = itemView.findViewById(R.id.share_button);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    message = "Title: " + title.getText().toString().trim() + "\n\n"
                            + "Thought: " + thought.getText().toString().trim() + "\n\n";


                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "This is my journal thought!");
                    intent.setType("text/plain");
                    context.startActivity(Intent.createChooser(intent, "send"));
                }
            });



        }
    }
}
