package com.drextended.extable;

import android.support.annotation.IntRange;
import android.util.SparseArray;

/**
 * Created on 24.09.2017.
 */
public final class SpanSizeConfig {

    final SparseArray<CeilSpan> mSpans;

    public CeilSpan getSpanSize(@IntRange(from = 0, to = Short.MAX_VALUE) int row,
                                @IntRange(from = 0, to = Short.MAX_VALUE) int col) {
        return mSpans.get(Utils.buildIndex(col, row), CeilSpan.DEFAULT);
    }

    SpanSizeConfig(SparseArray<CeilSpan> spans) {
        this.mSpans = spans;
    }

    public static final class Builder {
        final SparseArray<CeilSpan> mSpans = new SparseArray<>(0);

        public Builder setSpan(@IntRange(from = 0, to = Short.MAX_VALUE)  int row,
                               @IntRange(from = 0, to = Short.MAX_VALUE)  int col,
                               int rowSpan, int colSpan) {
            if (colSpan != CeilSpan.DEFAULT.colSpan || rowSpan != CeilSpan.DEFAULT.rowSpan) {
                mSpans.put(Utils.buildIndex(col, row), new CeilSpan(rowSpan, colSpan));
            }
            return this;
        }

        public SpanSizeConfig build() {
            return new SpanSizeConfig(mSpans);
        }
    }
}
