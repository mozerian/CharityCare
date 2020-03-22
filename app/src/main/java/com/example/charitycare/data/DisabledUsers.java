package com.example.charitycare.data;

public class DisabledUsers {

    public String  phoneNumber,amount,disabiliType,fullName, help;

    public DisabledUsers(String phoneNumber, String amount, String disabiliType, String fullName, String help) {
        this.phoneNumber = phoneNumber;
        this.amount = amount;
        this.disabiliType = disabiliType;
        this.fullName = fullName;
        this.help = help;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDisabiliType() {
        return disabiliType;
    }

    public void setDisabiliType(String disabiliType) {
        this.disabiliType = disabiliType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }
}
