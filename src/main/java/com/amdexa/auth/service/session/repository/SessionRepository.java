package com.amdexa.auth.service.session.repository;

import com.amdexa.auth.service.session.model.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository  extends CrudRepository<Session, Long> {
}
