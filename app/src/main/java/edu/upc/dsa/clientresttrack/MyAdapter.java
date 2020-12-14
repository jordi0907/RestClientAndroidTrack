package edu.upc.dsa.clientresttrack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;



public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Track> trackList;
    private static int MODIFY_TRACK = 1;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        //primera y segunda linea del rowlayout
        public TextView txtTitle;
        public TextView txtSinger;
        public View layout;
        // equivalente al oncreate, es un constructor , guardo la vista y recupero los dos elementos del row_layout
        //porque se utiliza recycler, una lista devuelve 5000 por ejemplo lista, el recycler view, genera los que aparece
        //en una vista y el doble en cache, los que desaparecen los reutilizamos y los ponemos alfinal, con 20 ViewHolder los tengo todos
        //No crea 5000 viewHolder , esto ahorra memoria
        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtTitle = (TextView) v.findViewById(R.id.firstLine);
            txtSinger = (TextView) v.findViewById(R.id.secondLine);
        }
    }

    public MyAdapter(List<Track> myDataset) {
        trackList = myDataset;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        //false es que no es el raiz
        View v =
                inflater.inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        final Track track = trackList.get(position);
        final ViewHolder myHolder = holder;

        holder.txtTitle.setText(trackList.get(position).getTitle());
        holder.txtSinger.setText(trackList.get(position).getSinger());


        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Track track = trackList.get(holder.getAdapterPosition());

                Intent intent = new Intent(holder.itemView.getContext(), EditTrackActivity.class);
                intent.putExtra("id", track.id);
                intent.putExtra("title", track.title);
                intent.putExtra("singer", track.singer);
                intent.putExtra("position", holder.getAdapterPosition());

                Activity a = (Activity)holder.itemView.getContext();
                a.startActivityForResult(intent, MODIFY_TRACK);

            }


        });

    }



    @Override
    public int getItemCount() {
        return trackList.size();
    }
}