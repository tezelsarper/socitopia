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

import com.sarpertezel.socitopia.GroupsFeedActivity;
import com.sarpertezel.socitopia.R;
import com.sarpertezel.socitopia.databinding.GroupsRecyclerRowBinding;
import com.sarpertezel.socitopia.databinding.RecyclerRowBinding;
import com.sarpertezel.socitopia.model.Groups;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsHolder> {

    String groupId;
    ArrayList<Groups> groupsArrayList;

    public GroupsAdapter(ArrayList<Groups> groupsArrayList)
    {

        this.groupsArrayList = groupsArrayList;
    }


    @NonNull
    @Override
    public GroupsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GroupsRecyclerRowBinding groupsRecyclerRowBinding = GroupsRecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new GroupsHolder(groupsRecyclerRowBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull GroupsHolder holder, int position) {
        holder.binding.GroupsRecyclerTextView.setText(groupsArrayList.get(position).groupName);

        String groupId = groupsArrayList.get(position).groupdId;

        holder.binding.GroupsRecyclerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.binding.GroupsRecyclerTextView.getContext(), GroupsFeedActivity.class);
                intent.putExtra("groupId",groupId);
                holder.binding.GroupsRecyclerTextView.getContext().startActivity(intent);
            }
        });

        Picasso.get().load(groupsArrayList.get(position).groupProfilePicURL).into(holder.binding.GroupsimageView);
        Picasso picasso = Picasso.get();
        if (groupsArrayList.get(position).groupProfilePicURL != null) {
            picasso.load(groupsArrayList.get(position).groupProfilePicURL)
                    .transform(new CircleTransform())
                    .into(holder.binding.GroupsimageView);
        } else {
            picasso.load(R.drawable.profilepic)
                    .transform(new CircleTransform())
                    .into(holder.binding.GroupsimageView);
        }


    }

    @Override
    public int getItemCount() {


        return groupsArrayList.size();
    }

    public class GroupsHolder extends RecyclerView.ViewHolder {
        private GroupsRecyclerRowBinding binding;

        public GroupsHolder(GroupsRecyclerRowBinding binding) {


            super(binding.getRoot());
            this.binding = binding;


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