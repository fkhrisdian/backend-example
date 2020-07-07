package com.kaspro.bank.controller;

import com.kaspro.bank.services.TransferService;
import com.kaspro.bank.vo.*;
import com.kaspro.bank.vo.TransferKasproBank.TransferKasproBankReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

  @RequestMapping(method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE,
      value="/Payment/InterBank"
  )
  @ResponseBody
  public ResponseEntity<ResultVO> paymentInterBank(@RequestBody final InterBankPaymentVO vo) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.interBankPayment(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE,
      value="/Payment/Status"
  )
  @ResponseBody
  public ResponseEntity<ResultVO> paymentStatus(@RequestBody final PaymentStatusVO vo) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.paymentStatus(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/Inquiry/KasproBank")
  @ResponseBody
  public ResponseEntity<ResultVO> findPartnerDetail(@RequestParam(value="source", required = true) String source,
                                                    @RequestParam(value="destination", required = true) String destination,
                                                    @RequestParam(value="sku", required = true) String sku,
                                                    @RequestParam(value="amount", required = true) String amount) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.kasproBankInquiry(source, destination, sku,amount);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/Transfer/KasproBank")
  @ResponseBody
  public ResponseEntity<ResultVO> add(@RequestBody final TransferKasproBankReqVO vo) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.transferKasproBank(vo);
      }
    };
    return handler.getResult();
  }
}
