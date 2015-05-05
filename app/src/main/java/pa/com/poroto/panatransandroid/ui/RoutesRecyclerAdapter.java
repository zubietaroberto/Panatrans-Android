package pa.com.poroto.panatransandroid.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pa.com.poroto.panatransandroid.api.QueryStationModel;

/**
 * Created by zubietaroberto on 04/29/15.
 */
public class RoutesRecyclerAdapter extends RecyclerView.Adapter<RoutesRecyclerAdapter.RouteHolder> {

    private final ArrayList<QueryStationModel.Route> mRouteList = new ArrayList<>();

    @Override
    public RouteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final TextView view = (TextView) LayoutInflater
                .from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);


        return new RouteHolder(view);
    }

    @Override
    public void onBindViewHolder(RouteHolder holder, int position) {
        holder.bind(mRouteList.get(position));
    }

    @Override
    public int getItemCount() {
        return mRouteList.size();
    }

    public void setupAdapter(List<QueryStationModel.Route> pTripList){
        mRouteList.clear();
        mRouteList.addAll(pTripList);
        this.notifyDataSetChanged();
    }

    static public class RouteHolder extends RecyclerView.ViewHolder{

        final TextView mTextView;

        public RouteHolder(TextView itemView) {
            super(itemView);

            mTextView = itemView;
        }

        public void bind(QueryStationModel.Route pRoute){
            mTextView.setText(pRoute.name);
        }


    }
}
