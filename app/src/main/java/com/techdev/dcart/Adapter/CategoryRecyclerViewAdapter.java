package com.techdev.dcart.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techdev.dcart.Activity.ImageUploadMultipartActivity;
import com.techdev.dcart.Activity.LoadDataBindText;
import com.techdev.dcart.ModelClass.MainProducts;
import com.techdev.dcart.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.listitemViewHolder> {

    Activity context;
    List<MainProducts> arrayLists = new ArrayList<>();

  //  List<ToolBox_Invitation_listResponseModel.InvdataList> arrayLists = new ArrayList<ToolBox_Invitation_listResponseModel.InvdataList>();

    public CategoryRecyclerViewAdapter(Activity context, List<MainProducts> listDetails) {

        this.context        = context;
        this.arrayLists     = listDetails;
    }

    @NonNull
    @Override
    public listitemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemview = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.dash_list_item_rv, viewGroup, false);
        return new listitemViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull listitemViewHolder listitemViewHolder, int i) {

        MainProducts mainProductsList = arrayLists.get(i);

        Random r    = new Random();
        int red     =   r.nextInt(255 - 0 + 2)+0;
        int green   =   r.nextInt(255 - 0 + 2)+0;
        int blue    =   r.nextInt(255 - 0 + 2)+0;

        GradientDrawable draw = new GradientDrawable();
        draw.setShape(GradientDrawable.RECTANGLE);
        draw.setColor(Color.rgb(red,green,blue));
        listitemViewHolder.invite_item.setBackground(draw);

        Glide.with(context).load(mainProductsList.getImageName())
                .circleCrop()
                .into(listitemViewHolder.imageView);

        listitemViewHolder.invite_id.setText(mainProductsList.getName());


        listitemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PreferenceConnector.writeString(context, PreferenceConnector.inviteId, model.getId());
                Intent i = new Intent(context, LoadDataBindText.class);
                //Intent i = new Intent(context, ImageUploadMultipartActivity.class);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayLists.size();
    }

    public final class listitemViewHolder extends RecyclerView.ViewHolder {

        TextView invite_id;
        ImageView imageView;
        RelativeLayout invite_item;
        MainProducts mainProducts;

        public listitemViewHolder(@NonNull View itemView) {
            super(itemView);

            invite_id           = (TextView) itemView.findViewById(R.id.txt_display_name);
            imageView           =   itemView.findViewById(R.id.image_apply__);
            invite_item         = (RelativeLayout) itemView.findViewById(R.id.back_rl);

        }
    }
}