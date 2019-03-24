package fr.nashani.musishare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CardsAdapter extends ArrayAdapter<Cards> {

    Context context;

    public CardsAdapter(Context context , int resource , List<Cards> items) {
        super(context , resource , items);
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        Cards cardItem = (Cards) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent , false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.userName);
        ImageView userImage = (ImageView) convertView.findViewById(R.id.userImage);
        ImageView albumImage = (ImageView) convertView.findViewById(R.id.imageAlbum);

        name.setText(cardItem.getName());
        userImage.setImageResource(R.mipmap.ic_launcher);
        albumImage.setImageResource(R.mipmap.ic_launcher);

        return convertView;
    }
}
