package com.civilsupplies.api.service;

import com.civilsupplies.api.dto.NewsletterDtos.NewsletterSubscribeRequest;
import com.civilsupplies.api.dto.NewsletterDtos.NewsletterSubscribeResponse;
import com.civilsupplies.api.entity.NewsletterSubscriber;
import com.civilsupplies.api.repository.NewsletterSubscriberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsletterService {

    private final NewsletterSubscriberRepository subscriberRepository;

    public NewsletterService(NewsletterSubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @Transactional
    public NewsletterSubscribeResponse subscribe(NewsletterSubscribeRequest request) {
        if (!subscriberRepository.existsByEmail(request.email())) {
            NewsletterSubscriber subscriber = new NewsletterSubscriber(request.email());
            subscriberRepository.save(subscriber);
        }
        return new NewsletterSubscribeResponse(true, "Successfully subscribed to newsletter");
    }
}
