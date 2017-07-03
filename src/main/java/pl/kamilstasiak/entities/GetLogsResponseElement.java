package pl.kamilstasiak.entities;

import java.util.List;

/**
 * Created by Kamil on 2016-11-04.
 */
public class GetLogsResponseElement {
    String logName;
    String source;
    String timestamp;
    String expandedField; // field representin other field of log
    int id;

    public GetLogsResponseElement(LogRecord logRecord, Log log) {
        StringBuilder expandedFieldSB = new StringBuilder();
        List<String> fieldsNames = log.getFields();
        List<String> fieldsValues = logRecord.getOtherFields();

        logName = logRecord.getLogName();
        source = logRecord.getSource();
        timestamp = logRecord.getTimestamp();
        id = logRecord.getId();

        for (int fieldIndex = 3; fieldIndex < fieldsNames.size(); fieldIndex++) {
            expandedFieldSB.append(fieldsNames.get(fieldIndex) + ": " + fieldsValues.get(fieldIndex-3) + ",  ");
        }
        if (fieldsNames.size() > 3) {
            expandedFieldSB.delete(expandedFieldSB.length() - 3,expandedFieldSB.length() - 1);
        }
        expandedField = expandedFieldSB.toString();
    }

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getExpandedField() {
        return expandedField;
    }

    public void setExpandedField(String expandedField) {
        this.expandedField = expandedField;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
