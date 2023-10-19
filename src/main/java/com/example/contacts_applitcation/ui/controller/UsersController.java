package com.example.contacts_applitcation.ui.controller;


import com.example.contacts_applitcation.service.UserService;
import com.example.contacts_applitcation.shared.dto.UserDto;
import com.example.contacts_applitcation.ui.model.request.UserDetailsRequestModel;
import com.example.contacts_applitcation.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users") // http://localhost:8080/users

public class UsersController {
    @Autowired
    UserService userService;
    @GetMapping(path = "/{id}")
    public UserRest getUsers(@PathVariable String id) {
        UserRest returnValue  = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);
        return returnValue;

    }
    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel user) {
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user,userDto);
        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser,returnValue);
        return returnValue;
    }
    @PutMapping("/users")
    public UserRest updateUser(@PathVariable String id,@RequestBody UserDetailsRequestModel user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user,userDto);
        UserDto createdUser = userService.updateUser(id,userDto);
        UserRest returnValue = new UserRest();
        BeanUtils.copyProperties(createdUser,returnValue);
        return returnValue;
    }
    @DeleteMapping(path = "/{id}")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "User with id" + id + "has been deleted";
    }



}
