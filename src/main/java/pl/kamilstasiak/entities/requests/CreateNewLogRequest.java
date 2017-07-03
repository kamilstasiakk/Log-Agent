package pl.kamilstasiak.entities.requests;

import java.util.List;

/**
 * Created by Kamil on 2016-11-01.
 */
public class CreateNewLogRequest {
    String logName;
    String source;
    String time;
    List<String> otherFields;
//todo ewentualnie dodac time zeby klient mogl go ustawiac


    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getOtherFields() {
        return otherFields;
    }

    public void setOtherFields(List<String> otherFields) {
        this.otherFields = otherFields;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
