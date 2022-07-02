package com.example.assignment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Creates a CRUD repository
 * Stores all user information in an in-memory db
 * TODO: Change the class name
 */
@RepositoryRestResource
public interface RestRepo extends CrudRepository<UserData, Long> {

}