package com.example.firebasetask;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * @author paz malul
 *
 * when sent to this activity the user can enter data for a new order.
 */

public class add_new_meal extends AppCompatActivity {

    Intent send_data;

    EditText worker_id,company_id,firstCourse,mainCourse,appetizer,dessert,drink;
    String mealDetails;

    ArrayList<String> worker_ids;
    ArrayList<String> company_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_meal);

        send_data = getIntent();
        worker_ids = send_data.getStringArrayListExtra("worker_ids");
        company_ids = send_data.getStringArrayListExtra("company_ids");
        System.out.println(worker_ids);
        System.out.println(company_ids);

        mealDetails = "";

        worker_id = (EditText) findViewById(R.id.input0);
        company_id = (EditText) findViewById(R.id.input1);
        firstCourse = (EditText) findViewById(R.id.input2);
        mainCourse = (EditText) findViewById(R.id.input3);
        appetizer  = (EditText) findViewById(R.id.input4);
        dessert = (EditText) findViewById(R.id.input5);
        drink = (EditText) findViewById(R.id.input6);

    }

    /**
     * return to the database you came from
     * @param view
     */
    public void back_to_db(View view) {
        finish();
    }

    /**
     * check if the input matches up with the rules and send back to the database activity
     * @param view
     */
    public void save(View view) {
        if (firstCourse.getText().toString().matches("") && mainCourse.getText().toString().matches("")
                && appetizer.getText().toString().matches("") && dessert.getText().toString().matches("")
                && drink.getText().toString().matches("")){
            Toast.makeText(this, "fill at least one meal detail", Toast.LENGTH_SHORT).show();
        }
        else if (company_id.getText().toString().matches("") || worker_id.getText().toString().matches("")){
            Toast.makeText(this, "Enter valid ids", Toast.LENGTH_SHORT).show();
        }
        else {
            if(!worker_ids.contains(worker_id.getText().toString())){
                Toast.makeText(this, "worker id doesn't exist", Toast.LENGTH_SHORT).show();
            }
            else{
                if(!company_ids.contains(company_id.getText().toString())){
                    Toast.makeText(this, "company id doesn't exist", Toast.LENGTH_SHORT).show();
                }

                else {
                    mealDetails += " First Course: " + firstCourse.getText().toString() + "\n";
                    mealDetails += " Main Course: " + mainCourse.getText().toString() + "\n";
                    mealDetails += " Appetizer: " + appetizer.getText().toString() + "\n";
                    mealDetails += " Dessert: " + dessert.getText().toString() + "\n";
                    mealDetails += " Drink: " + drink.getText().toString();

                    send_data.putExtra("worker_id", worker_id.getText().toString());
                    send_data.putExtra("company_id", company_id.getText().toString());
                    send_data.putExtra("mealDetails", mealDetails);
                    setResult(RESULT_OK, send_data);
                    finish();
                }
            }
        }
    }
}