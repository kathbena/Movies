package com.example.kathleenbenavides.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.kathleenbenavides.movies.DO.MovieDetailDO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kathleenbenavides on 10/26/15.
 */
public class MovieAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MovieDetailDO> movieList;

    public MovieAdapter(Context context, ArrayList<MovieDetailDO> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;

        //Inflate view if it is null
        if (view == null) {

            view = inflater.inflate(R.layout.grid_item, null);

            ImageView imageView = (ImageView) view.findViewById(R.id.movie_poster);
            MovieDetailDO movie = movieList.get(position);
            Picasso.with(context).load(movie.getPoster_path()).into(imageView);

        } else {
            ImageView imageView = (ImageView) view.findViewById(R.id.movie_poster);
            MovieDetailDO movie = movieList.get(position);
            Picasso.with(context).load(movie.getPoster_path()).into(imageView);
        }

        return view;
    }

}
