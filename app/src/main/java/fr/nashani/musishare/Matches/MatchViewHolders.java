package fr.nashani.musishare.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.nashani.musishare.Chat.ChatActivity;
import fr.nashani.musishare.R;

public class MatchViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView mMatchId;
    TextView mMatchName;
    TextView mtrackName;
    ImageView matchImage;


    public MatchViewHolders(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);

        mMatchId = (TextView) itemView.findViewById(R.id.matchId);
        mMatchName = (TextView) itemView.findViewById(R.id.matchName);
        mtrackName = (TextView) itemView.findViewById(R.id.matchTrackName);
        matchImage = (ImageView ) itemView.findViewById(R.id.matchImage);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("matchId", mMatchId.getText().toString());
        intent.putExtras(bundle);
        v.getContext().startActivity(intent);
    }
}
