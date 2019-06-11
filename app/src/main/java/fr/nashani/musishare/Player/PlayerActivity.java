package fr.nashani.musishare.Player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


import fr.nashani.musishare.R;


/**
 * The type Player activity.
 */
public class PlayerActivity extends Activity {

    /**
     * Les informations concernant l'application récupérés depuis le dashboard spotify developer
     */
    private static final String CLIENT_ID = "d7188f7125b143b8b980134e5a1adcb1";
    private static final String REDIRECT_URI = "http://fr.nashani.musishare/callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    /**
     * The constant AUTH_TOKEN_REQUEST_CODE.
     */
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    /**
     * The constant AUTH_CODE_REQUEST_CODE.
     */
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;

    /**
     * The Music.
     */
    Music music;

    private TextView mTrackName, mTrackArtists, mtrackAlbumName, myPlayerInstruction;
    private ImageView mtrackAlbumCover;

    private FirebaseAuth mAuth;
    /**
     * The Current user db.
     */
    DatabaseReference currentUserDB;

    private String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mAuth = FirebaseAuth.getInstance();

        /*
          Ouvrir le Spotify de l'utilisateur pour l'authentifier afin de récupérer le Token
         */
        final AuthenticationRequest request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);

        mTrackName = findViewById(R.id.player_trackName);
        mtrackAlbumName = findViewById(R.id.player_trackAlbum);
        mTrackArtists = findViewById(R.id.player_trackArtist);
        mtrackAlbumCover = findViewById(R.id.player_albumCover);
        myPlayerInstruction = findViewById(R.id.player_instruction);
        populateLastPlayingTrackView();

    }

    /*
    Quand Spotify est connecté, la méthode connected est appelé
     */
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
                        connected();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                    }
                });
    }


    /*
     Peupler la vue de la dernière chanson écoutée en récupérant les informations depuis la base de donnée
    */
    private void populateLastPlayingTrackView(){

        String userId = mAuth.getCurrentUser().getUid();
        currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("LastPlayedTrack");

        currentUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    myPlayerInstruction.setText("Your currently playing track on Spotify : ");
                    mTrackName.setText(dataSnapshot.child("trackName").getValue().toString());
                    mTrackArtists.setText(dataSnapshot.child("trackArtists").getValue().toString());
                    mtrackAlbumName.setText(dataSnapshot.child("trackAlbum").getValue().toString());

                    /*
                     * Appeler l'asyncTask SetAlbumCoverAsyncTask pour mettre à jour la cover
                     */
                    SetAlbumCoverAsyncTask c = new SetAlbumCoverAsyncTask(mtrackAlbumCover);
                    c.execute(dataSnapshot.child("trackAlbumCoverURL").getValue().toString());
                }else {
                    myPlayerInstruction.setText("Please play some music on Spotify !");
                    mTrackName.setText("Track name");
                    mTrackArtists.setText("Artists");
                    mtrackAlbumName.setText("Album name");
                    mtrackAlbumCover.setImageResource(R.drawable.music_note);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {
        return new AuthenticationRequest.Builder(CLIENT_ID, type, REDIRECT_URI)
                .setShowDialog(false)
                .build();
    }

    /*
    Permet de récupérer le token après l'ouverture de l'activity de la connexion à Spotify
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            //Variables pour stocker l'AccessToken et l'AcessCode
             mAccessToken = response.getAccessToken();
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            String mAccessCode = response.getCode();
        }
    }


    /*
    Cette méthode sert à appeler la méthode getTrackData à chaque fois que le player change d'état (Musique suivante ou précédente)
     */
    private void connected() {
        // Récupererl'état du player
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;

                    if (track != null) {

                        /*
                        get the track ID
                         */
                        StringBuilder trackUri = new StringBuilder(track.uri);
                        trackUri.delete(0,14);
                        String trackId = trackUri.toString();

                        /*
                        call spotify service with the track id and the user's token
                         */
                        music = new Music();
                        SpotifyService spotifyService = new SpotifyService(music,trackId,mAccessToken);
                        spotifyService.getTrackData();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateLastPlayingTrackView();
    }
}