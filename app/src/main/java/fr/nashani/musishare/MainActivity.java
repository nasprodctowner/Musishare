package fr.nashani.musishare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

import fr.nashani.musishare.Cards.Card;
import fr.nashani.musishare.Cards.CardAdapter;
import fr.nashani.musishare.Matches.MatchActivity;
import fr.nashani.musishare.Player.PlayerActivity;
import fr.nashani.musishare.User.ChooseLoginRegistrationActivity;
import fr.nashani.musishare.User.ProfileActivity;


public class MainActivity extends Activity {

    private CardAdapter CardAdapter;
    List<Card> rowItems;

    private String currentUId;
    private DatabaseReference userDB;
    private DatabaseReference usersDB, chatDB;

    private FirebaseAuth mAuth;

    private String userSex;
    private String oppositeUserSex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // lancer le thread pour mettre à jour le player de l'utilisateur
        PlayerActivity.interrupt = false;

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

                    // Create a child inside Chat with new id key
                    FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();


                    // create unique chat ID
                    usersDB.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("chatId").setValue(key);
                    usersDB.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("chatId").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                        String lastTrackName = "none";
                        String lastTrackArtists = "none";
                        String lastTrackAlbumName = "none";
                        String lastTrackAlbumCoverURL = "default";


                        dataSnapshot.child("profileImageUrl").getValue();

                        if(dataSnapshot.child("profileImageUrl").getValue() != null)
                            if(!dataSnapshot.child("profileImageUrl").getValue().equals("default")){
                                profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                            }


                        if(dataSnapshot.child("LastPlayedTrack").child("trackId").getValue() != null)
                            if(!dataSnapshot.child("LastPlayedTrack").child("trackName").getValue().equals("none")){
                                lastTrackName = dataSnapshot.child("LastPlayedTrack").child("trackName").getValue().toString();
                                lastTrackArtists = dataSnapshot.child("LastPlayedTrack").child("trackArtists").getValue().toString();
                                lastTrackAlbumName = dataSnapshot.child("LastPlayedTrack").child("trackAlbum").getValue().toString();
                                lastTrackAlbumCoverURL = dataSnapshot.child("LastPlayedTrack").child("trackAlbumCoverURL").getValue().toString();
                            }

                        if(dataSnapshot.child("name").getValue() != null){
                            Card item = new Card(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), lastTrackName, lastTrackArtists,lastTrackAlbumName,lastTrackAlbumCoverURL, profileImageUrl);
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


    public void connectToPlayer(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    public void logOutUser (View view){
            PlayerActivity.interrupt = true;
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


}