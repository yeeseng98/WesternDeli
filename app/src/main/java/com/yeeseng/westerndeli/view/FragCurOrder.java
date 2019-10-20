package com.yeeseng.westerndeli.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.Order_Item;
import com.yeeseng.westerndeli.presenter.OrderAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

public class FragCurOrder extends Fragment {

    private ArrayAdapter<Order_Item> adapter;
    private ArrayList<Order_Item> orderItems;
    @BindView(R.id.list_order)
    ListView listView;
    private TextView totalPrice;
    private SwipeLayout swipeLayout;
    private View header;

    private final static String TAG = FragCurOrder.class.getSimpleName();

    @BindView(R.id.confirmBt)
    Button confirmBt;

    @OnClick(R.id.confirmBt)
    public void confirm() {
        if(orderItems.size() >0) {
            Intent startPlacePicker = new Intent(getActivity(), PlacePickerActivity.class);
            startPlacePicker.putExtra("OrderList", orderItems);
            startActivity(startPlacePicker);
        } else{
            onButtonShowErrorWindowClick(getView(), "Cannot send empty order!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.current_order, null);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Current Order");

        SharedPreferences prefs = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        Boolean isLog = prefs.getBoolean("isLogin", false);
        if (isLog) {
            showsplash();
            String username = prefs.getString("username", "No name defined");

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Users").document(username).collection("ConfirmedOrder")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().size() > 0){
                        confirmBt.setVisibility(View.GONE);
                        onButtonShowErrorWindowClick(view, "You have an ongoing order right now!");
                    } else {
                        orderItems = new ArrayList<>();

                        getData();
                    }
                }
            });
        } else {
            confirmBt.setVisibility(View.GONE);
            onButtonShowErrorWindowClick(view, "Please log in to access this page!");
        }
    }

    private void getData() {

        SharedPreferences prefs = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        String username = prefs.getString("username", "No name defined");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(username).collection("CurrentOrder").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Order_Item data = document.toObject(Order_Item.class);
                                orderItems.add(data);
                            }
                            setListViewHeader();
                            setListViewAdapter();
                        } else {
                            Log.d("Invalid: ", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setListViewHeader() {
    LayoutInflater inflater = getLayoutInflater();
    header = inflater.inflate(R.layout.order_list_header, listView, false);
    totalPrice = (TextView) header.findViewById(R.id.total);
    swipeLayout = (SwipeLayout) header.findViewById(R.id.swipe_layout);
    setSwipeViewFeatures();
        listView.addHeaderView(header);
}

    private void setSwipeViewFeatures() {
        //set show mode.
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, header.findViewById(R.id.bottom_wrapper));

        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                Log.i(TAG, "onClose");
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                Log.i(TAG, "on swiping");
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                Log.i(TAG, "on start open");
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                Log.i(TAG, "the BottomView totally show");
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                Log.i(TAG, "the BottomView totally close");
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });
    }

    private void setListViewAdapter() {

        adapter = new OrderAdapter(this, R.layout.order_list_item, orderItems);
        listView.setAdapter(adapter);

        double priceSum = 0;

        for (int i = 0; i < orderItems.size(); i++) {
            priceSum = priceSum + (orderItems.get(i).getItemCost() * orderItems.get(i).getPortion());
        }

        totalPrice.setText("RM" + priceSum + "\n" + orderItems.size() + " items");
    }

    public void updateAdapter() {
        adapter.notifyDataSetChanged(); //update adapter

        double priceSum = 0;

        for (int i = 0; i < orderItems.size(); i++) {
            priceSum = priceSum + (orderItems.get(i).getItemCost() * orderItems.get(i).getPortion());
        }

        totalPrice.setText("RM" + priceSum + "\n" + orderItems.size() + " items");

    }

    public void onButtonShowErrorWindowClick(View view, String popupText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(popupText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
        handler.postDelayed(runnable, 1500);
    }


}
