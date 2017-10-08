package com.drextended.extable.sample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drextended.extable.ExTable;
import com.drextended.extable.TableConfig;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FrameLayout container = findViewById(R.id.container);

        ExTable exTable = new ExTable(this);
        final int colCount = 36;
        final int rowCount = 100;
        exTable.setConfig(
                new TableConfig.Builder()
                        .setColCount(colCount)
                        .setRowCount(rowCount)
                        .setFixedColCount(1)
                        .setFixedRowCount(2)
                        .setSpan(0, 0, 1, 2)
//                        .setSpan(1, 0, 2, 1)
//                        .setSpan(0, 3, 2, 2)
                        .addCeilViewDelegate(1, new ExTable.ExTableAdapter.ItemDelegate() {
                            @Override
                            public ExTable.CellViewHolder onCreateViewHolder(final ViewGroup parent) {
                                final TextView itemView = new TextView(parent.getContext());
                                itemView.setGravity(Gravity.CENTER);
                                itemView.setEllipsize(TextUtils.TruncateAt.END);
                                itemView.setMaxLines(1);
                                itemView.setPadding(10,5,10,5);
                                return new ExTable.CellViewHolder(itemView);
                            }

                            @Override
                            public void onBindViewHolder(final ExTable.CellViewHolder holder, final int position, final ExTable.ExTableAdapter.CellsData.Cell cell) {
                                final TextView textView = (TextView) holder.itemView;
                                textView.setText(String.valueOf(cell.value));
//                                ((TextView) holder.itemView).setTextColor(ceil.color);
                                textView.setBackgroundColor(cell.color);
                            }
                        })
                        .build()
        );

        final ExTable.ExTableAdapter.CellsData cellsData = new ExTable.ExTableAdapter.CellsData();

        Random r = new Random();
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                cellsData.addCell(col, row, new ExTable.ExTableAdapter.CellsData.Cell(1, col + "x" + row, Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255))));
            }
        }

        cellsData.addCell(0,0, new ExTable.ExTableAdapter.CellsData.Cell(1, "#", Color.RED));
        cellsData.addCell(1,0, new ExTable.ExTableAdapter.CellsData.Cell(1, "FIO", Color.GREEN));
        cellsData.addCell(2,0, new ExTable.ExTableAdapter.CellsData.Cell(1, "Class 1", Color.BLUE));
        cellsData.addCell(3,0, new ExTable.ExTableAdapter.CellsData.Cell(1, "Class 2", Color.CYAN));
        cellsData.addCell(4,0, new ExTable.ExTableAdapter.CellsData.Cell(1, "Class 3", Color.MAGENTA));
        cellsData.addCell(0,1, new ExTable.ExTableAdapter.CellsData.Cell(1, "1", Color.RED));
        cellsData.addCell(1,1, new ExTable.ExTableAdapter.CellsData.Cell(1, "Student 1", Color.GREEN));
        cellsData.addCell(2,1, new ExTable.ExTableAdapter.CellsData.Cell(1, "n", Color.BLUE));
        cellsData.addCell(3,1, new ExTable.ExTableAdapter.CellsData.Cell(1, "5", Color.BLACK));
        cellsData.addCell(4,1, new ExTable.ExTableAdapter.CellsData.Cell(1, "12", Color.CYAN));
        exTable.setCellsData(cellsData);

//        exTable.setBackgroundColor(Color.MAGENTA);
        container.addView(exTable, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
