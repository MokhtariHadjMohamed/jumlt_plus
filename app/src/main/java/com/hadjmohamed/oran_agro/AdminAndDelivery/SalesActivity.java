package com.hadjmohamed.oran_agro.AdminAndDelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.ListFormatter;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.hadjmohamed.oran_agro.R;

public class SalesActivity extends AppCompatActivity {


    private TableLayout tableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        tableView = findViewById(R.id.tableView);
        String[] Header = {"Id", "Name", "Surname", "Age"};
        String[][] data = {{"1", "Jhon", "Doa", "89"},
                {"2","Alfred","Doa","101"},
        };

        addRow(data[0]);
        addRow(data[1]);

    }

    private void addRow(String[] data){
        TableRow tableRow = new TableRow(this);
        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                10f));

        TextView textView2 = new TextView(this);
        textView2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f));

        TextView textView3 = new TextView(this);
        textView3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f));

        TextView textView4 = new TextView(this);
        textView4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f));

        textView1.setText(data[0]);
        textView2.setText(data[1]);
        textView3.setText(data[2]);
        textView4.setText(data[3]);

        tableRow.addView(textView1);
        tableRow.addView(textView2);
        tableRow.addView(textView3);
        tableRow.addView(textView4);

        tableView.addView(tableRow);
    }
}