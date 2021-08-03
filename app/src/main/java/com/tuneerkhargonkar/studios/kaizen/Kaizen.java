package com.tuneerkhargonkar.studios.kaizen;

import android.widget.ImageView;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Kaizen implements Serializable {
    String start_date, end_date;
    String currentDate;
    String pillar,department;
    List<String> category;
    String machine_name, idea_of_kaizen, problem, analysis, counter_measures, results, uid;
    String beforeUriPath, afterUriPath;
    List<String> name;
    String kaizenid;
    String madeby_string, checkedby_string, approvedby_string, helpedby_string;
    boolean MADEBY_boolean, CHECKEDBY_boolean, APPROVEDBY_boolean, HELPEDBY_boolean;


    // CONSTRUCTORS

    public Kaizen(String start_date, String end_date, String pillar, List<String> category, String machine_name, String idea_of_kaizen,
                  String problem, String analysis, String counter_measures, String results, String beforeUriPath, String afterUriPath,
                  List<String> name, String uid , String kaizenid, String madeby_string, String checkedby_string, String approvedby_string, String helpedby_string,String department,String currentDate)
    {
        this.start_date = start_date;
        this.end_date = end_date;
        this.pillar = pillar;
        this.category = category;
        this.machine_name = machine_name;
        this.idea_of_kaizen = idea_of_kaizen;
        this.problem = problem;
        this.analysis = analysis;
        this.counter_measures = counter_measures;
        this.results = results;
        this.uid = uid;
        this.beforeUriPath = beforeUriPath;
        this.afterUriPath = afterUriPath;
        this.name = name;
        this.kaizenid = kaizenid;
        this.madeby_string = madeby_string;
        this.checkedby_string = checkedby_string;
        this.approvedby_string = approvedby_string;
        this.helpedby_string = helpedby_string;
        this.department=department;
        this.currentDate = currentDate;

    }

    public Kaizen(String start_date, String end_date, String pillar, List<String> category, String machine_name, String idea_of_kaizen, String problem,
                  String analysis, String counter_measures, String results, List<String> name, String uid, String kaizenid, String madeby_string,
                  String checkedby_string, String approvedby_string, String helpedby_string,String department,String currentDate)
    {
        this.start_date = start_date;
        this.end_date = end_date;
        this.pillar = pillar;
        this.category = category;
        this.machine_name = machine_name;
        this.idea_of_kaizen = idea_of_kaizen;
        this.problem = problem;
        this.analysis = analysis;
        this.counter_measures = counter_measures;
        this.results = results;
        this.uid = uid;
        this.name = name;
        this.kaizenid = kaizenid;
        this.madeby_string = madeby_string;
        this.checkedby_string = checkedby_string;
        this.approvedby_string = approvedby_string;
        this.helpedby_string = helpedby_string;
        this.department=department;
        this.currentDate = currentDate;
    }

    public Kaizen() { }


    //GETTERS


    public String getCurrentDate() { return currentDate; }

    public String getDepartment() {
        return department;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getPillar() {
        return pillar;
    }

    public List<String> getCategory() {
        return category;
    }

    public String getMachine_name() {
        return machine_name;
    }

    public String getIdea_of_kaizen() {
        return idea_of_kaizen;
    }

    public String getProblem() {
        return problem;
    }

    public String getAnalysis() {
        return analysis;
    }

    public String getCounter_measures() {
        return counter_measures;
    }

    public String getResults() {
        return results;
    }

    public String getUid() {
        return uid;
    }

    public String getBeforeUriPath() {
        return beforeUriPath;
    }

    public String getAfterUriPath() {
        return afterUriPath;
    }

    public List<String> getName() {
        return name;
    }

    public String getKaizenid() {
        return kaizenid;
    }

    public String getMadeby_string() {
        return madeby_string;
    }

    public String getCheckedby_string() {
        return checkedby_string;
    }

    public String getApprovedby_string() {
        return approvedby_string;
    }

    public String getHelpedby_string() {
        return helpedby_string;
    }

    public boolean isMADEBY_boolean() {
        return MADEBY_boolean;
    }

    public boolean isCHECKEDBY_boolean() {
        return CHECKEDBY_boolean;
    }

    public boolean isAPPROVEDBY_boolean() {
        return APPROVEDBY_boolean;
    }

    public boolean isHELPEDBY_boolean() {
        return HELPEDBY_boolean;
    }

    //SETTERS


    public void setDepartment(String department) {
        this.department = department;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public void setPillar(String pillar) {
        this.pillar = pillar;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public void setMachine_name(String machine_name) {
        this.machine_name = machine_name;
    }

    public void setIdea_of_kaizen(String idea_of_kaizen) {
        this.idea_of_kaizen = idea_of_kaizen;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public void setCounter_measures(String counter_measures) { this.counter_measures = counter_measures; }

    public void setResults(String results) {
        this.results = results;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setBeforeUriPath(String beforeUriPath) {
        this.beforeUriPath = beforeUriPath;
    }

    public void setAfterUriPath(String afterUriPath) {
        this.afterUriPath = afterUriPath;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public void setKaizenid(String kaizenid) {
        this.kaizenid = kaizenid;
    }

    public void setMadeby_string(String madeby_string) {
        this.madeby_string = madeby_string;
    }

    public void setCheckedby_string(String checkedby_string) {
        this.checkedby_string = checkedby_string;
    }

    public void setApprovedby_string(String approvedby_string) {
        this.approvedby_string = approvedby_string;
    }

    public void setHelpedby_string(String helpedby_string) {
        this.helpedby_string = helpedby_string;
    }

    public void setMADEBY_boolean(boolean MADEBY_boolean) {
        this.MADEBY_boolean = MADEBY_boolean;
    }

    public void setCHECKEDBY_boolean(boolean CHECKEDBY_boolean) {
        this.CHECKEDBY_boolean = CHECKEDBY_boolean;
    }

    public void setAPPROVEDBY_boolean(boolean APPROVEDBY_boolean) {
        this.APPROVEDBY_boolean = APPROVEDBY_boolean;
    }

    public void setHELPEDBY_boolean(boolean HELPEDBY_boolean) {
        this.HELPEDBY_boolean = HELPEDBY_boolean;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}