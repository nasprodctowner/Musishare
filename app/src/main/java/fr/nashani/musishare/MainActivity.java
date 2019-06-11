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
import fr.nashani.musishare.User.ProfileActivity;


/**
 * The type Main activity.
 */
public class MainActivity extends Activity {

    private CardAdapter CardAdapter;
    /**
     * The Row items.
     */
    List<Card> rowItems;

    private String currentUId;
    private DatabaseReference usersDB;

    private FirebaseAuth mAuth;

    private String userSex;
    private String oppositeUserSex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Récupérer tous les utilisateurs
         */
        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        /*
        Récupérer l'uid de l'utilisateur connecté
         */
        currentUId = mAuth.getCurrentUser().getUid();

        checkUserSex();

        rowItems = new ArrayList<Card>();

        CardAdapter = new CardAdapter(this,rowItems);


        /*
        Initiation de la liste des cartes
         */
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(CardAdapter);

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {

            // Supprimer le premier object de la liste
            @Override
            public void removeFirstObjectInAdapter() {

                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                CardAdapter.notifyDataSetChanged();
            }

            //Swip à gauche
            @Override
            public void onLeftCardExit(Object dataObject) {
                Card obj = (Card) dataObject;
                String userId = obj.getUserId();
                usersDB.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
            }

            // Supprimer à droite
            @Override
            public void onRightCardExit(Object dataObject) {
                Card obj = (Card) dataObject;
                String userId = obj.getUserId();
                usersDB.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                isConnectionMatch(userId);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });
    }


    /**
     * Cette méthode sert à savoir si un utilisateur a un match avec un autre utilisateur en prenant un userID en paramètre
     *
     * @param userId
     */
    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDB.child(currentUId).child("connections").child("yeps").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this,"new matching", Toast.LENGTH_LONG).show();

                    /*
                    Créer un child à l'interieur de Chat avec un nouvel id
                      */
                    FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();


                    // Créer un uid pour le chat
                    usersDB.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("chatId").setValue(key);
                    usersDB.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("chatId").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /**
     * Check le user pou savoir s'il est un homme ou une femme
     */
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

    /**
     * Récupérer les utilisateurs ayant le sexe opposé à l'utilisateur connecté
     */
    public void getOppositeSexUsers(){

        usersDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                if(dataSnapshot.child("sex").getValue() != null){
                    if(dataSnapshot.exists()
                            && !dataSnapshot.child("connections").child("nope").hasChild(currentUId)
                            && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId)
                            && dataSnapshot.child("sex").getValue().toString().equals(oppositeUserSex)){


                        /*
                        Setter des valeurs par défaut
                         */
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

                            /*
                            Mise à jour de la liste des cartes
                             */
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


    /**
     * Connect to player.
     *
     * @param view the view
     */
    public void connectToPlayer(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    /**
     * Go to profile.
     *
     * @param view the view
     */
    public void goToProfile(View view) {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
        return;
    }

    /**
     * Go to matches.
     *
     * @param view the view
     */
    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchActivity.class);
        startActivity(intent);
        return;
    }


}