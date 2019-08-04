package ewald.horn.alc4phase1challenge2;

import android.widget.Toast;

import androidx.annotation.NonNull;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class FirebaseUtil {
    static FirebaseDatabase firebaseDatabase;
    static DatabaseReference databaseReference;
    private static FirebaseUtil firebaseUtil;
    private static FirebaseAuth firebaseAuth;
    static FirebaseStorage storage;
    static StorageReference storageRef;
    private static FirebaseAuth.AuthStateListener authListener;
    static ArrayList<TravelDeal> deals;
    private static final int RC_SIGN_IN = 123;
    private static ListActivity caller;
    static boolean isAdmin;

    private FirebaseUtil() {
    }

    static void openFbReference(String ref, final ListActivity callerActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;

            authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    } else {
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();

        }

        deals = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference().child(ref);
    }

    private static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    private static void checkAdmin(String uid) {
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = firebaseDatabase.getReference().child("administrators")
                .child(uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
    }

    static void attachListener() {
        firebaseAuth.addAuthStateListener(authListener);
    }

    static void detachListener() {
        firebaseAuth.removeAuthStateListener(authListener);
    }

    private static void connectStorage() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("deals_pictures");
    }
}
