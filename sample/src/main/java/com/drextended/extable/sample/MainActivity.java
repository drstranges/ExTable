package com.drextended.extable.sample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drextended.extable.ExTable;
import com.drextended.extable.TableConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FrameLayout container = findViewById(R.id.container);

        ExTable exTable = new ExTable(this);
        exTable.setConfig(
                new TableConfig.Builder()
                        .setColCount(36)
                        .setRowCount(36)
                        .setFixedColCount(1)
                        .setFixedRowCount(2)
//                        .setSpan(1, 0, 2, 1)
//                        .setSpan(0, 3, 2, 2)
                        .addCeilViewDelegate(1, new ExTable.ExTableAdapter.ItemDelegate() {
                            @Override
                            public ExTable.CeilViewHolder onCreateViewHolder(final ViewGroup parent) {
                                final TextView itemView = new TextView(parent.getContext());
                                itemView.setGravity(Gravity.CENTER);
                                return new ExTable.CeilViewHolder(itemView);
                            }

                            @Override
                            public void onBindViewHolder(final ExTable.CeilViewHolder holder, final int position, final ExTable.ExTableAdapter.CeilData.Ceil ceil) {
                                ((TextView) holder.itemView).setText(String.valueOf(ceil.value));
//                                ((TextView) holder.itemView).setTextColor(ceil.color);
                                ((TextView) holder.itemView).setBackgroundColor(ceil.color);
                            }
                        })
                        .build()
        );

        final ExTable.ExTableAdapter.CeilData ceilData = new ExTable.ExTableAdapter.CeilData();
        ceilData.addCeil(0,0, new ExTable.ExTableAdapter.CeilData.Ceil(1, "#", Color.RED));
        ceilData.addCeil(1,0, new ExTable.ExTableAdapter.CeilData.Ceil(1, "FIO", Color.GREEN));
        ceilData.addCeil(2,0, new ExTable.ExTableAdapter.CeilData.Ceil(1, "Class 1", Color.BLUE));
        ceilData.addCeil(3,0, new ExTable.ExTableAdapter.CeilData.Ceil(1, "Class 2", Color.CYAN));
        ceilData.addCeil(4,0, new ExTable.ExTableAdapter.CeilData.Ceil(1, "Class 3", Color.MAGENTA));
        ceilData.addCeil(0,1, new ExTable.ExTableAdapter.CeilData.Ceil(1, "1", Color.RED));
        ceilData.addCeil(1,1, new ExTable.ExTableAdapter.CeilData.Ceil(1, "Student 1", Color.GREEN));
        ceilData.addCeil(2,1, new ExTable.ExTableAdapter.CeilData.Ceil(1, "n", Color.BLUE));
        ceilData.addCeil(3,1, new ExTable.ExTableAdapter.CeilData.Ceil(1, "5", Color.BLACK));
        ceilData.addCeil(4,1, new ExTable.ExTableAdapter.CeilData.Ceil(1, "12", Color.CYAN));
        exTable.setCeilData(ceilData);

//        exTable.setBackgroundColor(Color.MAGENTA);
        container.addView(exTable, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
