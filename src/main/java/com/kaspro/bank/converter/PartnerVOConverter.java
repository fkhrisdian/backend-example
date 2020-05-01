package com.kaspro.bank.converter;

import com.kaspro.bank.persistance.domain.Partner;
import com.kaspro.bank.util.ExtendedSpringBeanUtil;
import com.kaspro.bank.vo.PartnerRequestVO;
import com.kaspro.bank.vo.PartnerResponseVO;
import org.springframework.stereotype.Component;

@Component
public class PartnerVOConverter extends BaseVOConverter<PartnerRequestVO, PartnerResponseVO, Partner> {

  @Override
  public Partner transferVOToModel(PartnerRequestVO vo, Partner model){
    if (model == null) model = new Partner();
    super.transferVOToModel(vo, model);
    ExtendedSpringBeanUtil.copySpecificProperties(vo, model,
        new String[]{"name"});
    return model;
  }

  @Override
  public PartnerResponseVO transferModelToVO(Partner model, PartnerResponseVO vo){
    if (vo == null) vo = new PartnerResponseVO();
    super.transferModelToVO(model, vo);
    ExtendedSpringBeanUtil.copySpecificProperties(model, vo,
        new String[]{"name"});
    return vo;
  }
}
