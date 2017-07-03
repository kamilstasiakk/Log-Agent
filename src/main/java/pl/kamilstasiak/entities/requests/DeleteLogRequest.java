package pl.kamilstasiak.entities.requests;

/**
 * Created by Kamil on 2016-11-03.
 */
public class DeleteLogRequest {
    int id;
    String logName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }
}
