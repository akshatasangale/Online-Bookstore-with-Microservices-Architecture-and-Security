package com.shashirajraja.onlinebookstore.service;

import java.util.List;

import com.shashirajraja.onlinebookstore.entity.User;

public interface UserService {

	List<User> getAllUsers();

	User getUserByUsername(String username);
}
