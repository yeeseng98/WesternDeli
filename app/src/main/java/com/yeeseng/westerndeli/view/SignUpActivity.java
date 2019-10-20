package com.yeeseng.westerndeli.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yeeseng.westerndeli.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPass;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etRpassword)
    EditText etRPass;
    @BindView(R.id.etUsername)
    EditText etUser;

    @OnClick(R.id.btClose)
    public void close() {
        finish();
    }

    @OnClick(R.id.btConfirm)
    public void register() {
        boolean flagUser = checkUserOrPass(String.valueOf(etUser.getText()));
        boolean flagPass = checkUserOrPass(String.valueOf(etPass.getText()));
        boolean flagEmail = verifyEmail(String.valueOf(etEmail.getText()));
        boolean flagRpass = passwordMatcher(String.valueOf(etPass.getText()), String.valueOf(etRPass.getText()));
        boolean flagPhone = verifyPhone(String.valueOf(etPhone.getText()));
        boolean allSuccess = false;

        if (flagUser) {
            if (flagPass) {
                if (flagEmail) {
                    if (flagRpass) {
                        if (flagPhone) {
                            allSuccess = true;
                        } else {
                            onButtonShowErrorWindowClick(this.findViewById(android.R.id.content)
                                    , "Phone number must be a valid Malaysian Phone Number!");
                        }
                    } else {
                        onButtonShowErrorWindowClick(this.findViewById(android.R.id.content)
                                , "Retyped password does not match!");
                    }
                } else {
                    onButtonShowErrorWindowClick(this.findViewById(android.R.id.content)
                            , "Email is invalid!");
                }
            } else {
                onButtonShowErrorWindowClick(this.findViewById(android.R.id.content)
                        , "Password is invalid!");
            }
        } else {
            onButtonShowErrorWindowClick(this.findViewById(android.R.id.content)
                    , "Username is invalid!");
        }

        if (allSuccess == true) {
            Map<String, Object> user = new HashMap<>();
            user.put("Email", String.valueOf(etEmail.getText()));
            user.put("Password", String.valueOf(etPass.getText()));
            user.put("PhoneNumber", String.valueOf(etPhone.getText()));
            user.put("Username", String.valueOf(etUser.getText()));

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(String.valueOf(etUser.getText())).set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            onButtonShowCompleteWindowClick(SignUpActivity.this.findViewById(android.R.id.content), "Successfully registered, please try logging in now!" );
                            Log.d("Success", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onButtonShowErrorWindowClick(SignUpActivity.this.findViewById(android.R.id.content), "Oops! Something went wrong, please try again!");
                            Log.w("Failed", "Error writing document", e);
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
    }

    private static final Pattern userNamePattern
            = Pattern.compile("^[a-zA-Z0-9._-]{3,}$");

    public boolean checkUserOrPass(String et) {
        return userNamePattern.matcher(et).matches();
    }

    public boolean passwordMatcher(String pass, String rpass) {
        if (pass.equals(rpass)) {
            return true;
        }
        return false;
    }

    public boolean verifyEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private static final Pattern phonePattern
            = Pattern.compile("^(\\+?6?01)[0-46-9]-*[0-9]{7,8}$");

    public boolean verifyPhone(String phone) {
        return phonePattern.matcher(phone).matches();
    }

    public void onButtonShowErrorWindowClick(View view, String popupText) {
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

    public void onButtonShowCompleteWindowClick(View view, String popupText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(popupText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent returnToMain = new Intent(SignUpActivity.this, MainActivity.class);
                        SignUpActivity.this.startActivity(returnToMain);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
