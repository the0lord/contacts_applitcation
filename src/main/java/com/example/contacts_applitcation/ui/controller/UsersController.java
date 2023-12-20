package com.example.contacts_applitcation.ui.controller;


import com.example.contacts_applitcation.exceptions.UserServiceExceptions;
import com.example.contacts_applitcation.service.AddressService;
import com.example.contacts_applitcation.service.UserService;
import com.example.contacts_applitcation.shared.dto.AddressDTO;
import com.example.contacts_applitcation.shared.dto.UserDto;
import com.example.contacts_applitcation.ui.model.request.UserDetailsRequestModel;
import com.example.contacts_applitcation.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users") // http://localhost:8080/users

public class UsersController {
    @Autowired
    UserService userService;
    @Autowired
    AddressService addressService;
    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
    consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE} )
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page,limit);

        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto,userModel);
            returnValue.add(userModel);
        }

        return returnValue;

    }
    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId,@PathVariable String addressId) {
        AddressDTO addressDto = addressService.getAddress(addressId);

        AddressesRest returnValue = new ModelMapper().map(addressDto, AddressesRest.class);

        // Implementing HATEOAS
        Link userLink = WebMvcLinkBuilder.linkTo(UsersController.class)
                .slash(userId)
                .withRel("user");

        Link userAddressesLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UsersController.class).getUserAddresses(userId))
                .withRel("addresses");

        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(UsersController.class).getUserAddress(userId, addressId))
                .withSelfRel();

        return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));

    }
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE} )
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;
    }
    @PostMapping(produces = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE},
    consumes = {MediaType.APPLICATION_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel user) {
        UserRest returnValue;

        if(user.getFirstName().isEmpty() || user.getLastName().isEmpty() ||
        user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
            throw new UserServiceExceptions(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

//        UserDto userDto = new UserDto();
//        BeanUtils.copyProperties(user,userDto);
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(user, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
//        BeanUtils.copyProperties(createdUser,returnValue);
        returnValue = modelMapper.map(createdUser,UserRest.class);
        return returnValue;
    }
    @PutMapping(path = "/{id}")
    public UserRest updateUser(@PathVariable String id,@RequestBody UserDetailsRequestModel user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user,userDto);
        UserDto createdUser = userService.updateUser(id,userDto);
        UserRest returnValue = new UserRest();
        BeanUtils.copyProperties(createdUser,returnValue);
        return returnValue;
    }
    @DeleteMapping(path = "/{id}")
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        userService.deleteUser(id);
        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationResult(RequestOperationStatus.SUCCES.name());
        return returnValue;
    }
    @GetMapping(path = "/{userId}/addresses", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String userId) {
        List<AddressesRest> returnValue = new ArrayList<>();
        List<AddressDTO> addressDTOS = addressService.getAddresses(userId);

        if (addressDTOS != null && !addressDTOS.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            returnValue = new ModelMapper().map(addressDTOS, listType);

            for (AddressesRest address : returnValue) {
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsersController.class)
                        .getUserAddress(userId, address.getAddressId()))
                        .withSelfRel();
                address.add(selfLink);

            }

        }

        //HATEOS
        Link userLink = WebMvcLinkBuilder.linkTo(UsersController.class)
                .slash(userId)
                .withRel("user");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsersController.class).getUserAddresses(userId))
                .withSelfRel();

        return CollectionModel.of(returnValue).add(userLink).add(selfLink);
    }
    @GetMapping(path="/email-verification",produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token", defaultValue = "") String token) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerify = userService.verifyEmailToken(token);

        if (isVerify == true) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCES.name());
        } else {
            returnValue.setOperationResult(RequestOperationStatus.ERROE.name());
        }


        return returnValue;
    }

}
