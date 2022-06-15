package com.rrf.ebanxapi.ebanxapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rrf.ebanxapi.ebanxapp.model.Account;
import com.rrf.ebanxapi.ebanxapp.model.dto.AccountDto;
import com.rrf.ebanxapi.ebanxapp.repository.AccountRepository;

@RestController
public class Controller {

	private AccountRepository accountRepo;

	public Controller(AccountRepository accRepo) {
		super();
		this.accountRepo = accRepo;
	}

	@PostMapping(value = "/event")
	public ResponseEntity<?> save(@RequestBody Map<String, String> req) {
		try {
			// Solution I found if the request body had Upper or Lower case strings
			Map<String, String> accs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			accs.putAll(req);			
			
			String orignOrDest = (accs.keySet().toArray()[1].toString()).toLowerCase();
			String type = accs.get("type").toLowerCase();			
			long id = Long.parseLong(accs.get(orignOrDest));
			int amount = Integer.parseInt(accs.get("amount"));
			
			Optional<Account> accOp = accountRepo.findById(id);
			boolean accExists = !accOp.isEmpty();

			
			if (!accExists && (type.contentEquals("withdraw"))) {
				return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
			}
			else if (!accExists && type.contentEquals("deposit")) {
				Account acc = new Account(id, amount);
				accountRepo.save(acc);
				
				
				HashMap<String, AccountDto> result = new HashMap<>();
				result.put(orignOrDest, new AccountDto(acc));
				return new ResponseEntity<>(result, HttpStatus.CREATED);
			}
			else if(type.contentEquals("transfer")){
				long origId = Long.parseLong(accs.values().toArray()[2].toString());
				long destId = Long.parseLong(accs.values().toArray()[1].toString());
				Optional<Account> accOriOp = accountRepo.findById(origId);
				Optional<Account> accDestOp = accountRepo.findById(destId);
				
				boolean accOrigExists = !accOriOp.isEmpty();
				boolean accDestExists = !accDestOp.isEmpty();
				
				if(!accDestExists || !accOrigExists) {
					return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
				}
				Account origAcc = accOriOp.get(); 		
				Account destAcc = accDestOp.get();
			
				origAcc.setBalance(origAcc.getBalance()-amount);
				accountRepo.save(origAcc);

				destAcc.setBalance(destAcc.getBalance()+amount);
				accountRepo.save(destAcc);
				
				String originstr = accs.keySet().toArray()[2].toString().toLowerCase();
				String destinationstr = accs.keySet().toArray()[1].toString().toLowerCase();
				
				
				HashMap<String, AccountDto> result = new HashMap<>();
				result.put(originstr, new AccountDto(origAcc));
				result.put(destinationstr, new AccountDto(destAcc));
				
				return new ResponseEntity<>(result, HttpStatus.CREATED);
						
			}
			else {
				Account acctmp = accOp.get();

				if (type.contentEquals("deposit")) {
					acctmp.setBalance(acctmp.getBalance()+amount);
				} else if (type.contentEquals("withdraw")) {
					acctmp.setBalance(acctmp.getBalance()-amount);
				}
				accountRepo.save(acctmp);
				HashMap<String, AccountDto> result = new HashMap<>();
				result.put(orignOrDest, new AccountDto(acctmp));
				return new ResponseEntity<>(result, HttpStatus.CREATED);
			}
		} catch (Exception e) {
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

		boolean accExists = !accOp.isEmpty();
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
