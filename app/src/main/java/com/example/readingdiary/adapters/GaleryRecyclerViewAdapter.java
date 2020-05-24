package com.example.readingdiary.adapters;

//package com.example.galeryproject;

//package com.example.readingdiary;

//package com.example.readingdiary;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.readingdiary.Classes.ImageClass;
import com.example.readingdiary.Classes.SmallGaleryTransform;
import com.example.readingdiary.R;
import com.squareup.picasso.Picasso;

import java.util.List;

// Адаптер для GaleryActivity.
public class GaleryRecyclerViewAdapter extends RecyclerView.Adapter<GaleryRecyclerViewAdapter.ViewHolder>{

    private List<ImageClass> buttons;

    private GaleryRecyclerViewAdapter.OnItemClickListener mListener;
    private Context context;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public GaleryRecyclerViewAdapter(List<ImageClass> buttons, Context context) {
        this.buttons = buttons;
        Log.d("qwerty23", buttons.size()+"");
        this.context = context;
//        this.newImages = newImages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.galery_view_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
//        v.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (buttons.get(i).getType()==1){
            DisplayMetrics metricsB = context.getResources().getDisplayMetrics();
            float size = metricsB.widthPixels / 3;
            SmallGaleryTransform smallGaleryTransform = new SmallGaleryTransform(metricsB.widthPixels, metricsB.heightPixels);
            Picasso.get()
                    .load(buttons.get(i).getUri())
                    .transform(new SmallGaleryTransform(metricsB.widthPixels, metricsB.heightPixels))
                    .into(viewHolder.imageView);
        }
        else{
                    Bitmap source = buttons.get(i).getBitmap();
//        Display display = context.getResources().getDisplayMetrics()
        DisplayMetrics metricsB = context.getResources().getDisplayMetrics();
//        display.getMetrics(metricsB);
        float size = metricsB.widthPixels / 3;
        float size1 = Math.min(source.getWidth(), source.getHeight());
        float k = size / size1;
        Log.d("SCALE", k+" " + size + " " + size1);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, (int)(source.getWidth() * k), (int)(source.getHeight() * k), false);
        int x = (int)((resizedBitmap.getWidth() - size) / 2);
        int y = (int)((resizedBitmap.getHeight() - size) / 2);
        Bitmap result = Bitmap.createBitmap(resizedBitmap, x, y, (int)size, (int)size);

        viewHolder.imageView.setImageBitmap(result);
        }
////        viewHolder.imageView.setImageBitmap(buttons.get(i);
    }
//
//    @Override
//    public int getItemViewType(int position) {
//        // определяем какой тип в текущей позиции
//        int type = notes.get(position).getItemType();
//        if (type == 0) return TYPE_ITEM1;
//        else return TYPE_ITEM2;
//
//    }

    @Override
    public int getItemCount() {
        return buttons.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void clearAdapter() {
        buttons.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //        private TextView path1;
//        private TextView path2;
//
//        private TextView title;
//        private TextView author;
        private ImageView imageView;

//        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.galery_image_el0);
//            title = (TextView) itemView.findViewById(R.id.titleViewCatalog);
//            author = (TextView) itemView.findViewById(R.id.authorViewCatalog);
//            path2 = (TextView) itemView.findViewById(R.id.pathViewCatalog1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }


}



