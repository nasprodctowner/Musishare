package fr.nashani.musishare.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import fr.nashani.musishare.R;

public class CardAdapter extends ArrayAdapter<Card> {

    Context context;

    public CardAdapter(Context context ,  List<Card> items) {
        super(context , 0 , items);
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        Card cardItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent , false);
        }
        TextView name = convertView.findViewById(R.id.userName);
        ImageView image = convertView.findViewById(R.id.userImage);
        ImageView imageAlbum = convertView.findViewById(R.id.imageAlbum);
        TextView trackName = convertView.findViewById(R.id.trackName);
        TextView TrackArtist = convertView.findViewById(R.id.trackArtist);
        TextView TrackAlbum = convertView.findViewById(R.id.trackAlbum);

        name.setText(cardItem.getName());
        trackName.setText(cardItem.getTrackName());
        TrackArtist.setText(cardItem.getTrackArtist());
        TrackAlbum.setText(cardItem.getTrackAlbum());


        switch (cardItem.getProfileImageUrl()){
            case "default" :
                image.setImageResource(R.drawable.ic_person_black_24dp);
                imageAlbum.setImageResource(R.drawable.ic_launcher_background);
            break;

            default:
                Glide.with(convertView.getContext()).load(cardItem.getProfileImageUrl()).into(image);
            break;

        }
        // Set image

        return convertView;
    }

}
