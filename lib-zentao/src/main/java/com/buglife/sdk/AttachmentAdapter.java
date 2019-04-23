/*
 * Copyright (C) 2017 Buglife, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.buglife.sdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for showing a list of attachment objects in the bug reporter UI
 */
class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder> {

    private Context mContext;

    private ArrayList<FileAttachment> mDataSource;

    private ItemClickListener mItemClickListener;

    AttachmentAdapter(Context context, List<FileAttachment> attachments) {
        mContext = context;
        mDataSource = new ArrayList<>(attachments);
    }

    void setAttachments(List<FileAttachment> attachments) {
        mDataSource = new ArrayList<>(attachments);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_list_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        final FileAttachment attachment = mDataSource.get(position);

        File file = attachment.getFile();
        if (attachment.isImage()) {
            String path = file.getAbsolutePath();
            Bitmap scaledBitmap = scaleBitmapForThumbnail(mContext, BitmapFactory.decodeFile(path));
            viewHolder.thumbnailView.setImageBitmap(scaledBitmap);
            viewHolder.thumbnailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.itemClidk(position, attachment);
                    }
                }
            });
        }
        viewHolder.titleView.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        if (mDataSource != null)
            return mDataSource.size();
        return 0;
    }

    private Bitmap scaleBitmapForThumbnail(Context context, Bitmap bitmap) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        float aspectRatio = (float) originalWidth / (float) originalHeight;

        // 40dp width is the standard size for a row icon according to material design guidelines.
        int scaledWidth = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
        int scaledHeight = (int) (scaledWidth / aspectRatio);

        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        ImageView thumbnailView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView =  itemView.findViewById(R.id.attachment_list_title);
            thumbnailView =  itemView.findViewById(R.id.attachment_list_thumbnail);
        }
    }

    public void setItemListener(ItemClickListener itemListener) {
        this.mItemClickListener = itemListener;
    }

    public interface ItemClickListener {
        void itemClidk(int pistion, FileAttachment attachment);
    }
}
