package com.yeeseng.westerndeli.view;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.Global_Logon;
import com.yeeseng.westerndeli.model.Menu_Item;
import com.yeeseng.westerndeli.model.PortionViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

public class FragItemDetail extends Fragment {

    Menu_Item item;

    PortionViewModel portionViewModel;

    @BindView(R.id.itemName)
    TextView itemName;
    @BindView(R.id.itemCost)
    TextView itemCost;
    @BindView(R.id.prepTime)
    TextView itemPrepTime;
    @BindView(R.id.itemFilter)
    TextView itemFilter;
    @BindView(R.id.itemDesc)
    TextView itemDesc;
    @BindView(R.id.itemImage)
    ImageView itemImage;
    @BindView(R.id.porcCounter)
    TextView counterView;
    @BindView(R.id.addItemBtn)
    Button addBt;

    Boolean userLogged = false;

    @OnClick({R.id.posIncrement, R.id.negIncrement, R.id.addItemBtn})
    public void setViewOnlick(View view) {
        switch (view.getId()) {
            case R.id.posIncrement:
                portionViewModel.setPortion(portionViewModel.portion + 1);
                counterView.setText("Portion: " + String.valueOf(portionViewModel.portion));
                break;

            case R.id.negIncrement:
                if (portionViewModel.portion == 1) {
                    onButtonShowErrorWindowClick(view, "Portion cannot go below 1!");
                } else {
                    portionViewModel.setPortion(portionViewModel.portion - 1);
                    Log.e("SCORE-", String.valueOf(portionViewModel.portion));
                    counterView.setText("Portion: " + String.valueOf(portionViewModel.portion));
                }
                break;

            case R.id.addItemBtn:
                if (item != null) {

                    SharedPreferences prefs = getContext().getSharedPreferences("Login", MODE_PRIVATE);
                    Boolean isLog = prefs.getBoolean("isLogin", false);
                    String username = prefs.getString("username", "No name defined");

                    if (isLog) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("Users").document(username).collection("ConfirmedOrder")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().size() > 0){
                                    onButtonShowErrorWindowClick(view, "You have an ongoing order!");
                                } else {
                                    onButtonShowConfirmWindowClick(view, "Confirm Order?");
                                }
                            }
                        });
                    } else{
                        onButtonShowErrorWindowClick(view, "Please log in to order an item!");
                    }
                } else {
                    Log.e("INVALIDITEM", "ITEM IS NULL");
                }
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.item_detail, null);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        portionViewModel = ViewModelProviders.of(this).get(PortionViewModel.class);

        //receive bundle obtained previously
        if (getArguments() != null) {
            item = getArguments().getParcelable("selectedItem");

            getActivity().setTitle(item.getItemName());

            itemName.setText(item.getItemName());
            itemCost.setText(" RM" + item.getItemCost().toString());
            itemPrepTime.setText(" " + item.getItemPrepTime().toString() + " Minutes");
            itemFilter.setText(item.getItemFilterBy() + " " + item.getItemCategory());
            itemDesc.setText(item.getItemDescription());
            if (item.getChefRecommended()) {
                itemName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_rated, 0);
            }
            Picasso.get()
                    .load(item.getItemUrl())
                    .into(itemImage);

        }

        if (portionViewModel.portion == null) {
            portionViewModel.setPortion(1);
        }

        counterView.setText("Portion: " + String.valueOf(portionViewModel.portion));

        SharedPreferences prefs = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        Boolean isLog = prefs.getBoolean("isLogin", false);
        if(!isLog) {
            addBt.setBackgroundColor(Color.rgb(191, 191, 191));
        } else {
            userLogged = true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_bar, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem register = menu.findItem(R.id.sign_up);
        MenuItem logout = menu.findItem(R.id.log_out);
        MenuItem signin = menu.findItem(R.id.sign_in);
        MenuItem filter = menu.findItem(R.id.action_filter);

        register.setVisible(!userLogged);
        logout.setVisible(userLogged);
        signin.setVisible(!userLogged);
        filter.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.sign_in:
                Intent goToSignIn = new Intent(getActivity(), SignInActivity.class);
                startActivity(goToSignIn);
                return true;

            case R.id.sign_up:
                Intent goToSignUp = new Intent(getActivity(), SignUpActivity.class);
                startActivity(goToSignUp);
                return true;

            case R.id.log_out:
                SharedPreferences preferences = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
                preferences.edit().putBoolean("isLogin",false).commit();
                preferences.edit().remove("username").commit();
                getActivity().invalidateOptionsMenu();
                Global_Logon.isWelcomed = false;
                userLogged = false;
                Intent backToMain = new Intent(getActivity(), MainActivity.class);
                startActivity(backToMain);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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


    public void onButtonShowConfirmWindowClick(View view, String popupText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(popupText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences prefs = getContext().getSharedPreferences("Login", MODE_PRIVATE);
                        String username = prefs.getString("username", "No name defined");

                        Map<String, Object> order = new HashMap<>();
                        order.put("ItemName", item.getItemName());
                        order.put("ItemCost", item.getItemCost());
                        order.put("ItemPrepTime", item.getItemPrepTime());
                        order.put("ItemCategory", item.getItemCategory());
                        order.put("Portion", portionViewModel.getPortion());

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            db.collection("Users").document(username).collection("CurrentOrder")
                                    .document(item.getItemName()).set(order)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            onButtonShowCompleteWindowClick(view, "Item successfully added!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            onButtonShowErrorWindowClick(view, "Item failed to be added!");
                                        }
                                    });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
