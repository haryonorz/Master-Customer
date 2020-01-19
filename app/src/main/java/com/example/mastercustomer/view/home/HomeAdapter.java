package com.example.mastercustomer.view.home;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mastercustomer.R;
import com.example.mastercustomer.repository.model.Customers;
import com.example.mastercustomer.utility.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private boolean mIsLoadingAdded = false;

    private final Activity activity;
    private final Context context;
    private final List<Customers> listCustomer;
    private final onClickListener listener;

    HomeAdapter(Activity activity, Context context, onClickListener listener){
        this.activity = activity;
        this.context = context;
        this.listener = listener;
        this.listCustomer = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View view = inflater.inflate(R.layout.view_item_loading_footer, parent, false);
                viewHolder = new LoadingViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater){
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_customer, parent, false);
        RecyclerView.ViewHolder viewHolder;
        View view1= inflater.inflate(R.layout.view_item_customer, parent, false);
        viewHolder = new CustomersViewHolder(view1);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case ITEM:
                CustomersViewHolder holder1 = (CustomersViewHolder) holder;
                holder1.bind(listCustomer.get(position));
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listCustomer == null ? 0 : listCustomer.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == listCustomer.size() - 1 && mIsLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    private void add(Customers r) {
        listCustomer.add(r);
        notifyItemInserted(listCustomer.size() - 1);
    }

    void addAll(List<Customers> orderList) {
        for (Customers result : orderList) {
            add(result);
        }
    }

    private void remove(Customers r) {
        int position = listCustomer.indexOf(r);
        if (position > -1) {
            listCustomer.remove(position);
            notifyItemRemoved(position);
        }
    }

    void clear() {
        mIsLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    void addLoadingFooter() {
        mIsLoadingAdded = true;
        add(new Customers());
    }

    void removeLoadingFooter() {
        mIsLoadingAdded = false;

        int position = listCustomer.size() - 1;
        Customers order = getItem(position);

        if (order != null) {
            listCustomer.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Customers getItem(int position) {
        return listCustomer.get(position);
    }



    class CustomersViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout) View layout;
        @BindView(R.id.image_view_outlet) ImageView photoOutlet;
        @BindView(R.id.progress_bar_loading) View loadingView;
        @BindView(R.id.text_view_coutlet_distributors) TextView outletDistributorText;
        @BindView(R.id.text_view_coutlet_name) TextView outletNameText;
        @BindView(R.id.text_view_outlet_address) TextView outletAddressText;
        @BindView(R.id.button_location_not_exist) Button locationNotExistButton;

        CustomersViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            layout.setOnClickListener(v -> listener.onItemClicked(listCustomer.get(getAdapterPosition())));
        }

        void bind(Customers customers) {
            ImageUtils.setImage(activity, context, loadingView, photoOutlet, customers.getFOTO_T());
            if (customers.getFOTO_T() != null)
                Log.e("foto", customers.getFOTO_T());

            outletDistributorText.setText(customers.getKODECABANG_NAME());
            outletNameText.setText(customers.getCUSTNAME());
            outletAddressText.setText(customers.getCUSTADD1());
            locationNotExistButton.setVisibility(
                    (customers.getLA().equals("") || customers.getLA().equals(" ") || customers.getLA().equals("0.0")) &&
                            (customers.getLG().equals("") || customers.getLG().equals(" ") || customers.getLG().equals("0.0")) ?
                            View.VISIBLE : View.GONE);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface onClickListener {

        void onItemClicked(Customers customers);
    }
}
