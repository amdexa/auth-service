package com.amdexa.auth.service;


import com.amdexa.auth.service.repository.Group;
import com.amdexa.auth.service.repository.GroupRepository;
import com.amdexa.auth.service.repository.User;
import com.amdexa.auth.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    public Group getGroup(final String name) {
        return groupRepository.findByName(name);
    }

}

