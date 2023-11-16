package com.example.contacts_applitcation.io;

import com.example.contacts_applitcation.io.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity,Long> {
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String id);
    Page<UserEntity> findAll(Pageable pageable);
}
