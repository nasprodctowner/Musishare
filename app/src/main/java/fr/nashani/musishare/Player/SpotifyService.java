package fr.nashani.musishare.Player;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyService {

    private Music music;
    private FirebaseAuth mAuth;
    private String trackId;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private Call mCall;

    public SpotifyService(Music music, String trackId, String mAccessToken) {
        this.music = music;
        this.trackId = trackId;
        this.mAccessToken = mAccessToken;
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void getTrackData() {
        if (mAccessToken == null) {
            return;
        }

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/tracks/"+trackId)
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

                    //getTrackId
                    String trackId = jsonObject.get("id").toString();
                    music.setTrackId(trackId);

                    //getName
                    String trackName = jsonObject.get("name").toString();
                    music.setTrackName(trackName);

                    //getTrackArtists
                    JSONArray artistsArray = jsonObject.getJSONArray("artists");

                    StringBuilder stringBuilder = new StringBuilder("");

                    for(int i=0;i<artistsArray.length();i++){
                        JSONObject artist = artistsArray.getJSONObject(i);
                        stringBuilder.append(artist.getString("name"));
                        stringBuilder.append(" , ");
                    }
                    stringBuilder.setLength(stringBuilder.length() - 3);
                    String artists = stringBuilder.toString();

                    music.setTrackArtists(artists);

                    final JSONObject album = (JSONObject) jsonObject.get("album");
                    //getTrackAlbum
                    String trackAlbumName = album.getString("name");
                    music.setTrackAlbumName(trackAlbumName);


                    //getAlbumCoverName
                    JSONArray images = album.getJSONArray("images");
                    String trackAlbumCover = images.getJSONObject(1).getString("url");

                    music.setTrackAlbumCover(trackAlbumCover);

                    saveTrackInTheDatabase(music);

                } catch (JSONException e) {

                }
            }
        });
    }

    private void saveTrackInTheDatabase(Music music){


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
