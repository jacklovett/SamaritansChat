package com.gibsams.gibsamscoremodule.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.Role;
import com.gibsams.gibsamscoremodule.repository.RoleRepository;

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
