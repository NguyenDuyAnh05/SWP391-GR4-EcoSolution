package org.swp391_group4_backend.ecosolution.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.entity.SubscriptionTier;
import org.swp391_group4_backend.ecosolution.entity.Ward;
import org.swp391_group4_backend.ecosolution.service.SubscriptionTierService;
import org.swp391_group4_backend.ecosolution.service.WardService;

import java.util.List;

@RestController

@RequestMapping("/api/v1/meta")

@CrossOrigin(origins = "*")
public class MetaDataController {
    private final WardService wardService;
    private final SubscriptionTierService subscriptionTierService;

    public MetaDataController(WardService wardService, SubscriptionTierService subscriptionTierService) {
        this.wardService = wardService;
        this.subscriptionTierService = subscriptionTierService;
    }

    @GetMapping("/wards")
    public ResponseEntity<List<Ward>> getWards(){
        return new ResponseEntity<>(wardService.getAllWards(), HttpStatus.OK);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<SubscriptionTier>> getSubscriptionTiers(){
        return new ResponseEntity<>(subscriptionTierService.getAllTiers(), HttpStatus.OK);
    }

}
