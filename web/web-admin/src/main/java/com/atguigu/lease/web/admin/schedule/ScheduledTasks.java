package com.atguigu.lease.web.admin.schedule;

import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
定时任务类
 */
@Component
public class ScheduledTasks {

    @Autowired
    private LeaseAgreementService leaseAgreementService;
    /*
    定时检查租约状态 改为已到期
     */

    @Scheduled(cron = "0 0 0 * * *")
    public void checkLeaseStatus() {

        //修改状态为已到期 条件:lease_end_date 租约结束日期 小于 当前日期 new date
            //租约状态为 2,5
        leaseAgreementService.update(new LambdaUpdateWrapper<LeaseAgreement>()
                .lt(LeaseAgreement::getLeaseEndDate, new Date()).in(LeaseAgreement::getStatus,2,5)
                .set(LeaseAgreement::getStatus,LeaseStatus.EXPIRED)
        );
    }
}
