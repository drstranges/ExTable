package com.drextended.extable;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
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

    List<CellInfo> mCellInfos = new ArrayList<>();
    private SparseIntArray mMaxColWidths = new SparseIntArray();
    private SparseIntArray mMaxRowHeights = new SparseIntArray();
    private boolean isCeilInfoDirty = true;
    private int mContentWidth;
    private int mContentHeight;
    private int mFixedContentWidth;
    private int mFixedContentHeight;
    private int mScrollX = 0;
    private int mScrollY = 0;
    private int mDividerSize = 3;


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
    public void onMeasure(final RecyclerView.Recycler recycler, final RecyclerView.State state, int widthSpec, int heightSpec) {
        long time = System.currentTimeMillis();
        Log.d("LOG_TAG", "onMeasure");
        int widthSize;
        int heightSize;
        final int widthSpecMode = View.MeasureSpec.getMode(widthSpec);
        final int heightSpecMode = View.MeasureSpec.getMode(heightSpec);
        if (widthSpecMode != View.MeasureSpec.EXACTLY) {
            fillCeilInfo(recycler);
            widthSize = chooseContainerSize(widthSpec, mContentWidth, mRecyclerView.getMinimumWidth());
        } else {
            widthSize = View.MeasureSpec.getSize(widthSpec);
        }
        if (heightSpecMode != View.MeasureSpec.EXACTLY) {
            fillCeilInfo(recycler);
            heightSize = chooseContainerSize(heightSpec, mContentHeight, mRecyclerView.getMinimumHeight());
        } else {
            heightSize = View.MeasureSpec.getSize(heightSpec);
        }
        setMeasuredDimension(widthSize, heightSize);
        Log.d("LOG_TAG", "onMeasure === end: " + (System.currentTimeMillis() - time));
    }

    private int chooseContainerSize(int spec, int desired, int min) {
        final int mode = View.MeasureSpec.getMode(spec);
        final int size = View.MeasureSpec.getSize(spec);
        switch (mode) {
            case View.MeasureSpec.EXACTLY:
                return size;
            case View.MeasureSpec.AT_MOST:
                return Math.min(size, Math.max(desired, min));
            case View.MeasureSpec.UNSPECIFIED:
            default:
                return Math.max(desired, min);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
        long time = System.currentTimeMillis();
        Log.d("LOG_TAG", "onLayoutChildren -- start");

        detachAndScrapAttachedViews(recycler);
        fillCeilInfo(recycler);
        final List<CellInfo> cellInfos = getCeilForLayout();
        fill(recycler, cellInfos);
        Log.d("LOG_TAG", "onLayoutChildren -- end: " + (System.currentTimeMillis() - time));
    }

    private void fill(final RecyclerView.Recycler recycler, final List<CellInfo> cellInfos) {
        final int fixedColCount = mConfig.fixedColCount;
        final int fixedRowCount = mConfig.fixedRowCount;
        int left, top, right, bottom;
        for (final CellInfo cellInfo : cellInfos) {
            View view = recycler.getViewForPosition(cellInfo.index);
            addView(view);
//            measureChildWithMargins(view, 0, 0);
            final boolean horizontalScrollable = cellInfo.col >= fixedColCount;
            if (horizontalScrollable) {
                left = cellInfo.left - mScrollX;
                right = cellInfo.right - mScrollX;
            } else {
                left = cellInfo.left;
                right = cellInfo.right;
            }
            final boolean verticalScrollable = cellInfo.row >= fixedRowCount;
            if (verticalScrollable) {
                top = cellInfo.top - mScrollY;
                bottom = cellInfo.bottom - mScrollY;
            } else {
                top = cellInfo.top;
                bottom = cellInfo.bottom;
            }
            setClipBounds(view, horizontalScrollable, verticalScrollable, left, top, right, bottom);
            layoutDecoratedWithMargins(view, left, top, right, bottom);
//            view.layout(left, top, right, bottom);
        }
    }

    private void setClipBounds(final View view, final boolean horizontalScrollable, final boolean verticalScrollable,
                               final int left, final int top, final int right, final int bottom) {
        int clipWidth = 0;
        int clipHeight = 0;
        if (horizontalScrollable && left < mFixedContentWidth) {
            clipWidth = mFixedContentWidth - left;
        }
        if (verticalScrollable && top < mFixedContentHeight) {
            clipHeight = mFixedContentHeight - top;
        }
        if (!verticalScrollable && horizontalScrollable && left < mFixedContentWidth) {
            Log.d("LOG_TAG", "setClipBounds: l= " + left + ", r= " + right + ", cW = " + clipWidth);
        }
        if (clipWidth == 0 && clipHeight == 0) {
            view.setClipBounds(null);
        } else if (clipWidth >= right || clipHeight >= bottom) {
            view.setClipBounds(new Rect(0,0,0,0));
        } else {
            view.setClipBounds(new Rect(clipWidth, clipHeight, right - left, bottom - top));
        }
    }

    private List<CellInfo> getCeilForLayout() {
        final int width = getWidth();
        final int height = getHeight();
        final List<CellInfo> ceilForLayout = new ArrayList<>(mCellInfos.size());
        for (final CellInfo cellInfo : mCellInfos) {
            if ((cellInfo.col <= mConfig.fixedColCount || (cellInfo.right - mScrollX > -10 && cellInfo.left - mScrollX < width + 10))
                    && (cellInfo.row <= mConfig.fixedRowCount || (cellInfo.bottom - mScrollY > -10 && cellInfo.top - mScrollY < height + 10))) {
                ceilForLayout.add(cellInfo);
            }
        }
        return ceilForLayout;
    }

    private void fillCeilInfo(final RecyclerView.Recycler recycler) {
        if (isCeilInfoDirty) {
            isCeilInfoDirty = false;
            long time = System.currentTimeMillis();
            Log.d("LOG_TAG", "fillCeilInfo === start");
            final TableConfig config = mConfig;
            mCellInfos.clear();
            mMaxColWidths.clear();
            mMaxRowHeights.clear();
            SparseIntArray skipSpanned = new SparseIntArray();
            // find ceil and fill info
            for (int row = 0; row < config.rowCount; row++) {
                int maxRowHeight = 0;
                int col = 0;
                while (col < config.colCount) {
                    final int index = Utils.buildIndex(col, row);
                    final int skipCols = skipSpanned.get(index, 0);
                    if (skipCols > 0) {
                        col += skipCols;
                        continue;
                    }
                    final int measuredWidth;
                    final int measuredHeight;
                    if (config.hasFixedCellSize && col > 0 && row > 0
                            && col >= config.fixedColCount
                            && row >= config.fixedRowCount){
                        measuredWidth = mMaxColWidths.get(col);
                        measuredHeight = mMaxRowHeights.get(row);
                    } else {
                        final View view = recycler.getViewForPosition(index);
                        measureChildWithMargins(view, 0, 0);
                        measuredWidth = view.getMeasuredWidth();
                        measuredHeight = view.getMeasuredHeight();
                    }
                    final int maxWidth = mMaxColWidths.get(col, 0);
                    final CellSpan spanSize = config.getSpanSize(index);
                    final int rowSpan = spanSize.rowSpan;
                    final int colSpan = spanSize.colSpan;
                    if (colSpan == 1 && measuredWidth > maxWidth) {
                        mMaxColWidths.put(col, measuredWidth);
                    }
                    if (rowSpan == 1 && measuredHeight > maxRowHeight) {
                        maxRowHeight = measuredHeight;
                    }
                    mCellInfos.add(new CellInfo(index, row, col, colSpan, rowSpan));
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
                mMaxRowHeights.put(row, maxRowHeight);
            }

            // calc cells position
            SparseIntArray xOffsets = new SparseIntArray();
            SparseIntArray yOffsets = new SparseIntArray();

            int contentWidth = 0;
            int contentHeight = 0;

            for (final CellInfo cellInfo : mCellInfos) {
                final int row = cellInfo.row;
                final int col = cellInfo.col;
                final int rowSpan = cellInfo.rowSpan;
                final int colSpan = cellInfo.colSpan;

                final int left = xOffsets.get(row);
                final int top = yOffsets.get(col);
                int right = left;
                for (int i = 0; i < colSpan; i++) {
                    right += mMaxColWidths.get(col + i, 0);
                }
                right += (colSpan - 1) * mDividerSize;
                int bottom = top;
                for (int i = 0; i < rowSpan; i++) {
                    bottom += mMaxRowHeights.get(row + i, 0);
                }
                bottom += (rowSpan - 1) * mDividerSize;

                for (int i = 0; i < colSpan; i++) {
                    yOffsets.put(col + i, bottom + mDividerSize);
                }
                for (int i = 0; i < rowSpan; i++) {
                    xOffsets.put(row + i, right + mDividerSize);
                }
                cellInfo.setLayoutPosition(left, top, right, bottom);
                if (right > contentWidth) contentWidth = right;
                if (bottom > contentHeight) contentHeight = bottom;
            }
            mContentWidth = contentWidth + getPaddingRight() ;
            mContentHeight = contentHeight + getPaddingBottom();
            mFixedContentWidth = 0;
            mFixedContentHeight = 0;
            for (int col = 0; col < mConfig.fixedColCount; col++) {
                mFixedContentWidth += mMaxColWidths.get(col) + mDividerSize;
            }
            for (int row = 0; row < mConfig.fixedRowCount; row++) {
                mFixedContentHeight += mMaxRowHeights.get(row) + mDividerSize;
            }
            Log.d("LOG_TAG", "fillCeilInfo === end: " + (System.currentTimeMillis() - time));
        }
    }

    @Override
    public void onLayoutCompleted(final RecyclerView.State state) {
        super.onLayoutCompleted(state);
    }

    @Override
    public int scrollHorizontallyBy(final int dx, final RecyclerView.Recycler recycler, final RecyclerView.State state) {
        if (dx == 0) return 0;
        int consumed = 0;
        if (dx > 0) {
            consumed = Math.max(Math.min(dx, mContentWidth - getWidth() - mScrollX), 0);
        } else {
            consumed = - Math.min(Math.abs(dx), mScrollX);
        }

        fillCeilInfo(recycler);

        List<CellInfo> cellInfos = getCeilForRemove(consumed, 0);
        for (final CellInfo info : cellInfos) {
            View view = findViewByPosition(info.index);
            if (view != null) {
                detachAndScrapView(view, recycler);
            }
        }
        mScrollX += consumed;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View view = getChildAt(i);
            final int position = getPosition(view);
            final int col = Utils.indexToCol(position);
            final int row = Utils.indexToRow(position);
            if (col >= mConfig.fixedColCount) {
                view.offsetLeftAndRight(-consumed);
                setClipBounds(view, col >= mConfig.fixedColCount, row >= mConfig.fixedRowCount, view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            }
        }
//        offsetChildrenHorizontal(-consumed);
        cellInfos = getCeilForAdd(consumed, 0);
        fill(recycler, cellInfos);
        return consumed;
    }

    @Override
    public int scrollVerticallyBy(final int dy, final RecyclerView.Recycler recycler, final RecyclerView.State state) {
        if (dy == 0) return 0;
        int consumed = 0;
        if (dy > 0) {
            consumed = Math.max(Math.min(dy, mContentHeight - getHeight() - mScrollY), 0);
        } else {
            consumed = - Math.min(Math.abs(dy), mScrollY);
        }

        fillCeilInfo(recycler);

        List<CellInfo> cellInfos = getCeilForRemove(0, consumed);
        for (final CellInfo info : cellInfos) {
            View view = findViewByPosition(info.index);
            if (view != null) {
                detachAndScrapView(view, recycler);
            }
        }
        mScrollY += consumed;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View view = getChildAt(i);
            final int position = getPosition(view);
            final int row = Utils.indexToRow(position);
            final int col = Utils.indexToCol(position);
            if (row >= mConfig.fixedRowCount) {
                view.offsetTopAndBottom(-consumed);
                setClipBounds(view, col >= mConfig.fixedColCount, row >= mConfig.fixedRowCount, view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            }
        }
        //offsetChildrenVertical(-consumed);
        cellInfos = getCeilForAdd(0, consumed);
        fill(recycler, cellInfos);
        return consumed;
    }

    private List<CellInfo> getCeilForRemove(final int consumedX, int consumedY) {
        final int width = getWidth();
        final int height = getHeight();
        final List<CellInfo> ceilForRemove = new ArrayList<>();
        final int scrollX = mScrollX;
        final int scrollY = mScrollY;
        final int newScrollX = mScrollX + consumedX;
        final int newScrollY = mScrollY + consumedY;

        for (final CellInfo cellInfo : mCellInfos) {
            if ((cellInfo.col <= mConfig.fixedColCount || (cellInfo.right - scrollX > -10 && cellInfo.left - scrollX < width + 10))
                    && (cellInfo.row <= mConfig.fixedRowCount || (cellInfo.bottom - scrollY > -10 && cellInfo.top - scrollY < height + 10))) {
                if (!((cellInfo.col <= mConfig.fixedColCount || (cellInfo.right - newScrollX > -10 && cellInfo.left - newScrollX < width + 10))
                        && (cellInfo.row <= mConfig.fixedRowCount || (cellInfo.bottom - newScrollY > -10 && cellInfo.top - newScrollY < height + 10)))) {
                    ceilForRemove.add(cellInfo);
                }
            }
        }
        return ceilForRemove;
    }

    private List<CellInfo> getCeilForAdd(final int consumedX, int consumedY) {
        final int width = getWidth();
        final int height = getHeight();
        final List<CellInfo> ceilForAdd = new ArrayList<>();
        final int scrollX = mScrollX - consumedX;
        final int scrollY = mScrollY - consumedY;
        final int newScrollX = mScrollX;
        final int newScrollY = mScrollY;

        for (final CellInfo cellInfo : mCellInfos) {
            if ((cellInfo.col <= mConfig.fixedColCount || (cellInfo.right - newScrollX > -10 && cellInfo.left - newScrollX < width + 10))
                    && (cellInfo.row <= mConfig.fixedRowCount || (cellInfo.bottom - newScrollY > -10 && cellInfo.top - newScrollY < height + 10))) {
            if (!((cellInfo.col <= mConfig.fixedColCount || (cellInfo.right - scrollX > -10 && cellInfo.left - scrollX < width + 10))
                    && (cellInfo.row <= mConfig.fixedRowCount || (cellInfo.bottom - scrollY > -10 && cellInfo.top - scrollY < height + 10)))) {
                    ceilForAdd.add(cellInfo);
                }
            }
        }
        return ceilForAdd;
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
        isCeilInfoDirty = true;
    }

    @Override
    public void onItemsChanged(final RecyclerView recyclerView) {
        isCeilInfoDirty = true;
    }

    @Override
    public void onItemsAdded(final RecyclerView recyclerView, final int positionStart, final int itemCount) {
        isCeilInfoDirty = true;
    }

    @Override
    public void onItemsRemoved(final RecyclerView recyclerView, final int positionStart, final int itemCount) {
        isCeilInfoDirty = true;
    }

    @Override
    public void onItemsUpdated(final RecyclerView recyclerView, final int positionStart, final int itemCount) {
        isCeilInfoDirty = true;
    }

    @Override
    public void onItemsUpdated(final RecyclerView recyclerView, final int positionStart, final int itemCount, final Object payload) {
        isCeilInfoDirty = true;
    }

    @Override
    public void onItemsMoved(final RecyclerView recyclerView, final int from, final int to, final int itemCount) {
        isCeilInfoDirty = true;
    }

    private static class CellInfo {
        int row;
        int col;
        int index;
        int colSpan;
        int rowSpan;
        int left;
        int top;
        int right;
        int bottom;

        public CellInfo(final int index, final int row, final int col, final int colSpan, final int rowSpan) {
            this.index = index;
            this.row = row;
            this.col = col;
            this.colSpan = colSpan;
            this.rowSpan = rowSpan;
        }

        public void setLayoutPosition(final int left, final int top, final int right, final int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }
}
