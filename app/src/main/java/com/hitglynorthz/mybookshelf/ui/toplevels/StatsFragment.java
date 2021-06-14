package com.hitglynorthz.mybookshelf.ui.toplevels;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hitglynorthz.mybookshelf.R;
import com.hitglynorthz.mybookshelf.db.Author;
import com.hitglynorthz.mybookshelf.db.DBHelper;
import com.hitglynorthz.mybookshelf.db.Publisher;
import com.hitglynorthz.mybookshelf.db.Shelves;

import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {
    private View rootView;
    private Context context;

    private TextView tv_nbooks, tv_nbooks_unread, tv_nbooks_reading, tv_nbooks_readed, tv_nbooks_favs
            , tv_nauthors, tv_nauthors_most_books, tv_nauthors_most_readed
            , tv_npublishers, tv_npublishers_most_books, tv_npublishers_most_readed;
    private ChipGroup chipgroup_genres, chipgroup_author_most_books, chipgroup_author_most_readed
            , chipgroup_publishers_most_books, chipgroup_publishers_most_readed;
    private LinearLayout ll_authors_info, ll_publishers_info;

    private Author authorMost = null, authorMostReaded = null;
    private Publisher publisherMost = null, publisherMostReaded = null;

    private DBHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_stats, container, false);
        context = getContext();
        db = DBHelper.getInstance(context);

        initViews();

        setupData();

        return rootView;
    }

    //
    private void initViews() {
        // Books
        tv_nbooks = rootView.findViewById(R.id.tv_nbooks);
        tv_nbooks_unread = rootView.findViewById(R.id.tv_nbooks_unread);
        tv_nbooks_reading = rootView.findViewById(R.id.tv_nbooks_reading);
        tv_nbooks_readed = rootView.findViewById(R.id.tv_nbooks_readed);
        tv_nbooks_favs = rootView.findViewById(R.id.tv_nbooks_favs);
        chipgroup_genres = rootView.findViewById(R.id.chipgroup_genres);
        // Authors
        tv_nauthors = rootView.findViewById(R.id.tv_nauthors);
        ll_authors_info = rootView.findViewById(R.id.ll_authors_info);
        tv_nauthors_most_books = rootView.findViewById(R.id.tv_nauthors_most_books);
        tv_nauthors_most_readed = rootView.findViewById(R.id.tv_nauthors_most_readed);
        chipgroup_author_most_books = rootView.findViewById(R.id.chipgroup_author_most_books);
        chipgroup_author_most_readed = rootView.findViewById(R.id.chipgroup_author_most_readed);
        // Publishers
        tv_npublishers = rootView.findViewById(R.id.tv_npublishers);
        ll_publishers_info = rootView.findViewById(R.id.ll_publishers_info);
        tv_npublishers_most_books = rootView.findViewById(R.id.tv_npublishers_most_books);
        tv_npublishers_most_readed = rootView.findViewById(R.id.tv_npublishers_most_readed);
        chipgroup_publishers_most_books = rootView.findViewById(R.id.chipgroup_publishers_most_books);
        chipgroup_publishers_most_readed = rootView.findViewById(R.id.chipgroup_publishers_most_readed);
    }

    //
    private void setupData() {
        // Books
        int totalBooks = db.getAllBooksBean().size();
        int booksUnreaded = db.getAllBooksBeanUnreaded().size();
        int booksReading = db.getAllBooksBeanReading().size();
        int booksReaded = db.getAllBooksBeanReaded().size();
        int booksFavs = db.getAllBooksBeanFavs().size();
        int percentUnreaded = (booksUnreaded * 100) / totalBooks;
        int percentReading = (booksReading * 100) / totalBooks;
        int percentReaded = (booksReaded * 100) / totalBooks;
        int percentFavs = (booksFavs * 100) / totalBooks;
        tv_nbooks.setText(String.format(getString(R.string.stats_nbooks), totalBooks));
        tv_nbooks_unread.setText(String.format(getString(R.string.stats_nbooks_unread), booksUnreaded, percentUnreaded));
        tv_nbooks_reading.setText(String.format(getString(R.string.stats_nbooks_reading), booksReading, percentReading));
        tv_nbooks_readed.setText(String.format(getString(R.string.stats_nbooks_readed), booksReaded, percentReaded));
        tv_nbooks_favs.setText(String.format(getString(R.string.stats_nbooks_favs), booksFavs, percentFavs));
        // Genres
        List<String> genresList = db.getMostGenres();
        for(int i = 0; i < genresList.size(); i++) {
            Chip chip = new Chip(rootView.getContext());
            if(TextUtils.isEmpty(genresList.get(i))) {
                chip.setText(getString(R.string.stats_no_genre));
            } else {
                chip.setText(genresList.get(i));
            }
            chip.setClickable(false);
            chipgroup_genres.addView(chip);
        }
        // Authors
        tv_nauthors.setText(String.format(getString(R.string.stats_nauthors), db.getAllAuthors().size()));
        tv_nauthors_most_books.setText(getString(R.string.stats_nauthors_most_books));
        List<Author> authorListMostBooks = new ArrayList<>(db.getAuthorMostBooks());
        for(int i = 0; i < authorListMostBooks.size(); i++) {
            authorMost = authorListMostBooks.get(i);
            Chip chip = new Chip(rootView.getContext());
            chip.setText(authorMost.getAuthor_name());
            chip.setClickable(false);
            chipgroup_author_most_books.addView(chip);
        }
        tv_nauthors_most_readed.setText(getString(R.string.stats_nauthors_most_readed));
        List<Author> authorListMostReaded = new ArrayList<>(db.getAuthorMostReaded());
        if(authorListMostReaded.size() > 0) {
            for(int i = 0; i < authorListMostReaded.size(); i++) {
                authorMostReaded = authorListMostReaded.get(i);
                Chip chip = new Chip(rootView.getContext());
                chip.setText(authorMostReaded.getAuthor_name());
                chip.setClickable(false);
                chipgroup_author_most_readed.addView(chip);
            }
        }
        // Publishers
        tv_npublishers.setText(String.format(getString(R.string.stats_npublishers), db.getAllPublishers().size()));
        tv_npublishers_most_books.setText(getString(R.string.stats_npublishers_most_books));
        List<Publisher> publisherListMostBooks = new ArrayList<>(db.getPublisherMostBooks());
        for(int i = 0; i < authorListMostBooks.size(); i++) {
            publisherMost = publisherListMostBooks.get(i);
            Chip chip = new Chip(rootView.getContext());
            chip.setText(publisherMost.getPublisher_name());
            chip.setClickable(false);
            chipgroup_publishers_most_books.addView(chip);
        }
        tv_npublishers_most_readed.setText(getString(R.string.stats_npublishers_most_readed));
        List<Publisher> publisherListMostReaded = new ArrayList<>(db.getPublisherMostReaded());
        if(publisherListMostReaded.size() > 0) {
            for(int i = 0; i < authorListMostBooks.size(); i++) {
                publisherMostReaded = publisherListMostReaded.get(i);
                Chip chip = new Chip(rootView.getContext());
                chip.setText(publisherMostReaded.getPublisher_name());
                chip.setClickable(false);
                chipgroup_publishers_most_readed.addView(chip);
            }
        }
    }

}
