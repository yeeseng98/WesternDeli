package com.yeeseng.westerndeli.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yeeseng.westerndeli.model.Global_Logon;
import com.yeeseng.westerndeli.presenter.MainAdapter;
import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.List_Category;
import com.yeeseng.westerndeli.presenter.MainListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class FragMainMenu extends Fragment implements MainListener {

    private RecyclerView rv;
    private List<List_Category> list_category;

    private MainAdapter adapter;

    private Boolean userLogged = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        showsplash();
        return inflater.inflate(R.layout.main_menu,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        //ADD DUMMY ITEM HERE
//        Map<String, Object> item1 = new HashMap<>();
//        item1.put("ChefRecommended", false);
//        item1.put("ItemCategory","Entrée");
//        item1.put("ItemCost", 16.0);
//        item1.put("ItemDescription", "Aspo Capsium Iecna");
//        item1.put("ItemFilterBy", "Beef");
//        item1.put("ItemName", "Grilled Beef");
//        item1.put("ItemPrepTime", 5);
//        item1.put("ItemUrl","https://cdn.discordapp.com/attachments/557530144567459860/580421893933236254/1.png" );
//
//        Map<String, Object> item2 = new HashMap<>();
//        item2.put("ChefRecommended", true);
//        item2.put("ItemCategory","Entrée");
//        item2.put("ItemCost", 17.0);
//        item2.put("ItemDescription", "Red Wine Extraction");
//        item2.put("ItemFilterBy", "Beef");
//        item2.put("ItemName", "Beef Brisket");
//        item2.put("ItemPrepTime", 8);
//        item2.put("ItemUrl","https://cdn.discordapp.com/attachments/557530144567459860/580421862182486036/Beef-Brisket-1-600x400.png" );
//
//        Map<String, Object> item3 = new HashMap<>();
//        item3.put("ChefRecommended", false);
//        item3.put("ItemCategory","Entrée");
//        item3.put("ItemCost", 9.0);
//        item3.put("ItemDescription", "Refreshing");
//        item3.put("ItemFilterBy", "Chicken");
//        item3.put("ItemName", "Grilled Balsamic Chicken");
//        item3.put("ItemPrepTime", 3);
//        item3.put("ItemUrl","https://cdn.discordapp.com/attachments/557530144567459860/580421267900071977/balsamic-chicken640x3601.png" );
//
//        Map<String, Object> item4 = new HashMap<>();
//        item4.put("ChefRecommended", false);
//        item4.put("ItemCategory","Entrée");
//        item4.put("ItemCost", 6.0);
//        item4.put("ItemDescription", "Mild and Warm");
//        item4.put("ItemFilterBy", "Chicken");
//        item4.put("ItemName", "Rice On Chicken");
//        item4.put("ItemPrepTime", 5);
//        item4.put("ItemUrl","https://cdn.discordapp.com/attachments/557530144567459860/580421567721373717/swbbqentree_pop.png" );
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Dish Categories").document("Entrée").collection("Entrée Dishes").document(String.valueOf(item1.get("ItemName"))).set(item1);
//        db.collection("Dish Categories").document("Entrée").collection("Entrée Dishes").document(String.valueOf(item2.get("ItemName"))).set(item2);
//        db.collection("Dish Categories").document("Entrée").collection("Entrée Dishes").document(String.valueOf(item3.get("ItemName"))).set(item3);
//        db.collection("Dish Categories").document("Entrée").collection("Entrée Dishes").document(String.valueOf(item4.get("ItemName"))).set(item4);

        getActivity().setTitle("Main Menu");
        rv=(RecyclerView)view.findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        list_category =new ArrayList<>();
        getdata();

        SharedPreferences prefs = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        Boolean isLog = prefs.getBoolean("isLogin", false);
        if (isLog) {
            String welcome = getString(R.string.welcome);
            String username = prefs.getString("username", "No name defined");

            if(!Global_Logon.isWelcomed){
                showLoginUser(view,welcome + username + "!");
                Global_Logon.isWelcomed = true;
            }
            userLogged = true;
        }
    }

    private void getdata() {
        //get all categories for main menu
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Dish Categories").orderBy("CategoryOrder", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List_Category data = document.toObject(List_Category.class);
                                list_category.add(data);
                            }
                            setupData(list_category);
                        } else {
                            Log.d("Invalid: ", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setupData(List<List_Category>list_category) {

        adapter=new MainAdapter(list_category,getContext(),this);
        rv.setAdapter(adapter);

    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        // get item clicked etc.
        adapter = new MainAdapter(list_category,getContext(),this);

        List_Category clickeditem = list_category.get(position);

        Log.e("clicked",clickeditem.getCategoryName());
        Intent intent = new Intent(getActivity().getBaseContext(),
                MenuGenActivity.class);
        intent.putExtra("chosenCategory", clickeditem.getCategoryName());
        getActivity().startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater ) {
        menu.clear();
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
        // Handle item selection
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
                this.userLogged = false;
                getActivity().invalidateOptionsMenu();
                showLoginUser(getView(), "Logout Successful!");
                Global_Logon.isWelcomed = false;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showLoginUser(View view, String popupText) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        new Handler().postDelayed(new Runnable(){

            public void run() {
                popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER,0,0);
            }

        }, 200L);
        TextView message = (TextView) popupView.findViewById(R.id.popupTxt);

        message.setText(popupText);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
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
        handler.postDelayed(runnable, 1000);
    }
}
