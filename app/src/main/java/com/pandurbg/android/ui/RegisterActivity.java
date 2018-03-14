package com.pandurbg.android.ui;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.pandurbg.android.R;
import com.pandurbg.android.model.User;
import com.pandurbg.android.util.UserCredentials;
import com.pandurbg.android.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_email)
    EditText mEtEmail;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_repeat_password)
    EditText mEtRepeatPassowrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_register)
    public void register() {
        new UserCredentials().registerUser(mEtEmail.getText().toString().trim(), mEtPassword.getText().toString(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    finish();
                    Utils.showToast(RegisterActivity.this, getString(R.string.register_success));
                } else {
                    Utils.showToast(RegisterActivity.this, getString(R.string.register_failure));
                }
            }
        });
    }


}
