package com.example.amedentix_with_firebase.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amedentix_with_firebase.R;
import com.example.amedentix_with_firebase.models.ImageUrl;

import java.util.ArrayList;

public class MediaAdapter  extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private ArrayList<ImageUrl> imageUrls;
    private Context context;

    public MediaAdapter(Context context, ArrayList<ImageUrl> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;

    }

    @Override
    public MediaAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_media, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * gets the image url from adapter and passes to Glide API to load the image
     *
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final int position = i;
        final ViewHolder holder = viewHolder;
        Glide.with(context).load(imageUrls.get(i).getImageUrl()).into(viewHolder.img);
        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog settingsDialog = new Dialog(v.getContext());
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(LayoutInflater.from(v.getContext()).inflate(R.layout.item_media, (ViewGroup)v.getParent(), false));
                settingsDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        LayoutInflater li;

        public ViewHolder(final View view) {
            super(view);
            img = view.findViewById(R.id.imageViewGallery);
            li = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog settingsDialog = new Dialog(view.getContext());
                    settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    settingsDialog.setContentView(li.inflate(R.layout.item_media
                            , null));
                    settingsDialog.show();
                    Toast.makeText(context,"Image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
