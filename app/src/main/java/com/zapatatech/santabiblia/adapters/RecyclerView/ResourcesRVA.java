package com.zapatatech.santabiblia.adapters.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.models.Resource;

import java.util.List;

public class ResourcesRVA extends RecyclerView.Adapter<ResourcesRVA.ResourceViewHolder> {

    private List<Resource> resources;

    //initial data when this Adapter is instatiated
    public ResourcesRVA(List<Resource> resources) {
        this.resources = resources;
    }

    //function available so View can update the RecyclerView List once the information is available.
    public void updateResources(List<Resource> newResources) {
        resources.clear();
        resources.addAll(newResources);
        //I have new data, delete everything and add new data
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the CUSTOM layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resource_item, parent, false);
        return new ResourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResourceViewHolder holder, int position) {
        //call the .bind() custom methond in the ViewHolder class to BIND THE ViewHolder data at this position to the Layout
        holder.bind(resources.get(position));
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    class ResourceViewHolder extends RecyclerView.ViewHolder {

        TextView resourceName;
        TextView resourceSize;

        public ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            resourceName = itemView.findViewById(R.id.fileName);
            resourceSize = itemView.findViewById(R.id.fileSize);
        }

        void bind(Resource resource) {
            //Bind data to layout elements
            resourceName.setText(resource.getName());
            resourceSize.setText(resource.getSize());
            //Util.loadImage(resourceImage, resource.getFlag(), Util.getProgressDrawable(resourceImage.getContext()));
        }
    }
}