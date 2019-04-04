package it.appacademy.chatup.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import it.appacademy.chatup.R;
import it.appacademy.chatup.model.Messaggio;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>{

    private Activity mActivity;
    private DatabaseReference mDataBaseRefence;
    private String mDisplayName;

    //La classe DataSnapshot contiene tipo di dati provenienti da un database firebase alla nostra app
    private ArrayList<DataSnapshot> mDataSnapshot;

    /*
    Listener, all'interno ci sono tutti gli eventi che vengono notificati alll'adapter
     */
    private ChildEventListener mListener = new ChildEventListener() {



        //Chiamato quando viene aggiunto un nuovo messaggio
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            //vogliamo sapere tutti i nodi figli del nodo messaggi
            //DataSnaphot è il messaggio in formato .json
            mDataSnapshot.add(dataSnapshot);

            //Notifichiamo che c'è stato un cambiamento sui dati
            notifyDataSetChanged();

        }

        //Chiamato quando un messaggio cambia
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        //Chiamato quando un messaggio viene rimosso
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        //Chiamato quando un messaggio viene spostato
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        //Chiamato quando un messaggio viene cancellato
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public ChatListAdapter(Activity activity, DatabaseReference ref, String name){
        mActivity = activity;
        mDataBaseRefence = ref.child("messaggi");
        mDisplayName = name;
        mDataSnapshot = new ArrayList<>();

        //Colleghiamo il database all'adapter e al listener
        mDataBaseRefence.addChildEventListener(mListener);

    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        //Variabili membro;
        TextView autore;
        TextView messaggio;
        LinearLayout.LayoutParams params;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            //Inizializzo elementi ViewHolder
            autore = (TextView) itemView.findViewById(R.id.tv_autore);
            messaggio = (TextView) itemView.findViewById(R.id.tv_messaggio);
            params = (LinearLayout.LayoutParams) autore.getLayoutParams();
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.chat_msg_row, parent, false);
        ChatViewHolder vh = new ChatViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        //Dal database riceviamo il vettore DataSnapshot pieno di messaggi
        DataSnapshot snapshot = mDataSnapshot.get(position);

        //Leggere il contenuto di SnapShot e lo mette in msg
        Messaggio msg = snapshot.getValue(Messaggio.class);

        //Riempiamo il ViewHolder
        holder.autore.setText(msg.getAutore());
        holder.messaggio.setText(msg.getMessaggio());

        boolean sonoIo = msg.getAutore().equals(mDisplayName);
        setChatItemStyle(sonoIo, holder);

    }

    private void setChatItemStyle(boolean sonoIo, ChatViewHolder holder){

        if (sonoIo){
            holder.params.gravity = Gravity.END;
            holder.autore.setTextColor(Color.BLUE);
        }
        else {
            holder.params.gravity = Gravity.START;
            holder.autore.setTextColor(Color.MAGENTA);

        }
        holder.autore.setLayoutParams(holder.params);
        holder.messaggio.setLayoutParams(holder.params);

    }

    @Override
    public int getItemCount() {
        return mDataSnapshot.size();
    }

    public void clean(){
        mDataBaseRefence.removeEventListener(mListener);
    }

}
