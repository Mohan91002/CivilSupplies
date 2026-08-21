package com.civilsupplies.api;

import com.civilsupplies.api.entity.Enquiry;
import com.civilsupplies.api.entity.EnquiryStatus;
import com.civilsupplies.api.repository.CategoryRepository;
import com.civilsupplies.api.repository.EnquiryRepository;
import com.civilsupplies.api.repository.NewsletterSubscriberRepository;
import com.civilsupplies.api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards the seam between {@code db/migration} and the JPA entities.
 *
 * <p>The {@code it} profile applies the real migrations and sets {@code ddl-auto: validate},
 * so a mismatch between an entity mapping and the migrated schema fails the context load.
 * The {@code test} profile cannot catch that, because {@code create-drop} derives the schema
 * from the entities and so always agrees with them.
 */
class SchemaMigrationIT extends AbstractIntegrationTest {

    @Autowired DataSource dataSource;
    @Autowired CategoryRepository categoryRepository;
    @Autowired ProductRepository productRepository;
    @Autowired EnquiryRepository enquiryRepository;
    @Autowired NewsletterSubscriberRepository newsletterSubscriberRepository;

    @Test
    void entityMappingsValidateAgainstTheMigratedSchema() {
        // Reaching this point means ddl-auto: validate accepted every mapping.
        assertThat(dataSource).isNotNull();
    }

    @Test
    void migrationsCreateEveryExpectedTable() throws Exception {
        List<String> tables = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME").toLowerCase());
                }
            }
        }

        assertThat(tables).contains(
                "categories", "products", "enquiries",
                "quotes", "admin_users", "newsletter_subscribers");
    }

    @Test
    void seedMigrationPopulatedCategoriesAndProducts() throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT (SELECT COUNT(*) FROM categories) AS categories,"
                             + " (SELECT COUNT(*) FROM products) AS products")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("categories")).isEqualTo(5);
            assertThat(rs.getInt("products")).isPositive();
        }
    }

    @Test
    void seededCategoriesAreReadableThroughTheirMapping() {
        assertThat(categoryRepository.count()).isEqualTo(5);
        assertThat(categoryRepository.findAll())
                .extracting(c -> c.getSlug())
                .contains("cement", "tmt-steel");
    }

    @Test
    void seededProductsResolveTheirCategoryRelation() {
        var products = productRepository.findAll();

        assertThat(products).isNotEmpty();
        assertThat(products).allSatisfy(product ->
                assertThat(product.getCategory()).isNotNull());
    }

    /** Writes and re-reads a row, so the columns are proven usable rather than merely present. */
    @Test
    void enquiryRoundTripsAgainstTheMigratedSchema() {
        Enquiry enquiry = new Enquiry();
        enquiry.setName("Integration Test");
        enquiry.setPhone("9505056386");
        enquiry.setEmail("it@example.com");
        enquiry.setCity("Hyderabad");
        enquiry.setProjectType("Commercial");
        enquiry.setMaterials("Cement,TMT Steel");
        enquiry.setQuantity("50 tons");
        enquiry.setMessage("Long enough to exercise the TEXT column mapping end to end.");
        enquiry.setStatus(EnquiryStatus.NEW);

        Long id = enquiryRepository.save(enquiry).getId();
        assertThat(id).isNotNull();

        Enquiry reloaded = enquiryRepository.findById(id).orElseThrow();
        assertThat(reloaded.getMessage()).contains("TEXT column mapping");
        assertThat(reloaded.getStatus()).isEqualTo(EnquiryStatus.NEW);
        assertThat(reloaded.getCreatedAt()).isNotNull();
    }

    @Test
    void newsletterSubscriberRoundTripsAgainstTheMigratedSchema() {
        var subscriber = new com.civilsupplies.api.entity.NewsletterSubscriber();
        subscriber.setEmail("subscriber@example.com");

        Long id = newsletterSubscriberRepository.save(subscriber).getId();

        assertThat(id).isNotNull();
        assertThat(newsletterSubscriberRepository.findById(id)).isPresent();
    }
}
