package com.example.kathleenbenavides.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kathleenbenavides.movies.DO.MovieDetailDO;
import com.example.kathleenbenavides.movies.MovieListFragment.ListFragmentCallbackInterface;
import com.example.kathleenbenavides.movies.MovieDetailFragment.DetailFragmentCallbackInterface;


public class MovieListActivity extends AppCompatActivity implements ListFragmentCallbackInterface, DetailFragmentCallbackInterface{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    MovieListFragment listFragment;
    private MovieDetailDO movieSelected;
    MovieDetailFragment detailFragment;
    String requestURL;
    boolean isFavoritesView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (savedInstanceState != null)  {
            listFragment = (MovieListFragment) getSupportFragmentManager().getFragment(savedInstanceState, "listFragment");
        }

        if (listFragment == null) {
            listFragment = MovieListFragment.newInstance(mTwoPane);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.movie_list_container, listFragment);
        fragmentTransaction.commit();

        if (mTwoPane) {
            //show the detail view in this activity by adding or replacing the detail fragment
            detailFragment = new MovieDetailFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.movie_detail_container, detailFragment);
            ft.commit();

        }
    }

    //Update the movie selected and clear the detail fragment to show new selection
    public void updateMovie(MovieDetailDO movieSelected, boolean favView) {
        this.movieSelected = movieSelected;
        detailFragment.clearView();
        detailFragment.updateMovie(movieSelected, favView);
    }

    public void startDetailActivity(MovieDetailDO movieSelected) {
        Intent intent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
        intent.putExtra("movieSelected", movieSelected);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_sortpopular:
                requestURL = getString(R.string.popular_url) + "&" + getString(R.string.api_key);
                isFavoritesView = false;
                listFragment.updateList(requestURL, isFavoritesView);
                if (detailFragment != null){
                    detailFragment.clearView();
                }
                return true;
            case R.id.action_sortrated:
                requestURL = getString(R.string.top_rate_url) + getString(R.string.api_key);
                isFavoritesView = false;
                listFragment.updateList(requestURL, isFavoritesView);
                if (detailFragment != null){
                    detailFragment.clearView();
                }
                return true;
            case R.id.action_favorites:
                isFavoritesView = true;
                if (detailFragment != null){
                    detailFragment.clearView();
                }
                listFragment.getFavorites(isFavoritesView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Called to update the view if add/removing in favorites view
    public void updateFavoritesView() {
        if (detailFragment != null){
            detailFragment.clearView();
        }
        listFragment.getFavorites(isFavoritesView);
    }

        @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "listFragment", listFragment);
    }
}
