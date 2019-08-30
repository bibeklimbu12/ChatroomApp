package com.example.chatroomapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button add_room_button;
    private EditText room_name;
    private ListView listView_chatRoom;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_rooms = new ArrayList<>();

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_room_button = (Button) findViewById(R.id.add_room_btn);
        room_name = (EditText) findViewById(R.id.add_room);
        listView_chatRoom = (ListView) findViewById(R.id.add_rooms_listView);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list_rooms);
        listView_chatRoom.setAdapter(arrayAdapter);

        Request_Username();

        add_room_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mReference = database.getReference(room_name.getText().toString());
                mReference.setValue("");

                room_name.setText("");
                room_name.requestFocus();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mReference = database.getReference(room_name.getText().toString());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext())
                {
                    set.add(((DataSnapshot)i.next()).getKey());
                }

                list_rooms.clear();
                list_rooms.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView_chatRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent messages = new Intent(MainActivity.this, MessagesActivity.class);
                messages.putExtra("user_name", name);
                messages.putExtra("room_name",((TextView)view).getText().toString());
                startActivity(messages);
            }
        });
    }

    private void Request_Username()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter Username");

        final EditText inputField = new EditText(MainActivity.this);
        builder.setView(inputField);

        // When the user enters the name and press OK the name will be stored in the variable called name.
        builder.setPositiveButton("Ok", new Dialog.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int i)
            {
                name = inputField.getText().toString();
            }

        });

        // When the User press on cancel it will close the dialougebox and ask for the username agagin.
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();


                Request_Username();
            }
        });

        builder.show();
    }

}
