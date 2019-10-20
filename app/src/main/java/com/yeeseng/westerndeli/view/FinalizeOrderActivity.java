package com.yeeseng.westerndeli.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.Batch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.Order_Item;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FinalizeOrderActivity extends AppCompatActivity {

    ArrayList<Order_Item> orderList;
    String address;

    double estimateTime = 0;
    int portionCount = 0;
    double priceCount = 0;
    int totalTime = 0;

    @BindView(R.id.totalItems)
    TextView totalItems;
    @BindView(R.id.totalPrice)
    TextView totalPrice;
    @BindView(R.id.estimateDelivery)
    TextView estimate;
    @BindView(R.id.addressFull)
    TextView addressFull;

    @OnClick(R.id.backBt)
    public void back() {
        finish();
    }

    @OnClick(R.id.finalizeBt)
    public void finalize() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        String username = prefs.getString("username", "No name defined");

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        Map<String, Object> order = new HashMap<>();
        order.put("TotalPrice", priceCount);
        order.put("TotalPortions", portionCount);
        order.put("EstimateTime", estimateTime);
        order.put("Address", address);
        order.put("OrderTime", date);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference docRef = db.collection("Users").document(username).collection("CurrentOrder");

        db.collection("Users").document(username).collection("CurrentOrder")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                docRef.document(document.getId()).delete();
                            }
                        }
                    }
                });

        db.collection("Users").document(username).collection("ConfirmedOrder")
                .document().set(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onButtonShowCompleteWindowClick(FinalizeOrderActivity.this.findViewById(android.R.id.content), "Order Successfully Sent!!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onButtonShowErrorWindowClick(FinalizeOrderActivity.this.findViewById(android.R.id.content), "Something Wrong Happened!");
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle("Finalize Order");

        setContentView(R.layout.activity_finalize_order);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            orderList = new ArrayList<>();
            Bundle extras = getIntent().getExtras();
            orderList = (ArrayList<Order_Item>) extras.get("OrderList");
            address = (String) extras.get("Address");

            for (int i = 0; i < orderList.size(); i++) {
                portionCount = portionCount + orderList.get(i).getPortion();
                priceCount = priceCount + orderList.get(i).getItemCost();
                totalTime = totalTime + orderList.get(i).getItemPrepTime();
            }

            ArrayList<String> typeCheckArr = new ArrayList<>();
            ArrayList<Integer>  numChecker = new ArrayList<>();
            for (int i = 0; i < orderList.size(); i++) {
                if(!typeCheckArr.contains(String.valueOf(orderList.get(i).getItemName()))) {
                    typeCheckArr.add(orderList.get(i).getItemName());
                    numChecker.add(orderList.get(i).getItemPrepTime());
                }
            }

            estimateTime = 0;

            for(int i = 0; i < typeCheckArr.size();i++){
                estimateTime = estimateTime + numChecker.get(i);
            }

            totalItems.setText("Total Portions : " + portionCount + " Portions");
            totalPrice.setText("Total Price : RM" + priceCount );
            estimate.setText("Estimated Time : " + estimateTime + " Minutes");
            addressFull.setText(address);
        }
    }


    public void onButtonShowErrorWindowClick(View view, String popupText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(popupText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onButtonShowCompleteWindowClick(View view, String popupText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(popupText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent returnToMain = new Intent(FinalizeOrderActivity.this, MainActivity.class);
                        startActivity(returnToMain);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
