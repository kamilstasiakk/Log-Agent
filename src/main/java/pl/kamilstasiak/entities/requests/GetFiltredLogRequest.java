package pl.kamilstasiak.entities.requests;

import java.util.List;

/**
 * Created by Kamil on 2016-11-09.
 */
public class GetFiltredLogRequest {
    String logName;
    String startTime;
    String endTime;
    String source;
    List<String> otherFieldsValues;
    List<String> otherFieldsNames;

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getOtherFieldsValues() {
        return otherFieldsValues;
    }

    public void setOtherFieldsValues(List<String> otherFieldsValues) {
        this.otherFieldsValues = otherFieldsValues;
    }

    public List<String> getOtherFieldsNames() {
        return otherFieldsNames;
    }

    public void setOtherFieldsNames(List<String> otherFieldsNames) {
        this.otherFieldsNames = otherFieldsNames;
    }

    public String getFilter() {
        StringBuilder filter = new StringBuilder();
        for(int i = 0; i < otherFieldsNames.size(); i++) {
            filter.append("AND ");
            filter.append(otherFieldsNames.get(i));
            filter.append("= \"");
            filter.append(otherFieldsValues.get(i));
            filter.append("\" ");
        }
        return filter.toString();
    }
}
