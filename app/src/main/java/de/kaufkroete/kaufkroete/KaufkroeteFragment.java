package de.kaufkroete.kaufkroete;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.ArrayList;


public class KaufkroeteFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_default, container, false);
    }


    public CardView createCardView() {
        CardView c2 = new CardView(getActivity());
        AbsListView.LayoutParams c2params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT
        );
        c2.setLayoutParams(c2params);
        c2.setUseCompatPadding(true);
        c2.setContentPadding(toPixels(16), toPixels(16), toPixels(16), toPixels(16));
        return c2;
    }

    public int toPixels(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale);
    }

    class ViewHolder {
        ArrayList<View> view = new ArrayList<>();
        ArrayList<KKData> content = new ArrayList<>();
        LayoutInflater layout_inflater;
        boolean use_cache;
    }
}