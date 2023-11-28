package test.elasticsearch.model.session;

import java.util.ArrayList;
import java.util.List;

public class SessionResults {
    private List<Session> data;
    private long recordsTotal;
    private long recordsFiltered;

    public List<Session> getData() {
        return data;
    }
    public void addData(Session session) {
        if (data == null) {
            data = new ArrayList<>();
        }
        data.add(session);
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }
    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }
    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

}
