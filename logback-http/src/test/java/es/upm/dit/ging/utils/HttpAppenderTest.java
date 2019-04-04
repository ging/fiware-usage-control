package es.upm.dit.ging.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class HttpAppenderTest
{
	/**
	 * 

	 * 
	 *    mvn test -DX-Auth-Token="<my_access_token"
	 */
	@Test
	public void testLogDNALogbackConf() throws InterruptedException
	{
		
	    Logger logger = LoggerFactory.getLogger(HttpAppenderTest.class);

	    logger.info("Okay");
		Thread.sleep(2000);
	    logger.warn("Hmmm, that seemed odd...");
	    Thread.sleep(2000);
		logger.info("org.apache.flink.r.jobmaster.JobMaster dfgadg  adga Connecting to ResourceManager  -----------------------------------------------------2------------------------");
		Thread.sleep(2000);
		logger.info("org.apache.flink.runtime.jobmaster.JobMaster dfgadg  adga Connecting to ResourceManager  -----------------------------------------------------2------------------");
		Thread.sleep(2000);
		logger.info("org.fiware.cosmos.orion.flink.connector.OrionHttpHandler dfgadg  adga Connecting to ResourceManager  -----------------------------------------------------2----------------------");
		Thread.sleep(2000);
		logger.info("org.apache.flink.runtime.executiongraph.ExecutionGraph asdfasg asdfCREATED to SCHEDULED  -----------------------------------------------------2---------------------");
		Thread.sleep(2000);
		logger.info("org.apache.flink.runtime.jobmaster.JobManagerRunner  -----------------------------------------------------2---------------------");
		Thread.sleep(2000);
		logger.getName();
	}
}
