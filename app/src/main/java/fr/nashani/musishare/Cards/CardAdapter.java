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

    public CardAdapter(Context context , int resource , List<Card> items) {
        super(context , resource , items);
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        Card cardItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent , false);
        }
        TextView name = convertView.findViewById(R.id.userName);
        ImageView image = convertView.findViewById(R.id.userImage);
        TextView trackName = convertView.findViewById(R.id.trackName);

        name.setText(cardItem.getName());
        trackName.setText(cardItem.getTrackName());


        switch (cardItem.getProfileImageUrl()){
            case "default" : image.setImageResource(R.drawable.ic_person_black_24dp);
            break;

            default:
                Glide.with(convertView.getContext()).load(cardItem.getProfileImageUrl()).into(image);
            break;

        }
        // Set image

        return convertView;
    }
}
