package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.RoomInfoMapper;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    GraphInfoService graphInfoService;

    @Autowired
    RoomAttrValueService roomAttrValueService;
    @Autowired
    private RoomFacilityService roomFacilityService;

    @Autowired
    private RoomLabelService roomLabelService;

    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;
    @Autowired
    private RoomLeaseTermService roomLeaseTermService;


    @Override
    public void saveOrUpdateInfo(RoomSubmitVo roomSubmitVo) {
        boolean isUpdate = roomSubmitVo.getId() !=null;

        this.saveOrUpdate(roomSubmitVo);

        //若为更新操作，则先删除与room相关的各项信息列表
        if(isUpdate){
            //1.删除原有的graphInfoList
            LambdaQueryWrapper<GraphInfo> graphInfoWrapper = new LambdaQueryWrapper<>();
            graphInfoWrapper.eq(GraphInfo::getId, roomSubmitVo.getId());
            graphInfoWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphInfoService.remove(graphInfoWrapper);

            //2.删除原有roomAttrValueList
            LambdaQueryWrapper<RoomAttrValue> roomAttrValueWrapper = new LambdaQueryWrapper<>();
            roomAttrValueWrapper.eq(RoomAttrValue::getRoomId, roomSubmitVo.getId());
            roomAttrValueService.remove(roomAttrValueWrapper);

            //3.删除原有roomFacilityList
            LambdaQueryWrapper<RoomFacility> roomFacilityWrapper = new LambdaQueryWrapper<>();
            roomFacilityWrapper.eq(RoomFacility::getId, roomSubmitVo.getId());
            roomFacilityService.remove(roomFacilityWrapper);

            //4.删除原有标签roomLabelList
            LambdaQueryWrapper<RoomLabel> roomLabelWrapper = new LambdaQueryWrapper<>();
            roomLabelWrapper.eq(RoomLabel::getId, roomSubmitVo.getId());
            roomLabelService.remove(roomLabelWrapper);

            //5.删除原有支付方式 roomPaymentList
            LambdaQueryWrapper<RoomPaymentType> roomPaymentTypeWrapper = new LambdaQueryWrapper<>();
            roomPaymentTypeWrapper.eq(RoomPaymentType::getId, roomSubmitVo.getId());
            roomPaymentTypeService.remove(roomPaymentTypeWrapper);

            //1.保存新的graphInfoList
            List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
            if(!CollectionUtils.isEmpty(graphVoList)){
                ArrayList<GraphInfo> graphInfoList = new ArrayList<>();
                for (GraphVo graphVo : graphVoList) {
                    GraphInfo graphInfo = new GraphInfo();
                    graphInfo.setName(graphVo.getName());
                    graphInfo.setItemType(ItemType.ROOM);
                    graphInfo.setItemId(roomSubmitVo.getId());
                    graphInfo.setUrl(graphVo.getUrl());
                    graphInfoList.add(graphInfo);
                }
                graphInfoService.saveBatch(graphInfoList);
            }

            //2.保存新的roomAttrValueList

            List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
            if(!CollectionUtils.isEmpty(attrValueIds)){
                List<RoomAttrValue> roomAttrValueList = new ArrayList<>();
                for (Long attrValueId : attrValueIds) {
                    RoomAttrValue roomAttrValue =
                         RoomAttrValue.builder().attrValueId(attrValueId).roomId(roomSubmitVo.getId()).build();
                    roomAttrValueList.add(roomAttrValue);
                }
                roomAttrValueService.saveBatch(roomAttrValueList);
            }

            //3.保存新的facilityInfoList
            List<Long> facilityInfoIds = roomSubmitVo.getFacilityInfoIds();
            if(!CollectionUtils.isEmpty(facilityInfoIds)){
                List<RoomFacility> roomFacilityList = new ArrayList<>();
                for (Long facilityInfoId : facilityInfoIds) {
                    RoomFacility roomFacility =
                            RoomFacility.builder().roomId(roomSubmitVo.getId()).facilityId(facilityInfoId).build();
                        roomFacilityList.add(roomFacility);
                }

                roomFacilityService.saveBatch(roomFacilityList);
            }


            //4.保存新的labelInfoList
            List<Long> labelInfoIds = roomSubmitVo.getLabelInfoIds();
            if(!CollectionUtils.isEmpty(labelInfoIds)){
                List<RoomLabel> roomLabelList = new ArrayList<>();
                for (Long labelInfoId : labelInfoIds) {
                    RoomLabel roomLabel = RoomLabel.builder().labelId(labelInfoId).roomId(roomSubmitVo.getId()).build();
                        roomLabelList.add(roomLabel);
                }
                roomLabelService.saveBatch(roomLabelList);
            }


            //5.保存新的paymentTypeList
            List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
            if(!CollectionUtils.isEmpty(paymentTypeIds)){
                List<RoomPaymentType> roomPaymentTypeList = new ArrayList<>();
                for (Long paymentTypeId : paymentTypeIds) {
                    RoomPaymentType roomPaymentType = RoomPaymentType.builder().paymentTypeId(paymentTypeId).build();
                    roomPaymentTypeList.add(roomPaymentType);
                }
                roomPaymentTypeService.saveBatch(roomPaymentTypeList);
            }


            //6.保存新的leaseTermList
            List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();
            if(!CollectionUtils.isEmpty(leaseTermIds)){
                List<RoomLeaseTerm> roomLeaseTermList = new ArrayList<>();
                for (Long leaseTermId : leaseTermIds) {
                    RoomLeaseTerm roomLeaseTerm = RoomLeaseTerm.builder().leaseTermId(leaseTermId).build();
                    roomLeaseTermList.add(roomLeaseTerm);
                }
                roomLeaseTermService.saveBatch(roomLeaseTermList);
            }




        }
    }
}




