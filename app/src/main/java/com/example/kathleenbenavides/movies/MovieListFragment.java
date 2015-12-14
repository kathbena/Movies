package com.example.kathleenbenavides.movies;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.kathleenbenavides.movies.DO.MovieDetailDO;
import com.example.kathleenbenavides.movies.DO.ReviewDetailsDO;
import com.example.kathleenbenavides.movies.DO.TrailerDetailsDO;
import com.example.kathleenbenavides.movies.FetchMoviesTask.CompletedTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
// This fragment displays the list of movies and updates the views based on what is selected in movie
// list.
public class MovieListFragment extends Fragment {

    private final String LOG_TAG = MovieListFragment.class.getSimpleName();
    GridView gridView;
    private MovieAdapter myAdapter = new MovieAdapter(getActivity(), null);
    ArrayList<MovieDetailDO> movieDetails = new ArrayList<MovieDetailDO>();
    ArrayList<MovieDetailDO> favoritesList = new ArrayList<MovieDetailDO>();
    ArrayList<ReviewDetailsDO> reviewDetails = new ArrayList<ReviewDetailsDO>();
    ArrayList<TrailerDetailsDO> trailerDetails = new ArrayList<TrailerDetailsDO>();
    private String posterPath;
    private String requestURL;
    private ListFragmentCallbackInterface callbackListener;
    private boolean twoPane;
    MovieDetailDO movieSelected;
    boolean isFavoritesView;


    public static MovieListFragment newInstance(boolean twoPane) {
        MovieListFragment listFragment = new MovieListFragment();

        Bundle args = new Bundle();
        args.putBoolean("twoPane", twoPane);
        listFragment.setArguments(args);

        return listFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            twoPane = getArguments().getBoolean("twoPane");
        } else {
            twoPane = savedInstanceState.getBoolean("twoPane");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridView);

