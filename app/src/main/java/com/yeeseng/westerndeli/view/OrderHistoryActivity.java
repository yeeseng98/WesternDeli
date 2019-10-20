package com.yeeseng.westerndeli.view;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.Complete_Order;
import com.yeeseng.westerndeli.model.Confirm_Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderHistoryActivity extends AppCompatActivity {

    @BindView(R.id.main_table)
    TableLayout orderTable;

    @OnClick(R.id.backBt)
    public void back(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        ButterKnife.bind(this);

        this.setTitle("Order History");

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        String username = prefs.getString("username", "No name defined");

        TableRow tr_head = new TableRow(this);
        tr_head.setId(View.generateViewId());
        tr_head.setBackgroundColor(Color.rgb(255,194,87));
        tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView label_date = new TextView(this);
        label_date.setId(View.generateViewId());
        label_date.setText("Date");
        label_date.setTextColor(Color.BLACK);
        label_date.setPadding(5, 5, 5, 5);
        tr_head.addView(label_date);// add the column to the table row here

        TextView label_portion = new TextView(this);
        label_portion.setId(View.generateViewId());// define id that must be unique
        label_portion.setText("Portions"); // set the text for the header
        label_portion.setTextColor(Color.BLACK); // set the color
        label_portion.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_portion); // add the column to the table row here

        TextView label_price = new TextView(this);
        label_price.setId(View.generateViewId());// define id that must be unique
        label_price.setText("Price"); // set the text for the header
        label_price.setTextColor(Color.BLACK); // set the color
        label_price.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_price); // add the column to the table row here

        orderTable.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(username).collection("CompletedOrder")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e("OR","SUCCESS");
                    ArrayList<Complete_Order> orderHistArr = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Complete_Order item = document.toObject(Complete_Order.class);
                        orderHistArr.add(item);
                    }
                    Log.e("OR",String.valueOf(orderHistArr.size()));

                    int count = 0;
                    for(int i =0; i < orderHistArr.size(); i++){
                        Log.e("OR",String.valueOf(orderHistArr.get(i).getAddress()));

                        String date = orderHistArr.get(i).getOrderTime();
                        Double price = orderHistArr.get(i).getTotalPrice();
                        int portion = orderHistArr.get(i).getTotalPortions();

                        // Create the table row
                        TableRow tr = new TableRow(getApplicationContext());

                        TextView dateTxt = new TextView(getApplicationContext());
                        dateTxt.setId(View.generateViewId());// define id that must be unique
                        dateTxt.setText(date); // set the text for the header
                        dateTxt.setTextColor(Color.BLACK); // set the color
                        dateTxt.setPadding(5, 5, 5, 5); // set the padding (if required)
                        tr.addView(dateTxt); // add the column to the table row here

                        TextView portionTxt= new TextView(getApplicationContext());
                        portionTxt.setId(View.generateViewId());// define id that must be unique
                        portionTxt.setText(String.valueOf(portion)); // set the text for the header
                        portionTxt.setTextColor(Color.BLACK); // set the color
                        portionTxt.setPadding(5, 5, 5, 5); // set the padding (if required)
                        tr.addView(portionTxt); // add the column to the table row here

                        TextView priceTxt = new TextView(getApplicationContext());
                        priceTxt.setId(View.generateViewId());// define id that must be unique
                        priceTxt.setText("RM"+String.valueOf(price)); // set the text for the header
                        priceTxt.setTextColor(Color.BLACK); // set the color
                        priceTxt.setPadding(5, 5, 5, 5); // set the padding (if required)
                        tr.addView(priceTxt); // add the column to the table row here

                        if(count%2!=0) tr.setBackgroundColor(Color.rgb(255,194,87));

                        tr.setId(View.generateViewId());
                        tr.setLayoutParams(new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));

                        orderTable.addView(tr, new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));

                        count++;
                    }
                } else {
                    Log.d("Invalid: ", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private String changeDateFormat(String currentFormat,String requiredFormat,String dateString){
        String result="";
        if (Strings.isNullOrEmpty(dateString)){
            return result;
        }
        SimpleDateFormat formatterOld = new SimpleDateFormat(currentFormat, Locale.getDefault());
        SimpleDateFormat formatterNew = new SimpleDateFormat(requiredFormat, Locale.getDefault());
        Date date=null;
        try {
            date = formatterOld.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            result = formatterNew.format(date);
        }
        return result;
    }


}
