package com.hitglynorthz.mybookshelf.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hitglynorthz.mybookshelf.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Search_OISBN extends DialogFragment {
    private View rootView;
    private Context context;

    //https://openlibrary.org/api/books?bibkeys=ISBN:0385472579&jscmd=data&format=json
    private String URLbaseOL = "https://openlibrary.org/api/books?bibkeys=ISBN:";
    private String URLendOL = "&jscmd=data&format=json";
    private String isbnKey;
    private HashMap<String, String> hmap = new HashMap<String, String>();

    private TextView tv_isbn;
    private String title, authorName, publisherName, genre, year, pages, lang, descp, cover, web_link;

    public static Search_OISBN newInstance(String isbn) {
        Search_OISBN fragment = new Search_OISBN();
        Bundle args = new Bundle();
        args.putString("isbn", isbn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_search_isbn, container, false);
        context = getContext();

        tv_isbn = rootView.findViewById(R.id.tv_isbn);
        if (getArguments() != null) {
            isbnKey = getArguments().getString("isbn");
            tv_isbn.setText("ISBN: " + isbnKey);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLbaseOL + isbnKey + URLendOL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("JSON", "Parsing JSON");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject isbnObject = jsonObject.getJSONObject("ISBN:" + isbnKey);

                    if(isbnObject.has("title")) {
                        title = isbnObject.getString("title");
                        Log.e("T", title);
                    }

                    if(isbnObject.has("authors")) {
                        JSONArray authors = isbnObject.getJSONArray("authors");
                        JSONObject author = authors.getJSONObject(0);
                        if (author.has("name")) {
                            authorName = author.getString("name");
                            Log.e("T", authorName);
                        }
                    }

                    if(isbnObject.has("publishers")) {
                        JSONArray publishers = isbnObject.getJSONArray("publishers");
                        JSONObject publisher = publishers.getJSONObject(0);
                        if (publisher.has("name")) {
                            publisherName = publisher.getString("name");
                            Log.e("T", publisherName);
                        }
                    }

                    if(isbnObject.has("cover")) {
                        JSONObject imageObject = isbnObject.getJSONObject("cover");
                        if (imageObject.has("large")) {
                            cover = imageObject.getString("large");
                            Log.e("T", cover);
                        }
                    }

                    if(isbnObject.has("number_of_pages")) {
                        pages = isbnObject.getString("number_of_pages");
                        Log.e("T", pages);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    hmap.put("error", "error");
                }
                getDialog().dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hmap.put("error", "error");
                Log.e("JSON", error.toString());
                getDialog().dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface CustomListener {
        void onMyCustomAction(HashMap hashMap);
    }
    private Search_OISBN.CustomListener mListener;
    public void setMyCustomListener(Search_OISBN.CustomListener listener) {
        mListener = listener;
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        if(mListener != null) {
            hmap.put("title", title);
            hmap.put("author", authorName);
            hmap.put("publisher", publisherName);
            hmap.put("genre", genre);
            hmap.put("year", year);
            hmap.put("pages", pages);
            hmap.put("lang", lang);
            hmap.put("descp", descp);
            hmap.put("cover", cover);
            hmap.put("web_link", web_link);
            mListener.onMyCustomAction(hmap);
        }
        super.onDismiss(dialog);
    }
}
