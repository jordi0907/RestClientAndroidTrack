package edu.upc.dsa.clientresttrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditTrackActivity extends AppCompatActivity {

    public TextView idTextView ;
    public TextView trackNameTextView ;
    public TextView authorTextView ;
    Track track;
    TracksService tracksservice;
    String id ="";
    String name ="";
    String singer ="";
    int position;
    String action;

    //crear retrofit
    Retrofit retrofit2;



    @Override
    public String toString() {
        return "EditTrackActivity{" +
                "idTextView=" + idTextView +
                ", trackNameTextView=" + trackNameTextView +
                ", authorTextView=" + authorTextView +
                ", track=" + track +
                ", tracksservice=" + tracksservice +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", singer='" + singer + '\'' +
                '}';
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_track);

        Intent intent = getIntent();
        this.id = intent.getStringExtra("id");
        this.name = intent.getStringExtra("title");
        this.singer = intent.getStringExtra("singer");
        this.position = intent.getIntExtra("position",0);


        idTextView = this.findViewById(R.id.editText6);
        trackNameTextView = this.findViewById(R.id.editText3);
        authorTextView = this.findViewById(R.id.editText4);

        idTextView.setText(this.id);
        trackNameTextView.setText(this.name);
        authorTextView.setText(this.singer);

        //Configuracion del retrofit
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //Attaching Interceptor to a client
        OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();
        retrofit2 = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/dsaApp/")
                // .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        tracksservice = retrofit2.create(TracksService.class);




    }

    public void onButtonClickModify(View view) {
        track = new Track();
        this.track.setId(this.idTextView.getText().toString());
        this.track.setSinger(this.authorTextView.getText().toString());
        this.track.setTitle(this.trackNameTextView.getText().toString());
        NotifyUser("track" + track.getTitle());
        try {
            Call<Void> tracks = tracksservice.editTrack(track);
            tracks.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 201) {
                        NotifyUser("Successful");
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("action", "modificar");
                        returnIntent.putExtra("position", position);
                        returnIntent.putExtra("Track", (Parcelable) track);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else if (response.code() == 404) { // Validation Error
                        NotifyUser("Track not Found");
                    } else {
                        NotifyUser("Error Server");
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    NotifyUser("Error,could not retrieve data!");
                }
            });
        } catch (Exception e) {
            NotifyUser("Exception: " + e.toString());
        }


    }

    //Notifica mensajes
    private void NotifyUser(String MSG) {
        Toast toast = Toast.makeText(EditTrackActivity.this, MSG, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onButtonClickDelete(View view) {
        try {
            Call<Void> tracks = tracksservice.deleteTrack(id);
            tracks.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 201) {
                        NotifyUser("Successful");

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("position", position);
                        returnIntent.putExtra("action", "delete");
                        setResult(RESULT_OK, returnIntent); //By not passing the intent in the result, the calling activity will get null data.
                        finish();
                    } else if (response.code() == 404) { // Validation Error
                        NotifyUser("Track not Found");
                    } else {
                        NotifyUser("Error Server");
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    NotifyUser("Error,could not retrieve data!");
                }
            });
        } catch (Exception e) {
            NotifyUser("Exception: " + e.toString());
        }

    }

    public void onButtonClickAdd(View view) {
        track = new Track();
        this.track.setId(this.idTextView.getText().toString());
        this.track.setSinger(this.authorTextView.getText().toString());
        this.track.setTitle(this.trackNameTextView.getText().toString());
        try {
            Call<Track> tracks = tracksservice.createTrack(track);
            tracks.enqueue(new Callback<Track>() {
                @Override
                public void onResponse(Call<Track> call, Response<Track> response) {
                    if (response.code() == 201) {
                        NotifyUser("Successful");
                    }else {
                        NotifyUser("Error Server");
                    }
                }
                @Override
                public void onFailure(Call<Track> call, Throwable t) {
                    NotifyUser("Error,could not retrieve data!");
                }
            });
        } catch (Exception e) {
            NotifyUser("Exception: " + e.toString());
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("position", position);
        returnIntent.putExtra("action", "add");
        returnIntent.putExtra("Track", (Parcelable) this.track);
        setResult(RESULT_OK, returnIntent); //By not passing the intent in the result, the calling activity will get null data.
        finish();
    }

    public void onButtonClickCancel(View view) {
        finish();
    }
}