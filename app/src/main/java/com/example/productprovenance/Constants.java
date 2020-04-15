package com.example.productprovenance;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.Map;

public final class Constants {

    public static Map<String, String> accounts = new HashMap<String, String>();

    // NFC responses
    public static final int NFC_ENABLED = 0;
    public static final int NFC_DISABLED = 1;
    public static final int NFC_NOT_SUPPORTED = 2;

    // max/min latitude and longitude
    public static final int LATITUDE_MAX = 90;
    public static final int LATITUDE_MIN = -90;
    public static final int LONGITUDE_MIN = -180;
    public static final int LONGITUDE_MAX = 180;

    // permissions request code
    public static final int REQUEST_LOCATION_PERMISSION = 300;
    public static final int REQUEST_INTERNET_PERMISSION = 302;

    // INTENT RETURN CODES
    public static final int PICK_ACCOUNT_REQUEST = 1001;
    public static final int SELECT_ACCOUNT_REQUEST = 1002;

    public static final int QR_SCAN_ACTION = 1101;
    public static final int QR_SCAN_TRANSFER_PRODUCT = 1102;
    public static final int QR_SCAN_TRANSFER_TO_ACCOUNT = 1103;
    public static final int QR_SCAN_SET_PRODUCT_SELLER = 1104;

    public static final int NFC_SCAN_ACTION = 1201;

    public static final int SKIP_LOGIN_ACTION = 1990;

    // Intent extras
    public static final String GET_SCANNED_QR_DATA = "getScannedQRData";
    public static final String GET_SCANNED_NFC_DATA = "getScannedNFCData";
    public static final String GET_SELECTED_ACCOUNT_DATA = "loginAccountAddress";

    // SHARED PREFERENCES VARIABLES
    public static final String userDataStore = "userDetails";
    public static final String accountAddress = "accountAddress";
    public static final String accountName= "accountName";

    // WEB API REQUESTS
    public static final String ROUTE_DEFAULT = "http://192.168.0.10:5000/";

    public static final String GET_ACCOUNTS = ROUTE_DEFAULT + "accounts";
    public static final String GET_PRODUCT_DETAILS = ROUTE_DEFAULT + "product-details";
    public static final String GET_ALL_PRODUCTS = ROUTE_DEFAULT + "all-products";
    public static final String GET_PRODUCT_STATE = ROUTE_DEFAULT + "product-state";
    public static final String GET_CONTRACT_OWNER = ROUTE_DEFAULT + "contract-owner";

    public static final String POST_CREATE_USER = ROUTE_DEFAULT + "add-user";
    public static final String POST_CREATE_PRODUCT_CONTRACT = ROUTE_DEFAULT + "add-contract";
    public static final String POST_TRANSFER_PRODUCT = ROUTE_DEFAULT + "transfer-product";
    public static final String POST_RETURN_PRODUCT = ROUTE_DEFAULT + "return-product";
    public static final String POST_RESELL_PRODUCT = ROUTE_DEFAULT + "resell-product";
    public static final String POST_SELL_PRODUCT = ROUTE_DEFAULT + "sell-product";
    public static final String POST_EDIT_PRODUCT = ROUTE_DEFAULT + "edit-product";

    public static final String TRACKER_CONTRACT = "0x3449BB31ad95585Fe455b0713d2286a3FEcf0a50";
    public static final String PRODUCT_ON_CHAIN = "0x000000000000000000000000b78728350da4bf2cc308ab081eb8eea3a3be34d4";

    public static final int GET_REQUEST = Request.Method.GET;
    public static final int POST_REQUEST = Request.Method.POST;



    // POPULATING ACCOUNTS HASHMAP WITH VALUES LINUX MINT
//    static {
//        accounts.put("MANUFACTURER1", "0x0B4f77e363D616619EC62b6c170ec89dA009D3C1");
//        accounts.put("MANUFACTURER2", "0xfD2508300712d411A3A8a0A9ef977A125660862e");
//        accounts.put("SELLER1", "0xC84c54141C5bbB2beBDBEe60A72c382EdA4Ac946");
//        accounts.put("SELLER2", "0x91d99C048e487da7060F8362Fa1196236fd2b115");
//        accounts.put("DISTRIBUTOR1", "0x41aC78c7De22908a06173F1046bE05C85477f6d3");
//        accounts.put("DISTRIBUTOR2", "0x334805D9FC31F4325C8376b9C731B386A3fF0455");
//        accounts.put("ADMIN", "0x2Cd755E0836C99eDea2E7543B608b0440dF4746F");
//    }

    // POPULATING ACCOUNTS HASHMAP WITH VALUES WINDOWS 10
    static {
        accounts.put("MANUFACTURER1", "0x95bEc0f0067DBdd06Ad791440B77f80F7943D66d");
        accounts.put("MANUFACTURER2", "0x95bEc0f0067DBdd06Ad791440B77f80F7943D66d");
        accounts.put("SELLER1", "0xbD5Fbf4724C5607C397968bf065BC93536aB7726");
        accounts.put("SELLER2", "0xe3BE60D77C3f0fa27a9b82c48eC8b9fA75285d99");
        accounts.put("DISTRIBUTOR1", "0xB3DC0e9F140C06e4188F0c3096B3Df54AA7f6f5D");
        accounts.put("DISTRIBUTOR2", "0x4Da0ec157f15D7072C17962A6E2371A014E6f557");
        accounts.put("ADMIN", "0x0e92cb2a4B41990e290Ac79a883587B40eBbF0dC");
    }


}
