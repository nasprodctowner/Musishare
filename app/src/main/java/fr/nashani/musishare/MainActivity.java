package fr.nashani.musishare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.nashani.musishare.Cards.Card;
import fr.nashani.musishare.Cards.CardAdapter;
import fr.nashani.musishare.Matches.MatchActivity;
import fr.nashani.musishare.Spotify.SpotifyLoginActivity;
import fr.nashani.musishare.User.ChooseLoginRegistrationActivity;
import fr.nashani.musishare.User.ProfileActivity;


public class MainActivity extends Activity {

    private static final String CLIENT_ID = "d7188f7125b143b8b980134e5a1adcb1"; //past your client ID
    private static final String REDIRECT_URI = "http://fr.nashani.musishare/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private CardAdapter CardAdapter;
    List<Card> rowItems;

    private String currentUId;
    private DatabaseReference usersDB;
    private DatabaseReference userDB;

    private FirebaseAuth mAuth;

    private String userSex;
    private String oppositeUserSex;

    private String trackName;
    private String trackArtist;
    private String trackAlbum;
    private String latestTrackArtist = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get all users
        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");



        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        checkUserSex();

        rowItems = new ArrayList<Card>();

        CardAdapter = new CardAdapter(this,rowItems);


        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(CardAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                CardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Card obj = (Card) dataObject;
                String userId = obj.getUserId();

                usersDB.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "Left!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Card obj = (Card) dataObject;
                String userId = obj.getUserId();

                usersDB.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "Right!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> {
            Toast.makeText(MainActivity.this, "Clicked!!",Toast.LENGTH_SHORT).show();
        });

    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDB.child(currentUId).child("connections").child("yeps").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this,"new matching", Toast.LENGTH_LONG).show();
                    //saving match to data base
                    usersDB.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).setValue(true);
                    usersDB.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

                        text_currentlyPlayingMusic.setText("Currently playing music : ");
                        text_currentlyPlayingTrack.setText("Track :"+track.name);
                        text_currentlyPlayingArtist.setText("Artist :"+track.artist.name);
                        text_currentlyPlayingAlbum.setText("Album : "+track.album.name);

                        trackName = track.name;
                        trackArtist = track.artist.name;
                        trackAlbum = track.album.name;
                        saveTrack(trackName, trackAlbum, trackArtist);

                    }
                });
    }

    public void connect(View view) {
        Intent intent = new Intent(this, SpotifyLoginActivity.class);
        startActivity(intent);
    }


    public void checkUserSex(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDB = usersDB.child(user.getUid());
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){
                        if(dataSnapshot.child("sex").getValue() != null){
                            userSex = dataSnapshot.child("sex").getValue().toString();

                            switch (userSex){
                                case "Male" : oppositeUserSex = "Female";
                                    break;
                                case "Female" : oppositeUserSex = "Male";
                                    break;
                            }
                            getOppositeSexUsers();
                        }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getOppositeSexUsers(){

        usersDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                if(dataSnapshot.child("sex").getValue() != null){
                    if(dataSnapshot.exists()
                            && !dataSnapshot.child("connections").child("nope").hasChild(currentUId)
                            && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId)
                            && dataSnapshot.child("sex").getValue().toString().equals(oppositeUserSex)){


                        String profileImageUrl = "default";
                        String currentTrackName = "none";
                        String currentTrackArtist = "none";
                        String currentTrackAlbum = "none";

                        dataSnapshot.child("profileImageUrl").getValue();

                        if(dataSnapshot.child("profileImageUrl").getValue() != null)
                            if(!dataSnapshot.child("profileImageUrl").getValue().equals("default")){
                                profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                            }


                        if(dataSnapshot.child("CurrentTrack").child("trackName").getValue() != null)
                            if(!dataSnapshot.child("CurrentTrack").child("trackName").getValue().equals("none")){
                                currentTrackName = dataSnapshot.child("CurrentTrack").child("trackName").getValue().toString();
                                currentTrackArtist = dataSnapshot.child("CurrentTrack").child("trackArtist").getValue().toString();
                                currentTrackAlbum = dataSnapshot.child("CurrentTrack").child("trackAlbum").getValue().toString();
                            }

                        if(dataSnapshot.child("name").getValue() != null){
                            Card item = new Card(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), currentTrackName, currentTrackArtist,currentTrackAlbum, profileImageUrl);
                            rowItems.add(item);
                            CardAdapter.notifyDataSetChanged();
                        }

                    }
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

    public void saveTrack(String currentTrackName , String currentTrackAlbum , String currentTrackArtist){

        String userId = mAuth.getCurrentUser().getUid() ;

        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("CurrentTrack");

        Map<String, Object> userInformation = new HashMap<>();

        userInformation.put("trackName",currentTrackName);
        userInformation.put("trackArtist",currentTrackArtist);
        userInformation.put("trackAlbum",currentTrackAlbum);
        currentUserDB.setValue(currentTrackName);
        currentUserDB.setValue(currentTrackAlbum);
        currentUserDB.setValue(currentTrackArtist);

        currentUserDB.updateChildren(userInformation);
    }

    public void logOutUser (View view){
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
            startActivity(intent);
            finish();
            return;
        }


    public void goToProfile(View view) {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
        return;
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchActivity.class);
        startActivity(intent);
        return;
    }

    private void getLatestTrackInfo(String userId) {

        userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("CurrentTrack");

        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();

                    if (map.get("trackArtist") != null){
                        latestTrackArtist = map.get("trackArtist").toString();
                        CardAdapter.notifyDataSetChanged();

                        Log.i("couco",latestTrackArtist);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}