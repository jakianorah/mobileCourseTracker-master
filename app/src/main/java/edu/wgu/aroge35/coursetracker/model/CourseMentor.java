package edu.wgu.aroge35.coursetracker.model;

/**
 *
 */
public class CourseMentor {
    private long id;
    private long courseId;
    private String name;
    private String workEmail;
    private String homeEmail;
    private String otherEmail;
    private String workPhone;
    private String homePhone;
    private String cellPhone;

    public CourseMentor() {
    }

    public CourseMentor(long courseId, String name, String workEmail, String homeEmail, String otherEmail, String workPhone, String homePhone, String cellPhone) {
        this.courseId = courseId;
        this.name = name;
        this.workEmail = workEmail;
        this.homeEmail = homeEmail;
        this.otherEmail = otherEmail;
        this.workPhone = workPhone;
        this.homePhone = homePhone;
        this.cellPhone = cellPhone;
    }

    public CourseMentor(long id, long courseId, String name, String workEmail, String homeEmail, String otherEmail, String workPhone, String homePhone, String cellPhone) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.workEmail = workEmail;
        this.homeEmail = homeEmail;
        this.otherEmail = otherEmail;
        this.workPhone = workPhone;
        this.homePhone = homePhone;
        this.cellPhone = cellPhone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getHomeEmail() {
        return homeEmail;
    }

    public void setHomeEmail(String homeEmail) {
        this.homeEmail = homeEmail;
    }

    public String getOtherEmail() {
        return otherEmail;
    }

    public void setOtherEmail(String otherEmail) {
        this.otherEmail = otherEmail;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
