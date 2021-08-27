package com.ss.pizzeria.backend.data.dao;

import com.ss.pizzeria.backend.data.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

}
