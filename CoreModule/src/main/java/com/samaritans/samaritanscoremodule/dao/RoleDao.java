package com.samaritans.samaritanscoremodule.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.samaritans.samaritanscoremodule.exception.ResourceNotFoundException;
import com.samaritans.samaritanscoremodule.model.Role;
import com.samaritans.samaritanscoremodule.repository.RoleRepository;

public class RoleDao {

	@Autowired
	private RoleRepository roleRepository;

	public Role findRoleById(int id) {
		return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
	}

	public Role findRoleByName(String roleName) {
		return roleRepository.findByName(roleName).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
	}

}
