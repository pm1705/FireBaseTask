package com.example.firebasetask;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author paz malul
 *
 * when sent to this activity the user can enter data on one of 2 options
 * workers or companies, which will be checked for inaccuracies and added to the according databace
 */

public class add_new extends AppCompatActivity {

    Intent send_data;

    TextView tv; // will be changed according to the chosen database

    int chosendb; // 0 - workers, 1 - company

    EditText inp1,inp2,inp3,inp4,inp5; // input fields that are used accordingly with the chosen database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        send_data = getIntent();

        chosendb = send_data.getIntExtra("chosendb",0);

        inp1 = (EditText) findViewById(R.id.input0);
        inp2 = (EditText) findViewById(R.id.input2);
        inp3 = (EditText) findViewById(R.id.input4);
        inp4 = (EditText) findViewById(R.id.input5);
        inp5 = (EditText) findViewById(R.id.input6);

        tv = (TextView) findViewById(R.id.new_title);

        // set all layout attributes according to database
        if (chosendb == 0) {

            tv.setText("New Worker");

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
        }
        else if (chosendb == 1){

            tv.setText("New Company");

            inp1.setInputType(InputType.TYPE_CLASS_TEXT);
            inp1.setHint("Company Name");

            inp2.setInputType(InputType.TYPE_CLASS_TEXT);
            inp2.setHint("Serial ID");

            inp3.setInputType(InputType.TYPE_CLASS_NUMBER);
            inp3.setHint("First Phone");

            inp4.setInputType(InputType.TYPE_CLASS_NUMBER);
            inp4.setHint("Second Phone");

            inp5.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * return to the database you came from
     * @param view
     */
    public void back_to_db(View view) {
        finish();
    }

    /**
     * i made this function and an very proud of it thank you!
     * taking a string id and checking if the first 8 characters match to the last digit according to the
     * rules set by our beloved government.
     * @param str the id in string form
     * @return true if the id is valid
     */
    public boolean is_valid_id(String str){
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
     * check if the input matches up with the rules and send back to the database activity
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
            send_data.putExtra("first", inp1.getText().toString());
            send_data.putExtra("last", inp2.getText().toString());
            send_data.putExtra("company", inp3.getText().toString());
            send_data.putExtra("worker_id", inp4.getText().toString());
            send_data.putExtra("phone_number", inp5.getText().toString());
            send_data.putExtra("add",1);
            setResult(RESULT_OK, send_data);
            finish();
        }

    }

    /**
     * check if the input matches up with the rules and send back to the database activity
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
            send_data.putExtra("name", inp1.getText().toString());
            send_data.putExtra("serial", inp2.getText().toString());
            send_data.putExtra("phone1", inp3.getText().toString());
            send_data.putExtra("phone2", inp4.getText().toString());
            send_data.putExtra("add",1);
            setResult(RESULT_OK, send_data);
            finish();
        }
    }
}