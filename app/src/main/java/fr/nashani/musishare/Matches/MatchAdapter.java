package fr.nashani.musishare.Matches;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import fr.nashani.musishare.R;

public class MatchAdapter extends RecyclerView.Adapter<MatchViewHolders>  {

    private List<Match> matchesList;
    private Context context;

    public MatchAdapter(List<Match> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());


        View  layoutView = inflater.inflate(R.layout.item_match,null,false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutView.setLayoutParams(lp);

        return new MatchViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolders holder, int i) {

        holder.mMatchId.setText("ID : "+matchesList.get(i).getUserId());
        holder.mMatchName.setText("Name : "+matchesList.get(i).getName());
        holder.mtrackName.setText("Last playing track : "+matchesList.get(i).getTrackName());

        if(!matchesList.get(i).getProfileImageUrl().equals("default")){
            Glide.with(context).load(matchesList.get(i).getProfileImageUrl()).into(holder.matchImage);
        }


    }

    @Override
    public int getItemCount() {
       return matchesList.size();
    }
}
