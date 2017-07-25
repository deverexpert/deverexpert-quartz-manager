package kr.pe.db.quartz.scheduling;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.db.quartz.scheduling.job.SampleJob;

public class JobsListener implements ServletContextListener {
	
	private static final Logger logger = LoggerFactory.getLogger(JobsListener.class);

	// Initiate a Schedule Factory
	private static final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	// Retrieve a scheduler from schedule factory (Lazy init)
	private Scheduler scheduler = null;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			if (scheduler != null && scheduler.isStarted())
				scheduler.shutdown();
		} catch (SchedulerException e) {
			logger.error("", e);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		logger.info("Initializing JobsListener");

		try {
			scheduler = schedulerFactory.getScheduler();

			// Initiate JobDetail with job name, job group, and
			// executable job
			// class
			JobDetail jobDetail = JobBuilder.newJob(SampleJob.class).withIdentity("db_refresher", "refresher").build();

			// Initiate SimpleTrigger with its name and group name.
			/*
			 * SimpleTrigger trigger = TriggerBuilder .newTrigger()
			 * .withIdentity( TriggerKey
			 * .triggerKey("myTrigger","myTriggerGroup")) .withSchedule(
			 * SimpleScheduleBuilder.simpleSchedule()
			 * .withIntervalInHours(1).repeatForever())
			 * .startAt(DateBuilder.futureDate(5, IntervalUnit.MINUTE))
			 * .build();
			 */
			// 매 5초에 실행
			Trigger trigger = newTrigger().withIdentity("trigger1", "group1")
					.withSchedule(cronSchedule("0/5 * * * * ?")).build();

			// scheduler.scheduleJob(jobDetail, simpleTrigger);

			scheduler.scheduleJob(jobDetail, trigger);

			// Thread.sleep(60000);
			// scheduler.shutdown();
			// start the scheduler

			getScheduleList();

			scheduler.start();

		} catch (SchedulerException se) {
			logger.error("", se);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void getScheduleList() throws Exception {

		for (String groupName : scheduler.getJobGroupNames()) {

			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

				String jobName = jobKey.getName();
				String jobGroup = jobKey.getGroup();

				// get job's trigger
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				Date nextFireTime = triggers.get(0).getNextFireTime();

				logger.info("[groupName] : " + jobGroup + "[jobName] : " + jobName + " - " + nextFireTime);

			}

		}
	}
}
