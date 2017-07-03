package pl.kamilstasiak.entities.requests;

import java.util.List;

/**
 * Created by Kamil on 2016-11-01.
 */
public class CreateNewLogTypeRequest {
    String logName;
    List<String> fieldsNames; //fields except id, source and time

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public List<String> getFieldsNames() {
        return fieldsNames;
    }

    public void setFieldsNames(List<String> fieldsNames) {
        this.fieldsNames = fieldsNames;
    }



}
