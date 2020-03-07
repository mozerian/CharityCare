package com.example.charitycare.data;

public class Posts
{

    public String fullnames, amount, phonenumber,Help;


    public class posts
    {

    }

    public Posts(String fullnames, String help, String amount, String phonenumber)
    {
        this.fullnames = fullnames;
        this.amount = amount;
        this.phonenumber = phonenumber;
        Help = help;
    }

    public String getFullnames() {
        return fullnames;
    }

    public void setFullnames(String fullnames) {
        this.fullnames = fullnames;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getHelp() {
        return Help;
    }

    public void setHelp(String help) {
        Help = help;
    }
}
