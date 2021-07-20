package com.techdev.dcart.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.techdev.dcart.APICalls.FlightsRecyclerViewAdapter;
import com.techdev.dcart.APICalls.Flight;
import com.techdev.dcart.R;
import com.techdev.dcart.databinding.ActivityLoadDataBindTextBinding;

import java.util.ArrayList;
import java.util.List;

public class LoadDataBindText extends AppCompatActivity {

    private String TAG  =   LoadDataBindText.class.getSimpleName();

    public ActivityLoadDataBindTextBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_load_data_bind_text);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_load_data_bind_text);

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        binding.recyclerView.setLayoutManager(linearLayoutManager);

        binding.flightsRv.setLayoutManager(new LinearLayoutManager(this));
        binding.flightsRv.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        FlightsRecyclerViewAdapter adapter = new FlightsRecyclerViewAdapter(prepareData(), this);
        binding.flightsRv.setAdapter(adapter);


    }

    public List<Flight> prepareData(){

        List<Flight> flights = new ArrayList<>();

        Flight flight = new Flight("Delta", "Seattle", "London", "10:20", "17:30", "$388");
        flights.add(flight);
        flight = new Flight("Virgin Atlantic", "Seattle", "London", "10:20", "17:30", "$330");
        flights.add(flight);
        flight = new Flight("American Airlines", "Seattle", "London", "10:20", "17:30", "$400");
        flights.add(flight);
        flight = new Flight("British Airways", "Seattle", "London", "10:20", "17:30", "$440");
        flights.add(flight);
        flight = new Flight("Quatar Airways", "Seattle", "London", "10:20", "17:30", "$300");
        flights.add(flight);
        flight = new Flight("KLM", "Seattle", "London", "10:20", "17:30", "$350");
        flights.add(flight);
        flight = new Flight("Emirates", "Seattle", "London", "10:20", "17:30", "$420");
        flights.add(flight);
        flight = new Flight("Lufthansa","Seattle", "London", "10:20", "17:30", "$390");
        flights.add(flight);
        flight = new Flight("Air India", "Seattle", "London", "10:20", "17:30", "$350");
        flights.add(flight);
        flight = new Flight("Jet Airways", "Seattle", "London", "10:20", "17:30", "$390");
        flights.add(flight);
        flight = new Flight("United", "Seattle", "London", "10:20", "17:30", "$3450");
        flights.add(flight);
        flight = new Flight("Air Canada","Seattle", "London", "10:20", "17:30", "$398");
        flights.add(flight);

        return flights;
    }

}