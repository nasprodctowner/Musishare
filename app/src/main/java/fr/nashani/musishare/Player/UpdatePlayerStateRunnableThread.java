package fr.nashani.musishare.Player;

import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdatePlayerStateRunnableThread implements Runnable {

    private Music music;
    private Handler handlerUI;
    private FirebaseAuth mAuth;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private Call mCall;

    public UpdatePlayerStateRunnableThread(Music music, Handler handlerUI, String mAccessToken) {
        this.music = music;
        this.handlerUI = handlerUI;
        this.mAccessToken = mAccessToken;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void run() {


        if(!PlayerActivity.interrupt){
            getMusicData();
            handlerUI.postDelayed(this,5000);
        }

    }


    private void getMusicData() {
        if (mAccessToken == null) {
            return;
        }

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/player")
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    final JSONObject items = (JSONObject) jsonObject.get("item");

                    //getTrackId
                    String trackId = items.get("id").toString();
                    music.setTrackId(trackId);

                    //getName
                    String trackName = items.get("name").toString();
                    music.setTrackName(trackName);

                    //getTrackArtists
                    List<String> trackArtists = new ArrayList<>();
                    JSONArray artistsArray = items.getJSONArray("artists");

                    StringBuilder stringBuilder = new StringBuilder("");

                    for(int i=0;i<artistsArray.length();i++){
                        JSONObject artist = artistsArray.getJSONObject(i);
                        stringBuilder.append(artist.getString("name"));
                        stringBuilder.append(" , ");
                    }
                    stringBuilder.setLength(stringBuilder.length() - 3);
                    String artists = stringBuilder.toString();

                    music.setTrackArtists(artists);

                    final JSONObject album = (JSONObject) items.get("album");
                    //getTrackAlbum
                    String trackAlbumName = album.getString("name");
                    music.setTrackAlbumName(trackAlbumName);


                    //getAlbumCoverName
                    JSONArray images = album.getJSONArray("images");
                    String trackAlbumCover = images.getJSONObject(1).getString("url");

                    music.setTrackAlbumCover(trackAlbumCover);

                    saveTrack(music);

                } catch (JSONException e) {

                }
            }
        });
    }

    private void saveTrack(Music music){


        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("LastPlayedTrack");


        Map<String, Object> LastPlayedTrackInformation = new HashMap<>();

        LastPlayedTrackInformation.put("trackId",music.getTrackId());
        LastPlayedTrackInformation.put("trackName",music.getTrackName());
        LastPlayedTrackInformation.put("trackAlbum",music.getTrackAlbumName());
        LastPlayedTrackInformation.put("trackAlbumCoverURL",music.getTrackAlbumCover());
        LastPlayedTrackInformation.put("trackArtists",music.getTrackArtists());

        currentUserDB.setValue(LastPlayedTrackInformation);
        currentUserDB.updateChildren(LastPlayedTrackInformation);

    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }



}
