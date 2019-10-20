package com.yeeseng.westerndeli.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yeeseng.westerndeli.R;
import com.yeeseng.westerndeli.model.Global_Logon;
import com.yeeseng.westerndeli.model.Menu_Item;
import com.yeeseng.westerndeli.presenter.MenuItemAdapter;
import com.yeeseng.westerndeli.presenter.MenuItemListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class FragMenuDetails extends Fragment implements MenuItemListener {

    private ArrayList<Menu_Item> list_item = new ArrayList<>();
    private ArrayList<Menu_Item> filter_item = new ArrayList<>();

    private MenuItemAdapter adapter;
    private String chosenCategory;
    String categoryPath;

    @BindView(R.id.recyclerview)
    RecyclerView rv;

    boolean isLoading = false;

    Query first;
    int docCount = 0;
    List<String> filterArray = new ArrayList<>();
    Boolean userLogged = false;
    Boolean isFiltering = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.menu_details, null);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            chosenCategory = getArguments().getString("chosenCategory");
        }
        categoryPath = chosenCategory + " Dishes";

        getActivity().setTitle(chosenCategory);

        SharedPreferences prefs = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        Boolean isLog = prefs.getBoolean("isLogin", false);
        if (isLog) {
            userLogged = true;
        }

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        getdata();
        initScrollListener();
    }

    public void loadMore() {

        list_item.add(null);
        adapter.notifyItemInserted(list_item.size() - 1);
        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // Get the last visible document
                        DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1);

                        String categoryPath = chosenCategory + " Dishes";

                        // Construct a new query starting at this document,
                        Query next = db.collection("Dish Categories")
                                .document(chosenCategory)
                                .collection(categoryPath)
                                .startAfter(lastVisible)
                                .limit(2);

                        next.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            list_item.remove(list_item.size() - 1);
                                            int scrollPosition = list_item.size();
                                            adapter.notifyItemRemoved(scrollPosition);
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Menu_Item data = document.toObject(Menu_Item.class);
                                                list_item.add(data);
                                            }
                                            adapter.notifyDataSetChanged();
                                            isLoading = false;
                                            first = next;
                                        } else {
                                            Log.d("Invalid: ", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }
                });

    }

    private int getDocumentCount() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Dish Categories")
                .document(chosenCategory)
                .collection(categoryPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            docCount = task.getResult().size();
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return docCount;
    }

    private void getdata() {

        String categoryPath = chosenCategory + " Dishes";
        //get all categories for main menu
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        first = db.collection("Dish Categories").document(chosenCategory).collection(categoryPath).limit(3);

        first.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Menu_Item data = document.toObject(Menu_Item.class);
                                list_item.add(data);
                            }
                            setupData(list_item);
                        } else {
                            Log.d("Invalid: ", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setupData(List<Menu_Item> list_item) {

        adapter = new MenuItemAdapter(list_item, getContext(), this);
        rv.setAdapter(adapter);

    }

    private void initScrollListener() {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                if (!isLoading) {
                    if (lm != null && lm.findLastCompletelyVisibleItemPosition() == list_item.size() - 1) {
                        //bottom of list!
                        if (list_item.size() != getDocumentCount()) {
                            if(!isFiltering) {
                                loadMore();
                                isLoading = true;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void recyclerViewMenuListClicked(View v, int position) {
        adapter = new MenuItemAdapter(list_item, getContext(), this);
        Menu_Item clickeditem;
        if (isFiltering){
            clickeditem = filter_item.get(position);
        } else{
            clickeditem = list_item.get(position);
        }

        Log.e("clicked", clickeditem.getItemName());
        Intent intent = new Intent(getActivity().getBaseContext(),
                ItemGenActivity.class);

        Menu_Item item = new Menu_Item();
        item.setItemName(clickeditem.getItemName());
        item.setItemCost(clickeditem.getItemCost());
        item.setChefRecommended(clickeditem.getChefRecommended());
        item.setItemCategory(clickeditem.getItemCategory());
        item.setItemDescription(clickeditem.getItemDescription());
        item.setItemFilterBy(clickeditem.getItemFilterBy());
        item.setItemPrepTime(clickeditem.getItemPrepTime());
        item.setItemUrl(clickeditem.getItemUrl());

        intent.putExtra("selectedItem", item);
        getActivity().startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_bar, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem register = menu.findItem(R.id.sign_up);
        MenuItem logout = menu.findItem(R.id.log_out);
        MenuItem signin = menu.findItem(R.id.sign_in);
        MenuItem filter = menu.findItem(R.id.action_filter);

        register.setVisible(!userLogged);
        logout.setVisible(userLogged);
        signin.setVisible(!userLogged);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String chosenFilter = chosenCategory + " Filter";

        db.collection("Dish Categories")
                .document(chosenCategory)
                .collection(chosenFilter)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String data = document.getString("Name");
                                if (!filterArray.contains(data)) {
                                    filterArray.add(data);
                                }
                            }
                            int id = 0;
                            filter.getSubMenu().clear();
                            for (int i = 0; i < filterArray.size(); i++) {
                                Log.e("filt", filterArray.get(i));
                                filter.getSubMenu().add(Menu.NONE, id, Menu.NONE, filterArray.get(i));
                                id = id + 1;
                            }
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.log_out) {

            SharedPreferences preferences = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
            preferences.edit().putBoolean("isLogin", false).commit();
            preferences.edit().remove("username").commit();
            getActivity().invalidateOptionsMenu();
            userLogged = false;
            Global_Logon.isWelcomed = false;
            Intent backToMain = new Intent(getActivity(), MainActivity.class);
            startActivity(backToMain);
            return true;
        } else if (item.getItemId() == R.id.sign_in) {

            Intent goToSignIn = new Intent(getActivity(), SignInActivity.class);
            startActivity(goToSignIn);
            return true;

        } else if (item.getItemId() == R.id.sign_up) {

            Intent goToSignUp = new Intent(getActivity(), SignUpActivity.class);
            startActivity(goToSignUp);
            return true;

        } else if (item.getItemId() >= 0 && item.getItemId() < 10) {
            adapter.clear();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.e("CLICKED", String.valueOf(item.getItemId()));
            Log.e("CLICKED2", String.valueOf(filterArray.get(item.getItemId())));

            Query filter = db.collection("Dish Categories").document(chosenCategory).collection(categoryPath).whereEqualTo("ItemFilterBy", filterArray.get(item.getItemId()));

            filter.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Menu_Item data = document.toObject(Menu_Item.class);
                                    filter_item.add(data);
                                }
                                setupData(filter_item);
                            } else {
                                Log.d("Invalid: ", "Error getting documents: ", task.getException());
                            }
                        }
                    });
            isFiltering = true;

        }
        return super.onOptionsItemSelected(item);
    }
}
