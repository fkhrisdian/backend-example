package com.kaspro.bank.converter;

import com.kaspro.bank.persistance.domain.Partner;
import com.kaspro.bank.util.ExtendedSpringBeanUtil;
import com.kaspro.bank.vo.RegisterPartnerVO;
import com.kaspro.bank.vo.RegisterPartnerResponseVO;
import org.springframework.stereotype.Component;

@Component
public class PartnerVOConverter extends BaseVOConverter<RegisterPartnerVO, RegisterPartnerResponseVO, Partner> {

  @Override
  public Partner transferVOToModel(RegisterPartnerVO vo, Partner model){
    if (model == null) model = new Partner();
    super.transferVOToModel(vo, model);
    ExtendedSpringBeanUtil.copySpecificProperties(vo, model,
        new String[]{"name"});
    return model;
  }

  @Override
  public RegisterPartnerResponseVO transferModelToVO(Partner model, RegisterPartnerResponseVO vo){
    if (vo == null) vo = new RegisterPartnerResponseVO();
    super.transferModelToVO(model, vo);
    ExtendedSpringBeanUtil.copySpecificProperties(model, vo,
        new String[]{"name"});
    return vo;
  }
}
