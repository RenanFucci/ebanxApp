package com.rrf.ebanxapi.ebanxapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rrf.ebanxapi.ebanxapp.model.Account;
import com.rrf.ebanxapi.ebanxapp.model.dto.AccountDto;
import com.rrf.ebanxapi.ebanxapp.model.form.EventForm;
import com.rrf.ebanxapi.ebanxapp.repository.AccountRepository;

@RestController
public class Controller {

	private AccountRepository accountRepo;

	public Controller(AccountRepository accRepo) {
		super();
		this.accountRepo = accRepo;
	}

	@PostMapping(value = "/event")
	public ResponseEntity<?> save(@RequestBody EventForm event) {
		try {
			String type = event.getType();			
			
			long destination = event.getDestination()== null ? -1: Long.parseLong(event.getDestination());
			long origin = event.getOrigin()== null ? -1: Long.parseLong(event.getOrigin());
			int amount = event.getAmount();
			
			Optional<Account> accOriOp = accountRepo.findById(origin);
			Optional<Account> accDestOp = accountRepo.findById(destination);
			
			boolean accOriExists = accOriOp.isPresent();
			boolean accDestExists = accDestOp.isPresent();
			HashMap<String, AccountDto> result = new HashMap<>();
			
			
			
			if (!accOriExists && (type.contentEquals("withdraw") || type.contentEquals("transfer"))) {
				return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
			}
			
			if (!accDestExists && type.contentEquals("deposit")) {
				Account acc = new Account(destination, amount);
				accountRepo.save(acc);
				
				result.put(event.getDestinationName(), new AccountDto(acc));
				return new ResponseEntity<>(result, HttpStatus.CREATED);
			}
			
			if (type.contentEquals("deposit")) {
				Account acctmp = accDestOp.get();
				
				acctmp.setBalance(acctmp.getBalance()+amount);
				accountRepo.save(acctmp);
				
				result.put(event.getDestinationName(), new AccountDto(acctmp));
				return new ResponseEntity<>(result, HttpStatus.CREATED);

			} 
			
			if (type.contentEquals("withdraw")) {
				Account acctmp =  accOriOp.get();			
				acctmp.setBalance(acctmp.getBalance()-amount);
				accountRepo.save(acctmp);
				
				result.put(event.getOriginName(), new AccountDto(acctmp));
				return new ResponseEntity<>(result, HttpStatus.CREATED);
			}

			else {// transfer	
				Account origAcc = accOriOp.get(); 		
				Account destAcc = new Account(destination, 0);
			
				origAcc.setBalance(origAcc.getBalance()-amount);
				accountRepo.save(origAcc);

				destAcc.setBalance(destAcc.getBalance()+amount);
				accountRepo.save(destAcc);
								
				result.put(event.getOriginName(), new AccountDto(origAcc));
				result.put(event.getDestinationName(), new AccountDto(destAcc));
				
				return new ResponseEntity<>(result, HttpStatus.CREATED);		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/reset")
	public ResponseEntity<?> reset() {
		try{
			accountRepo.deleteAll();
			return new ResponseEntity<>("OK", HttpStatus.OK);			
		}
		catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);		
		}		
	}
	
	@GetMapping(value = "/balance")
	public ResponseEntity<?> getById(@RequestParam String account_id) {

		Optional<Account> accOp = accountRepo.findById(Long.valueOf(account_id));

		boolean accExists = accOp.isPresent();
		if (!accExists) {
			return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
		} else {
			AccountDto accDto = new AccountDto(accOp.get());
			return new ResponseEntity<>(accDto.toString(), HttpStatus.OK);
		}
	}

	@GetMapping(value = "/")
	public ResponseEntity<List<Account>> getAll() {
		List<Account> accounts = new ArrayList<>();
		accounts = accountRepo.findAll();
		return new ResponseEntity<>(accounts, HttpStatus.OK);
	}
	
	
}
