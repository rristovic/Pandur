package com.pandurbg.android.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.pandurbg.android.R;
import com.pandurbg.android.ui.MainActivity.MainActivity;
import com.pandurbg.android.util.UserCredentials;
import com.pandurbg.android.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_email)
    EditText mEtEmail;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    private UserCredentials mUserCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mUserCredentials = new UserCredentials();
        if (mUserCredentials.isLoggedIn()) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @OnClick(R.id.tv_register)
    public void openRegister() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.btn_login)
    public void login() {
        mUserCredentials.loginUser(mEtEmail.getText().toString().trim(), mEtPassword.getText().toString(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Utils.showToast(LoginActivity.this, getString(R.string.failed_login));
                }
            }
        });
    }
}
