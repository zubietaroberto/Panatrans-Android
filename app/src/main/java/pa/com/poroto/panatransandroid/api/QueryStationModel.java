package pa.com.poroto.panatransandroid.api;

import java.util.List;

/**
 * Created by zubietaroberto on 04/29/15.
 */
public class QueryStationModel {

    public String status;
    public StationData data;

    public static class StationData {

        public int id;
        public String name;
        public String URL;
        public List<Trip> trips;
    }

    public static class Trip{
        public int id;
        public String sequence;
        public int stop_id;
        public int trip_id;
    }

}