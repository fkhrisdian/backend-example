package com.kaspro.bank.controller;

import com.kaspro.bank.enums.StatusCode;
import com.kaspro.bank.exception.NostraException;
import com.kaspro.bank.persistance.domain.User;
import com.kaspro.bank.services.TransferService;
import com.kaspro.bank.services.UserService;
import com.kaspro.bank.vo.*;
import com.kaspro.bank.vo.TransferKasproBank.TransferKasproBankReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TransferController {

  @Autowired
  TransferService transferService;

  @Autowired
  UserService userService;

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
                                                    @RequestParam(value="amount", required = true) String amount,
                                                    @RequestParam(value="paymentMethod", required = true) String paymentMethod,
                                                    @RequestParam(value="chargingModel", required = true) String chargingModel,
                                                    @RequestParam(value="isAdmin", required = false) Boolean isAdmin) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.kasproBankInquiry(source, destination, sku,amount, paymentMethod, chargingModel,isAdmin);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/Transfer/KasproBank")
  @ResponseBody
  public ResponseEntity<ResultVO> transferKasproBank(@RequestBody final TransferKasproBankReqVO vo) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.transferKasproBank(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/Transfer/OtherBank")
  @ResponseBody
  public ResponseEntity<ResultVO> transferOtherBank(@RequestBody final TransferKasproBankReqVO vo) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.transferInterBank(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/Transfer/BNI")
  @ResponseBody
  public ResponseEntity<ResultVO> transferBNI(@RequestBody final TransferKasproBankReqVO vo) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.transferInHouse(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/Transfer/Kaspro")
  @ResponseBody
  public ResponseEntity<ResultVO> transferKaspro(@RequestBody final TransferKasproBankReqVO vo) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.transferKaspro(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/Inquiry/TransactionHistory")
  @ResponseBody
  public ResponseEntity<ResultVO> findEntrireTransaction() {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.findEntireTransaction();
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/Inquiry/TransactionHistoryFilter")
  @ResponseBody
  public ResponseEntity<ResultVO> findFilteredTransaction(@RequestParam(value="accType", required = true) String accType,
                                                          @RequestParam(value="partnerId", required = false) String partnerId,
                                                          @RequestParam(value="senderId", required = false) String senderId,
                                                          @RequestParam(value="msisdn", required = false) String msisdn,
                                                          @RequestParam(value="tid", required = false) String tid,
                                                          @RequestParam(value="startDate", required = false) String startDate,
                                                          @RequestParam(value="endDate", required = false) String endDate) {
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.findFilteredTransaction(accType,partnerId,senderId,msisdn,tid,startDate,endDate);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/K2KB/InquiryInHouse")
  @ResponseBody
  public ResponseEntity<K2KBResultVO> k2kbInquiryInhouse(@RequestBody final K2KBInquiryInhouseReqVO vo) {
    K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.k2kbInquiryInhouse(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/K2KB/InquiryInterBank")
  @ResponseBody
  public ResponseEntity<K2KBResultVO> k2kbInquiryInterBank(@RequestBody final K2KBInquiryInterBankReqVO vo) {
    K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.k2kbInquireInterBank(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/K2KB/PaymentInhouse")
  @ResponseBody
  public ResponseEntity<K2KBResultVO> k2kbTransferInhouse(@RequestBody final K2KBPaymentInhouseReqVO vo) {
    K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.k2kbPaymentInhouse(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/K2KB/PaymentInterBank")
  @ResponseBody
  public ResponseEntity<K2KBResultVO> k2kbTransferInterBank(@RequestBody final K2KBPaymentInterBankReqVO vo) {
    K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.k2kbPaymentInterBank(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/K2KB/GetBalance")
  @ResponseBody
  public ResponseEntity<K2KBResultVO> k2kbGetBalance(@RequestBody final K2KBGetBalanceReqVO vo) {
    K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.getBalance(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/K2KB/GetPaymentStatus")
  @ResponseBody
  public ResponseEntity<K2KBResultVO> k2kbGetPaymentStatus(@RequestBody final K2KBGetPaymentStatusReqVO vo) {
    K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.getPaymentStatus(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/Transfer/Wrapper")
  @ResponseBody
  public ResponseEntity<K2KBResultVO> wrapperTransfer(@RequestBody final WrapperTransferReqVO vo) {
    K2KBAbstractRequestHandler handler = new K2KBAbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.wrapperTransfer(vo);
      }
    };
    return handler.getResult();
  }

  @RequestMapping(method = RequestMethod.DELETE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          value="/Transfer/Cancel")
  @ResponseBody
  public ResponseEntity<ResultVO> findDetail(@RequestHeader(value = "Authorization") String authorization,
                                             @RequestParam(value="tid", required = true) String tid) {
    User user = userService.validateToken(authorization);
    if(user==null){
      throw new NostraException("Unauthorized", StatusCode.UNAUTHORIZED);
    }
    AbstractRequestHandler handler = new AbstractRequestHandler() {
      @Override
      public Object processRequest() {
        return transferService.cancelTID(user.getUsername(), tid);
      }
    };
    return handler.getResult();
  }
}
