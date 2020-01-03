package com.example.mastercustomer.view.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mastercustomer.R;
import com.example.mastercustomer.repository.model.BaseResponse;
import com.example.mastercustomer.repository.model.Customers;
import com.example.mastercustomer.utility.SessionManager;
import com.example.mastercustomer.view.detail.DetailAct;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

public class HomeAct extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeView, HomeAdapter.onClickListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.edit_text_search) EditText searchEditText;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipe;
    @BindView(R.id.shimmer_view) ShimmerFrameLayout shimmerView;
    @BindView(R.id.data_not_found) TextView dataNotFound;
    @BindView(R.id.view_divider) View dividerLayout;
    @BindView(R.id.recycler_view_customer) RecyclerView customerList;

    private SessionManager sessionManager;
    private HashMap<String, String> user;

    private HomePresenter presenter;
    private HomeAdapter adapter;

    private List<Customers> listCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_home);

        sessionManager = new SessionManager(this);
        presenter = new HomePresenter(this);
        ButterKnife.bind(this);

        user = sessionManager.getUserDetail();
        presenter.getListCustomers(user.get(SessionManager.USERNAME), "");

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        TextView accountNameText = headerView.findViewById(R.id.text_view_account_name);
        accountNameText.setText(user.get(SessionManager.USERNAME));
        navigationView.setNavigationItemSelectedListener(this);

        swipe.setOnRefreshListener(() -> {
            swipe.setRefreshing(true);
            presenter.getListCustomers(user.get(SessionManager.USERNAME), "");
        });
    }

    @OnTextChanged(value = R.id.edit_text_search,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onSearchChanged(Editable editable){
        presenter.getListCustomers(user.get(SessionManager.USERNAME), editable.toString());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_home) {
            // Handle the camera action
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Anda yakin ingin keluar dari aplikasi?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sessionManager.signOut();
                        finishAffinity();
                        dialog.dismiss();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLoading() {
        customerList.setVisibility(View.GONE);
        shimmerView.setVisibility(View.GONE);
        shimmerView.startShimmer();
    }

    @Override
    public void onComplete() {
        swipe.setRefreshing(false);
        shimmerView.stopShimmer();
        shimmerView.setVisibility(View.GONE);
    }

    @Override
    public void onResponseSuccess(BaseResponse baseResponse) {
        if (baseResponse.getMessage().equals("Success")){
            listCustomers = baseResponse.getCustomersList();
            adapter = new HomeAdapter(this,this,this, listCustomers);
            customerList.setAdapter(adapter);
            RecyclerView.LayoutManager layoutManager =
                    new LinearLayoutManager(this);
            customerList.setLayoutManager(layoutManager);
            customerList.setHasFixedSize(true);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(customerList.getContext(),
                    DividerItemDecoration.VERTICAL);
            customerList.addItemDecoration(dividerItemDecoration);

            Log.e("panjag", String.valueOf(listCustomers.size()));

            if (listCustomers.size() == 0) {
                Log.e("kosong", String.valueOf(listCustomers.size()));
                String notFound = getResources().getString(R.string.home_data_not_found_text);
                dataNotFound.setText(notFound.replace("nama", searchEditText.getText().toString()));
                dataNotFound.setVisibility(View.VISIBLE);
                dividerLayout.setVisibility(View.VISIBLE);
                customerList.setVisibility(View.GONE);
            } else {
                Log.e("isi", String.valueOf(listCustomers.size()));
                dataNotFound.setVisibility(View.GONE);
                dividerLayout.setVisibility(View.GONE);
                customerList.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResponseError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(this, DetailAct.class);
        intent.putExtra("custno", listCustomers.get(position).getCUSTNO());
        startActivity(intent);
    }
}
