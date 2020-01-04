package sut.se.team15.Controller;

import sut.se.team15.Entity.*;
import sut.se.team15.Repository.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;




import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class PayInsuranceController{

    @Autowired
    private final PayInsuranceRepository payinsuranceRepository;
    @Autowired
    private Insurance_staffRepository insurance_staffRepository;
   

    PayInsuranceController(PayInsuranceRepository payinsuranceRepository) {
        this.payinsuranceRepository = payinsuranceRepository;
    }

    @GetMapping("/payinsurances")
    public Collection<PayInsurance> PayInsurances(){
        return payinsuranceRepository.findAll().stream().collect(Collectors.toList());
    }

    @PostMapping("/payinsurances/{staff_ids}/{amount}")
    public PayInsurance newPayInsurance(PayInsurance newpayinsurance,
    
    @PathVariable long staff_ids,
    @PathVariable Double amount
  
    ){
        
        
        Insurance_staff staffid = insurance_staffRepository.findById(staff_ids);
        
        newpayinsurance.setStaffID(staffid);
        newpayinsurance.setAmount(amount);


        return payinsuranceRepository.save(newpayinsurance);
    }
}