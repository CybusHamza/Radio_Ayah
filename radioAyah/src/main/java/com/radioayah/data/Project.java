package com.radioayah.data;

import java.util.ArrayList;

public class Project {
    String userid, juzid, type, surahid, ayahid, ayahto, ayahfrom, projectid,
            taskid, surahname, ayahtoname, ayahfromname, juzzname;
    ArrayList<Project> members = new ArrayList<Project>();
    String name = "", desc = "", member_name = "", typename = "", isadmin = "",
            canchangestatus = "", status = "", canremove = "",
            removecaption = "";

    /**
     * @return the removecaption
     */
    public String getRemovecaption() {
        return removecaption;
    }

    /**
     * @param removecaption the removecaption to set
     */
    public void setRemovecaption(String removecaption) {
        this.removecaption = removecaption;
    }

    /**
     * @return the member_name
     */
    public String getMember_name() {
        return member_name;
    }

    /**
     * @param member_name the member_name to set
     */
    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    /**
     * @return the typename
     */
    public String getTypename() {
        return typename;
    }

    /**
     * @param typename the typename to set
     */
    public void setTypename(String typename) {
        this.typename = typename;
    }

    /**
     * @return the isadmin
     */
    public String getIsadmin() {
        return isadmin;
    }

    /**
     * @param isadmin the isadmin to set
     */
    public void setIsadmin(String isadmin) {
        this.isadmin = isadmin;
    }

    /**
     * @return the canchangestatus
     */
    public String getCanchangestatus() {
        return canchangestatus;
    }

    /**
     * @param canchangestatus the canchangestatus to set
     */
    public void setCanchangestatus(String canchangestatus) {
        this.canchangestatus = canchangestatus;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the canremove
     */
    public String getCanremove() {
        return canremove;
    }

    /**
     * @param canremove the canremove to set
     */
    public void setCanremove(String canremove) {
        this.canremove = canremove;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return the members
     */
    public ArrayList<Project> getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(ArrayList<Project> members) {
        this.members = members;
    }

    /**
     * @return the surahname
     */
    public String getSurahname() {
        return surahname;
    }

    /**
     * @param surahname the surahname to set
     */
    public void setSurahname(String surahname) {
        this.surahname = surahname;
    }

    /**
     * @return the ayahtoname
     */
    public String getAyahtoname() {
        return ayahtoname;
    }

    /**
     * @param ayahtoname the ayahtoname to set
     */
    public void setAyahtoname(String ayahtoname) {
        this.ayahtoname = ayahtoname;
    }

    /**
     * @return the ayahfromname
     */
    public String getAyahfromname() {
        return ayahfromname;
    }

    /**
     * @param ayahfromname the ayahfromname to set
     */
    public void setAyahfromname(String ayahfromname) {
        this.ayahfromname = ayahfromname;
    }

    /**
     * @return the juzzname
     */
    public String getJuzzname() {
        return juzzname;
    }

    /**
     * @param juzzname the juzzname to set
     */
    public void setJuzzname(String juzzname) {
        this.juzzname = juzzname;
    }

    /**
     * @return the taskid
     */
    public String getTaskid() {
        return taskid;
    }

    /**
     * @param taskid the taskid to set
     */
    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    /**
     * @return the projectid
     */
    public String getProjectid() {
        return projectid;
    }

    /**
     * @param projectid the projectid to set
     */
    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    /**
     * @return the userid
     */
    public String getUserid() {
        return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * @return the juzid
     */
    public String getJuzid() {
        return juzid;
    }

    /**
     * @param juzid the juzid to set
     */
    public void setJuzid(String juzid) {
        this.juzid = juzid;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the surahid
     */
    public String getSurahid() {
        return surahid;
    }

    /**
     * @param surahid the surahid to set
     */
    public void setSurahid(String surahid) {
        this.surahid = surahid;
    }

    /**
     * @return the ayahid
     */
    public String getAyahid() {
        return ayahid;
    }

    /**
     * @param ayahid the ayahid to set
     */
    public void setAyahid(String ayahid) {
        this.ayahid = ayahid;
    }

    /**
     * @return the ayahto
     */
    public String getAyahto() {
        return ayahto;
    }

    /**
     * @param ayahto the ayahto to set
     */
    public void setAyahto(String ayahto) {
        this.ayahto = ayahto;
    }

    /**
     * @return the ayahfrom
     */
    public String getAyahfrom() {
        return ayahfrom;
    }

    /**
     * @param ayahfrom the ayahfrom to set
     */
    public void setAyahfrom(String ayahfrom) {
        this.ayahfrom = ayahfrom;
    }
}
