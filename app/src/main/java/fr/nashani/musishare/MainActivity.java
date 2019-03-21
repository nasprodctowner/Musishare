package fr.nashani.musishare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;



public class MainActivity extends Activity {

    private static final String CLIENT_ID = "d7188f7125b143b8b980134e5a1adcb1"; //past your client ID
    private static final String REDIRECT_URI = "http://fr.nashani.musishare/callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        checkUserSex();

        al = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, al );

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(MainActivity.this, "Left!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(MainActivity.this, "Right!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked!!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();


        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                        Button button_connectToSpotifyApp = findViewById(R.id.button_connectToSpotifyApp);
                        button_connectToSpotifyApp.setVisibility(View.GONE);
                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {

        TextView text_currentlyPlayingMusic = findViewById(R.id.text_currentlyPlayingMusic);
        TextView text_currentlyPlayingTrack = findViewById(R.id.text_currentlyPlayingTrack);
        TextView text_currentlyPlayingArtist = findViewById(R.id.text_currentlyPlayingArtist);
        TextView text_currentlyPlayingAlbum = findViewById(R.id.text_currentlyPlayingAlbum);

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                        text_currentlyPlayingMusic.setText("Currently playing music : ");
                        text_currentlyPlayingTrack.setText("Track :"+track.name);
                        text_currentlyPlayingArtist.setText("Artist :"+track.artist.name);
                        text_currentlyPlayingAlbum.setText("Album : "+track.album.name);
                    }
                });
    }

    public void connect(View view) {
        Intent intent = new Intent(this, SpotifyLoginActivity.class);
        startActivity(intent);
    }


    private String userSex;
    private String oppositeUserSex;

    public void checkUserSex(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference maleDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Male");
        maleDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.getKey().equals(user.getUid())){
                    userSex = "Male";
                    oppositeUserSex = "Female";
                    getOppositeSexUsers();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference femaleDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Female");
        femaleDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.getKey().equals(user.getUid())){
                    userSex = "Female";
                    oppositeUserSex  = "Male";
                    getOppositeSexUsers();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void getOppositeSexUsers(){
        DatabaseReference oppositeSexDB = FirebaseDatabase.getInstance().getReference().child("Users").child(oppositeUserSex);
        oppositeSexDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){
                    al.add(dataSnapshot.child("name").getValue().toString() );
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void logOutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this,ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
    }
}