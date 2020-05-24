package com.example.readingdiary.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readingdiary.Classes.Directory;
import com.example.readingdiary.Classes.Note;
import com.example.readingdiary.R;
import com.example.readingdiary.Classes.RealNote;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;


// адаптер элементов каталога Note
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private boolean actionMode;
    private List<Note> notes;
    private OnItemClickListener mListener;
    private final int TYPE_ITEM1 = 0;
    private final int TYPE_ITEM2 = 1;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onItemLongClick(int position);
        void onCheckClick(int position);
        void onUncheckClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public RecyclerViewAdapter(List<Note> notes) {
        this.notes = notes;
        this.actionMode = false;
    }

    /**
     * Создание новых View и ViewHolder элемента списка, которые впоследствии могут переиспользоваться.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == TYPE_ITEM1){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_catalog_item0, viewGroup, false);
        }
        else{
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_catalog_item1, viewGroup, false);
        }
        ViewHolder vh = new ViewHolder(v);
//        v.setOnClickListener(this);
        return vh;
    }

    /**
     * Заполнение виджетов View данными из элемента списка с номером i
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);
        if (actionMode == false){
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        else{
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setChecked(false);
        }
        if (type == TYPE_ITEM1){

            RealNote realNote = (RealNote) notes.get(i);
//            viewHolder.path1.setText(realNote.getPath());
            viewHolder.author.setText(realNote.getAuthor());
            viewHolder.title.setText(realNote.getTitle());
            viewHolder.ratingBar.setRating((float)realNote.getRating());
//            if (actionMode == false){
//                viewHolder.checkBox.setVisibility(View.GONE);
//            }
//            else{
//                viewHolder.checkBox.setVisibility(View.VISIBLE);
//                viewHolder.checkBox.setChecked(false);
//            }
            if (!realNote.getCoverPath().equals("")){
                // TODO
            }
        }
        if (type == TYPE_ITEM2){
            Directory directory = (Directory) notes.get(i);
            viewHolder.path2.setText(directory.getDirectory());
        }

//
//
//        Note note = notes.get(i);
//        viewHolder.path.setText(note.getPath());
//        viewHolder.title.setText(note.getTitle());
//        viewHolder.author.setText(note.getAuthor());

    }

    @Override
    public int getItemViewType(int position) {
        // определяем какой тип в текущей позиции
        int type = notes.get(position).getItemType();
        if (type == 0) return TYPE_ITEM1;
        else return TYPE_ITEM2;

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



    public void clearAdapter() {
        notes.clear();
        notifyDataSetChanged();
    }

    public void setActionMode(boolean mode){
        actionMode = mode;
    }

    public void updateAdapter(ArrayList<Note> list){
        for (Note note : list){
            notes.remove(note);
        }
        notifyDataSetChanged();
    }

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */

    class ViewHolder extends RecyclerView.ViewHolder {
//        private TextView path1;
        private TextView path2;

        private TextView title;
        private TextView author;
        private ImageView cover;
        private CheckBox checkBox;
        private RatingBar ratingBar;
        private MaterialCardView cardView;
        private MaterialCardView cardView2;


//        private ImageView icon;

        public ViewHolder(final View itemView) {
            super(itemView);
//            path1 = (TextView) itemView.findViewById(R.id.catalogN);
            title = (TextView) itemView.findViewById(R.id.catalogNoteTitleView);
            author = (TextView) itemView.findViewById(R.id.catalogNoteAuthorView);
            cover = (ImageView) itemView.findViewById(R.id.catalogNoteImageView) ;
            ratingBar = (RatingBar) itemView.findViewById(R.id.catalogNoteRatingView);
            checkBox = (CheckBox) itemView.findViewById(R.id.catalogNoteCheckBox);
//            cardView = (CardView) itemView.findViewById(R.id.catalogNoteCardView);
            path2 = (TextView) itemView.findViewById(R.id.pathViewCatalog1);
            cardView = (MaterialCardView) itemView.findViewById(R.id.catalogNoteCardView);
            cardView2 = (MaterialCardView) itemView.findViewById(R.id.catalogDirectoryCardView);


            Log.d("toBeOrNotToBe", cardView + "! ");
            if (cardView != null){
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemLongClick(position);
                        }
                        return true;
                    }
                    return false;
                }

            });
            }

            if (checkBox != null){
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()){
                            mListener.onCheckClick(getAdapterPosition());
                        }
                        else{
                            mListener.onUncheckClick(getAdapterPosition());
                        }
                    }

                });
            }
            if (cardView2 != null){
                cardView2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mListener != null){
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION){
                                mListener.onItemLongClick(position);
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }

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

//            cardView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    if (mListener != null){
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION){
//                            mListener.onItemLongClick(position);
//                        }
//                        return true;
//                    }
//                    return false;
//                }
//
//            });
        }

    }



}
