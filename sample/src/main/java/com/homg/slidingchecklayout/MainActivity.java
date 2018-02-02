package com.homg.slidingchecklayout;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.homg.scl.SlidingCheckLayout;
import com.homg.scl.SlidingCheckLayout.OnSlidingPositionListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnSlidingPositionListener {
    private SlidingCheckLayout mSlidingCheckLayout;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private MainRvAdapter mMainRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSlidingCheckLayout = findViewById(R.id.scl);
        mRecyclerView = findViewById(R.id.rv);
        mSlidingCheckLayout.setOnSlidingPositionListener(this);
        mGridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mMainRvAdapter = new MainRvAdapter(createData());
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dip2px(this, 15f), true));
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mMainRvAdapter);
    }

    private List<MainEntity> createData() {
        List<MainEntity> dataList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            dataList.add(new MainEntity("Item " + i));
        }
        return dataList;
    }

    @Override
    public void onSlidingPosition(int position) {
        MainEntity entity = mMainRvAdapter.getEntityByPosition(position);
        entity.setSelect(!entity.isSelect());
        mMainRvAdapter.notifyItemChanged(position);
    }

    @Override
    public void onSlidingRangePosition(int startPosition, int endPosition) {
        for (int i = startPosition; i <= endPosition; i++) {
            MainEntity entity = mMainRvAdapter.getEntityByPosition(i);
            entity.setSelect(!entity.isSelect());
        }
        mMainRvAdapter.notifyItemRangeChanged(startPosition, endPosition - startPosition + 1);
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
