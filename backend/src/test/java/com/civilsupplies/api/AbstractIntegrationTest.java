package com.civilsupplies.api;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

/**
 * Shared configuration for the integration tests.
 *
 * <p>Every IT must declare an identical context configuration so Spring's test context
 * cache hands them all the same context. If two ITs differ — even by one annotation —
 * Spring builds a second context, {@code spring.sql.init} replays V1 against the
 * already-populated in-memory database, and every test in the second class errors with
 * "table already exists". Inheriting the annotations from here keeps them in step.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
abstract class AbstractIntegrationTest {
}
