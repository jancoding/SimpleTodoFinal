package com.example.simpletodofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class RemovedActivity extends AppCompatActivity {

    RecyclerView removedItems;
    ItemsAdapter itemsAdapter;
    Button btnBack;
    ArrayList<String> removedItemsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removed);
        removedItems = findViewById(R.id.removedItems);
        btnBack = findViewById(R.id.btnBack);


        Bundle b = getIntent().getExtras();
        if (b != null) {
            removedItemsList = b.getStringArrayList("KEY");
        }

        itemsAdapter = new ItemsAdapter(removedItemsList);
        removedItems.setAdapter(itemsAdapter);
        removedItems.setLayoutManager(new LinearLayoutManager(this));


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent which will contain the results
                Intent intent = new Intent();
                // set the result of the intent
                setResult(RESULT_OK, intent);
                // finish the activity (close the current screen and go back)
                finish();
            }
        });
    }
}