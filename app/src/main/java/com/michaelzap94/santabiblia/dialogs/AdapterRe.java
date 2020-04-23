//package com.michaelzap94.santabiblia.dialogs;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.michaelzap94.santabiblia.Bible;
//import com.michaelzap94.santabiblia.MainActivity;
//import com.michaelzap94.santabiblia.R;
//
//public class AdapterRe extends RecyclerView.Adapter<AdapterRe.MyViewHolder> {
//
//    private LayoutInflater inflater;
//    private String[] myImageNameList;
//
//
//    public AdapterRe(Context ctx, String[] myImageNameList){
//
//        inflater = LayoutInflater.from(ctx);
//        this.myImageNameList = myImageNameList;
//    }
//
//    @Override
//    public AdapterRe.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View view = inflater.inflate(R.layout.rv_layout, parent, false);
//        MyViewHolder holder = new MyViewHolder(view);
//
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(AdapterRe.MyViewHolder holder, int position) {
//
//        holder.name.setText(myImageNameList[position]);
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return myImageNameList.length;
//    }
//
//    class MyViewHolder extends RecyclerView.ViewHolder{
//        TextView name;
//        public MyViewHolder(View itemView) {
//            super(itemView);
//            name = (TextView) itemView.findViewById(R.id.name);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Bible.textView.setText("You have selected : "+myImageNameList[getAdapterPosition()]);
//                    Bible.dialog.dismiss();
//                }
//            });
//        }
//    }
//}
//
