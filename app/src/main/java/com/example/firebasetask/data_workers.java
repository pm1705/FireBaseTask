package com.example.firebasetask;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.firebasetask.workers.*;
import static com.example.firebasetask.Fbrefs.refWorkers;


/**
 * @author paz malul
 *
 * a hub for displaying, sorting, adding, and updating the databse of the workers.
 */

public class data_workers extends AppCompatActivity implements AdapterView.OnItemClickListener{

    Intent input_intent, update_intent;
    String first_back,last_back,company_back,worker_id_back,phone_number_back;
    int add_back;

    ListView data_display;
    ArrayAdapter adp;
    ArrayList<ArrayList<String>> tbl;
    ArrayList<String> texttbl;
    ArrayList<String> keys;
    ArrayList<String> tmp;
    String strtmp;

    AlertDialog.Builder sortby;
    String[] sort_options = {"First", "Last", "Company"};
    String[] sort_helpers = {FIRST_NAME, LAST_NAME, COMPANY};
    Query query;
    int sortorderhelper;

    String[] show_options = {"First Name", "Last Name", "Company", "Id","Phone Number"};
    int sort_value, show_count;
    String sort_order;

    AlertDialog.Builder showthis;
    int[] show_list = {1,2,3,4,5};


    Button sort_button, show_button;
    ImageButton sort_order_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workers_data);

        input_intent = new Intent(this, com.example.firebasetask.add_new.class);
        update_intent = new Intent(this, com.example.firebasetask.update_remove.class);
        data_display = (ListView) findViewById(R.id.data_display);

        sort_value = 0;
        sort_order = "up";
        sort_button = (Button) findViewById(R.id.sort_button);
        sort_order_button = (ImageButton) findViewById(R.id.sort_order_button);
        show_button = (Button) findViewById(R.id.show_button);
        ValueEventListener workListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                tbl.clear();
                keys.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    tmp = new ArrayList<>();

                    tmp.add("" + data.child(FIRST_NAME).getValue());
                    tmp.add("" + data.child(LAST_NAME).getValue());
                    tmp.add("" + data.child(COMPANY).getValue());
                    tmp.add("" + data.getKey());
                    tmp.add("" + data.child(PHONE_NUMBER).getValue());
                    tmp.add("" + data.child(ACTIVE).getValue());

                    tbl.add(tmp);
                    keys.add("" + data.getKey());
                }
                update_data(sort_value);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        query = refWorkers.orderByChild(sort_helpers[sort_value]);
        query.addValueEventListener(workListener);


        data_display.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        data_display.setOnItemClickListener(this);

        sortby = new AlertDialog.Builder(this);
        sortby.setTitle("Sort workers By");
        sortby.setItems(sort_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sort_value = which;
                update_data(sort_value);
                query = refWorkers.orderByChild(sort_helpers[sort_value]);
                query.removeEventListener(workListener);
                query.addValueEventListener(workListener);
            }
        });

        showthis = new AlertDialog.Builder(this);
        showthis.setTitle("Show worker information:");
        showthis.setMultiChoiceItems(show_options ,null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b){
                    show_list[i] = i;
                }
                else{
                    show_list[i] = -1;
                }
            }
        });
        showthis.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int k) {
                update_data(sort_value);
                show_count = 0;
                for (int i=1;i<6;i++){
                    if (show_list[i-1] != -1){
                        show_count += 1;
                    }
                }
                if (show_count == 5){
                    show_button.setText("all fields");
                }
                else{
                    show_button.setText("" + show_count + " fields");
                }
                dialogInterface.cancel();
            }
        });

        tbl = new ArrayList<>();
        keys = new ArrayList<>();
        texttbl = new ArrayList<>();

        update_data(sort_value);
    }

    /**
     * return to home screen
     * @param view
     */
    public void back_home(View view) {
        finish();
    }

    /**
     * launch add_new activity with 0 paramater meaning we want to add a worker
     * @param view
     */
    public void add_input(View view) {
        input_intent.putExtra("chosendb",0);
        startActivityForResult(input_intent ,1);
    }

    /**
     * this function will be called from two diffrent activities
     * if called from the add_new activity the information sent will be added to the db as a new line
     * if from the update_remove activity the information was already updated so nothing will happen
     * except the update data so all displayed data is up to date.
     * @param source i couldn't understand how this worked so i added an extra for what activity
     * @param good
     * @param data_back
     */
    @Override
    protected void onActivityResult(int source, int good, @Nullable Intent data_back) {
        super.onActivityResult(source, good, data_back);
        if (data_back != null){
            add_back = data_back.getIntExtra("add", 0);
            if (add_back == 1){
                first_back = data_back.getStringExtra("first");
                last_back = data_back.getStringExtra("last");
                company_back = data_back.getStringExtra("company");
                worker_id_back = data_back.getStringExtra("worker_id");
                phone_number_back = data_back.getStringExtra("phone_number");

                refWorkers.child(worker_id_back).setValue("");
                refWorkers.child(worker_id_back).child(FIRST_NAME).setValue(first_back);
                refWorkers.child(worker_id_back).child(LAST_NAME).setValue(last_back);
                refWorkers.child(worker_id_back).child(COMPANY).setValue(company_back);
                refWorkers.child(worker_id_back).child(PHONE_NUMBER).setValue(phone_number_back);
                refWorkers.child(worker_id_back).child(ACTIVE).setValue(0);
            }
            update_data(sort_value);
        }
    }

    /**
     * sort the database according to the sort list, show only the wanted fields and display them in table.
     * @param sort the sort list
     */
    public void update_data(int sort){
        texttbl.clear();

        sort_button.setText(sort_options[sort]);

        for (int i=0;i<tbl.size();i++) {

            strtmp = "";

            if (sort_order == "up") sortorderhelper = i;
            else if (sort_order == "down") sortorderhelper = tbl.size() - i - 1;

            for (int j=0;j<5;j++){
                if (show_list[j] != -1){
                    strtmp += tbl.get(sortorderhelper).get(j) + " ";
                }
            }

            if (tbl.get(sortorderhelper).get(5).equals("0")){
                strtmp += "ACTIVE ";
            }
            else{
                strtmp += " INACTIVE ";
            }

            texttbl.add(strtmp);
        }

        adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, texttbl);
        data_display.setAdapter(adp);
    }

    /**
     * bring up dialog box to choose sorting options
     * @param view
     */
    public void sort_choose(View view) {
        AlertDialog sort_now = sortby.create();
        sort_now.show();
    }

    /**
     * flips the current sorting order from a->z or z->a
     * @param view
     */
    public void change_order(View view) {
        if (sort_order == "up") {
            sort_order = "down";
            sort_order_button.setImageResource(R.drawable.sort_down_dec);
        }
        else {
            sort_order = "up";
            sort_order_button.setImageResource(R.drawable.sort_down_inc);
        }
        update_data(sort_value);
    }

    /**
     * reset all show variables and launch dialog to choose them again
     * @param view
     */
    public void show_choose(View view) {
        show_list = new int[]{-1, -1, -1, -1, -1};
        AlertDialog show_now = showthis.create();
        show_now.show();
    }

    /**
     * when item is clicked send the paramaters to the update activity
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (sort_order == "up") sortorderhelper = i;
        else if (sort_order == "down") sortorderhelper = tbl.size() - i - 1;
        update_intent.putExtra("key", keys.get(sortorderhelper));
        update_intent.putExtra("chosendb", 0);
        startActivityForResult(update_intent, 1);
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
            Intent creds = new Intent(this, com.example.firebasetask.creditscreen.class);
            startActivity(creds);
        }

        else if (id == R.id.home){
            finish();
        }

        return true;
    }
}