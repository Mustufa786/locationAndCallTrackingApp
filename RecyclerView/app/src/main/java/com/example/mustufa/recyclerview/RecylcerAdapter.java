package com.example.mustufa.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mustufa on 2/22/2018.
 */

public class RecylcerAdapter extends RecyclerView.Adapter<RecylcerAdapter.MyViewHolder> {


    private Context mContext;
    private ArrayList<String> mImagesName = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();

    public RecylcerAdapter(Context mContext, ArrayList<String> mImagesName, ArrayList<String> mImages) {
        this.mContext = mContext;
        this.mImagesName = mImagesName;
        this.mImages = mImages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_item,
                parent,false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.name.setText(mImagesName.get(position));

        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.mImageView);



    }

    @Override
    public int getItemCount() {
        return mImagesName.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CircleImageView mImageView;
        RelativeLayout relativeLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

             name = itemView.findViewById(R.id.names);
             mImageView = itemView.findViewById(R.id.circleImageView);
             relativeLayout = itemView.findViewById(R.id.layout1);
        }
    }
}


