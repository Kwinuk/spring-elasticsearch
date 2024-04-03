package com.xcurenet.common.schedule.service;

import com.xcurenet.common.schedule.service.JobVO;
import com.xcurenet.common.util.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class QuartzCronTrigger {

	private final static String JOB_GROUP = "XCN_FULL_PACKET_INTERLOCKING";

	public boolean putJob(final String jobKey, final Class<?> jobClass, final String cronExp, final String description) {
		try {

			deleteJob(jobKey);

			log.info("[ADD JOB] add job {} {} {}", jobKey, cronExp, description);
			CronTrigger trigger = TriggerBuilder.newTrigger().withDescription(cronExp).withIdentity(jobKey, JOB_GROUP).withSchedule(CronScheduleBuilder.cronSchedule(cronExp)).withDescription(cronExp).build();

			JobKey jk = new JobKey(jobKey, JOB_GROUP);
			@SuppressWarnings("unchecked")
			JobDetail job = JobBuilder.newJob((Class<? extends Job>) jobClass).withIdentity(jk).storeDurably().withDescription(description).build();

			SchedulerFactory schedFact = new StdSchedulerFactory();
			Scheduler scheduler = schedFact.getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, trigger);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String runJob(JobVO vo) {
		String rsMsg = "";
		try {
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			for (String groupName : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					if (Common.isEquals(jobKey.getName(), vo.getJobId())) {
						JobDetail jd = scheduler.getJobDetail(jobKey);
						scheduler.scheduleJob(TriggerBuilder.newTrigger().withIdentity("now_execute", JOB_GROUP).startNow().forJob(jd).build());
						return rsMsg;
					}
				}
			}
			return "00";//"JOB ID를 찾을 수 없습니다.(삭제 되었거나 JOB이 등록되지 않았습니다.)";
		} catch (Exception e) {
			e.printStackTrace();
			return "01";//"JOB 실행 도중 에러가 발생 하였습니다.";
		}
	}

	public List<JobVO> getJobList() throws Exception {
		List<JobVO> result = new ArrayList<>();
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		for (String groupName : scheduler.getJobGroupNames()) {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				String jobName = jobKey.getName();
				String jobGroup = jobKey.getGroup();

				JobDetail jd = scheduler.getJobDetail(jobKey);
				// System.out.println(jd.getJobDataMap().getKeys().length);
				// System.out.println(jd.getJobDataMap().get("result"));

				@SuppressWarnings("unchecked")
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);

				Date nextFireTime = triggers.get(0).getNextFireTime();
				Date startTime = triggers.get(0).getStartTime();
				Date previousFireTime = triggers.get(0).getPreviousFireTime();

				JobVO vo = new JobVO();
				vo.setJobGroup(jobGroup);
				vo.setJobId(jobName);
				vo.setCronExp(triggers.get(0).getDescription());
				vo.setDescription(jd.getDescription());
				vo.setJobClass(jd.getJobClass().getName());
				if (nextFireTime != null) vo.setNextFireTime(Common.getDateTime(nextFireTime.getTime()));
				if (startTime != null) vo.setStartTime(Common.getDateTime(startTime.getTime()));
				if (previousFireTime != null) vo.setPreviousFireTime(Common.getDateTime(previousFireTime.getTime()));
				result.add(vo);
			}
		}
		return result;
	}

	public JobVO getScheduleJob(String jobId) throws Exception {
		JobVO resultVo = new JobVO();
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();

		for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(JOB_GROUP))) {
			String jobName = jobKey.getName();
			if(StringUtils.equals(jobName, jobId)) {
				JobDetail jd = scheduler.getJobDetail(jobKey);

				@SuppressWarnings("unchecked")
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);

				Date nextFireTime = triggers.get(0).getNextFireTime();
				Date startTime = triggers.get(0).getStartTime();
				Date previousFireTime = triggers.get(0).getPreviousFireTime();

				resultVo.setJobGroup(JOB_GROUP);
				resultVo.setJobId(jobName);
				resultVo.setCronExp(triggers.get(0).getDescription());
				resultVo.setDescription(jd.getDescription());
				resultVo.setJobClass(jd.getJobClass().getName());
				if (nextFireTime != null) {
					resultVo.setNextFireTime(Common.getDateTime(nextFireTime.getTime()));
				}
				if (startTime != null) {
					resultVo.setStartTime(Common.getDateTime(startTime.getTime()));
				}
				if (previousFireTime != null) {
					resultVo.setPreviousFireTime(Common.getDateTime(previousFireTime.getTime()));
				}
			}
		}

		return resultVo;
	}

	public static boolean isExists(final String jobKey) {
		try {
			JobKey key = new JobKey(jobKey, JOB_GROUP);
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			return scheduler.checkExists(key);
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean deleteJob(final String jobKey) {
		try {
			JobKey key = new JobKey(jobKey, JOB_GROUP);
			if (isExists(jobKey)) {
				Scheduler scheduler = new StdSchedulerFactory().getScheduler();
				return scheduler.deleteJob(key);
			} else {
				return false;
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
	}
}