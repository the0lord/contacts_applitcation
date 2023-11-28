package com.example.contacts_applitcation.service.impl;

import com.example.contacts_applitcation.io.AddressRepository;
import com.example.contacts_applitcation.io.UserRepository;
import com.example.contacts_applitcation.io.entity.AddressEntity;
import com.example.contacts_applitcation.io.entity.UserEntity;
import com.example.contacts_applitcation.service.AddressService;
import com.example.contacts_applitcation.shared.dto.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressesServiceImpl implements AddressService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;
    @Override
    public List<AddressDTO> getAddresses(String id) {
        ModelMapper modelMapper = new ModelMapper();
        List<AddressDTO> returnValue = new ArrayList<>();

        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) {
            return returnValue;
        }

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity address : addresses) {
            returnValue.add(modelMapper.map(address, AddressDTO.class));
        }
        return returnValue;
    }
    @Override
    public AddressDTO getAddress(String addressId) {
        AddressDTO returnValue = null;
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if (addressEntity != null) {
            returnValue = new ModelMapper().map(addressEntity, AddressDTO.class);
        }
        return returnValue;
    }
}
