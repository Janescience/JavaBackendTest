package com.javainuse.respository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.javainuse.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
	
	User findByUsername(String username);
	
}