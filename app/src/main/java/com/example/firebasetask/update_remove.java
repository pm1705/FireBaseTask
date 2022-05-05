package com.example.firebasetask;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.firebasetask.Fbrefs.refCompanies;
import static com.example.firebasetask.companies.*;
import static com.example.firebasetask.workers.*;
import static com.example.firebasetask.Fbrefs.refWorkers;
import static com.example.firebasetask.workers.ACTIVE;
import static com.example.firebasetask.workers.PHONE_NUMBER;

/**
 * @author paz malul
 *
 * when sent to this activity the user can update data on one of 2 options
 * workers or companies, which will be checked for inaccuracies and added to the according databace
 */

public class update_remove extends AppCompatActivity {

    Intent come;
    EditText inp1,inp2,inp3,inp4,inp5;
    String key;
    TextView tv;

    int chosendb; // 0 - workers, 1 - company

    Switch active_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_remove);

        come = getIntent();
        key = come.getStringExtra("key");

        chosendb = come.getIntExtra("chosendb",0);

        inp1 = (EditText) findViewById(R.id.input0);
        inp2 = (EditText) findViewById(R.id.input2);
        inp3 = (EditText) findViewById(R.id.input4);
        inp4 = (EditText) findViewById(R.id.input5);
        inp5 = (EditText) findViewById(R.id.input6);

        tv = (TextView) findViewById(R.id.new_title);

        active_switch = (Switch) findViewById(R.id.active_switch);
        active_switch.setChecked(true);

        if (chosendb == 0) {
            tv.setText("Update Worker");

            inp1.setInputType(InputType.TYPE_CLASS_TEXT);
            inp1.setHint("First Name");

            inp2.setInputType(InputType.TYPE_CLASS_TEXT);
            inp2.setHint("Last Name");

            inp3.setInputType(InputType.TYPE_CLASS_TEXT);
            inp3.setHint("Company");

            inp4.setInputType(InputType.TYPE_CLASS_NUMBER);
            inp4.setHint("Worker ID");

            inp5.setInputType(InputType.TYPE_CLASS_NUMBER);
            inp5.setHint("Phone Number");


            refWorkers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {
                    for(DataSnapshot data : dS.getChildren()) {
                        if (data.getKey().equals(key)) {
                            inp1.setText("" + data.child(FIRST_NAME).getValue());
                            inp2.setText("" + data.child(LAST_NAME).getValue());
                            inp3.setText("" + data.child(COMPANY).getValue());
                            inp4.setText(key);
                            inp5.setText("" + data.child(PHONE_NUMBER).getValue());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


        }
        else if (chosendb == 1){
            tv.setText("Update company");

            inp1.setInputType(InputType.TYPE_CLASS_TEXT);
            inp1.setHint("Company Name");

            inp2.setInputType(InputType.TYPE_CLASS_TEXT);
            inp2.setHint("Serial ID");

            inp3.setInputType(InputType.TYPE_CLASS_NUMBER);
            inp3.setHint("First Phone");

            inp4.setInputType(InputType.TYPE_CLASS_NUMBER);
            inp4.setHint("Second Phone");

            inp5.setVisibility(View.INVISIBLE);

            refCompanies.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {
                    for(DataSnapshot data : dS.getChildren()) {
                        if (data.getKey().equals(key)) {
                            inp1.setText("" + data.child(COMAPNY_NAME).getValue());
                            inp2.setText(key);
                            inp3.setText("" + data.child(PHONE_NUMBER).getValue());
                            inp4.setText("" + data.child(SECOND_PHONE_NUMBER).getValue());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }


    }

    /**
     * i made this function and an very proud of it thank you!
     * taking a string id and checking if the first 8 characters match to the last digit according to the
     * rules set by our beloved government.
     * @param str the id in string form
     * @return true if the id is valid
     */
    public static boolean is_valid_id(String str){
        int last_dig = Integer.parseInt(String.valueOf(str.charAt(8)));
        int sum = 0;
        for (int i=0;i<8;i+=2){
            sum = sum + Integer.parseInt(String.valueOf(str.charAt(i))) * 1;
            if (Integer.parseInt(String.valueOf(str.charAt(i+1))) * 2 < 10){
                sum = sum + Integer.parseInt(String.valueOf(str.charAt(i+1))) * 2;
            }
            else{
                sum = sum + 1;
                sum = sum + (Integer.parseInt(String.valueOf(str.charAt(i+1))) * 2)-10;
            }
        }
        if (((last_dig) + sum)%10 == 0){
            return true;
        }
        return false;
    }

    /**
     * return to the database you came from
     * @param view
     */
    public void back_to_db(View view) {
        finish();
    }

    /**
     * send to the according save function
     * @param view
     */
    public void save(View view) {
        if (chosendb == 0) {
            save_worker(view);
        }
        else if(chosendb == 1){
            save_company(view);
        }
    }

    /**
     * check if the input matches up with the rules, update in the db and send back to the database activity
     * @param view
     */
    public void save_worker(View view) {
        if (inp1.getText().toString().matches("") || inp2.getText().toString().matches("") ||
                inp3.getText().toString().matches("") || inp4.getText().toString().matches("") ||
                inp5.getText().toString().matches("")){

            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
        }
        else if (inp4.getText().toString().length() != 9){
            Toast.makeText(this, "Enter a valid ID.", Toast.LENGTH_SHORT).show();
        }
        else if (inp5.getText().toString().length() != 10 && inp5.getText().toString().length() != 9){
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
        }
        else if (!is_valid_id(inp4.getText().toString())){
            Toast.makeText(this, "Enter a valid id number", Toast.LENGTH_SHORT).show();
        }
        else {

            refWorkers.child(key).removeValue();

            refWorkers.child(inp4.getText().toString()).setValue("");
            refWorkers.child(inp4.getText().toString()).child(FIRST_NAME).setValue(inp1.getText().toString());
            refWorkers.child(inp4.getText().toString()).child(LAST_NAME).setValue(inp2.getText().toString());
            refWorkers.child(inp4.getText().toString()).child(COMPANY).setValue(inp3.getText().toString());
            refWorkers.child(inp4.getText().toString()).child(PHONE_NUMBER).setValue(inp5.getText().toString());

            if (active_switch.isChecked()){
                refWorkers.child(inp4.getText().toString()).child(ACTIVE).setValue(0);
            }
            else{
                refWorkers.child(inp4.getText().toString()).child(ACTIVE).setValue(1);
            }

            come.putExtra("add",0);
            setResult(RESULT_OK, come);

            finish();
        }

    }

    /**
     * check if the input matches up with the rules, update in the db and send back to the database activity
     * @param view
     */
    public void save_company(View view) {
        if (inp1.getText().toString().matches("") || inp2.getText().toString().matches("") ||
                inp3.getText().toString().matches("") || inp4.getText().toString().matches("")){
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
        }
        else if (inp3.getText().toString().length() < 9 || inp4.getText().toString().length() < 9 || inp3.getText().toString().length() > 10 || inp4.getText().toString().length() > 10){
            Toast.makeText(this, "Enter valid phone Numbers", Toast.LENGTH_SHORT).show();
        }
        else {
            refCompanies.child(key).removeValue();
            refCompanies.child(inp2.getText().toString()).setValue("");
            refCompanies.child(inp2.getText().toString()).child(COMAPNY_NAME).setValue(inp1.getText().toString());
            refCompanies.child(inp2.getText().toString()).child(PHONE_NUMBER).setValue(inp3.getText().toString());
            refCompanies.child(inp2.getText().toString()).child(SECOND_PHONE_NUMBER).setValue(inp4.getText().toString());

            if (active_switch.isChecked()){
                refCompanies.child(inp2.getText().toString()).child(ACTIVE).setValue(0);
            }
            else{
                refCompanies.child(inp2.getText().toString()).child(ACTIVE).setValue(1);
            }

            come.putExtra("add",0);
            setResult(RESULT_OK, come);
            finish();
        }
    }


}