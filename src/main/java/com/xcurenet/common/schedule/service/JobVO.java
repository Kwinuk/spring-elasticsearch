package com.xcurenet.common.schedule.service;

import lombok.Data;

@Data
public class JobVO {
	private String jobGroup;
	private String jobId;
	private String jobClass;
	private String cronExp;
	private String description;

	private String previousFireTime;
	private String nextFireTime;
	private String startTime;
}
