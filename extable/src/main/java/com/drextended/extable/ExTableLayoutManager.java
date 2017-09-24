package com.drextended.extable;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 22.09.2017.
 */

public class ExTableLayoutManager extends RecyclerView.LayoutManager {

    private TableConfig mConfig;
    private RecyclerView mRecyclerView;


    /**
     * Creates a ExTableLayoutManager
     *
     * @param context Current context, will be used to access resources.
     */
    public ExTableLayoutManager(Context context) {
        init();
    }

    /**
     * Constructor used when layout manager is set in XML by RecyclerView attribute
     * "layoutManager".
     */
    public ExTableLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                                int defStyleRes) {
//        Properties properties = getProperties(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setAutoMeasureEnabled(false);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onAttachedToWindow(final RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        removeAndRecycleAllViews(recycler);
        recycler.clear();
        mRecyclerView = null;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(final Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canScrollVertically() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View findViewByPosition(final int position) {
        return super.findViewByPosition(position);
    }

    @Override
    public void onMeasure(final RecyclerView.Recycler recycler, final RecyclerView.State state, final int widthSpec, final int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
        // TODO: 23.09.2017 Implement this
        detachAndScrapAttachedViews(recycler);
        List<CeilView> mCeilViews = new ArrayList<>();
        SparseIntArray maxColWidths = new SparseIntArray();
        SparseIntArray maxRowHeights = new SparseIntArray();
        SparseIntArray skipSpanned = new SparseIntArray();
        for (int row = 0; row < mConfig.rowCount; row++) {
            int maxRowHeight = 0;
            int col = 0;
            while (col < mConfig.colCount) {
                final int index = Utils.buildIndex(col, row);
                final int skipCols = skipSpanned.get(index, 0);
                if (skipCols > 0) {
                    col += skipCols;
                    continue;
                }
                final View view = recycler.getViewForPosition(index);
                addView(view);
                measureChildWithMargins(view, 0, 0);
                final int measuredWidth = view.getMeasuredWidth();
                final int measuredHeight = view.getMeasuredHeight();
                final int maxWidth = maxColWidths.get(col, 0);
                final CeilSpan spanSize = mConfig.getSpanSize(row, col);
                final int rowSpan = spanSize.rowSpan;
                final int colSpan = spanSize.colSpan;
                if (colSpan == 1 && measuredWidth > maxWidth) {
                    maxColWidths.put(col, measuredWidth);
                }
                if (rowSpan == 1 && measuredHeight > maxRowHeight) {
                    maxRowHeight = measuredHeight;
                }
                mCeilViews.add(new CeilView(row, col, colSpan, rowSpan, view));
                if (colSpan > 1 || rowSpan > 1) {
                    for (int c = 0; c < colSpan; c++) {
                        for (int r = 0; r < rowSpan; r++) {
                            if (c == 0 && r == 0) continue;
                            skipSpanned.put(Utils.buildIndex(col + c, row + r), colSpan);
                        }
                    }
                }
                col += colSpan;
            }
            maxRowHeights.put(row, maxRowHeight);
        }

        SparseIntArray xOffsets = new SparseIntArray();
        SparseIntArray yOffsets = new SparseIntArray();
        for (final CeilView ceilView : mCeilViews) {
            final int row = ceilView.row;
            final int col = ceilView.col;
            final int left = xOffsets.get(row);
            final int top = yOffsets.get(col);
            int right = left;
            final CeilSpan spanSize = mConfig.getSpanSize(row, col);
            for (int i = 0; i < spanSize.colSpan; i++) {
                right += maxColWidths.get(col + i, 0);
            }
            right += (spanSize.colSpan - 1) * 3;
            int bottom = top;
            for (int i = 0; i < spanSize.rowSpan; i++) {
                bottom += maxRowHeights.get(row + i, 0);
            }
            bottom += (spanSize.rowSpan - 1) * 3;

            for (int i = 0; i < spanSize.colSpan; i++) {
                yOffsets.put(col + i, bottom + 3);
            }
            for (int i = 0; i < spanSize.rowSpan; i++) {
                xOffsets.put(row + i, right + 3);
            }
            if (right > left && bottom > top) {
                ceilView.view.layout(left, top, right, bottom);
            }
        }
    }

    @Override
    public void onLayoutCompleted(final RecyclerView.State state) {
        super.onLayoutCompleted(state);
    }

    @Override
    public int scrollHorizontallyBy(final int dx, final RecyclerView.Recycler recycler, final RecyclerView.State state) {
        // TODO: 23.09.2017 Implement this
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(final int dy, final RecyclerView.Recycler recycler, final RecyclerView.State state) {
        // TODO: 23.09.2017 Implement this
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    @Override
    public void collectInitialPrefetchPositions(final int adapterItemCount, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        super.collectInitialPrefetchPositions(adapterItemCount, layoutPrefetchRegistry);
    }

    @Override
    public void collectAdjacentPrefetchPositions(final int dx, final int dy, final RecyclerView.State state, final LayoutPrefetchRegistry layoutPrefetchRegistry) {
        super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry);
    }

    public void setConfig(final TableConfig config) {
        mConfig = config;
    }

    private static class CeilView {
        int row;
        int col;
        int colSpan;
        int rowSpan;
        View view;

        public CeilView(final int row, final int col, final int colSpan, final int rowSpan, final View view) {
            this.row = row;
            this.col = col;
            this.view = view;
            this.colSpan = colSpan;
            this.rowSpan = rowSpan;
        }
    }
}
