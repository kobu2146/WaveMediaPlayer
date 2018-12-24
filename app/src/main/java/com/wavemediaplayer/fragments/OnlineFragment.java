package com.wavemediaplayer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.Wave;
import com.wavemediaplayer.MainActivity;
import com.wavemediaplayer.R;
import com.wavemediaplayer.adapter.jamendo.AlbumAdapter;
import com.wavemediaplayer.adapter.jamendo.ArtistAdapter;
import com.wavemediaplayer.adapter.jamendo.SongAdapter;
import com.wavemediaplayer.jamendo.data.Album;
import com.wavemediaplayer.jamendo.data.Artist;
import com.wavemediaplayer.jamendo.data.Songs;
import com.wavemediaplayer.jamendo.info.GetData;
import com.wavemediaplayer.jamendo.info.JamendoApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class OnlineFragment extends Fragment {

    /**
     * @param muzikTuru :
     * 0 --> Songs
     * 1 --> Albums
     * 2 --> Artist
     */
    int muzikTuru = 0;

    GridView mGridView;
    Button btn_songs;
    Button btn_albums;
    Button btn_artist;
    EditText edit_search;
    ImageView image_search;
    ProgressBar progressBar;
    //----
    SongAdapter songAdapter;
    AlbumAdapter albumAdapter;
    ArtistAdapter artistAdapter;
    //---
    ArrayList<Songs> songs;
    ArrayList<Album> albums;
    ArrayList<Artist> artists;
    int gridOncekiSize = 0;
    boolean ilkIstek = false;
    //next bir sonraki sayfa icin json linkini tutuyor
    String next = "";
    Context context;


    public OnlineFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mGridView = view.findViewById(R.id.grid_view);
        progressBar = view.findViewById(R.id.spin_kit);
        btn_albums = view.findViewById(R.id.btn_albums);
        btn_artist = view.findViewById(R.id.btn_artist);
        btn_songs = view.findViewById(R.id.btn_songs);
        edit_search = view.findViewById(R.id.online_edit_search);
        image_search = view.findViewById(R.id.online_image_search);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        context = view.getContext();
        //--
        songs = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
        scrollingListener();
        next = "https://api.jamendo.com/v3.0/tracks?client_id=71d8b9c0&format=json&limit=20&namespace=";
        fetchSongsData();

        girdViewClickListener();
        buttonClickListener();


    }

    private void buttonClickListener() {
        image_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = edit_search.getText().toString().replace(" ","+");
                switch (muzikTuru){
                    case 0:
                        next = "https://api.jamendo.com/v3.0/tracks?client_id=71d8b9c0&format=json&limit=20&namespace=";
                        next = next + query;
                        songs.clear();
                        fetchSongsData();
                        songAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        next = "https://api.jamendo.com/v3.0/tracks?client_id=71d8b9c0&format=json&limit=20&artist_name=";
                        next = next + query;
                        artists.clear();
                        fetchArtistData();
                        artistAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        next = "https://api.jamendo.com/v3.0/tracks?client_id=71d8b9c0&format=json&limit=20&album_name=";
                        next = next + query;
                        albums.clear();
                        fetchAlbumsData();
                        albumAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        btn_songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next = "https://api.jamendo.com/v3.0/tracks?client_id=71d8b9c0&format=json&limit=20&namespace=";
                gridOncekiSize = 0;
                fetchSongsData();
            }
        });

        btn_artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next = "https://api.jamendo.com/v3.0/tracks?client_id=71d8b9c0&format=json&limit=20&artist_name=";
                gridOncekiSize = 0;
                fetchArtistData();
            }
        });

        btn_albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next = "https://api.jamendo.com/v3.0/tracks?client_id=71d8b9c0&format=json&limit=20&album_name=";
                gridOncekiSize = 0;
                fetchAlbumsData();
            }
        });
    }

    private void girdViewClickListener() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (muzikTuru) {
                    case 0:
                        MainActivity.fPlayListener.song_title.setText(songs.get(i).getSongsTitle());
                        MainActivity.fPlayListener.song_artis.setText(songs.get(i).getArtist());
                        MainActivity.fPlayListener.playMusicFromUrl(songs.get(i).getSongLink());
                        break;
                    case 1:
                        MainActivity.fPlayListener.song_title.setText(albums.get(i).getSongsTitle());
                        MainActivity.fPlayListener.song_artis.setText(albums.get(i).getArtist());
                        MainActivity.fPlayListener.playMusicFromUrl(albums.get(i).getSongLink());
                        break;
                    case 2:
                        MainActivity.fPlayListener.song_title.setText(artists.get(i).getSongsTitle());
                        MainActivity.fPlayListener.song_artis.setText(artists.get(i).getArtist());
                        MainActivity.fPlayListener.playMusicFromUrl(artists.get(i).getSongLink());
                        break;
                    default:
                        Log.e("Hatalı", "Bilinmeyen müzik türü");
                }
            }
        });
    }

    private void scrollingListener() {
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                //Algorithm to check if the last item is visible or not
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {
                    // here you have reached end of list, load more data
                    if (ilkIstek) {
                        switch (muzikTuru) {
                            case 0:
                                fetchSongsData();
                                break;
                            case 1:
                                fetchAlbumsData();
                                Log.e("scroll","album");
                                break;
                            case 2:
                                fetchArtistData();
                                break;
                        }
                        ilkIstek = false;
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //blank, not required in your case
            }
        });
    }


    private void fetchSongsData() {

        if (albums != null) {
            albums.clear();
        }
        if (artists != null) {
            artists.clear();
        }
        GetData getData = new GetData(next);
        getData.getJsonData(new JamendoApi() {
            @Override
            public void beforeData() {
                progressBar.setVisibility(View.VISIBLE);
                muzikTuru = 0;
                gridOncekiSize = songs.size();
            }

            @Override
            public void getApiData(String jsonStr) {
                if (jsonStr != null) {
                    try {
                        Songs song;
                        JSONObject jsonObj = new JSONObject(jsonStr);
                         next = jsonObj.getJSONObject("headers").getString("next");

                            for (int i = 0; i < jsonObj.getJSONArray("results").length(); i++) {
                                song = new Songs();
                                String id = jsonObj.getJSONArray("results").getJSONObject(i).getString("id");
                                String title = jsonObj.getJSONArray("results").getJSONObject(i).getString("name");
                                String artist = jsonObj.getJSONArray("results").getJSONObject(i).getString("artist_name");
                                String album = jsonObj.getJSONArray("results").getJSONObject(i).getString("album_name");
                                String image = jsonObj.getJSONArray("results").getJSONObject(i).getString("image");
                                String link = jsonObj.getJSONArray("results").getJSONObject(i).getString("audio");

                                Log.e("title",title);

                                song.setSongsId(id);
                                song.setSongsTitle(title);
                                song.setArtist(artist);
                                song.setAlbum_name(album);
                                song.setSongsImage(image);
                                song.setSongLink(link);

                                songs.add(song);
                            }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterData() {
                progressBar.setVisibility(View.GONE);
                btn_songs.setBackground(getResources().getDrawable(R.drawable.rounded_button_selected));
                btn_albums.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                btn_artist.setBackground(getResources().getDrawable(R.drawable.rounded_button));

                songAdapter = new SongAdapter(context, R.layout.item_grid, songs);
                mGridView.setAdapter(songAdapter);
                mGridView.setSelection(gridOncekiSize);

                ilkIstek = true;
            }
        });
    }

    private void fetchAlbumsData() {
        if (songs != null) {
            songs.clear();
        }
        if (artists != null) {
            artists.clear();
        }
        GetData getData = new GetData(next);
        getData.getJsonData(new JamendoApi() {
            @Override
            public void beforeData() {
                progressBar.setVisibility(View.VISIBLE);
                muzikTuru = 1;
                gridOncekiSize = albums.size();
            }

            @Override
            public void getApiData(String jsonStr) {
                if (jsonStr != null) {
                    try {
                        Album album;
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        next = jsonObj.getJSONObject("headers").getString("next");

                            Log.e("albumNext", next);
                            for (int i = 0; i < jsonObj.getJSONArray("results").length(); i++) {
                                album = new Album();
                                String id = jsonObj.getJSONArray("results").getJSONObject(i).getString("id");
                                String title = jsonObj.getJSONArray("results").getJSONObject(i).getString("name");
                                String artist = jsonObj.getJSONArray("results").getJSONObject(i).getString("artist_name");
                                String albumName = jsonObj.getJSONArray("results").getJSONObject(i).getString("album_name");
                                String image = jsonObj.getJSONArray("results").getJSONObject(i).getString("image");
                                String link = jsonObj.getJSONArray("results").getJSONObject(i).getString("audio");

                                album.setSongsId(id);
                                album.setSongsTitle(title);
                                album.setArtist(artist);
                                album.setAlbum_name(albumName);
                                album.setSongsImage(image);
                                album.setSongLink(link);

                                albums.add(album);
                            }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterData() {
                progressBar.setVisibility(View.GONE);
                btn_songs.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                btn_albums.setBackground(getResources().getDrawable(R.drawable.rounded_button_selected));
                btn_artist.setBackground(getResources().getDrawable(R.drawable.rounded_button));

                albumAdapter = new AlbumAdapter(context, R.layout.item_grid, albums);
                mGridView.setAdapter(albumAdapter);
                Log.e("onceki",""+gridOncekiSize);
                mGridView.setSelection(gridOncekiSize);

                ilkIstek = true;
            }
        });
    }

    private void fetchArtistData() {
        if (albums != null) {
            albums.clear();
        }
        if (songs != null) {
            songs.clear();
        }
        GetData getData = new GetData(next);
        getData.getJsonData(new JamendoApi() {
            @Override
            public void beforeData() {
                muzikTuru = 2;
                progressBar.setVisibility(View.VISIBLE);
                gridOncekiSize = artists.size();
            }

            @Override
            public void getApiData(String jsonStr) {
                if (jsonStr != null) {
                    try {
                        Artist artist;
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        String tempNext = jsonObj.getJSONObject("headers").getString("next");
                        if (!next.equals(tempNext)) {
                            next = tempNext;
                            Log.e("Artisnext", next);
                            for (int i = 0; i < jsonObj.getJSONArray("results").length(); i++) {
                                artist = new Artist();
                                String id = jsonObj.getJSONArray("results").getJSONObject(i).getString("id");
                                String title = jsonObj.getJSONArray("results").getJSONObject(i).getString("name");
                                String artistName = jsonObj.getJSONArray("results").getJSONObject(i).getString("artist_name");
                                String albumName = jsonObj.getJSONArray("results").getJSONObject(i).getString("album_name");
                                String image = jsonObj.getJSONArray("results").getJSONObject(i).getString("image");
                                String link = jsonObj.getJSONArray("results").getJSONObject(i).getString("audio");

                                artist.setSongsId(id);
                                artist.setSongsTitle(title);
                                artist.setArtist(artistName);
                                artist.setAlbum_name(albumName);
                                artist.setSongsImage(image);
                                artist.setSongLink(link);

                                artists.add(artist);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterData() {
                progressBar.setVisibility(View.GONE);
                btn_songs.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                btn_albums.setBackground(getResources().getDrawable(R.drawable.rounded_button));
                btn_artist.setBackground(getResources().getDrawable(R.drawable.rounded_button_selected));

                artistAdapter = new ArtistAdapter(context, R.layout.item_grid, artists);
                mGridView.setAdapter(artistAdapter);
                mGridView.setSelection(gridOncekiSize);

                ilkIstek = true;
            }
        });
    }
}
