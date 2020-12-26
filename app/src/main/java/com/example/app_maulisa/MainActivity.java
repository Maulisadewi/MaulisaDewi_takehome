package com.example.app_maulisa;

import android.app.SearchManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;


import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.google.android.gms.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements GithubAdapter.UserAdapterListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Githubuser> userlist;
    private GithubAdapter mAdapter;
    private SearchView searchView;
    private ProgressBar pro;
    private static int count = 0;
    private static final long threshold_time = 3600000;
    private static long timelapse = 0;

    private static String page = "1";
    private static String URL = "https://api.github.com/users?page=" + page + "&per_page=100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());

        } catch (GooglePlayServicesRepairableException e) {

            GoogleApiAvailability.getInstance().showErrorNotification(this, e.getConnectionStatusCode());
        } catch (GooglePlayServicesNotAvailableException e) {

            e.getMessage();
        }
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Github User");

        recyclerView = findViewById(R.id.recycler_view);
        pro = findViewById(R.id.progbar);

        userlist = new ArrayList<>();
        mAdapter = new GithubAdapter(this, userlist,this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
      //  RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        fetchUser(URL);

        recyclerView.addOnScrollListener(new EndlessOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore() {
                long now = SystemClock.elapsedRealtime();
                if (now - timelapse < threshold_time) {
                    if(count>60){
                        Toast.makeText(getApplicationContext(), "Too many request per minute, wait a moment!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        addDataToList();
                    }
                }
                else{
                    addDataToList();
                }
                timelapse = now;
                count++;


            }
        });



    }

    private void addDataToList() {
        page = Integer.toString(mAdapter.getItemCount()) ;

        pro.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchUser(URL);
                pro.setVisibility(View.GONE);
            }
        } , 3000);


    }



    private void fetchUser(String URl) {
        JsonArrayRequest request = new JsonArrayRequest(URl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if (response == null) {
                    Toast.makeText(getApplicationContext(), "Couldn't fetch the user! Please try again.", Toast.LENGTH_LONG).show();
                    return;
                }

                List<Githubuser> items = new Gson().fromJson(response.toString(), new TypeToken<List<Githubuser>>() {
                }.getType());

                userlist.addAll(items);
                mAdapter = new GithubAdapter(getApplicationContext(), userlist, MainActivity.this::onUserSelected);
                recyclerView.setAdapter(mAdapter);

                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.searching).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                mAdapter.getFilter().filter(query);
                pro.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {


                mAdapter.getFilter().filter(query);
                pro.setVisibility(View.GONE);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.searching) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }



    @Override
    public void onUserSelected(Githubuser githubuser) {
        Toast.makeText(getApplicationContext(), "Selected: " + githubuser.getName() + ", " + githubuser.getUrlname(), Toast.LENGTH_LONG).show();
    }
}