package com.example.productprovenance;

public class TransitParty {
    String transferredTo;
    String productId;
    String companyName;
    String productName;
    String latitudeLocation;
    String longitudeLocation;
    String dateTransferred;

    public TransitParty(String transferredTo,
                        String productId,
                        String companyName,
                        String productName,
                        String latitudeLocation,
                        String longitudeLocation,
                        String dateTransferred) {
        this.productId = productId;
        this.productName = productName;
        this.companyName = companyName;
        this.latitudeLocation = latitudeLocation;
        this.longitudeLocation = longitudeLocation;
        this.dateTransferred = dateTransferred;
        this.transferredTo = transferredTo;
    }
}
