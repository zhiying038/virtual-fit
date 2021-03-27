package com.yohzhiying.testposeestimation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder> {

    Context mContext;
    List<Outfit> mOutfits;
    Bitmap outfitBitmap = null;

    public OutfitAdapter(Context mContext, List<Outfit> mOutfits) {
        this.mContext = mContext;
        this.mOutfits = mOutfits;
    }

    public static class OutfitViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView category, name, description;

        public OutfitViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.outfit_image);
            category = itemView.findViewById(R.id.category);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
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
        holder.description.setText(outfit.getOutfitDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.get().load(outfit.getOutfitUrl()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        outfitBitmap = bitmap;
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
                String itemCategory = outfit.getOutfitCategory();
                String itemName = outfit.getOutfitName();
                DrawView.currentOutfit = new Outfit(itemCategory, itemName, outfitBitmap);
                Intent intent = new Intent(mContext, CameraActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOutfits.size();
    }
}
