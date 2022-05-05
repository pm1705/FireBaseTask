package com.example.firebasetask;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.firebasetask.Fbrefs.refCompanies;
import static com.example.firebasetask.Fbrefs.refMeals;
import static com.example.firebasetask.Fbrefs.refWorkers;
import static com.example.firebasetask.orders.*;
import static com.example.firebasetask.workers.*;

/**
 * @author paz malul
 *
 * the home of the app, enetr all hubs + a quick access button to add a new order.
 */

public class MainActivity extends AppCompatActivity {

    Intent worker_intent, company_intent, orders_intent, add_intent;
    String worker_id_back,company_id_back,meal_details;

    ArrayList<String> worker_ids, company_ids, meal_ids;
    Query query_workers, query_companies;

    String current_meal_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        worker_intent = new Intent(this, data_workers.class);
        company_intent = new Intent(this, data_companies.class);
        orders_intent = new Intent(this, data_orders.class);
        add_intent = new Intent(this, add_new_meal.class);

        worker_ids = new ArrayList<>();
        company_ids = new ArrayList<>();
        meal_ids = new ArrayList<>();

        ValueEventListener workListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                worker_ids.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    worker_ids.add("" + data.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        query_workers = refWorkers.orderByKey();
        query_workers.addValueEventListener(workListener);

        ValueEventListener compListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                company_ids.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    company_ids.add("" + data.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        query_companies = refCompanies.orderByKey();
        query_companies.addValueEventListener(compListener);

        ValueEventListener mealListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                meal_ids.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    meal_ids.add("" + data.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        query_companies = refMeals.orderByKey().limitToLast(1);
        query_companies.addValueEventListener(mealListener);
    }

    /**
     * enter worker hub
     * @param view
     */
    public void show_workers(View view) {
        startActivity(worker_intent);
    }

    /**
     * enter company hub
     * @param view
     */
    public void show_companies(View view) { startActivity(company_intent); }

    /**
     * enter orders hub
     * @param view
     */
    public void show_orders(View view) { startActivity(orders_intent); }

    /**
     * quick access to add a new order
     * @param view
     */
    public void add_order(View view) {
        add_intent.putExtra("worker_ids", worker_ids);
        add_intent.putExtra("company_ids", company_ids);
        startActivityForResult(add_intent, 0); }

    /**
     * add the info from the add_new_meal activity
     * @param source
     * @param good
     * @param data_back
     */
    @Override
    protected void onActivityResult(int source, int good, @Nullable Intent data_back) {
        super.onActivityResult(source, good, data_back);
        if (data_back != null){
            worker_id_back = data_back.getStringExtra("worker_id");
            company_id_back = data_back.getStringExtra("company_id");
            meal_details = data_back.getStringExtra("mealDetails");

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            if (meal_ids.size() != 0)
                current_meal_id = "" + (Integer.parseInt(meal_ids.get(meal_ids.size()-1)) + 1);
            else current_meal_id = "1";

            refMeals.child(current_meal_id).setValue("");
            refMeals.child(current_meal_id).child(orders.WORKER_ID).setValue(worker_id_back);
            refMeals.child(current_meal_id).child(COMPANY_ID).setValue(company_id_back);
            refMeals.child(current_meal_id).child(MEAL_DETAILS).setValue(meal_details);
            refMeals.child(current_meal_id).child(TIME).setValue(formatter.format(calendar.getTime()));
        }
    }

    /**
     * menu funcs
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.credits){
            Intent creds = new Intent(this,creditscreen.class);
            startActivity(creds);
        }

        return true;
    }
}