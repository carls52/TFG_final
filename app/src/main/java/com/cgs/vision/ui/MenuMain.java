package com.cgs.vision.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.cgs.vision.Datos;
import com.cgs.vision.R;
import com.cgs.vision.Usuario;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MenuMain extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final NavController[] navController = new NavController[1];
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        View view = navigationView.getHeaderView(0);
        final TextView nombre = view.findViewById(R.id.nav_header_title);
        TextView correo = view.findViewById(R.id.nav_header_subtitle);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(FirebaseAuth.getInstance().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario actual = snapshot.getValue(Usuario.class);
                Datos d = new Datos(MenuMain.this);
                try {
                    d.setUsuario(actual);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                nombre.setText(actual.getNombre());
                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_home, R.id.nav_cPublicos, R.id.nav_grupos, R.id.nav_explorar,R.id.nav_favoritos,
                        R.id.mainActivity,R.id.ajustes2,R.id.ayuda2)
                        .setDrawerLayout(drawer)
                        .build();
                navController[0] = Navigation.findNavController(MenuMain.this, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController(MenuMain.this, navController[0], mAppBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, navController[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        correo.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().trim());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
       /*
       mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_cPublicos, R.id.nav_grupos, R.id.nav_explorar,R.id.nav_favoritos,
                R.id.mainActivity,R.id.ajustes2,R.id.ayuda2)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
