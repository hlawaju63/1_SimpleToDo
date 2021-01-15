package com.example.a1_simpletodo;

import android.content.ClipData;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION =  "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvView;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvView = findViewById(R.id.rvView);

        etItem.setText("I am doing this from java");

        loadItems();
        /*
        items = new ArrayList<>();
        items.add("Buy Milk");
        items.add("Go to gym");
        items.add("Have fun");
        */

        ItemAdapter.OnClickListener onClickListener = new ItemAdapter.OnClickListener() {
            @Override
            public void onClickListener(int position) {
                Log.d("MainActivity", "single click at position" + position);

                //Create a new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //pass the data being edited.
                i.putExtra(KEY_ITEM_TEXT, items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                //display the activity
                startActivityForResult(i, EDIT_TEXT_CODE );
            }
        };

        ItemAdapter.OnLongClickListener onLongClickListener = new ItemAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                //delete the item on the position
                items.remove(position);
                //notify the adapter
                itemAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "The item was removed", Toast.LENGTH_LONG);
                saveItems();
            }
        };

        itemAdapter = new ItemAdapter(items, onLongClickListener, onClickListener);
        rvView.setAdapter(itemAdapter);
        rvView.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toDoItem = etItem.getText().toString();
                //Add item to the model
                items.add(toDoItem);
                //notify adapter that item is inserted
                itemAdapter.notifyItemInserted(items.size()-1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "New items added", Toast.LENGTH_LONG);
                saveItems();
            }
        });
    }


    //handle the result of the edit Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE)
        {
            //retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);

            //extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //update the model at the right position with new item text
            items.set(position, itemText);

            //notify the adapter
            itemAdapter.notifyItemChanged(position);

            //persist the change
            saveItems();
        }
        else {
            Log.w("MainActibity", "Unkown call to MainActivity");
            Toast.makeText(getApplicationContext(), "Item updated sucess", Toast.LENGTH_SHORT).show();
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    private File getDataFile()
    {
        return new File(getFilesDir(), "data.txt");
    }

    //This function will load  items by reading our data of txt file
    //also we will utilize apache commons library
    private void loadItems()
    {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
        }
    }



    //This function saves item by writing them into data txt file
    private void saveItems()
    {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
        }
    }
}
