package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        // Mock data
        items = new ArrayList<>();
        items.add("Buy Milk");
        items.add("Complete Assignment");
        items.add("Buy gift");
        items.add("Read book");
        items.add("Finish Flutter Project");

        ItemsAdaptor itemsAdaptor = new ItemsAdaptor(items);
        rvItems.setAdapter(itemsAdaptor);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        // Render Mock Data in RecyclerView

    }
}
