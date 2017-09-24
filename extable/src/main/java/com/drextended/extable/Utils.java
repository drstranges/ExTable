package com.drextended.extable;

import android.support.annotation.IntRange;

/**
 * Created on 24.09.2017.
 */

class Utils {

    public static int buildIndex(@IntRange(from = 0, to = Short.MAX_VALUE) final int col,
                                 @IntRange(from = 0, to = Short.MAX_VALUE) final int row) {
        return ((row & 0xffff) << 16) | (col & 0xffff);
    }

    @IntRange(from = -1, to = Short.MAX_VALUE)
    public static int indexToRow(final int index) {
        return index > 0 ? (index >> 16) : -1;
    }

    @IntRange(from = -1, to = Short.MAX_VALUE)
    public static int indexToCol(final int index) {
        return index > 0 ? index & 0xffff : -1;
    }
}
