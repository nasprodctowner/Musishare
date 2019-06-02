package fr.nashani.musishare.Player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


import fr.nashani.musishare.R;


public class PlayerActivity extends Activity {

    //Les informations propre à mon application
    private static final String CLIENT_ID = "d7188f7125b143b8b980134e5a1adcb1";
    private static final String REDIRECT_URI = "http://fr.nashani.musishare/callback";

    //Les codes des requetes
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;

    Music music;
    Handler handlerUI = new Handler();

    Thread thread;

    public static boolean interrupt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        interrupt = false;
        final AuthenticationRequest request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {
        return new AuthenticationRequest.Builder(CLIENT_ID, type, REDIRECT_URI)
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-playback-state"})
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            //Variables pour stocker l'AccessToken et l'AcessCode
            String mAccessToken = response.getAccessToken();

            //démarrer le thread pour get les informations depuis Spotify API
            music = new Music();
            thread = new Thread(new UpdatePlayerStateRunnableThread(music,handlerUI,mAccessToken));
            thread.start();


        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            String mAccessCode = response.getCode();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}