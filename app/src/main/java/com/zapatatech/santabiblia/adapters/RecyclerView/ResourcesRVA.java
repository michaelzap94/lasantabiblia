package com.zapatatech.santabiblia.adapters.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.models.Resource;
import com.zapatatech.santabiblia.utilities.CommonMethods;
import com.zapatatech.santabiblia.utilities.Util;

import java.util.List;

public class ResourcesRVA extends RecyclerView.Adapter<ResourcesRVA.ResourceViewHolder> {
    private static final String TAG = "ResourcesRVA";
    private List<Resource> resources;
    private Context context;

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

    public void updateResourceState(int updateIndex, String value) {
        Resource newResource = resources.get(updateIndex);
        newResource.setTemporalState(value);
        resources.set(updateIndex, newResource);
        //I have new data, delete everything and add new data
        notifyItemChanged(updateIndex);
    }

    public void updateResourceStateByName(String fileName, String value) {
        int updateIndex = findResourceIndexByName(fileName);
        if(updateIndex > -1){
            Resource newResource = resources.get(updateIndex);
            newResource.setTemporalState(value);
            resources.set(updateIndex, newResource);
            //I have new data, delete everything and add new data
            notifyItemChanged(updateIndex);
        }
    }

    public void updateResourceStateProgressByName(String fileName, int value) {
        int updateIndex = findResourceIndexByName(fileName);
        if(updateIndex > -1){
            Resource newResource = resources.get(updateIndex);
            newResource.setTemporalProgress(value);
            newResource.setTemporalState("processing");
            resources.set(updateIndex, newResource);
            //I have new data, delete everything and add new data
            notifyItemChanged(updateIndex);
        }
    }

    private int findResourceIndexByName(String fileName){
        for(int i = 0; i < resources.size(); i++) {
            if(resources.get(i).getFilename().equals(fileName)) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public ResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        //Inflate the CUSTOM layout
        View view = LayoutInflater.from(context).inflate(R.layout.resource_item, parent, false);
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
        TextView resourceInfo;
        TextView resourceState;
        ProgressBar resourceProgress;

        public ResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            resourceName = itemView.findViewById(R.id.fileName);
            resourceInfo = itemView.findViewById(R.id.fileSize);
            resourceState = itemView.findViewById(R.id.fileState);
            resourceProgress = (ProgressBar) itemView.findViewById(R.id.fileProgress);
        }

        void bind(Resource resource) {
            //State-------------------------------------------------------------------------------
            if(resource.getTemporalState() == "processing"){
                Log.d(TAG, "bind: processing " + resource.getTemporalProgress());
                resourceProgress.setVisibility(View.VISIBLE);
                resourceState.setText("Downloading...");
                resourceState.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.stat_sys_download_done, 0, 0, 0);
                resourceProgress.setProgress(resource.getTemporalProgress());
                resourceState.setClickable(false);
            } else if(resource.getTemporalState() == "completed") {
                Log.d(TAG, "bind: completed ");
                resourceProgress.setVisibility(View.GONE);
                resourceState.setText("Downloaded");
                resourceState.setTextColor(Color.GRAY);
                resourceState.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                resourceState.setClickable(false);
            } else {
                resourceProgress.setVisibility(View.GONE);
                boolean isInstalled = Util.isResourceFullyInstalled(context, resource.getFilename(), resource.getSize());
                if(isInstalled) {
                    Log.d(TAG, "bind: isInstalled " + resource.getFilename());
                    resourceState.setText("Downloaded");
                    resourceState.setTextColor(Color.GRAY);
                    resourceState.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                    resourceState.setClickable(false);
                } else {
                    Log.d(TAG, "bind: ELSE");
                    resourceState.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.stat_sys_download_done, 0, 0, 0);
                    resourceState.setText("Download");
                    resourceState.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            CommonMethods.startWorkManager((Activity) context, resource.getResource(), resource.getFilename());
                            resourceState.setClickable(false);
                            //resourceState.setText("Downloading...");
                        }
                    });
                }
            }
//                WorkInfo.State state = CommonMethods.getWorkState((Activity) context, resource.getFilename());
//                if(state == WorkInfo.State.RUNNING){
//                    resourceState.setText("Downloading...");
//                } else if(state == WorkInfo.State.ENQUEUED){
//                    resourceState.setText("Waiting...");
//                } else {
//                }


            //------------------------------------------------------------------------------------
            //Bind data to layout elements
            resourceName.setText(resource.getName());
            resourceInfo.setText(resource.getDescription());
        }
    }

}