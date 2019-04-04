package es.upm.dit.ging.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filter {
    private String filter;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Filter(String filter) {
        this.filter = filter;
    }

    public boolean applyFilter(ILoggingEvent ev){
        boolean b= false;
        String message = ev.getLoggerName()+" "+ev.getFormattedMessage();
        switch (this.filter) {
            case "flink":
                Pattern p = Pattern.compile("(?:org.apache.flink.runtime.jobmaster.JobManagerRunner)(.*?)"+
                "|(?:org.apache.flink.runtime.executiongraph.ExecutionGraph .*CREATED to SCHEDULED)(.*?)"+
                "|(?:org.apache.flink.runtime.executiongraph.ExecutionGraph .*CREATED to SCHEDULED)(.*?)" +
                        "|(?:org.fiware.cosmos.orion.flink.connector.OrionHttpHandler)(.*?)" +
                        "|(?:org.apache.flink.runtime.jobmaster.JobMaster .*Connecting to ResourceManager)(.*?)"+
                        "|(?:org.fiware.cosmos.orion.flink.connector.OrionHttpHandler)(.*?)");
                Matcher m = p.matcher(message);
                b = m.find();
                return b;
            case "nifi":

                break;

            default:
                break;
        }
        return b;
    }
}
