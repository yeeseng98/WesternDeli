package com.yeeseng.westerndeli.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yeeseng.westerndeli.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.etUsername) EditText usernameTxt;
    @BindView(R.id.etPassword) EditText passwordTxt;

    @OnClick(R.id.btConfirm)
    public void submit(View view) {

        String username = String.valueOf(usernameTxt.getText());
        String password = String.valueOf(passwordTxt.getText());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .whereEqualTo("Username", username)
                .whereEqualTo("Password", password)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                            editor.putString("username", String.valueOf(document.get("Username"))).commit();
                            editor.putBoolean("isLogin", true).commit();

                            Intent returnToMain = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(returnToMain);
                        }
                        if(queryDocumentSnapshots.isEmpty()){
                            popup(view, "Invalid login credentials!");
                        }
                    }
                });
    }

    @OnClick(R.id.btClose)
    public void close(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
    }


    public void popup(View view, String popupText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(popupText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
