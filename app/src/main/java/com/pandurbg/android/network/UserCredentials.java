package com.pandurbg.android.network;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * Created by Quantox 1 on 3/14/2018.
 */

public class UserCredentials {
    private FirebaseAuth mAuth;

    public UserCredentials() {
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void registerUser(String email, String password, OnCompleteListener<AuthResult> completeListener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(completeListener);
    }

    public void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    public void logout() {
        mAuth.signOut();
    }
}
