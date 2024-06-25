package com.example.onyjase.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onyjase.R;
import com.example.onyjase.models.stickers.Sticker;

import java.util.ArrayList;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {
    ArrayList<Sticker> stickers;
    Context context;

    public StickerAdapter(ArrayList<Sticker> stickers, Context context) {
        super();
        this.stickers = stickers;
        this.context = context;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_stickers_picker, parent, false);
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        Sticker sticker = stickers.get(position);
        String stickerURL = sticker.getImages().getOriginal().getUrl();
        Glide.with(context).load(stickerURL).into(holder.stickerItem);
        // todo: add on click listener
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }

    // reload adapter
    @SuppressLint("NotifyDataSetChanged")
    public void reload() {
        notifyDataSetChanged();
    }

    // view holder for sticker adapter
    class StickerViewHolder extends RecyclerView.ViewHolder {
        ImageView stickerItem;

        public StickerViewHolder(View view) {
            super(view);
            stickerItem = view.findViewById(R.id.stickerItem);
        }
    }
}
