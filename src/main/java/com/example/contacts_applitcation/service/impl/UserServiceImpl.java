package com.example.contacts_applitcation.service.impl;

import com.example.contacts_applitcation.io.UserRepository;
import com.example.contacts_applitcation.io.entity.UserEntity;
import com.example.contacts_applitcation.service.UserService;
import com.example.contacts_applitcation.shared.Utils;
import com.example.contacts_applitcation.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    Utils utils;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public UserDto createUser(UserDto userDto) {
        UserEntity storedUserDetails = userRepository.findByEmail(userDto.getEmail());
        if (storedUserDetails != null) {
            throw new RuntimeException("Record already exist");
        }


        UserEntity userEntity = new UserEntity();
        System.out.println(userDto);
        BeanUtils.copyProperties(userDto,userEntity);

        String publicUserID = utils.generateUserId(30);
        userEntity.setUserId(publicUserID);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        System.out.println(userEntity);
        System.out.println(userDto.getPassword());
        System.out.println(bCryptPasswordEncoder.encode(userDto.getPassword()));


        UserEntity storedUser = userRepository.save(userEntity);
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedUser,returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }

        return new User(username, userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
