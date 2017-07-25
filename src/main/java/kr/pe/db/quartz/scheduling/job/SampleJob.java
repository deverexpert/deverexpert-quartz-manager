package kr.pe.db.quartz.scheduling.job;

import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleJob implements Job {
	
	private static final Logger logger = LoggerFactory.getLogger(SampleJob.class);
	
	// This is the method that will be called by the
	// scheduler when the trigger fires the job.
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// Do you scheduled task here. This is usually the repetitive
		// piece that runs for every firing of the trigger.
		Calendar date = Calendar.getInstance();
		String stamp = date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + ":"
				+ date.get(Calendar.SECOND) + ":" + date.get(Calendar.MILLISECOND);
		logger.info(stamp + " " + "Generating report");
	}
}