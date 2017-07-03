package pl.kamilstasiak.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.kamilstasiak.entities.LogRecord;
import pl.kamilstasiak.entities.Log;
import pl.kamilstasiak.entities.requests.CreateNewLogRequest;
import pl.kamilstasiak.entities.requests.CreateNewLogTypeRequest;
import pl.kamilstasiak.entities.requests.DeleteLogRequest;
import pl.kamilstasiak.entities.requests.GetFiltredLogRequest;
import pl.kamilstasiak.entities.responses.GetCompleteLogsResponse;
import pl.kamilstasiak.repositories.LogsRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Kamil on 2016-11-01.
 */
@Controller
@RequestMapping("logs")
public class LogController {

    private LogsRepository logsRepository;

    public LogController() {
        this.logsRepository = new LogsRepository();
    }


    @RequestMapping(value = "/addLog", method = RequestMethod.POST)
    public ResponseEntity<String> addLog(@RequestBody CreateNewLogTypeRequest createNewLogTypeRequest) {
        switch (logsRepository.createNewLog(createNewLogTypeRequest.getLogName(),createNewLogTypeRequest.getFieldsNames())) {
            case ERROR: return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
            case OK: return new ResponseEntity<String>("Dodano typ logu", HttpStatus.CREATED);
            case NAME_OCCUPIED: return new ResponseEntity<String>("NAME_OCCUPIED", HttpStatus.CONFLICT);
            default: return new ResponseEntity<String>("Dodano typ logu", HttpStatus.CREATED);
        }


    }

    @RequestMapping(value = "/addLogRecord", method = RequestMethod.POST)
    public ResponseEntity<String> addLogRecord(@RequestBody CreateNewLogRequest createNewLogRequest) {
        switch (logsRepository.createNewLogRecord(createNewLogRequest.getLogName(),createNewLogRequest.getSource(),createNewLogRequest.getTime(),createNewLogRequest.getOtherFields())) {
            case ERROR: return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
            case OK: return new ResponseEntity<String>("Dodano log", HttpStatus.CREATED);
            case LOG_TYPE_DOESNT_EXIST: return new ResponseEntity<String>("LOG_TYPE_DOESNT_EXIST", HttpStatus.UNPROCESSABLE_ENTITY);
            default: return new ResponseEntity<String>("Dodano log", HttpStatus.CREATED);
        }
    }
    @RequestMapping(value = "/getLogRecords", method = RequestMethod.POST)
    public ResponseEntity<List<LogRecord>> getLogRecords(@RequestBody GetFiltredLogRequest getFiltredLogRequest) {
        return new ResponseEntity<List<LogRecord>>(logsRepository.getLogRecords(getFiltredLogRequest.getLogName(),getFiltredLogRequest.getStartTime(),
                getFiltredLogRequest.getEndTime(),getFiltredLogRequest.getSource(),getFiltredLogRequest.getFilter()), HttpStatus.CREATED);

    }

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<LogRecord> getAllLogsRecords() {

        return logsRepository.getLogsRecords(null,"2000-01-01",new Date().toString(),null);
    }

    @RequestMapping(value = "/period/{startDate}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody ResponseEntity<List<LogRecord>> getLogsRecordsFromDate(@PathVariable String startDate) {

        return new ResponseEntity<List<LogRecord>>(logsRepository.getLogsRecords(null,startDate,new Date().toString(),null), HttpStatus.OK);

    }
    @RequestMapping(value = "/period/{startDate}/{endDate}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<LogRecord> getLogsRecordsWithinPeriod(@PathVariable String startDate, @PathVariable String endDate) {

        return logsRepository.getLogsRecords(null,startDate,endDate,null);
    }


    @RequestMapping(value = "/source/{source}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<LogRecord> getLogsRecordsFilteredBySource(@PathVariable String source) {

        return logsRepository.getLogsRecords(source,"2000-01-01", new Date().toString(),null);
    }

    @RequestMapping(value = "/source/{source}/{startDate}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<LogRecord> getLogsRecordsFilteredBySourceAndDate(@PathVariable String source, @PathVariable String startDate) {

        return logsRepository.getLogsRecords(source,startDate, new Date().toString(),null);

    }

    @RequestMapping(value = "/source/{source}/{startDate}/{endDate}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<LogRecord> getLogsRecordsFilteredBySourceAndDate(@PathVariable String source, @PathVariable String startDate, @PathVariable String endDate) {

        return logsRepository.getLogsRecords(source,startDate, endDate,null);
    }


    @RequestMapping(value = "/complete/period/{startDate}/{endDate}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody GetCompleteLogsResponse getCompleteResponse(@PathVariable String startDate,@PathVariable String endDate) {

        List<LogRecord> logRecordList = logsRepository.getLogsRecords(null,startDate,endDate,null);
        List<Log> logs = findAllTypesOfLogs(logRecordList);
        return new GetCompleteLogsResponse(logRecordList, logs);
    }

    @RequestMapping(value = "/complete/period/{startDate}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody GetCompleteLogsResponse getCompleteResponse(@PathVariable String startDate) {

        List<LogRecord> logRecordList = logsRepository.getLogsRecords(null,startDate,new Date().toString(),null);
        List<Log> logs = findAllTypesOfLogs(logRecordList);
        return new GetCompleteLogsResponse(logRecordList, logs);
    }


    @RequestMapping(value = "/complete/source/{source}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody GetCompleteLogsResponse getCompleteResponseFilteredBySource(@PathVariable String source) {

        List<LogRecord> logRecordList = logsRepository.getLogsRecords(source,"2000-01-01", new Date().toString(),null);
        List<Log> logs = findAllTypesOfLogs(logRecordList);
        return new GetCompleteLogsResponse(logRecordList, logs);
    }

    @RequestMapping(value = "/complete/source/{source}/{startDate}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody GetCompleteLogsResponse getCompleteResponseFilteredBySourceAndDate(@PathVariable String source, @PathVariable String startDate) {

        List<LogRecord> logRecordList = logsRepository.getLogsRecords(source,startDate, new Date().toString(),null);
        List<Log> logs = findAllTypesOfLogs(logRecordList);
        return new GetCompleteLogsResponse(logRecordList, logs);
    }

    @RequestMapping(value = "/complete/source/{source}/{startDate}/{endDate}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody GetCompleteLogsResponse getCompleteResponseFilteredBySourceAndDate(@PathVariable String source, @PathVariable String startDate, @PathVariable String endDate) {

        List<LogRecord> logRecordList = logsRepository.getLogsRecords(source,startDate, endDate,null);
        List<Log> logs = findAllTypesOfLogs(logRecordList);
        return new GetCompleteLogsResponse(logRecordList, logs);
    }

    private List<Log> findAllTypesOfLogs(List<LogRecord> logRecordList) {
        List<String> addedTypes = new ArrayList<>();
        List<Log> logs = new ArrayList<>();
        for (LogRecord logRecord : logRecordList) {
            if(addedTypes.contains(logRecord.getLogName())) {
                continue;
            } else {
                logs.add(logsRepository.getLog(logRecord.getLogName()));
                addedTypes.add(logRecord.getLogName());
            }
        }
        return logs;
    }

    @RequestMapping(value = "/logType/{logName}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Log getLogType(@PathVariable String logName) {

        return logsRepository.getLog(logName);
    }


    @RequestMapping(value = "/logRecord", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity<String> getLogsFromDate(@RequestBody DeleteLogRequest deleteLogRequest) {
        if(logsRepository.deleteLogRecord(deleteLogRequest.getLogName(),deleteLogRequest.getId())) {
            return new ResponseEntity<String>("", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
