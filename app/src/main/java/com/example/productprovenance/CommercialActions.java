package com.example.productprovenance;

public interface CommercialActions {
    void onQRScan(String qrCode);
    void onNFCScan(String nfcCode);
    String getNfcCode();
    String getQRCode();
    String[] getInputs();
    void clearInputs();
}
