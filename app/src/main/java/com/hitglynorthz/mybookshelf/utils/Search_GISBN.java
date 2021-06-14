package com.hitglynorthz.mybookshelf.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Search_GISBN extends DialogFragment {
    private View rootView;
    private Context context;

    private String URLbaseGoogle = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
    private String isbnKey;
    private HashMap<String, String> hmap = new HashMap<String, String>();

    private TextView tv_isbn;
    private String title, author, publisher, genre, year, pages, lang, descp, cover, web_link;

    public static Search_GISBN newInstance(String isbn) {
        Search_GISBN fragment = new Search_GISBN();
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

        // Usamos el StringRequest de Volley para recuperar los datos de JSON
        // para conseguir el JSON se usa la API de Google Books v1
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLbaseGoogle + isbnKey, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("JSON", "Parsing JSON");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.isNull("items")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("items");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonItem = (JSONObject) jsonArray.get(i);
                            if(jsonItem.has("volumeInfo")) {
                                JSONObject volumeInfo = (JSONObject) jsonItem.get("volumeInfo");
                                // title
                                if(volumeInfo.has("title")) {
                                    title = volumeInfo.getString("title");
                                }
                                // author
                                if(volumeInfo.has("authors")) {
                                    JSONArray Authors = volumeInfo.getJSONArray("authors");
                                    List<String> iAuthors = new ArrayList<>();
                                    for(int authorCount = 0; authorCount < Authors.length(); authorCount++) {
                                        iAuthors.add(Authors.getString(authorCount));
                                    }
                                    String[] separated = TextUtils.join(", ", iAuthors).split(",");
                                    author = separated[0];
                                }
                                // publisher
                                if(volumeInfo.has("publisher")) {
                                    publisher = volumeInfo.getString("publisher");
                                }
                                // genre
                                if(volumeInfo.has("categories")) {
                                    JSONArray Cat = volumeInfo.getJSONArray("categories");
                                    List<String> iCat = new ArrayList<>();
                                    for(int catCount = 0; catCount < Cat.length(); catCount++) {
                                        iCat.add(Cat.getString(catCount));
                                    }
                                    String[] separated = TextUtils.join(", ", iCat).split(",");
                                    genre = separated[0];
                                }
                                // year
                                if(volumeInfo.has("publishedDate")) {
                                    year = Utils.splitYear(volumeInfo.getString("publishedDate"));
                                }
                                // pages
                                if(volumeInfo.has("pageCount")) {
                                    pages = volumeInfo.getString("pageCount");
                                }
                                // language
                                if(volumeInfo.has("language")) {
                                    lang = volumeInfo.getString("language");
                                }
                                // description
                                if(volumeInfo.has("description")) {
                                    descp = volumeInfo.getString("description");
                                }
                                // thumbnail
                                if(volumeInfo.has("imageLinks")) {
                                    JSONObject imageLinks = (JSONObject) volumeInfo.get("imageLinks");
                                    if (imageLinks.get("thumbnail") != null) {
                                        Log.i("COVER", imageLinks.get("thumbnail").toString());
                                        cover = imageLinks.get("thumbnail").toString();
                                    }
                                }
                                // web_link
                                if(volumeInfo.has("canonicalVolumeLink")) {
                                    web_link = volumeInfo.getString("canonicalVolumeLink");
                                }
                            }
                        }
                    } else {
                        Log.e("JSON", "Error: no items in JSON");
                        hmap.put("error", "error");
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
    private CustomListener mListener;
    public void setMyCustomListener(CustomListener listener) {
        mListener = listener;
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        if(mListener != null) {
            hmap.put("title", title);
            hmap.put("author", author);
            hmap.put("publisher", publisher);
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
