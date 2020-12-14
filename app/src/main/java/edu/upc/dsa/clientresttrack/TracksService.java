package edu.upc.dsa.clientresttrack;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TracksService {
    @GET("tracks/")
    Call<List<Track>> listTracks();
    @PUT("tracks/")
    Call<Void> editTrack(@Body Track track);
    @DELETE("tracks/{id}")
    Call<Void> deleteTrack(@Path("id") String id);
    @POST("tracks/")
    Call<Track> createTrack(@Body Track track);


}