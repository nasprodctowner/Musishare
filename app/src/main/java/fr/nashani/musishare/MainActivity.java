package fr.nashani.musishare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;



public class MainActivity extends Activity {

    private static final String CLIENT_ID = "d7188f7125b143b8b980134e5a1adcb1";
    private static final String REDIRECT_URI = "http://fr.nashani.musishare/callback";
    private SpotifyAppRemote mSpotifyAppRemote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                        Button button_connectToSpotifyApp = (Button)findViewById(R.id.button_connectToSpotifyApp);
                        button_connectToSpotifyApp.setText("Connecté");
                        button_connectToSpotifyApp.setEnabled(false);
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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}