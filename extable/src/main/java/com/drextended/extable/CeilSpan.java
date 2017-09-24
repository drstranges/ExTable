package com.drextended.extable;

/**
 * Created on 23.09.2017.
 */

public class CeilSpan {
    public static final CeilSpan DEFAULT = new CeilSpan(1, 1);

    public final int rowSpan;
    public final int colSpan;

    public CeilSpan(final int rowSpan, final int colSpan) {
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
    }
}
