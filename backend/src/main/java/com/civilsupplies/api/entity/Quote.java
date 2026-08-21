package com.civilsupplies.api.entity;

import com.civilsupplies.api.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "quotes")
public class Quote extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(name = "project_details", columnDefinition = "TEXT")
    private String projectDetails;

    @Column(name = "site_location", length = 300)
    private String siteLocation;

    @Column(name = "boq_filename", length = 300)
    private String boqFilename;

    @Column(name = "boq_file_url", length = 500)
    private String boqFileUrl;

    @Column(length = 100)
    private String timeline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuoteStatus status = QuoteStatus.NEW;

    public Quote() {}

    // ---- Getters & Setters ----

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProjectDetails() { return projectDetails; }
    public void setProjectDetails(String projectDetails) { this.projectDetails = projectDetails; }

    public String getSiteLocation() { return siteLocation; }
    public void setSiteLocation(String siteLocation) { this.siteLocation = siteLocation; }

    public String getBoqFilename() { return boqFilename; }
    public void setBoqFilename(String boqFilename) { this.boqFilename = boqFilename; }

    public String getBoqFileUrl() { return boqFileUrl; }
    public void setBoqFileUrl(String boqFileUrl) { this.boqFileUrl = boqFileUrl; }

    public String getTimeline() { return timeline; }
    public void setTimeline(String timeline) { this.timeline = timeline; }

    public QuoteStatus getStatus() { return status; }
    public void setStatus(QuoteStatus status) { this.status = status; }
}
