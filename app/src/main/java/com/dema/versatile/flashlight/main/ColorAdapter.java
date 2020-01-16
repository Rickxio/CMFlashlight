package com.dema.versatile.flashlight.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dema.versatile.flashlight.R;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {

    private static final int[] colorText = new int[]{
           R.drawable.icon_color1, R.drawable.icon_color2, R.drawable.icon_color3,
            R.drawable.icon_color4, R.drawable.icon_color5, R.drawable.icon_color6,
            R.drawable.icon_color7, R.drawable.icon_color8, R.drawable.icon_color9
    };
    private OnItemClickListener onItemClickListener;


    ColorAdapter(){

    }

    private void setSelectPosition(int position){
        notifyDataSetChanged();
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ColorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorAdapter.ViewHolder holder, int position) {
        holder.iv_color.setImageResource(colorText[position]);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
            setSelectPosition(position);
        });
    }

    @Override
    public int getItemCount() {
        return colorText.length;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }


   class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_color;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_color = itemView.findViewById(R.id.iv_color);
        }
    }
}
