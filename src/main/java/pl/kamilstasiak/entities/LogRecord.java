package pl.kamilstasiak.entities;

import java.util.List;

/**
 * Created by Kamil on 2016-11-01.
 */
public class LogRecord {
    String logName;
    String source;
    String timestamp;
    int id;
    List<String> otherFields;

    public LogRecord(String logName, String source, String timestamp, List<String> otherFields, int id) {
        this.logName = logName;
        this.source = source;
        this.timestamp = timestamp;
        this.otherFields = otherFields;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getOtherFields() {
        return otherFields;
    }

    public void setOtherFields(List<String> otherFields) {
        this.otherFields = otherFields;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogTypeName(String logName) {
        this.logName = logName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


}
