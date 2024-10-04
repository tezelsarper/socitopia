package com.sarpertezel.socitopia.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sarpertezel.socitopia.R;
import com.sarpertezel.socitopia.databinding.RecyclerRowBinding;
import com.sarpertezel.socitopia.model.Post;
import com.sarpertezel.socitopia.view.ProfileActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    String userId;
    private ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
        this.userId =  userId;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recyclerRowBinding);
    }



        @Override
        public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recyclerRowBinding.recyclerViewUserEmailText.setText(postArrayList.get(position).fullName);
        holder.recyclerRowBinding.recyclerViewCommentText.setText(postArrayList.get(position).comment);
        String userId = postArrayList.get(position).userId;
        holder.recyclerRowBinding.recyclerViewUserEmailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.recyclerRowBinding.recyclerViewUserEmailText.getContext(), ProfileActivity.class);
                intent.putExtra("userId",userId);
                holder.recyclerRowBinding.recyclerViewUserEmailText.getContext().startActivity(intent);
            }
        });
        if (postArrayList.get(position).comment != null && !postArrayList.get(position).comment.isEmpty()) {
            holder.recyclerRowBinding.fullNameTextView.setText(postArrayList.get(position).fullName);
        } else {
            holder.recyclerRowBinding.fullNameTextView.setText("");
        }

        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.recyclerRowBinding.recyclerViewImageView);
        Picasso picasso = Picasso.get();
        if (postArrayList.get(position).profilePicUrl != null) {
            picasso.load(postArrayList.get(position).profilePicUrl)
                    .transform(new CircleTransform())
                    .into(holder.recyclerRowBinding.imageView6);
        } else {
            picasso.load(R.drawable.profilepic)
                    .transform(new CircleTransform())
                    .into(holder.recyclerRowBinding.imageView6);
        }
    }


    @Override
    public int getItemCount() {

        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder
    {
        RecyclerRowBinding recyclerRowBinding;
        public PostHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
           this.recyclerRowBinding = recyclerRowBinding;
        }
    }
    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }


}
