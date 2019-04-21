package com.amdexa.auth.service.repository;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.util.HashSet;
import java.util.Set;

@Entry(base = "ou=groups", objectClasses = { "posixGroup" })
public class Group {

    @Id
    private Name id;

    private @Attribute(name = "cn") String name;
    private @Attribute(name = "gidnumber") String gidNumber;
    private @Attribute(name = "memberuid") Set<String> members;

    public Group() {
    }

    public Group(String name, String gidNumber) {
        this.name = name;
        this.gidNumber = gidNumber;
    }

    public Group(String name, String gidNumber, Set<String> members) {
        this.name = name;
        this.gidNumber = gidNumber;
        this.members = members;
    }

    public Name getId() {
        return id;
    }

    public void setId(Name id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGidNumber() {
        return gidNumber;
    }

    public void setGidNumber(String gidNumber) {
        this.gidNumber = gidNumber;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }

    public void addMember(String member) {
        if (this.members == null){
            this.members = new HashSet<>();
        }
        members.add(member);
    }

    public void removeMember(String member) {
        members.remove(member);
    }

    @Override
    public String toString() {
        return name;
    }

}
