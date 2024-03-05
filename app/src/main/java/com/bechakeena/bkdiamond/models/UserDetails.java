package com.bechakeena.bkdiamond.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetails {

    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("zoneId")
    @Expose
    private Integer zoneId;
    @SerializedName("zone")
    @Expose
    private String zone;
    @SerializedName("userStatus")
    @Expose
    private String userStatus;
    @SerializedName("userTypeId")
    @Expose
    private Integer userTypeId;
    @SerializedName("userTypeName")
    @Expose
    private String userTypeName;
    @SerializedName("userRole")
    @Expose
    private Integer userRole;
    @SerializedName("userRoleName")
    @Expose
    private String userRoleName;
    @SerializedName("accountNo")
    @Expose
    private String accountNo;
    @SerializedName("contactId")
    @Expose
    private Integer contactId;
    @SerializedName("storeName")
    @Expose
    private String storeName;
    @SerializedName("storePhoto")
    @Expose
    private String storePhoto;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("userPhoto")
    @Expose
    private String userPhoto;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phoneNo")
    @Expose
    private String phoneNo;
    @SerializedName("mobileNo")
    @Expose
    private String mobileNo;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("dob")
    @Expose
    private Long dob;
    @SerializedName("nid")
    @Expose
    private String nid;
    @SerializedName("tradeLicense")
    @Expose
    private String tradeLicense;
    @SerializedName("addressId")
    @Expose
    private Integer addressId;
    @SerializedName("addressLine")
    @Expose
    private String addressLine;
    @SerializedName("house")
    @Expose
    private String house;
    @SerializedName("road")
    @Expose
    private String road;
    @SerializedName("block")
    @Expose
    private String block;
    @SerializedName("sector")
    @Expose
    private String sector;
    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("zipCode")
    @Expose
    private String zipCode;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;

    public Integer getUserId() {
        return userId;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public String getZone() {
        return zone;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public Integer getUserTypeId() {
        return userTypeId;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public Integer getUserRole() {
        return userRole;
    }

    public String getUserRoleName() {
        return userRoleName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public Integer getContactId() {
        return contactId;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStorePhoto() {
        return storePhoto;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getGender() {
        return gender;
    }

    public Long getDob() {
        return dob;
    }

    public String getNid() {
        return nid;
    }

    public String getTradeLicense() {
        return tradeLicense;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getHouse() {
        return house;
    }

    public String getRoad() {
        return road;
    }

    public String getBlock() {
        return block;
    }

    public String getSector() {
        return sector;
    }

    public String getSection() {
        return section;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
