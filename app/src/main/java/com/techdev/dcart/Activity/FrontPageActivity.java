package com.techdev.dcart.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.techdev.dcart.Adapter.CategoryRecyclerViewAdapter;
import com.techdev.dcart.ModelClass.MainProducts;
import com.techdev.dcart.R;

import java.util.ArrayList;
import java.util.List;

public class FrontPageActivity extends AppCompatActivity {

    private String TAG = FrontPageActivity.class.getSimpleName();

    RecyclerView recyclerView;
    List<MainProducts> mainProductsList = new ArrayList<>();
    CategoryRecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        recyclerView    =   findViewById(R.id._front_recycle_list);

        //  https://stackoverflow.com/questions/48780647/set-image-from-drawable-to-recyclerview

        MainProducts products = new MainProducts();
        products.setName("Furniture");
        products.setImageName(R.drawable.single_chair_);

        MainProducts products1 = new MainProducts();
        products1.setName("Watches");
        products1.setImageName(R.drawable.gray_watch);

        MainProducts products2 = new MainProducts();
        products2.setName("Gadgets");
        products2.setImageName(R.drawable.mobile_one);

        MainProducts products3 = new MainProducts();
        products3.setName("Shirts");
        products3.setImageName(R.drawable.green_shirt);

        MainProducts products4 = new MainProducts();
        products4.setName("Mobiles");
        products4.setImageName(R.drawable.samsung_img);

        MainProducts products5 = new MainProducts();
        products5.setName("Shoes");
        products5.setImageName(R.drawable.shoes);

        mainProductsList.add(products);
        mainProductsList.add(products1);mainProductsList.add(products2);
        mainProductsList.add(products3);
        mainProductsList.add(products4);
        mainProductsList.add(products5);

        //Shape XMl File Link:
        //  https://betterprogramming.pub/shape-drawables-the-most-powerful-tool-for-your-android-ui-e5c2b1ab9eef

        recyclerViewAdapter = new CategoryRecyclerViewAdapter(FrontPageActivity.this,mainProductsList);
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setAdapter(recyclerViewAdapter);



    }


}