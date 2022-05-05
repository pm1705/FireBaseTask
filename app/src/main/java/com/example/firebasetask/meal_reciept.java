package com.example.firebasetask;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.example.firebasetask.Fbrefs.refMeals;
import static com.example.firebasetask.orders.*;

public class meal_reciept extends AppCompatActivity {

    TextView compTitle, food, datest;
    Intent orderdata;

    String id, worker_id, serial, order, at;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_reciept);

        compTitle = (TextView) findViewById(R.id.comptitle);
        food = (TextView) findViewById(R.id.food);
        datest = (TextView) findViewById(R.id.datest);

        orderdata = getIntent();
        id = orderdata.getStringExtra("id");


        refMeals.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                for(DataSnapshot data : dS.getChildren()) {
                    if (data.getKey().equals(id)) {
                        System.out.println("succes");
                        worker_id = "" + data.child(WORKER_ID).getValue();
                        serial = "" + data.child(COMPANY_ID).getValue();
                        order = "" + data.child(MEAL_DETAILS).getValue();
                        at = "" + data.child(TIME).getValue();

                        compTitle.setText("RECEIPT:\n\tworker id:" + worker_id
                                + "\n\tsupplier id:" + serial);
                        food.setText("ORDER:\n" + order);
                        datest.setText("AT: " + at);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void back_to_db(View view) {
        finish();
    }
}