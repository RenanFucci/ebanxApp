package com.rrf.ebanxapi.ebanxapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Account {

	@Id	
	@Column(name = "id")
	private long id;
	
	@Column(name = "balance")
	private int balance;
	
	public Account() {
	}
	
	public Account(long id, int balance) {
		super();
		this.id = id;
		this.balance = balance;
	}
	

	public int getBalance() {
		return balance;
	}

	public long getId() {
		return id;
	}



	public void setId(long id) {
		this.id = id;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	@Override
	public String toString() {
		return "{"
				+ this.id+", "
				+ this.balance+
				"}";
	}
}
