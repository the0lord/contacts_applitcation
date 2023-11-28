package com.example.contacts_applitcation.service;

import com.example.contacts_applitcation.shared.dto.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO getAddress(String addressId);
    List<AddressDTO> getAddresses(String userId);
}
