package com.example.pdfviewer.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.pdfviewer.R;
import com.example.pdfviewer.View.PageFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<PageFragment> items = new ArrayList<>();
    private Context context;
    private String mainTitle = null;

    public PagerAdapter(Context context, FragmentManager fragmentManager){
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    public void addItem(PageFragment item){
        items.add(item);
        item.setPageNum(items.size());
        item.setMainTitle(mainTitle);
    }

    public void removeItem(ViewPager v, int pageNum){

        items.remove(pageNum);
        v.removeViewAt(pageNum);

        Log.v("ht", "removeItem() : " + pageNum);
    }

    @NonNull
    @Override
    public PageFragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    public void setMainTitle(String name){ this.mainTitle = name; }
}
