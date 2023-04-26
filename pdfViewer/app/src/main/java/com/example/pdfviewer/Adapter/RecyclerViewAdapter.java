package com.example.pdfviewer.Adapter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdfviewer.ItemTouchHelperListener;
import com.example.pdfviewer.Model.Book;

import com.example.pdfviewer.Model.MyDatabase;
import com.example.pdfviewer.Model.Repository;
import com.example.pdfviewer.R;
import com.example.pdfviewer.View.MainActivity;


import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.BookHolder> implements ItemTouchHelperListener {

    public interface OnItemClickListener{
        void onClick(Book book);
    }

    private ArrayList<Book> items = new ArrayList<>();
    private Context mContext;
    private Repository mRepo;
    private OnItemClickListener listener;

    public RecyclerViewAdapter(ArrayList<Book> items){
        this.items = items;
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder (@NonNull ViewGroup parent, int i){

        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.recycler_layout, parent, false);
        // getApplication()는 getApplicationContext()로 대체 가능.
        mRepo = new Repository((Application) mContext.getApplicationContext());

        return new BookHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {

        Book book = items.get(position);

        holder.tvItemTitle.setText(book.getTitle());
        //holder.tvItemPageNum.setText(book.getPageNum());
        holder.itemView.setOnClickListener(view -> listener.onClick(book));
    }

    public void setClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Book item){
        items.add(item);
        notifyDataSetChanged();
    }

    // 로~옹 클릭 앤 드랍으로 아이템 위치 변경.
    @Override
    public boolean onItemMove(int from_position, int to_position) {
        //이동할 객체 저장
        Book book = items.get(from_position);
        //이동할 객체 삭제
        items.remove(from_position);
        //이동하고 싶은 position에 추가
        items.add(to_position,book);

        //Adapter에 데이터 이동알림
        notifyItemMoved(from_position,to_position);
        return true;
    }

    // 스와이프 해서 아이템 제거.
    @Override
    public void onItemSwipe(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);


        mRepo.deleteData(items.get(position));
        items.remove(position);

        notifyItemRemoved(position);
    }

    class BookHolder extends RecyclerView.ViewHolder {

        TextView tvItemTitle;
        TextView tvItemPageNum;

        BookHolder(View itemView){
            super(itemView);

            tvItemTitle = (TextView) itemView.findViewById(R.id.recy_Tv_title);
            tvItemPageNum = (TextView) itemView.findViewById(R.id.recy_Tv_pageNum);

        }
    }
}
