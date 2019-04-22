package com.amdexa.auth.service;

import com.amdexa.auth.service.repository.Group;
import com.amdexa.auth.service.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    public Group getGroup(final String name) {
        return groupRepository.findByName(name);
    }


    public List<Group> getGroups(final String name) {
        return groupRepository.findByMembers(name);
    }

}

