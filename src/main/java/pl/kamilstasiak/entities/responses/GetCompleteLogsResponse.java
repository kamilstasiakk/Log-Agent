package pl.kamilstasiak.entities.responses;

import pl.kamilstasiak.entities.GetLogsResponseElement;
import pl.kamilstasiak.entities.LogRecord;
import pl.kamilstasiak.entities.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2016-11-01.
 */
public class GetCompleteLogsResponse {

    List<GetLogsResponseElement> logs;


    public GetCompleteLogsResponse(List<LogRecord> logRecords, List<Log> logs) {
        this.logs = new ArrayList<>();
        for (LogRecord logRecord : logRecords) {
            for (Log log : logs) {
                if ( logRecord.getLogName().equals(log.getName()) ) {
                    this.logs.add(new GetLogsResponseElement(logRecord, log));
                    break;
                }

            }
        }
    }

    public List<GetLogsResponseElement> getLogs() {
        return logs;
    }

    public void setLogs(List<GetLogsResponseElement> logs) {
        this.logs = logs;
    }
}
