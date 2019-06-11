package fr.nashani.musishare.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.nashani.musishare.R;

/**
 * The type Chat view holders.
 */
public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    /**
     * The M message.
     */
    public TextView mMessage;
    /**
     * The M container.
     */
    public LinearLayout mContainer;


    /**
     * Instantiates a new Chat view holders.
     *
     * @param itemView the item view
     */
    public ChatViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMessage = itemView.findViewById(R.id.message);
        mContainer = itemView.findViewById(R.id.container);

    }

    @Override
    public void onClick(View v) {

    }
}
