package pa.com.poroto.panatransandroid.api;

import java.util.ArrayList;

/**
 * Created by zubietaroberto on 04/29/15.
 */
public class QueryStationListModel {

    public String status;
    public ArrayList<Station> data;

    public static class Station {
            public String id;
            public String name;
            public double lat;
            public double lon;
    }
}
