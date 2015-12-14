package com.example.kathleenbenavides.movies;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kathleenbenavides.movies.DO.MovieDetailDO;
import com.example.kathleenbenavides.movies.DO.ReviewDetailsDO;
import com.example.kathleenbenavides.movies.DO.TrailerDetailsDO;
import com.squareup.picasso.Picasso;

/**
 * This fragment represents the Movie detail screen.
 */
public class MovieDetailFragment extends Fragment {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private DetailFragmentCallbackInterface callbackListener;
    private MovieDetailDO movieSelected;
    TextView title;
    ImageView poster;
    TextView release;
    TextView rating;
    ImageButton favoriteButton;
    TextView overView;
    View rootView;
    LinearLayout trailerLayout;
    TextView trailerSection;
    LinearLayout reviewLayout;
    TextView reviewSection;


    public static MovieDetailFragment newInstance(MovieDetailDO movieSelected) {
        MovieDetailFragment detailFragment = new MovieDetailFragment();

        Bundle args = new Bundle();
        args.putParcelable("movieSelected", movieSelected);
        detailFragment.setArguments(args);

        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            movieSelected = (MovieDetailDO) getArguments().getParcelable("movieSelected");
        }
        if (savedInstanceState != null){
            movieSelected = (MovieDetailDO) savedInstanceState.getParcelable("movieSelected");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        title = (TextView) rootView.findViewById(R.id.title);
        poster = (ImageView) rootView.findViewById(R.id.poster);
        release = (TextView) rootView.findViewById(R.id.release);
        rating = (TextView) rootView.findViewById(R.id.rating);
        overView = (TextView) rootView.findViewById(R.id.overview);
        favoriteButton = (ImageButton) rootView.findViewById(R.id.favorite_button);
        trailerLayout = (LinearLayout) rootView.findViewById(R.id.trailerLayout);
        trailerSection = (TextView) rootView.findViewById(R.id.trailerSection);
        reviewLayout = (LinearLayout) rootView.findViewById(R.id.reviewLayout);
        reviewSection = (TextView) rootView.findViewById(R.id.reviewSection);

        rootView.setVisibility(View.INVISIBLE);

        if (movieSelected != null) {
            //Show view and update UI
            updateMovie(movieSelected, false);
        }

        return rootView;
    }

    //Sets the UI
    public void updateMovie(final MovieDetailDO movieSelected, final boolean isFavoritesView) {

        this.movieSelected = movieSelected;
        final String movieId = movieSelected.getId();

        rootView.setVisibility(View.VISIBLE);

        title.setText(movieSelected.getOriginal_title());
        Picasso.with(getActivity()).load(movieSelected.getPoster_path()).into(poster);
        release.setText(movieSelected.getRelease_date());
        rating.setText(movieSelected.getVote_average());
        overView.setText(movieSelected.getOverview());

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                //if id does not exist, add to preferences (favoriting), if it does remove it (unfavoriting)
                if (preferences.getString("movie:"+movieSelected.getOriginal_title(), "").isEmpty()) {
                    editor.putString("movie:" + movieSelected.getOriginal_title(), movieId);
                    editor.commit();
                } else if (!preferences.getString("movie:"+movieSelected.getOriginal_title(), "").isEmpty()) {
                    editor.remove("movie:" + movieSelected.getOriginal_title());
                    editor.commit();
                    if (isFavoritesView) {
                        //update the view if in favorites view
                        callbackListener.updateFavoritesView();
                    }
                }
            }
        });

        //Check if there are trailers and display them
        if (movieSelected.getTrailers().isEmpty()) {
            trailerSection.setText(getString(R.string.no_trailers_title));

        } else {
            trailerSection.setText(getString(R.string.trailers_title));
            //Dynamically add trailers
            for (int i = 0; i < movieSelected.getTrailers().size(); i++) {
                TrailerDetailsDO trailer = new TrailerDetailsDO();
                trailer = movieSelected.getTrailers().get(i);
                final String url = trailer.getSource();
                TextView movieTrailer = new TextView(getActivity());
                movieTrailer.setText(trailer.getName());
                movieTrailer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow_black_18dp, 0, 0, 0);
                movieTrailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openTrailer(url);
                    }
                });
                trailerLayout.addView(movieTrailer);
            }
        }

        //Check if there are reviews and display them
        if (movieSelected.getReviews().isEmpty()) {
            reviewSection.setText(getString(R.string.no_reviews_title));
        } else {
            reviewSection.setText(getString(R.string.reviews_title));
            //Dynamically add reviews
            for (int i = 0; i < movieSelected.getReviews().size(); i++)
            {
                ReviewDetailsDO review = new ReviewDetailsDO();
                review = movieSelected.getReviews().get(i);
                TextView movieReview = new TextView(getActivity());
                movieReview.setText(Html.fromHtml(review.getContent() + "<b>" + " -- " + review.getAuthor() + "</b>" + "<br />"));
                movieReview.setPadding(0, 15, 0, 15);

                reviewLayout.addView(movieReview);
            }
        }
    }

    public void clearView(){
        //Clear the views and layouts sort preference is selected
        rootView.setVisibility(View.GONE);
        trailerLayout.removeAllViews();
        reviewLayout.removeAllViews();
    }

    public void openTrailer(String trailerURL){
        try{
            //Open in browser
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.youtube_base_url)+ trailerURL));
            startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Log.e(LOG_TAG, ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    public interface DetailFragmentCallbackInterface {
        public void updateFavoritesView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            callbackListener = (DetailFragmentCallbackInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement DetailFragmentCallbackInterface");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("movieSelected", movieSelected);
    }
}
