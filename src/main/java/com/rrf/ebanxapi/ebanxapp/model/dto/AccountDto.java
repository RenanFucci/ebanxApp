package com.rrf.ebanxapi.ebanxapp.model.dto;

import com.rrf.ebanxapi.ebanxapp.model.Account;

public class AccountDto {


	private String id;
	private int balance;
	
	public AccountDto(Account acc) {
		this.balance = acc.getBalance();
		this.id = acc.getId()+"";
	}

	public int getBalance() {
		return balance;
	}
	
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return balance+"";
	}
	
}
