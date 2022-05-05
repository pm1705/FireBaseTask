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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.firebasetask.Fbrefs.refMeals;
import static com.example.firebasetask.orders.COMPANY_ID;
import static com.example.firebasetask.orders.TIME;
import static com.example.firebasetask.orders.WORKER_ID;

/**
 * @author paz malul
 *
 * a hub for displaying, sorting, adding, and updating the databse of the orders.
 */

public class data_orders extends AppCompatActivity implements AdapterView.OnItemClickListener{

    Intent input_intent, receipt_intent;
    String card_id_back,company_id_back,meal_details;
    int active_back;

    ListView data_display;
    ArrayAdapter adp;
    ArrayList<ArrayList<String>> tbl;
    ArrayList<String> texttbl;
    ArrayList<String> keys;
    ArrayList<String> tmp;
    String strtmp;
    ArrayList<String> ids;

    AlertDialog.Builder sortby;
    String[] sort_options = {"Date"};
    String[] sort_helpers = {orders.KEY_ID};
    int sortorderhelper;

    String[] show_options = {"Order ID", "Worker ID", "Company ID", "Time"};
    int sort_value, show_count;
    String sort_order;

    AlertDialog.Builder showthis;
    int[] show_list = {1,2,3,4};


    Button sort_button, show_button;
    ImageButton sort_order_button;

    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_data);

        input_intent = new Intent(this,add_new_meal.class);
        receipt_intent = new Intent(this, com.example.firebasetask.meal_reciept.class);
        data_display = (ListView) findViewById(R.id.data_display);

        sort_value = 0;
        sort_order = "up";
        sort_button = (Button) findViewById(R.id.sort_button);
        sort_order_button = (ImageButton) findViewById(R.id.sort_order_button);
        show_button = (Button) findViewById(R.id.show_button);

        data_display.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        data_display.setOnItemClickListener(this);

        ValueEventListener workListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                tbl.clear();

                for(DataSnapshot data : dS.getChildren()) {
                    tmp = new ArrayList<>();

                    tmp.add("" + data.getKey());
                    tmp.add("" + data.child(WORKER_ID).getValue());
                    tmp.add("" + data.child(COMPANY_ID).getValue());
                    tmp.add("" + data.child(TIME).getValue());

                    tbl.add(tmp);
                }
                update_data(sort_value);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        query = refMeals.orderByChild(sort_helpers[sort_value]);
        query.addValueEventListener(workListener);


        sortby = new AlertDialog.Builder(this);
        sortby.setTitle("Sort Orders By");
        sortby.setItems(sort_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sort_value = which;
                update_data(sort_value);
            }
        });

        showthis = new AlertDialog.Builder(this);
        showthis.setTitle("Show order information:");
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
                for (int i=1;i<4;i++){
                    if (show_list[i-1] != -1){
                        show_count += 1;
                    }
                }
                if (show_count == 3){
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

            for (int j=0;j<4;j++){
                if (show_list[j] != -1){
                    strtmp += tbl.get(sortorderhelper).get(j) + " ";
                }
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
        show_list = new int[]{-1, -1, -1, -1};
        AlertDialog show_now = showthis.create();
        show_now.show();
    }

    /**
     * when item is clicked send the paramaters to the show "receipt" activity
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (sort_order == "up") sortorderhelper = i;
        else if (sort_order == "down") sortorderhelper = tbl.size() - i - 1;
        receipt_intent.putExtra("id", String.valueOf(sortorderhelper+1));

        startActivity(receipt_intent);
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