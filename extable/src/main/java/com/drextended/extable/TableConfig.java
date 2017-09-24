package com.drextended.extable;

import android.support.annotation.IntRange;
import android.util.SparseArray;

/**
 * Created on 24.09.2017.
 */

public final class TableConfig {
    public final int colCount;
    public final int rowCount;
    public final int fixedColCount;
    public final int fixedRowCount;
    final SparseArray<CeilSpan> mSpans;
    final SparseArray<ExTable.ExTableAdapter.ItemDelegate> mCeilViewDelegates;

    TableConfig(@IntRange(from = 0, to = Short.MAX_VALUE) final int colCount,
                @IntRange(from = 0, to = Short.MAX_VALUE) final int rowCount,
                @IntRange(from = 0) final int fixedColCount,
                @IntRange(from = 0) final int fixedRowCount,
                final SparseArray<CeilSpan> spans,
                final SparseArray<ExTable.ExTableAdapter.ItemDelegate> ceilViewDelegates) throws IllegalArgumentException {
        mCeilViewDelegates = ceilViewDelegates;
        assertParam(colCount >= 0 && colCount <= Short.MAX_VALUE, "Column count must be >= 0 and <= 32767");
        assertParam(rowCount >= 0 && rowCount <= Short.MAX_VALUE, "Row count must be >= 0 and <= 32767");
        assertParam(fixedColCount >= 0 && fixedColCount <= colCount, "Fixed column count must be >= 0 and <= column count");
        assertParam(fixedRowCount >= 0 && fixedRowCount <= rowCount, "Fixed row count must be >= 0 and <= row count");
        this.colCount = colCount;
        this.rowCount = rowCount;
        this.fixedColCount = fixedColCount;
        this.fixedRowCount = fixedRowCount;
        this.mSpans = spans;
    }

    private void assertParam(boolean valid, String message) {
        if (!valid) throw new IllegalArgumentException(message);
    }

    public CeilSpan getSpanSize(@IntRange(from = 0, to = Short.MAX_VALUE) int row,
                                @IntRange(from = 0, to = Short.MAX_VALUE) int col) {
        return mSpans.get(Utils.buildIndex(col, row), CeilSpan.DEFAULT);
    }

    public ExTable.ExTableAdapter.ItemDelegate getItemDelegat(final int type) {
        return mCeilViewDelegates.get(type, ExTable.ExTableAdapter.ItemDelegate.DEFAULT);
    }


    public static final class Builder {
        int mColCount = 0;
        int mRowCount = 0;
        int mFixedColCount = 0;
        int mFixedRowCount = 0;
        final SparseArray<CeilSpan> mSpans;
        final SparseArray<ExTable.ExTableAdapter.ItemDelegate> mCeilViewDelegates;

        public Builder() {
            mSpans = new SparseArray<>(0);
            mCeilViewDelegates = new SparseArray<>();
        }

        public Builder(final TableConfig src) {
            mColCount = src.colCount;
            mRowCount = src.rowCount;
            mFixedColCount = src.fixedColCount;
            mFixedRowCount = src.fixedRowCount;
            mSpans = src.mSpans.clone();
            mCeilViewDelegates = src.mCeilViewDelegates;
        }

        public Builder setColCount(@IntRange(from = 0, to = Short.MAX_VALUE) final int colCount) {
            mColCount = colCount;
            return this;
        }

        public Builder setRowCount(@IntRange(from = 0, to = Short.MAX_VALUE) final int rowCount) {
            mRowCount = rowCount;
            return this;
        }

        public Builder setFixedColCount(@IntRange(from = 0) final int fixedColCount) {
            mFixedColCount = fixedColCount;
            return this;
        }

        public Builder setFixedRowCount(@IntRange(from = 0) final int fixedRowCount) {
            mFixedRowCount = fixedRowCount;
            return this;
        }

        public Builder setSpan(@IntRange(from = 0, to = Short.MAX_VALUE) int col,
                               @IntRange(from = 0, to = Short.MAX_VALUE) int row,
                               int colSpan, int rowSpan) {
            if (colSpan != CeilSpan.DEFAULT.colSpan || rowSpan != CeilSpan.DEFAULT.rowSpan) {
                mSpans.put(Utils.buildIndex((int)col, (int)row), new CeilSpan(rowSpan, colSpan));
            }
            return this;
        }

        public Builder addCeilViewDelegate(int type, ExTable.ExTableAdapter.ItemDelegate delegate) {
            mCeilViewDelegates.put(type, delegate);
            return this;
        }
        /**
         *
         * @return the table config
         * @throws IllegalArgumentException if parameters is not valid.
         */
        public TableConfig build() throws IllegalArgumentException {
            return new TableConfig(mColCount, mRowCount, mFixedColCount, mFixedRowCount, mSpans.clone(), mCeilViewDelegates);
        }
    }
}
