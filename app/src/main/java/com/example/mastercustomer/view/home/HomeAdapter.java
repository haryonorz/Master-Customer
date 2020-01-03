package com.example.mastercustomer.view.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mastercustomer.R;
import com.example.mastercustomer.repository.model.Customers;
import com.example.mastercustomer.utility.ImageUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CustomersViewHolder> {

    private final Activity activity;
    private final Context context;
    private final List<Customers> listCustomer;
    private final onClickListener listener;

    HomeAdapter(Activity activity, Context context, onClickListener listener, List<Customers> listCustomer){
        this.activity = activity;
        this.context = context;
        this.listener = listener;
        this.listCustomer = listCustomer;
    }

    @NonNull
    @Override
    public CustomersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_customer, parent, false);
        return new CustomersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomersViewHolder holder, int position) {
        holder.bind(listCustomer.get(position));
    }

    @Override
    public int getItemCount() {
        return listCustomer.size();
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

            layout.setOnClickListener(v -> listener.onItemClicked(getAdapterPosition()));
        }

        void bind(Customers customers) {
            ImageUtils.setImage(activity, context, loadingView, photoOutlet, customers.getFOTO_T());

            outletDistributorText.setText(customers.getKODECABANG_NAME());
            outletNameText.setText(customers.getCUSTNAME());
            outletAddressText.setText(customers.getCUSTADD1());
            locationNotExistButton.setVisibility(
                    (customers.getLA().equals(" ") || customers.getLA().equals("0.0")) &&
                            (customers.getLG().equals(" ") || customers.getLG().equals("0.0")) ?
                            View.VISIBLE : View.GONE);
        }
    }

    public interface onClickListener {

        void onItemClicked(int position);
    }
}
