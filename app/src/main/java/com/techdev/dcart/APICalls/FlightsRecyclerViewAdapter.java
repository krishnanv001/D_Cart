package com.techdev.dcart.APICalls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.techdev.dcart.BR;
import com.techdev.dcart.R;
import com.techdev.dcart.databinding.FlightItemLayoutBinding;

import java.util.List;

public class FlightsRecyclerViewAdapter extends RecyclerView.Adapter<FlightsRecyclerViewAdapter.ViewHolder>
        implements FlightsEventListener {

    private List<Flight> flightsList;
    private Context context;

    public FlightsRecyclerViewAdapter(List<Flight> flsLst, Context ctx){
        flightsList = flsLst;
        context = ctx;
    }

    @Override
    public FlightsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        FlightItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.flight_item_layout, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Flight flight = flightsList.get(position);
        holder.flightItemBinding.setFlight(flight);

        holder.bind(flight);

        holder.flightItemBinding.setItemClickListener(this);
    }

    @Override
    public int getItemCount() {
        return flightsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public FlightItemLayoutBinding flightItemBinding;

        public ViewHolder(FlightItemLayoutBinding flightItemLayoutBinding) {
            super(flightItemLayoutBinding.getRoot());
            this.flightItemBinding = flightItemLayoutBinding;
        }

        public void bind(Object obj) {
            flightItemBinding.setVariable(BR.flight, obj);
            flightItemBinding.executePendingBindings();
        }

    }

    public void bookFlight(Flight f){

        Toast.makeText(context, "You booked "+f, Toast.LENGTH_LONG).show();
    }
}