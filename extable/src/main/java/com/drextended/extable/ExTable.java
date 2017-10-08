package com.drextended.extable;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created on 22.09.2017.
 */

public class ExTable extends LinearLayout {

    private RecyclerView mRecyclerView;
    private ExTableLayoutManager mLayoutManager;
    private ExTableAdapter mAdapter;
    private TableConfig mConfig;
    private ExTableAdapter.CellsData mCellsData = new ExTableAdapter.CellsData();

    public ExTable(final Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ExTable(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ExTable(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExTable(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        setOrientation(VERTICAL);
        mRecyclerView = new RecyclerView(context, attrs, defStyleAttr);
        addView(mRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setConfig(final TableConfig config) {
        mConfig = config;
        recreateTable();
    }

    public void setCellsData(ExTableAdapter.CellsData cellsData) {
        mCellsData = cellsData;
        mAdapter.setCellsData(cellsData);
        mAdapter.notifyDataSetChanged();
    }

    private void recreateTable() {
        mLayoutManager = new ExTableLayoutManager(getContext());
        mAdapter = new ExTableAdapter();
        mLayoutManager.setConfig(mConfig);
        mAdapter.setConfig(mConfig);
        mAdapter.setCellsData(mCellsData);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public static class ExTableAdapter extends RecyclerView.Adapter<CellViewHolder>{
        private TableConfig mConfig;
        private CellsData mCellsData;

        @Override
        public CellViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final ItemDelegate itemDelegate = mConfig.getItemDelegate(viewType);
            return itemDelegate.onCreateViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(final CellViewHolder holder, final int position) {
            final CellsData.Cell cell = mCellsData.getCell(position);
            final ItemDelegate itemDelegate = mConfig.getItemDelegate(cell.type);
            itemDelegate.onBindViewHolder(holder, position, cell);
        }

        @Override
        public int getItemViewType(final int position) {
            return mCellsData.getType(position);
        }

        @Override
        public int getItemCount() {
            return mConfig == null ? 0 : Integer.MAX_VALUE;//mConfig.colCount * mConfig.rowCount;
        }

        public void setCellsData(final CellsData cellsData) {
            mCellsData = cellsData;
        }

        public void setConfig(final TableConfig config) {
            mConfig = config;
        }

        public interface ItemDelegate {
            CellViewHolder onCreateViewHolder(final ViewGroup parent);
            void onBindViewHolder(final CellViewHolder holder, final int position, final CellsData.Cell cell);

            ItemDelegate DEFAULT = new ItemDelegate() {
                @Override
                public CellViewHolder onCreateViewHolder(final ViewGroup parent) {
                    final TextView itemView = new TextView(parent.getContext());
                    itemView.setGravity(Gravity.CENTER);
                    itemView.setMinWidth(100);
                    itemView.setMinHeight(50);
                    itemView.setPadding(10, 5, 10, 5);
                    return new CellViewHolder(itemView);
                }

                @Override
                public void onBindViewHolder(final CellViewHolder holder, final int position, final CellsData.Cell cell) {
                    holder.itemView.setBackgroundColor(cell.color);
                    ((TextView) holder.itemView).setText(cell.value == null ? null : String.valueOf(cell.value));
                }
            };
        }

        public static class CellsData {
            final LongSparseArray<Cell> mCells = new LongSparseArray<>();

            public Cell getCell(final int index) {
                return mCells.get(index, Cell.DEFAULT);
            }

            public void addCell(@IntRange(from = 0, to = Short.MAX_VALUE) int col,
                                @IntRange(from = 0, to = Short.MAX_VALUE) int row,
                                Cell cell) {
                mCells.put(Utils.buildIndex(col, row), cell);
            }

            public int getType(final int index) {
                return getCell(index).type;
            }

            public static class Cell {
                public static final int TYPE_DEFAULT = 0;
                public static final Cell DEFAULT = new Cell(TYPE_DEFAULT, null, Color.GRAY);

                public final Object value;
                public final int type;
                public final int color;

                public Cell(final int type, final Object value, final int color) {
                    this.type = type;
                    this.value = value;
                    this.color = color;
                }
            }
        }
    }

    public static class CellViewHolder extends RecyclerView.ViewHolder {
        public CellViewHolder(final View itemView) {
            super(itemView);
        }
    }
}
