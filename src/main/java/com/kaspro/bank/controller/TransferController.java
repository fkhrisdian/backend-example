package com.kaspro.bank.controller;

import com.kaspro.bank.services.TransferService;
import com.kaspro.bank.vo.BalanceVO;
import com.kaspro.bank.vo.InHouseInquiryVO;
import com.kaspro.bank.vo.InHousePaymentVO;
import com.kaspro.bank.vo.InterBankInquiryVO;
import com.kaspro.bank.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/Transfer")
public class TransferController {

  @Autowired
  TransferService transferService;

  @RequestMapping(method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE,
      value="/Balance"
  )
  @ResponseBody
  public ResponseEntity<ResultVO> balance(@RequestBody final BalanceVO vo) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.balance(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE,
      value="/Inquiry/InHouse"
  )
  @ResponseBody
  public ResponseEntity<ResultVO> inquiryInHouse(@RequestBody final InHouseInquiryVO vo) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.inHouseInquiry(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE,
      value="/Inquiry/InterBank"
  )
  @ResponseBody
  public ResponseEntity<ResultVO> inquiryInterBank(@RequestBody final InterBankInquiryVO vo) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.interBankInquiry(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE,
      value="/Payment/InHouse"
  )
  @ResponseBody
  public ResponseEntity<ResultVO> paymentInHouse(@RequestBody final InHousePaymentVO vo) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.inHousePayment(vo);
      }
    };
    return handler.getResult();
  }
}
