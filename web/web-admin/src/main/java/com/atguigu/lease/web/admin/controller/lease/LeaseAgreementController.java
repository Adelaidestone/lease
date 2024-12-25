package com.atguigu.lease.web.admin.controller.lease;


import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.entity.RoomInfo;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.model.enums.ReleaseStatus;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.atguigu.lease.web.admin.service.RoomInfoService;
import com.atguigu.lease.web.admin.service.impl.LeaseAgreementServiceImpl;
import com.atguigu.lease.web.admin.service.impl.RoomInfoServiceImpl;
import com.atguigu.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.atguigu.lease.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "租约管理")
@RestController
@RequestMapping("/admin/agreement")
public class LeaseAgreementController {
    @Autowired
    private LeaseAgreementService leaseAgreementService;
    @Autowired
    private RoomInfoService roomInfoService;


    @Operation(summary = "保存或修改租约信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody LeaseAgreement leaseAgreement) {
        leaseAgreementService.saveOrUpdate(leaseAgreement);
        return Result.ok();
    }

    @Operation(summary = "根据条件分页查询租约列表")
    @GetMapping("page")
    public Result<IPage<AgreementVo>> page(@RequestParam long current, @RequestParam long size, AgreementQueryVo queryVo) {
     IPage<AgreementVo> iPage  = leaseAgreementService.pageInfo(new Page<>(current, size), queryVo);

     return Result.ok(iPage);
    }

    @Operation(summary = "根据id查询租约信息")
    @GetMapping(name = "getById")
    public Result<AgreementVo> getById(@RequestParam Long id) {
        AgreementVo agreementVo = leaseAgreementService.getByIdInfo(id);
        return Result.ok(agreementVo);
    }

    @Operation(summary = "根据id删除租约信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        leaseAgreementService.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "根据id更新租约状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam LeaseStatus status) {
        leaseAgreementService.update( new LambdaUpdateWrapper<LeaseAgreement>().eq(LeaseAgreement::getId, id).eq(LeaseAgreement::getStatus, status));
        return Result.ok();
    }

    @Operation(summary = "检查分配的房间是否已经签约")
    @DeleteMapping("CheckStatus")
    public Result CheckStatus(@RequestParam Long id) {
        //如果房间已签约 抛异常
        LeaseAgreement one = leaseAgreementService.getOne(new LambdaQueryWrapper<LeaseAgreement>().eq(LeaseAgreement::getRoomId, id)
                .in(LeaseAgreement::getStatus, 2, 5));
        if(one!=null){
                throw new LeaseException(556,"房间已出租");
        }
        return Result.ok();
    }

}

