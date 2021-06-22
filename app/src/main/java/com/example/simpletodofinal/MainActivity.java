package com.example.simpletodofinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final String KEY_ITEM_LIST = "item_list";
    public static final int EDIT_TEXT_CODE = 20;
    public static final int REMOVED_CODE = 30;

    List<String> items;
    ArrayList<String> removedItems = new ArrayList<>();
    Button btnAdd;
    Button btnClear;
    Button btnRemoved;
    EditText newTodo;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        newTodo = findViewById(R.id.newTodo);
        rvItems = findViewById(R.id.rvItems);
        btnClear = findViewById(R.id.btnClear);
        btnRemoved = findViewById(R.id.btnRemoved);

        loadItems(true);
        loadItems(false);


        ItemsAdapter.OnLongClickListener onLongClickListener =  new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                removedItems.add(items.get(position));
                items.remove(position);
                // Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems(true);
                saveItems(false);
            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at position " + position);
                // create the new edit activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                // pass relevant data to edit activity
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                // display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };



        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = newTodo.getText().toString();
                // Add item to the model
                items.add(todoItem);
                // Notify adapter that we inserted an item
                itemsAdapter.notifyItemInserted(items.size() - 1);
                newTodo.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems(true);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.clear();
                removedItems.clear();
                newTodo.setText("");
                itemsAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Todo List was cleared", Toast.LENGTH_SHORT).show();
                saveItems(true);
            }
        });

        btnRemoved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Sending to Removed Items Screen");
                // create the new removed activity
                Intent i = new Intent(MainActivity.this, RemovedActivity.class);

                // pass relevant data to removed activity
                Bundle b = new Bundle();
                b.putStringArrayList("KEY",(ArrayList<String>) removedItems);
                i.putExtras(b);
                //i.putExtra(KEY_ITEM_LIST, removedItems);
                Log.d("MainActivity", String.valueOf(removedItems));
                // display the activity
                startActivityForResult(i, REMOVED_CODE);
            }
        });

    }

    // PERSISTENCE CODE

    private File getDataFile(boolean isFull) {
        if (isFull) {
            return new File(getFilesDir(), "data.txt");
        }
        return new File(getFilesDir(), "removed.txt");
    }

    // This function will load items by reading every line of the data.txt file
    private void loadItems(boolean isFull) {
        if (isFull) {
            try {
                items = new ArrayList<>(FileUtils.readLines(getDataFile(isFull), Charset.defaultCharset()));
            } catch (IOException e) {
                Log.e("MainActivity", "Error reading items", e);
                items = new ArrayList<>();
            }
        } else {
            try {
                removedItems = new ArrayList<>(FileUtils.readLines(getDataFile(isFull), Charset.defaultCharset()));
            } catch (IOException e) {
                Log.e("MainActivity", "Error reading items", e);
                removedItems = new ArrayList<>();
            }
        }
    }

    // This function saves items by writing them into the data file
    private void saveItems(boolean isFull) {
        if (isFull) {
            try {
                FileUtils.writeLines(getDataFile(isFull), items);
            } catch (IOException e) {
                Log.e("MainActivity", "Error writing items", e);
            }
        } else {
            try {
                FileUtils.writeLines(getDataFile(isFull), removedItems);
            } catch (IOException e) {
                Log.e("MainActivity", "Error writing items", e);
            }
        }


    }


    // handle the result of the edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_TEXT_CODE && resultCode == RESULT_OK) {
            // Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            // update the model at the right position with the new item text
            items.set(position, itemText);
            // notify the adapter
            itemsAdapter.notifyItemChanged(position);
            // persist the changes
            saveItems(true);
            Toast.makeText(getApplicationContext(), "Item updated successfully.", Toast.LENGTH_SHORT).show();

        } else if (requestCode == REMOVED_CODE) {
            Log.d("MainActivity", "Return successfully from removed screen.");
        }else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }
}