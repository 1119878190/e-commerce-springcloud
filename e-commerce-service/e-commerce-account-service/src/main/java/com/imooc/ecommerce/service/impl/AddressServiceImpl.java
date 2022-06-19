package com.imooc.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.account.AddressInfo;
import com.imooc.ecommerce.common.TableId;
import com.imooc.ecommerce.dao.EcommerceAddressDao;
import com.imooc.ecommerce.entity.EcommerceAddress;
import com.imooc.ecommerce.filter.AccessContext;
import com.imooc.ecommerce.service.IAddressService;
import com.imooc.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AddressServiceImpl implements IAddressService {

    private final EcommerceAddressDao ecommerceAddressDao;

    public AddressServiceImpl(EcommerceAddressDao ecommerceAddressDao) {
        this.ecommerceAddressDao = ecommerceAddressDao;
    }


    /**
     * 存储多个地址信息
     *
     * @param addressInfo
     * @return
     */
    @Override
    public TableId createAddressInfo(AddressInfo addressInfo) {

        // 当前登录用户信息(interceptor中存入的)
        LoginUserInfo loginUserInfo = AccessContext.getLoginUserInfo();

        // 将传递的参数转成实体对象
        List<EcommerceAddress> ecommerceAddresses = addressInfo.getAddressItems().stream()
                .map(item -> EcommerceAddress.to(loginUserInfo.getId(), item))
                .collect(Collectors.toList());

        // 保存到数据库，并把返回的记录的 id 给调用方
        List<EcommerceAddress> saveRecord = ecommerceAddressDao.saveAll(ecommerceAddresses);
        List<Long> ids = saveRecord.stream().map(EcommerceAddress::getId).collect(Collectors.toList());
        log.info("create address info : [{}], [{}]", loginUserInfo.getId(), JSON.toJSONString(ids));

        return new TableId(
                ids.stream().map(TableId.Id::new).collect(Collectors.toList())
        );
    }

    /**
     * 获取用户地址信息
     *
     * @return
     */
    @Override
    public AddressInfo getCurrentAddressInfo() {

        LoginUserInfo loginUserInfo = AccessContext.getLoginUserInfo();
        List<EcommerceAddress> ecommerceAddressList = ecommerceAddressDao.findAllByUserId(loginUserInfo.getId());

        List<AddressInfo.AddressItem> address = ecommerceAddressList.stream().map(item -> item.toAddressItem()).collect(Collectors.toList());

        return new AddressInfo(loginUserInfo.getId(), address);

    }

    @Override
    public AddressInfo getAddressInfoById(Long id) {

        EcommerceAddress ecommerceAddress = ecommerceAddressDao.findById(id).orElse(null);
        if (Objects.isNull(ecommerceAddress)) {
            throw new RuntimeException("address is not exist");
        }

        return new AddressInfo(ecommerceAddress.getUserId(),
                Collections.singletonList(ecommerceAddress.toAddressItem()));
    }

    @Override
    public AddressInfo getAddressInfoByTableId(TableId tableId) {

        List<Long> ids = tableId.getIds().stream().map(TableId.Id::getId).collect(Collectors.toList());
        log.info("get address info by table id:[{}]", JSON.toJSONString(ids));

        List<EcommerceAddress> ecommerceAddressList = ecommerceAddressDao.findAllById(ids);
        if (CollectionUtils.isEmpty(ecommerceAddressList)) {
            return new AddressInfo(-1L, Collections.emptyList());
        }

        List<AddressInfo.AddressItem> addressItemList = ecommerceAddressList.stream().map(item -> item.toAddressItem()).collect(Collectors.toList());
        return new AddressInfo(ecommerceAddressList.get(0).getUserId(), addressItemList);
    }
}
