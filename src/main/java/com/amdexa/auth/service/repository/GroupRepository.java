package com.amdexa.auth.service.repository;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends LdapRepository<Group> {

    Group findByName(String username);

    List<Group> findByMembers(String member);

}
