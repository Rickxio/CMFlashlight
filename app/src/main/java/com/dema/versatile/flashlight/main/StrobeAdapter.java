package com.dema.versatile.flashlight.main;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dema.versatile.flashlight.R;
import com.dema.versatile.flashlight.core.flashlight.in.IStrobeItem;

public class StrobeAdapter extends RecyclerView.Adapter<StrobeAdapter.ViewHolder> {
    DisplayMetrics dm;
    Context mContext;
    List<IStrobeItem> mItemList;
    StrobeClickListener mClickItemListener;
    public int mCurrIndex = -1;
    public int mCurrRealIndex = 0;

    public StrobeAdapter(Context context, List<IStrobeItem> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
        dm = context.getResources().getDisplayMetrics();
    }

    public void setOnClickItemListener(StrobeClickListener mClickItemListener) {
        this.mClickItemListener = mClickItemListener;
    }

    public void setCurrIndex(int index){
        mCurrIndex = index;
    }

    public int getmCurrIndex() {
        return mCurrIndex;
    }

    public int getCurrRealIndex() {
        return mCurrRealIndex;
    }

    public void setCurrRealIndex(int mCurrRealIndex) {
        this.mCurrRealIndex = mCurrRealIndex;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_main_strobe_item, viewGroup, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = dm.widthPixels / 5;
        view.setLayoutParams(layoutParams);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        int index = position;
        position = position % mItemList.size();
        IStrobeItem item = mItemList.get(position);

        if (position == mCurrIndex || item.isSelect()){
            mCurrRealIndex = index;
            viewHolder.mTvMainItem.setTextColor(mContext.getResources().getColor(R.color.colorBlue1));
        }else{
            viewHolder.mTvMainItem.setTextColor(mContext.getResources().getColor(R.color.colorBlack3));
        }
        viewHolder.mLinMainItem.setTag(position);
        viewHolder.mTvMainItem.setText(item.getText());
        int finalPosition = position;
        viewHolder.mLinMainItem.setOnClickListener(v -> {
            if (mClickItemListener != null){
                mClickItemListener.onItemClickListener(index, finalPosition, item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long getItemId(int position) {
        return (position % mItemList.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvMainItem;
        private LinearLayout mLinMainItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvMainItem = itemView.findViewById(R.id.tv_main_item);
            mLinMainItem = itemView.findViewById(R.id.lin_main_item);
        }
    }

    public List<IStrobeItem> getList(){
        return mItemList;
    }

}
