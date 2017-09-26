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
    private ExTableAdapter.CeilData mCeilData = new ExTableAdapter.CeilData();

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

    public void setCeilData(ExTableAdapter.CeilData ceilData) {
        mCeilData = ceilData;
        mAdapter.setCeilData(ceilData);
        mAdapter.notifyDataSetChanged();
    }

    private void recreateTable() {
        mLayoutManager = new ExTableLayoutManager(getContext());
        mAdapter = new ExTableAdapter();
        mLayoutManager.setConfig(mConfig);
        mAdapter.setConfig(mConfig);
        mAdapter.setCeilData(mCeilData);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public static class ExTableAdapter extends RecyclerView.Adapter<CeilViewHolder>{
        private TableConfig mConfig;
        private CeilData mCeilData;

        @Override
        public CeilViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final ItemDelegate itemDelegat = mConfig.getItemDelegat(viewType);
            return itemDelegat.onCreateViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(final CeilViewHolder holder, final int position) {
            final CeilData.Ceil ceil = mCeilData.getCeil(position);
            final ItemDelegate itemDelegat = mConfig.getItemDelegat(ceil.type);
            itemDelegat.onBindViewHolder(holder, position, ceil);
        }

        @Override
        public int getItemViewType(final int position) {
            return mCeilData.getType(position);
        }

        @Override
        public int getItemCount() {
            return mConfig == null ? 0 : Integer.MAX_VALUE;//mConfig.colCount * mConfig.rowCount;
        }

        public void setCeilData(final CeilData ceilData) {
            mCeilData = ceilData;
        }

        public void setConfig(final TableConfig config) {
            mConfig = config;
        }

        public interface ItemDelegate {
            CeilViewHolder onCreateViewHolder(final ViewGroup parent);
            void onBindViewHolder(final CeilViewHolder holder, final int position, final CeilData.Ceil ceil);

            ItemDelegate DEFAULT = new ItemDelegate() {
                @Override
                public CeilViewHolder onCreateViewHolder(final ViewGroup parent) {
                    final TextView itemView = new TextView(parent.getContext());
                    itemView.setGravity(Gravity.CENTER);
                    itemView.setMinWidth(100);
                    itemView.setMinHeight(50);
                    return new CeilViewHolder(itemView);
                }

                @Override
                public void onBindViewHolder(final CeilViewHolder holder, final int position, final CeilData.Ceil ceil) {
                    holder.itemView.setBackgroundColor(ceil.color);
                    ((TextView) holder.itemView).setText(ceil.value == null ? null : String.valueOf(ceil.value));
                }
            };
        }

        public static class CeilData {
            final LongSparseArray<Ceil> mCeils = new LongSparseArray<>();

            public Ceil getCeil(final int index) {
                return mCeils.get(index, Ceil.DEFAULT);
            }

            public void addCeil(@IntRange(from = 0, to = Short.MAX_VALUE) int col,
                                @IntRange(from = 0, to = Short.MAX_VALUE) int row,
                                Ceil ceil) {
                mCeils.put(Utils.buildIndex(col, row), ceil);
            }

            public int getType(final int index) {
                return getCeil(index).type;
            }

            public static class Ceil {
                public static final int TYPE_DEFAULT = 0;
                public static final Ceil DEFAULT = new Ceil(TYPE_DEFAULT, null, Color.GRAY);

                public final Object value;
                public final int type;
                public final int color;

                public Ceil(final int type, final Object value, final int color) {
                    this.type = type;
                    this.value = value;
                    this.color = color;
                }
            }
        }
    }

    public static class CeilViewHolder extends RecyclerView.ViewHolder {
        public CeilViewHolder(final View itemView) {
            super(itemView);
        }
    }
}