        requestURL = getString(R.string.movie_main_url) + getString(R.string.api_key);
        startAsyncTask(requestURL);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reviewDetails.clear();
                trailerDetails.clear();
                if (isFavoritesView) {
                    movieSelected = favoritesList.get(position);
                } else {
                    movieSelected = movieDetails.get(position);
                }
                String movieId = movieSelected.getId();
                String trailerReviewRequestUrl = getString(R.string.trailer_base_url) + movieId + "?" +
                        getString(R.string.api_key) + getString(R.string.trailer_review_append);
                findTrailersReviews(trailerReviewRequestUrl);
            }
        });

        return rootView;
    }

    //Request for all movies
    public void startAsyncTask(String requestURL){
        FetchMoviesTask task = new FetchMoviesTask(new CompletedTask() {
            @Override
            //public void completedTask(ArrayList<MovieDetailDO> result, ArrayList<MovieDetailDO> details) {
            public void completedTask(String movies) {
                try {
                    ArrayList<MovieDetailDO> result = getMovieDataFromJson(movies);
                    myAdapter = new MovieAdapter(getActivity(), result);
                    gridView.setAdapter(myAdapter);

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        });
        task.execute(requestURL);
    }

    //Request for trailers and reviews of a movie
    public void findTrailersReviews(String requestURL){
        FetchMoviesTask task = new FetchMoviesTask(new CompletedTask() {
            @Override
            public void completedTask(String movieResult) {
                try{
                    getMovieTrailerReviewsFromJson(movieResult);

                    if (twoPane) {
                        //if in tablet, update fragment
                        callbackListener.updateMovie(movieSelected, isFavoritesView);
                    } else {
                        //start activity
                        callbackListener.startDetailActivity(movieSelected);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        });
        task.execute(requestURL);
    }

    /**
     * Take the String representing the result of movies in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     */
    private ArrayList<MovieDetailDO> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String results = "results";
        final String MOVIEID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String POSTER_PATH = "poster_path";
        final String VOTE_AVERAGE = "vote_average";


        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(results);

        for (int i = 0; i < movieArray.length(); i++) {
            MovieDetailDO detail = new MovieDetailDO();

            // Set details for each movie and add to arraylist
            JSONObject movie = movieArray.getJSONObject(i);
            detail.setId(movie.getString(MOVIEID));
            detail.setOriginal_title(movie.getString(ORIGINAL_TITLE));
            detail.setOverview(movie.getString(OVERVIEW));
            detail.setRelease_date(movie.getString(RELEASE_DATE));
            // Get the poster url and construct it
            String poster = movie.getString(POSTER_PATH);
            detail.setPoster_path(constructPosterURL(poster));
            detail.setVote_average(movie.getString(VOTE_AVERAGE));

            movieDetails.add(detail);
        }
        return movieDetails;
    }

    /**
     * Take the String representing the result of a movie in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     */
    private void getMovieTrailerReviewsFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String REVIEWS = "reviews";
        final String TRAILERS = "trailers";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray reviews = movieJson.getJSONObject(REVIEWS).getJSONArray("results");
        JSONArray trailers = movieJson.getJSONObject(TRAILERS).getJSONArray("youtube");

        for (int i = 0; i < reviews.length(); i++) {
            ReviewDetailsDO reviewDetail = new ReviewDetailsDO();
            // Set details for each review and add to arraylist
            JSONObject review = reviews.getJSONObject(i);
            reviewDetail.setContent(review.getString("content"));
            reviewDetail.setAuthor(review.getString("author"));
            reviewDetails.add(reviewDetail);
        }

        for (int i = 0; i < trailers.length(); i++) {
            TrailerDetailsDO trailerDetail = new TrailerDetailsDO();
            // Set details for each trailer and add to arraylist
            JSONObject trailer = trailers.getJSONObject(i);
            trailerDetail.setSource(trailer.getString("source"));
            trailerDetail.setName(trailer.getString("name"));
            trailerDetails.add(trailerDetail);
        }
        //Add reviews and trailers to movieSelected
        movieSelected.setReviews(reviewDetails);
        movieSelected.setTrailers(trailerDetails);
    }

    public void updateList(String requestURL, boolean isFavoritesView){
        movieDetails.clear();
        reviewDetails.clear();
        trailerDetails.clear();
        this.isFavoritesView = isFavoritesView;
        myAdapter.notifyDataSetChanged();
        startAsyncTask(requestURL);
    }

    //Get favorites and update the view to display them
    public void getFavorites(boolean isFavoritesView){
        favoritesList.clear();
        this.isFavoritesView = isFavoritesView;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        for (int i = 0; i < preferences.getAll().size(); i++) {
            for(int j = 0; j < movieDetails.size(); j++) {
                MovieDetailDO possibleMovie = movieDetails.get(j);

                String movieID = preferences.getString("movie:" + possibleMovie.getOriginal_title(), "");
                if ((movieID != null) && (possibleMovie.getId().equalsIgnoreCase(movieID))) {

                    if (!favoritesList.contains(possibleMovie)) {
                        favoritesList.add(possibleMovie);
                    }
                }
            }
        }
        myAdapter.notifyDataSetChanged();
        myAdapter = new MovieAdapter(getActivity(), favoritesList);
        gridView.setAdapter(myAdapter);
    }

    //This function constructs the poster url and size for requesting in Picasso
    public String constructPosterURL(String poster) {
        String baseURL = getString(R.string.base_poster_url);
        String posterSize = "w500";
        posterPath = baseURL + posterSize + poster;

        return posterPath;
    }

    public interface ListFragmentCallbackInterface {
        public void updateMovie(MovieDetailDO movieSelected, boolean isFavoritesView);
        public void startDetailActivity(MovieDetailDO movieSelected);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            callbackListener = (ListFragmentCallbackInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement MovieListCallbackInterface");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("twoPane", twoPane);
    }
}
