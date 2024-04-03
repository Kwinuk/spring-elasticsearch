package com.xcurenet.common.schedule.controller;

import com.xcurenet.common.schedule.service.JobVO;
import com.xcurenet.common.schedule.service.QuartzCronTrigger;
import com.xcurenet.common.util.Common;
import com.xcurenet.common.util.SpringContextUtil;
import com.xcurenet.common.vo.XcnResponseVO;
import com.xcurenet.common.vo.XcnRspCode;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class QuartzController {

	@Autowired
	private QuartzCronTrigger trigger;

	@RequestMapping(value = "/putJob.xcn")
	@Description("스케줄 등록")
	@ResponseBody
	public XcnResponseVO putJob(final HttpServletRequest request, final JobVO job) throws Exception {
		if (Common.isEmpty(job.getJobId())) {
			return new XcnResponseVO(XcnRspCode.OK_CUSTOM).setMessage("스케줄 등록을 위한 JOB ID가 비어있습니다.");
		} else if (Common.isEmpty(job.getCronExp())) {
			return new XcnResponseVO(XcnRspCode.OK_CUSTOM).setMessage("스케줄 설정이 비어있습니다.");
		} else if (job.getJobClass() == null) {
			return new XcnResponseVO(XcnRspCode.OK_CUSTOM).setMessage("스케줄 대상이 존재하지 않습니다.");
		}
		try {
			trigger.putJob(job.getJobId(), SpringContextUtil.getBean(job.getJobClass()).getClass(), job.getCronExp(), job.getDescription());
		} catch (Exception e) {
			e.printStackTrace();
			return new XcnResponseVO(XcnRspCode.OK_CUSTOM).setMessage("스케줄 등록에 실패하였습니다.");
		}
		return new XcnResponseVO(XcnRspCode.OK);
	}

	@RequestMapping(value = "/runJob.xcn")
	@Description("스케줄 JOB 실행")
	@ResponseBody
	public XcnResponseVO runJob(final HttpServletRequest request, final JobVO job) throws Exception {
		if (Common.isEmpty(job.getJobId())) {
			return new XcnResponseVO(XcnRspCode.OK_CUSTOM).setMessage("스케줄 실행을 위한 JOB ID가 비어있습니다.");
		}
		String rtMsg = QuartzCronTrigger.runJob(job);
		if(Common.isEmpty(rtMsg)) return new XcnResponseVO(XcnRspCode.OK);
		else {
			if(Common.isEquals(rtMsg, "00")){
				rtMsg = "JOB ID를 찾을 수 없습니다.(삭제 되었거나 JOB이 등록되지 않았습니다.)";
			}else if(Common.isEquals(rtMsg, "01")){
				rtMsg = "JOB 실행 도중 에러가 발생하였습니다.";
			}

			return new XcnResponseVO(XcnRspCode.OK_CUSTOM).setMessage("스케줄 실행에 실패하였습니다.\n" + rtMsg);
		}
	}

	@RequestMapping(value = "/deleteJob.xcn")
	@Description("스케줄 제거")
	@ResponseBody
	public XcnResponseVO deleteJob(final HttpServletRequest request, final JobVO job) throws Exception {
		if (Common.isEmpty(job.getJobId())) {
			return new XcnResponseVO(XcnRspCode.OK_CUSTOM).setMessage("스케줄 제거를 위한 JOB ID가 비어있습니다.");
		}
		try {
			QuartzCronTrigger.deleteJob(job.getJobId());
		} catch (Exception e) {
			e.printStackTrace();
			return new XcnResponseVO(XcnRspCode.OK_CUSTOM).setMessage("스케줄 제거에 실패하였습니다.");
		}
		return new XcnResponseVO(XcnRspCode.OK);
	}

	@RequestMapping(value = "/getJobList.xcn")
	@Description("스케줄 목록 조회")
	@ResponseBody
	public XcnResponseVO getJobList(final HttpServletRequest request) throws Exception {
		try {
			return new XcnResponseVO(XcnRspCode.OK, trigger.getJobList());
		} catch (Exception e) {
			e.printStackTrace();
			return new XcnResponseVO(XcnRspCode.OK_CUSTOM).setMessage("스케줄 목록 조회에 실패하였습니다.");
		}
	}

	@RequestMapping(value = "/getScheduleJob.xcn")
	@Description("스케줄 조회")
	@ResponseBody
	public XcnResponseVO getScheduleJob(final HttpServletRequest request, final String jobId) throws Exception {
		try {
			XcnResponseVO resultVo = null;
			JobVO jobVo = trigger.getScheduleJob(jobId);
			if(StringUtils.equals(jobId, jobVo.getJobId())) {
				resultVo = new XcnResponseVO(XcnRspCode.OK, jobVo);
			} else {
				resultVo = new XcnResponseVO(XcnRspCode.OK);
			}

			return resultVo;
		} catch (Exception e) {
			e.printStackTrace();
			return new XcnResponseVO(XcnRspCode.OK_CUSTOM).setMessage("인사 연동 스케줄 조회에 실패하였습니다.");
		}
	}
}
