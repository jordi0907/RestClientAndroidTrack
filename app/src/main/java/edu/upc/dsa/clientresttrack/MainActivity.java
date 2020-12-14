package edu.upc.dsa.clientresttrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    //crear retrofit
    Retrofit retrofit;

    //Para crear el recycler
    private RecyclerView recyclerView;
    //  private RecyclerView.Adapter mAdapter;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //para la interfaz
    TracksService tracksservice;
    List<Track> trackList;
    private static int MODIFY_TRACK = 1;
    private boolean aBooleanServedAlready = false;
    String action;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Esto es configuracion del recycler
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Configuracion del retrofit
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //Attaching Interceptor to a client
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/dsaApp/")
                // .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        tracksservice = retrofit.create(TracksService.class);


    }

    //Notifica mensajes
    private void NotifyUser(String MSG) {
        Toast toast = Toast.makeText(MainActivity.this, MSG, Toast.LENGTH_SHORT);
        toast.show();
    }

    //obtienes la lista de tracks
    public void onButtonGetTracksClick(View view) {
        //Retrofit Implementation on Button Press
        //Adding Interceptor
        try {
            Call<List<Track>> tracks = tracksservice.listTracks();
            /* Android Doesn't allow synchronous execution of Http Request and so we must put it in queue*/
            tracks.enqueue(new Callback<List<Track>>() {
                @Override
                public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {

                    //Retrieve the result containing in the body
                    if (!response.body().isEmpty()) {
                        // non empty response, Mapping Json via Gson...
                        NotifyUser("Server Response Ok");
                        //tengo la lista del body de la peticion get y la inserto en Track_list
                        MainActivity.this.trackList = response.body();
                        //lamo a crear el recycler view
                        buildRecyclerView();
                        //Server has served client so we can now edit the list of Tracks/Repo
                        aBooleanServedAlready = true;
                    } else {
                        // empty response...
                        NotifyUser("Request Failed!");
                    }
                }

                @Override
                public void onFailure(Call<List<Track>> call, Throwable t) {
                    NotifyUser("Error,could not retrieve data!");
                }
            });
        } catch (Exception e) {
            NotifyUser("Exception: " + e.toString());
        }
    }

    private void buildRecyclerView(){
        // define an adapter y le paso el contenido que tiene que adaptar
        mAdapter = new MyAdapter(trackList);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            action = data.getStringExtra("action");
            if(action.equals("delete")) {
                int position = data.getIntExtra("position", -1);
                Track track = data.getParcelableExtra("Track");
                NotifyUser("la posicion" + position);
                trackList.remove(position);
                mAdapter.notifyItemRemoved(position);
                // deal with the item yourself
            }
            else if(action.equals("modificar")){
                int position  = data.getIntExtra("position",-1);
                Track track = data.getParcelableExtra("Track");
                NotifyUser("la posicion" + position);
                trackList.set(position,track);
                mAdapter.notifyItemChanged(position);
            }
            else if(action.equals("add")){
                int position  = data.getIntExtra("position",-1);
                Track track = data.getParcelableExtra("Track");
                NotifyUser("la posicion" + position);
                trackList.add(position,track);
                mAdapter.notifyItemInserted(position);
            }
        }

    }

    public void onButtonAddTracksClick(View view) {
        Intent intent = new Intent(this, EditTrackActivity.class);
        startActivityForResult(intent, MODIFY_TRACK);

    }
}