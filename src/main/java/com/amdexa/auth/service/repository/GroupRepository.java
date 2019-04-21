package com.amdexa.auth.service.repository;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GroupRepository extends LdapRepository<Group> {

    Group findByName(String username);

   // User findByUsernameAndPassword(String username, String password);

   List<Group> findByMembers(Set<String> members);

}
