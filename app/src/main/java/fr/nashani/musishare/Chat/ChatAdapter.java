package fr.nashani.musishare.Chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import fr.nashani.musishare.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders>  {

    private List<ChatObject> chatList;
    private Context context;

    public ChatAdapter(List<ChatObject> matchesList, Context context){
        this.chatList = matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());


        View  layoutView = inflater.inflate(R.layout.item_match,null,false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutView.setLayoutParams(lp);

        return new ChatViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolders holder, int i) {

    }

    @Override
    public int getItemCount() {
       return chatList.size();
    }
}
