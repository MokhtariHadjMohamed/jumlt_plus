package com.hadjmohamed.oran_agro.AdminAndDelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecTableUser;
import com.hadjmohamed.oran_agro.R;

import java.util.List;

public class ExpensesActivity extends AppCompatActivity {

    // TODO Recycle View Declaration variables
    private RecyclerView recyclerView;
    private AdapterRecTableUser adapterRecTableUser;
    private List<Expenses> expenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        // TODO Recycle View
        recyclerView = findViewById(R.id.recyclerViewExpensesActivity);

    }
}