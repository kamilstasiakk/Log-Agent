package pl.kamilstasiak.entities;

import java.util.List;

/**
 * Created by Kamil on 2016-10-31.
 */
public class Log {
    private String name;
    private List<String> fields;
    private String source;

    public Log(String name, List<String> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getOwner() {
        return source;
    }

    public void setOwner(String owner) {
        this.source = owner;
    }
}
