package com.civilsupplies.api.service;

import com.civilsupplies.api.common.exception.ResourceNotFoundException;
import com.civilsupplies.api.dto.EnquiryDtos.EnquiryCreateRequest;
import com.civilsupplies.api.dto.EnquiryDtos.EnquiryResponse;
import com.civilsupplies.api.entity.Enquiry;
import com.civilsupplies.api.entity.EnquiryStatus;
import com.civilsupplies.api.repository.EnquiryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnquiryServiceTest {

    @Mock EnquiryRepository enquiryRepository;
    @Mock EmailService emailService;
    @InjectMocks EnquiryService service;

    private static EnquiryCreateRequest request(List<String> materials) {
        return new EnquiryCreateRequest(
                "Ravi Reddy", "9505056386", "ravi@example.com",
                "Hyderabad", "Commercial", materials,
                "50 tons", "Need quote for slab"
        );
    }

    /** Makes save() behave like the DB: assign an id and hand the entity back. */
    private void stubSaveAssigningId(long id) {
        when(enquiryRepository.save(any(Enquiry.class))).thenAnswer(invocation -> {
            Enquiry e = invocation.getArgument(0);
            e.setId(id);
            return e;
        });
    }

    @Test
    void createPersistsEnquiryAndNotifiesAdmin() {
        stubSaveAssigningId(101L);

        EnquiryResponse response = service.createEnquiry(request(List.of("Cement", "TMT Steel")));

        assertThat(response.id()).isEqualTo(101L);
        assertThat(response.name()).isEqualTo("Ravi Reddy");
        assertThat(response.status()).isEqualTo(EnquiryStatus.NEW);

        ArgumentCaptor<Enquiry> persisted = ArgumentCaptor.forClass(Enquiry.class);
        verify(enquiryRepository).save(persisted.capture());
        verify(emailService).sendEnquiryNotification(persisted.getValue());
    }

    @Test
    void materialsCollapseToCommaJoinedColumnAndSplitBackOut() {
        stubSaveAssigningId(102L);

        EnquiryResponse response = service.createEnquiry(request(List.of("Cement", "TMT Steel")));

        ArgumentCaptor<Enquiry> persisted = ArgumentCaptor.forClass(Enquiry.class);
        verify(enquiryRepository).save(persisted.capture());

        assertThat(persisted.getValue().getMaterials()).isEqualTo("Cement,TMT Steel");
        assertThat(response.materials()).containsExactly("Cement", "TMT Steel");
    }

    @Test
    void emptyMaterialsLeaveColumnUnsetAndResponseNull() {
        stubSaveAssigningId(103L);

        EnquiryResponse response = service.createEnquiry(request(List.of()));

        ArgumentCaptor<Enquiry> persisted = ArgumentCaptor.forClass(Enquiry.class);
        verify(enquiryRepository).save(persisted.capture());

        assertThat(persisted.getValue().getMaterials()).isNull();
        assertThat(response.materials()).isNull();
    }

    @Test
    void nullMaterialsAreTolerated() {
        stubSaveAssigningId(104L);

        EnquiryResponse response = service.createEnquiry(request(null));

        assertThat(response.id()).isEqualTo(104L);
        assertThat(response.materials()).isNull();
    }

    @Test
    void updateStatusPersistsNewStatus() {
        Enquiry existing = new Enquiry();
        existing.setId(7L);
        existing.setStatus(EnquiryStatus.NEW);

        when(enquiryRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(enquiryRepository.save(any(Enquiry.class))).thenAnswer(i -> i.getArgument(0));

        EnquiryResponse response = service.updateEnquiryStatus(7L, EnquiryStatus.CLOSED);

        assertThat(response.status()).isEqualTo(EnquiryStatus.CLOSED);
    }

    @Test
    void updateStatusOnMissingEnquiryThrowsAndSavesNothing() {
        when(enquiryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateEnquiryStatus(999L, EnquiryStatus.CLOSED))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(enquiryRepository, never()).save(any(Enquiry.class));
    }
}
