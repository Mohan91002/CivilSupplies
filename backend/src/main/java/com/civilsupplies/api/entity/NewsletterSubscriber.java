package com.civilsupplies.api.entity;

import com.civilsupplies.api.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "newsletter_subscribers")
public class NewsletterSubscriber extends BaseEntity {

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    public NewsletterSubscriber() {}

    public NewsletterSubscriber(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
