package com.drextended.extable;

/**
 * Created on 23.09.2017.
 */

public class CellSpan {
    public static final CellSpan DEFAULT = new CellSpan(1, 1);

    public final int rowSpan;
    public final int colSpan;

    public CellSpan(final int rowSpan, final int colSpan) {
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
    }
}
