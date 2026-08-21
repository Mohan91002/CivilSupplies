package com.civilsupplies.api;

import com.civilsupplies.api.repository.EnquiryRepository;
import com.civilsupplies.api.repository.NewsletterSubscriberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises the public HTTP surface end to end: routing, JSON serialisation, bean
 * validation, the security rules, and the JPA layer against the migrated schema.
 */
class PublicApiIT extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired EnquiryRepository enquiryRepository;
    @Autowired NewsletterSubscriberRepository newsletterSubscriberRepository;

    // ---------- catalogue reads ----------

    @Test
    void listsSeededCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[?(@.slug == 'cement')]").exists());
    }

    @Test
    void listsSeededProductsAsAPage() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.content[0].name").exists());
    }

    @Test
    void fetchesASeededProductBySlug() throws Exception {
        mockMvc.perform(get("/api/products/{slug}", "ultratech-opc-53"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("ultratech-opc-53"));
    }

    @Test
    void unknownProductSlugIsNotFound() throws Exception {
        mockMvc.perform(get("/api/products/{slug}", "no-such-product"))
                .andExpect(status().isNotFound());
    }

    // ---------- public writes ----------

    @Test
    void acceptsAValidEnquiryAndPersistsIt() throws Exception {
        long before = enquiryRepository.count();

        mockMvc.perform(post("/api/enquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ravi Reddy",
                                  "phone": "9505056386",
                                  "email": "ravi@example.com",
                                  "city": "Hyderabad",
                                  "projectType": "Commercial",
                                  "materials": ["Cement", "TMT Steel"],
                                  "quantity": "50 tons",
                                  "message": "Need a quote for a slab pour."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.materials.length()").value(2));

        assertThat(enquiryRepository.count()).isEqualTo(before + 1);
    }

    @Test
    void rejectsAnEnquiryWithAMalformedEmail() throws Exception {
        long before = enquiryRepository.count();

        mockMvc.perform(post("/api/enquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ravi Reddy",
                                  "phone": "9505056386",
                                  "email": "not-an-email"
                                }
                                """))
                .andExpect(status().isBadRequest());

        assertThat(enquiryRepository.count()).isEqualTo(before);
    }

    @Test
    void rejectsAnEnquiryMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/api/enquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"phone\": \"\", \"email\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void acceptsANewsletterSubscription() throws Exception {
        mockMvc.perform(post("/api/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"reader@example.com\"}"))
                .andExpect(status().isOk());

        assertThat(newsletterSubscriberRepository.count()).isPositive();
    }

    // ---------- security ----------

    @Test
    void listingEnquiriesRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/enquiries"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void adminUserListingRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void actuatorHealthIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}
