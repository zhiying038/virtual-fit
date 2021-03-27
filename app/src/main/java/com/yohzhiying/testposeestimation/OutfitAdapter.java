package com.yohzhiying.testposeestimation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder> {

    Context mContext;
    List<Outfit> mOutfits;

    public OutfitAdapter(Context mContext, List<Outfit> mOutfits) {
        this.mContext = mContext;
        this.mOutfits = mOutfits;
    }

    public static class OutfitViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView category, name;

        public OutfitViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.outfit_image);
            category = itemView.findViewById(R.id.category);
            name = itemView.findViewById(R.id.name);
        }
    }

    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.outfit_card_design, parent, false);
        return new OutfitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OutfitViewHolder holder, final int position) {
        final Outfit outfit = mOutfits.get(position);
        Picasso.get().load(outfit.getOutfitUrl()).into(holder.image);
        holder.name.setText(outfit.getOutfitName());
        holder.category.setText(outfit.getOutfitCategory());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(mContext, ViewOutfitActivity.class);
            intent.putExtra("itemCategory", outfit.getOutfitCategory());
            intent.putExtra("itemName", outfit.getOutfitName());
            intent.putExtra("itemDescription", outfit.getOutfitDescription());
            intent.putExtra("itemUrl", outfit.getOutfitUrl());
            mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOutfits.size();
    }
}
