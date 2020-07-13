package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdaptor itemsAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();

        rvItems.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    rvItems.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rvItems.scrollToPosition(
                                    rvItems.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

        ItemsAdaptor.OnLongClickListener onLongClickListener = new ItemsAdaptor.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
               // Delete the item from the model
               items.remove(position);
               // Notify the adapter
               itemsAdaptor.notifyItemRemoved(position);
               Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };


        ItemsAdaptor.OnClickListener onClickListener = new ItemsAdaptor.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                // Create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);

                // Pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);

                // Display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };

        itemsAdaptor = new ItemsAdaptor(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdaptor);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        // Add Items
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                // Add item to the model
                items.add(todoItem);
                // Notify adapter that an item is inserted
                itemsAdaptor.notifyItemInserted(items.size() - 1);
                etItem.setText("");
                // Show added message
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }


    // Handle the result of the edit activity
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);

            // Extract the original position of the edited item
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            // Update the model with the new item (same position)
            items.set(position, itemText);

            // notify the adapter
            itemsAdaptor.notifyItemChanged(position);

            // persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated!", Toast.LENGTH_SHORT).show();

        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    // Load items by reading every line of the data file
    private void loadItems(){
        try {
            items = new ArrayList<>(org.apache.commons.io.FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e){
            Log.e("MainActiviy", "Error reading items", e);
            items = new ArrayList<>();
        }
    }

    // Saves items by writing them into the data file
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActiviy", "Error writing items", e);
        }
    }
}
