package com.yeeseng.westerndeli.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.Confirm_Order;
import com.yeeseng.westerndeli.model.Menu_Item;
import com.yeeseng.westerndeli.presenter.OrderAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

public class FragMyProfile extends Fragment {

    Boolean isLog;
    String username;

    @BindView(R.id.itemPrice)
    TextView itemPrice;
    @BindView(R.id.itemCount)
    TextView itemCount;
    @BindView(R.id.estimatedTime)
    TextView estimatedTime;
    @BindView(R.id.receivedBt)
    Button receive;
    @BindView(R.id.orderHistoryBt)
    Button orderHist;

    @OnClick(R.id.settingsBt)
    public void settings(){
        Intent gotoSettings = new Intent(getActivity(),SettingsActivity.class);
        startActivity(gotoSettings);
    }

    @OnClick(R.id.receivedBt)
    public void receive(){
        showConfirmDialog();
    }

    @OnClick(R.id.orderHistoryBt)
    public void orderhistory(){
        Intent gotoHistory = new Intent(getActivity(), OrderHistoryActivity.class);
        startActivity(gotoHistory);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.my_profile,null);
        ButterKnife.bind(this, rootView);

        SharedPreferences prefs = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        isLog = prefs.getBoolean("isLogin", false);
        username = prefs.getString("username", "No name defined");

        if(isLog){
            showsplash();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Profile");

        checkUser();
    }

    public void checkUser(){

        if (isLog) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Users").document(username).collection("ConfirmedOrder")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().size() == 1){
                        getOngoingOrder();
                    } else {
                        itemCount.setText("Total Portions: None");
                        itemPrice.setText("Total Price: None");
                        estimatedTime.setText("Estimated Time: \n None");
                        receive.setEnabled(false);
                        receive.setBackgroundColor(Color.rgb(191, 191, 191));
                    }
                }
            });
        } else{
            itemCount.setText("Total Portions: None");
            itemPrice.setText("Total Price: None");
            estimatedTime.setText("Estimated Time: \n None");
            receive.setVisibility(View.GONE);
            orderHist.setVisibility(View.GONE);
        }
    }

    public void getOngoingOrder(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(username).collection("ConfirmedOrder")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Confirm_Order item = document.toObject(Confirm_Order.class);
                        itemPrice.setText("Total Price of Order: RM " + String.valueOf(item.getTotalPrice()));
                        itemCount.setText(String.valueOf("Total Portions Ordered: "+ item.getTotalPortions() + " Portion(s)"));
                        estimatedTime.setText(String.valueOf("Estimate Time for Order: \n" + item.getEstimateTime() + " Minutes"));
                    }
                } else {
                    Log.d("Invalid: ", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void translateToCompleteOrder(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(username).collection("ConfirmedOrder")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Confirm_Order item = document.toObject(Confirm_Order.class);
                    setCompleteOrder(item);
                }
            } else {
                Log.d("Invalid: ", "Error getting documents: ", task.getException());
            }
        }
    });
}

    public void setCompleteOrder(Confirm_Order conOrder){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference docRef = db.collection("Users").document(username).collection("ConfirmedOrder");

        db.collection("Users").document(username).collection("ConfirmedOrder")
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

        db.collection("Users").document(username).collection("CompletedOrder").document()
                .set(conOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onButtonShowCompleteWindowClick(getView(), "Thank you for your purchase!");
            }
        });

    }

    public void onButtonShowCompleteWindowClick(View view, String popupText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(popupText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent returnToMain = new Intent(getActivity(), MainActivity.class);
                        startActivity(returnToMain);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showConfirmDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setMessage("Are you sure you have received your purchase?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        translateToCompleteOrder();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void showsplash() {

        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.activity_splash_screen);
        dialog.setCancelable(true);
        dialog.show();

        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                {
                    dialog.dismiss();
                }
            }
        };
        handler.postDelayed(runnable, 750);
    }
}
