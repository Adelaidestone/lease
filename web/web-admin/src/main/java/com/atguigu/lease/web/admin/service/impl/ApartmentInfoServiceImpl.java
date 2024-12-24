package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private ProvinceInfoMapper provinceInfoMapper;

    @Autowired
    private CityInfoMapper cityInfoMapper;

    @Autowired
    private DistrictInfoMapper districtInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private  RoomInfoMapper roomInfoMapper;




    /*
    1.添加
    2.修改
     */
    @Override
    public void saveOrUpdateInfo(ApartmentSubmitVo apartmentSubmitVo) {

        //已知provinceId 查询公寓所属的地址信息
        ProvinceInfo provinceInfo = provinceInfoMapper.selectById(apartmentSubmitVo.getProvinceId());
        apartmentSubmitVo.setProvinceName(provinceInfo.getName());

        CityInfo cityInfo = cityInfoMapper.selectById(apartmentSubmitVo.getCityId());
        apartmentSubmitVo.setCityName(cityInfo.getName());

        DistrictInfo districtInfo = districtInfoMapper.selectById(apartmentSubmitVo.getDistrictId());
        apartmentSubmitVo.setDistrictName(districtInfo.getName());


        Long id = apartmentSubmitVo.getId();//公寓id
        //添加公寓基本信息
        this.saveOrUpdate(apartmentSubmitVo);

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
            //批量添加标签
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

    @Override
    public IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> apartmentItemVoPage, ApartmentQueryVo queryVo) {
        return apartmentInfoMapper.pageItem(apartmentItemVoPage,queryVo);
    }

    /*
    回显的方法：根据公寓的id查询基本信息以及详细信息
     */
    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();





        //查询公寓的基本信息
        ApartmentInfo apartmentInfo = this.getById(id);
        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);

        //查询公寓配套信息
       List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectByApartmentId(id);
       apartmentDetailVo.setFacilityInfoList(facilityInfoList);

       //查询公寓标签信息
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListById(id);
        apartmentDetailVo.setLabelInfoList(labelInfoList);

        //查询公寓杂费信息
        List<FeeValueVo> feeValueVoList= feeValueMapper.selectListByApartmentId(id);

        //查询照片信息
        List<GraphVo> graphVoList= graphInfoMapper.selectListByItemTypeAndItemId(id,ItemType.APARTMENT);




        return apartmentDetailVo;


    }

    @Override
    public void removeByIdInfo(Long id) {

        //查询当前被删除的公寓是否有房间 如果有房间提示不能删除
        Long count = roomInfoMapper.selectCount(new LambdaQueryWrapper<RoomInfo>().eq(RoomInfo::getId, id));
        if(count>0){
            //有房间 提示不能删除 抛自定义异常
            throw new LeaseException(208,"该公寓有房间无法删除");

        }

        //删除公寓信息
        this.removeById(id);
        //删除公寓配套表信息
        //删除公寓关联表信息
        apartmentFacilityService.remove(new LambdaQueryWrapper<ApartmentFacility>().eq(ApartmentFacility::getApartmentId, id));

        apartmentLabelService.remove(new LambdaQueryWrapper<ApartmentLabel>().eq(ApartmentLabel::getApartmentId, id));

        apartmentFeeValueService.remove(new LambdaQueryWrapper<ApartmentFeeValue>().eq(ApartmentFeeValue::getApartmentId, id));

        graphInfoService.remove(new LambdaQueryWrapper<GraphInfo>().eq(GraphInfo::getItemId, id).eq(GraphInfo::getItemType, ItemType.APARTMENT));



    }


}




