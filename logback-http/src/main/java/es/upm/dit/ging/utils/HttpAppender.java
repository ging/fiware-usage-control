package es.upm.dit.ging.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.goebl.david.Response;
import com.goebl.david.Webb;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;


public class HttpAppender extends UnsynchronizedAppenderBase<ILoggingEvent>
{
	/** relying on the guard in the parent implementation to prevent recursive calls, hoping there is another ERROR destination */
	Logger emergencyLog = LoggerFactory.getLogger(es.upm.dit.ging.utils.HttpAppender.class);
	private String hostname;
	private Webb http;
	/*
	 * configurables ( plus ingestKey )
	 */
	private String host;
	private String port;
	private boolean includeStacktrace = true;
	private String filt;

	public HttpAppender() {
		try {
			this.hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			this.hostname = "localhost";
		}

		Webb webb = Webb.create();
		this.http = webb;
	}
	
	@Override
	protected void append(ILoggingEvent ev) 
	{
		Filter filterObj = new Filter(filt);
		if (!isNullOrEmpty(filt)&&filterObj.applyFilter(ev)) {
			/* not interested in consuming our own filth */
			if (ev.getLoggerName().equals(HttpAppender.class.getName())) {
				return;
			}

			StringBuilder sb = new StringBuilder()
					.append(ev.getLoggerName() + "  ")
					.append(ev.getFormattedMessage());

			if (ev.getThrowableProxy() != null && this.includeStacktrace) {
				IThrowableProxy tp = ev.getThrowableProxy();
				sb.append("\n\n").append(tp.getClassName()).append(": ").append(tp.getMessage());
				for (StackTraceElementProxy ste : tp.getStackTraceElementProxyArray()) {
					sb.append("\n\t").append(ste.getSTEAsString());
				}
			}

			try {
				JSONObject payload = new JSONObject();
				payload.put("message", sb.toString());
				payload.put("timestamp", ev.getTimeStamp());
				payload.put("level", ev.getLevel().toString());
				payload.put("host", this.hostname);

				System.out.println("Sending data to: " + host + " port: " + port +"  "+ payload.toString() + "···········");

				Response<JSONObject> response = http.post("http://" + host + ":" + port)
						.body(payload)
						.asJsonObject();

				if (!response.isSuccess()) {
					String msg = "Error posting to CEP ";
					msg += response.getStatusCode() + " ";
					try {
						msg += response.getStatusLine();
					} catch (Exception e) {
					}
					emergencyLog.error(msg);
				}
			} catch (JSONException e) {
				emergencyLog.error(e.getMessage(), e);
			}
		}
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setAccessToken(String accessToken) {
		this.http.setDefaultHeader("X-Auth-Token", accessToken);
	}

	public void setIncludeStacktrace(boolean includeStacktrace) {
		this.includeStacktrace = includeStacktrace;
	}

	public void setFilt(String filt) {
		this.filt = filt;
	}

	public static boolean isNullOrEmpty(String str) {
		if(str != null && !str.isEmpty())
			return false;
		return true;
	}

	private static String encode(String str) {
        try 
        {
            return URLEncoder.encode(str, "UTF-8");
        } 
        catch (UnsupportedEncodingException e) 
        {
            return str;
        }
    }
}
