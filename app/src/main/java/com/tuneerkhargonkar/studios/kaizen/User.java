package com.tuneerkhargonkar.studios.kaizen;

import android.util.Log;

import java.util.HashMap;

public class User {
    String name,email,password,uid,empID,department;
    HashMap<String,Kaizen> checkedByMap = new HashMap<String,Kaizen>();
    HashMap<String,Kaizen> approvedByMap = new HashMap<String,Kaizen>();


    public int getNoOfApprovals() {
        return noOfApprovals;
    }

    public void setNoOfApprovals(int noOfApprovals) {
        this.noOfApprovals = noOfApprovals;
    }

    int noOfApprovals = 0;

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HashMap<String, Kaizen> getCheckedByMap() {
        return checkedByMap;
    }

    public void setCheckedByMap(HashMap<String, Kaizen> checkedByMap) {
        this.checkedByMap = checkedByMap;
    }

    public HashMap<String, Kaizen> getApprovedByMap() {

        Log.d("check","approved by map returned");
        return approvedByMap;
    }

    public void setApprovedByMap(HashMap<String, Kaizen> approvedByMap) {
        this.approvedByMap = approvedByMap;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public User(String name, String email, String password, String uid, String empID, int noOfApprovals,String department) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.uid = uid;
        this.empID = empID;
        this.noOfApprovals = noOfApprovals;
        this.department = department;
    }

    public User() { }
}
