package com.rrf.ebanxapi.ebanxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rrf.ebanxapi.ebanxapp.model.Account;

public interface AccountRepository  extends JpaRepository<Account, Long>{

}
