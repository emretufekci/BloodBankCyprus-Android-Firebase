package com.example.emre.bloodbankcyprus;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {


    private Toolbar mainToolbar;

    private FirebaseAuth mAuth;
    private FloatingActionButton addPostBtn;
    private String current_user_id;

    private FirebaseFirestore firebaseFirestore;

    private BottomNavigationView mainbottomNav;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore  = FirebaseFirestore.getInstance();






    mainToolbar = (Toolbar)findViewById(R.id.main_toolbar);
    setSupportActionBar(mainToolbar);
    getSupportActionBar().setTitle("Blood Seeking List");


        if (mAuth.getCurrentUser() != null) {


    mainbottomNav = findViewById(R.id.mainBottomNav);

        //Fragment
        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        profileFragment = new ProfileFragment();


            initializeFragment();



        mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                switch (item.getItemId()){

                        case R.id.bottom_action_home:
                            replaceFragment(homeFragment, currentFragment);
                            return true;
                        case R.id.bottom_action_notif:
                                replaceFragment(notificationFragment, currentFragment);
                                return true;
                        case R.id.bottom_action_profile:
                            replaceFragment(profileFragment, currentFragment);
                            return true;

                            default:
                                return false;


                    }


            }
        });

    addPostBtn = findViewById(R.id.add_post_btn);

    //addPostBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    addPostBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);

        }
    });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser==null){
          sendToLogin();


        }else {
            current_user_id = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {

                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            finish();
                            startActivity(setupIntent);

                        }
                    }    else {

                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(MainActivity.this,"Error is:"+errorMessage, Toast.LENGTH_LONG).show();
                        }
                }

            });
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


    getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


            switch (item.getItemId()) {

                case R.id.action_logout_btn:
                logOut();
                return true;


                case R.id.action_settings_btn:
                    Intent settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                    startActivity(settingsIntent);
                    return true;

        default:
        return false;

            }





    }

    private void logOut() {

        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();

    }
    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, notificationFragment);
        fragmentTransaction.add(R.id.main_container, profileFragment);

        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.hide(profileFragment);

        fragmentTransaction.commit();

    }
    private void replaceFragment (Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(fragment == homeFragment){

            fragmentTransaction.hide(profileFragment);
            fragmentTransaction.hide(notificationFragment);

        }
        if(fragment == profileFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == notificationFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(profileFragment);

        }


        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();



    }
}
