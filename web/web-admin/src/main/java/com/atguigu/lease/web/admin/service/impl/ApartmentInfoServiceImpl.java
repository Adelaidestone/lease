package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.ApartmentInfoMapper;
import com.atguigu.lease.web.admin.service.ApartmentFacilityService;
import com.atguigu.lease.web.admin.service.ApartmentFeeValueService;
import com.atguigu.lease.web.admin.service.ApartmentInfoService;
import com.atguigu.lease.web.admin.service.GraphInfoService;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {


    @Autowired
    private ApartmentFacilityService apartmentFacilityService;

    @Autowired
    private ApartmentLabelServiceImpl apartmentLabelService;

    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;

    @Autowired
    private GraphInfoService graphInfoService;




    /*
    1.添加
    2.修改
     */
    @Override
    public void saveOrUpdateInfo(ApartmentSubmitVo apartmentSubmitVo) {

        Long id = apartmentSubmitVo.getId();//公寓id
        //添加公寓基本信息
        this.updateById(apartmentSubmitVo);

        //是否是修改
        if (id != null) {//是修改 先删掉与当前公寓有关的信息 再添加最新的关联信息
            //删除公寓配套关系表信息
            apartmentFacilityService.remove(new LambdaQueryWrapper<ApartmentFacility>().eq(ApartmentFacility::getApartmentId,id));
            //删除公寓标签关系表信息
            apartmentLabelService.remove(new LambdaQueryWrapper<ApartmentLabel>().eq(ApartmentLabel::getApartmentId,id));
            //删除公寓杂费关系表信息
            apartmentFeeValueService.remove(new LambdaQueryWrapper<ApartmentFeeValue>().eq(ApartmentFeeValue::getApartmentId,id));
            //删除公寓图片关系表信息
            graphInfoService.remove(new LambdaQueryWrapper<GraphInfo>().eq(GraphInfo::getItemId,id).eq(GraphInfo::getItemType, ItemType.APARTMENT));
        }



        //添加公寓配套信息
        List<Long> facilityInfoIds = apartmentSubmitVo.getFacilityInfoIds();
        if(!CollectionUtils.isEmpty(facilityInfoIds)){
            List<ApartmentFacility> apartmentFacilities = new ArrayList<>();//公寓和配套关系实体对象集合
            for (Long facilityInfoId:facilityInfoIds){
                ApartmentFacility apartmentFacility = ApartmentFacility.builder().apartmentId(apartmentSubmitVo.getId()).facilityId(facilityInfoId).build();
                apartmentFacilities.add(apartmentFacility);
            }
            apartmentFacilityService.saveBatch(apartmentFacilities);

        }

        //添加公寓标签信息
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if(!CollectionUtils.isEmpty(labelIds)){
            List<ApartmentLabel> apartmentLabels = new ArrayList<>();
            for (Long labelId:labelIds){
                ApartmentLabel apartmentLabel=ApartmentLabel.builder().apartmentId(apartmentSubmitVo.getId()).labelId(labelId).build();
                apartmentLabels.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(apartmentLabels);
        }

        //添加公寓杂费关系
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if(!CollectionUtils.isEmpty(feeValueIds)){
            List<ApartmentFeeValue> apartmentFeeValues = new ArrayList<>();
            for (Long feeValueId:feeValueIds){
                ApartmentFeeValue apartmentFeeValue=ApartmentFeeValue.builder().apartmentId(apartmentSubmitVo.getId()).feeValueId(feeValueId).build();
                    apartmentFeeValues.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(apartmentFeeValues);
        }

        //添加公寓的图片信息
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();

        if (!CollectionUtils.isEmpty(graphVoList)){
            List<GraphInfo> graphInfos = new ArrayList<>();
            for (GraphVo graphvo:graphVoList){
                GraphInfo graphInfo =new GraphInfo();
                graphInfo.setName(graphvo.getName());
                graphInfo.setUrl(graphvo.getUrl());
                graphInfo.setItemId(apartmentSubmitVo.getId());
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfos.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfos);
        }

    }

}




