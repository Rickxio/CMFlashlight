package com.dema.versatile.flashlight.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.dema.versatile.flashlight.main.MainActivity;

public class StrobeRecyclerView extends RecyclerView {
    Context mContext;
    ScrollSpeedLinearLayoutMgr mLinearLayoutManager;
    SnapHelper mSnapHelper;
    public float childDistance = 0;
    public float offSetDistance = 0;
    private ItemSelectedListener listener;

    public StrobeRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public StrobeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StrobeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
        mLinearLayoutManager = new ScrollSpeedLinearLayoutMgr(mContext, LinearLayoutManager.HORIZONTAL, false);
        //布局
        setLayoutManager(mLinearLayoutManager);

        //居中
        mSnapHelper = new LinearSnapHelper();
        mSnapHelper.attachToRecyclerView(this);

        addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if (getChildCount() <= 0) {
                    return;
                }
                //计算子view之间的间距
                float child1X = getChildMiddleX(0);
                float child2X = getChildMiddleX(1);
                childDistance = Math.abs(child2X - child1X);

            }
        });
    }

    /**
     * 缩放和平移
     *
     * @param child
     * @param fraction
     */
    private void scale(View child, float fraction) {
        final float MIN_SCALE = 0.7f;
        float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(fraction));
        child.setScaleX(scaleFactor);
        child.setScaleY(scaleFactor);
        float translationY = (getHeight() - child.getHeight()) * (fraction);
        child.setTranslationY(translationY);
    }

    public float getChildDistance() {
        return childDistance;
    }

    public float getChildMiddleX(int childIndex) {
        View child = getChildAt(childIndex);
        if (child == null) {
            return 0;
        }
        //当前这个子view中间位置的坐标
        float childMiddle = child.getX() + child.getWidth() / 2;

        return childMiddle;
    }

    public float getMiddleX() {
        return getX() + getWidth() / 2;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        //recyclerview的中间位置x坐标
        float middle = getMiddleX();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            //当前这个子view中间位置的坐标
            float childMiddle = getChildMiddleX(i);
            //距离父view中间的距离
            float gap = Math.abs(middle - childMiddle);
            //距离系数理的越近越小
            float fraction = gap / (getWidth() / 2);
            if (fraction == 0 && listener != null) {
                listener.onItemSelect((Integer) getChildAt(1).getTag());
//                        Log.i(TAG, "onScrolled: "+getChildAt(1).getTag());
            }
//            scale(child, fraction);
        }
    }

    public int mOriginalOffset = 0;

    public void scrollToPositionMiddle(final int position) {

//        scrollToPosition(position);
        postDelayed(new Runnable() {
            @Override
            public void run() {
//                scrollBy(getScrollOffset(),0);
                mOriginalOffset = getScrollOffset();
                mLinearLayoutManager.scrollToPositionWithOffset(position, mOriginalOffset);
            }
        }, 0);

    }

    public void scrollPosition(int position){

        View snapView = getChildAt(1);
        int w = snapView.getWidth();
        smoothScrollBy(w * position, 0);

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setAutoMiddle(boolean isAutoMiddle) {
        mSnapHelper.attachToRecyclerView(isAutoMiddle ? this : null);
    }

    public int getScrollOffset() {
//        View snapView = mSnapHelper.findSnapView(mLinearLayoutManager);
        View snapView = getChildAt(1);
        if (snapView == null) {
            return 0;
        }
        int middle = (int) (getX() + getWidth() / 2);
        int childMiddle = (int) (snapView.getX() + snapView.getWidth() / 2);
        int gap = -(childMiddle - middle);
        return gap;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= 0.1f;
        return super.fling(velocityX, velocityY);
    }

    public interface ItemSelectedListener {
        void onItemSelect(int index);
    }

    public void setOnItemSelectedListener(ItemSelectedListener listener) {
        this.listener = listener;
    }

}

//    class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder> {
//        List<ZodiacInfo> mDataList;
//
//        public GalleryAdapter(final List<ZodiacInfo> datas) {
//            mDataList = datas;
//        }
//
//        @NonNull
//        @Override
//        public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new GalleryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_selectzodiac_gallery_item_view, parent, false));
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {
//            position = position % mDataList.size();
//            holder.itemView.setTag(position);
//            holder.itemView.setBackgroundResource(mDataList.get(position).getBigIcon());
//        }
//
//        @Override
//        public int getItemCount() {
//            return Integer.MAX_VALUE;
//        }
//
//
//        @Override
//        public long getItemId(int position) {
//        return (position % mDataList.size());
//    }
//
//    }


