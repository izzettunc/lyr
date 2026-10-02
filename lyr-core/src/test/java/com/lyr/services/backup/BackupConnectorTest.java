package com.lyr.services.backup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.lyr.services.ServiceProvider;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.backup.BackupClient;
import software.amazon.awssdk.services.backup.model.BackupPlansListMember;
import software.amazon.awssdk.services.backup.model.BackupSelection;
import software.amazon.awssdk.services.backup.model.BackupSelectionsListMember;
import software.amazon.awssdk.services.backup.model.GetBackupSelectionRequest;
import software.amazon.awssdk.services.backup.model.GetBackupSelectionResponse;
import software.amazon.awssdk.services.backup.model.ListBackupPlansResponse;
import software.amazon.awssdk.services.backup.model.ListBackupSelectionsRequest;
import software.amazon.awssdk.services.backup.model.ListBackupSelectionsResponse;
import software.amazon.awssdk.services.backup.model.ResourceNotFoundException;
import software.amazon.awssdk.services.backup.paginators.ListBackupPlansIterable;
import software.amazon.awssdk.services.backup.paginators.ListBackupSelectionsIterable;

class BackupConnectorTest {

    final BackupClient mockedBackupClient = Mockito.mock(BackupClient.class);
    BackupConnector testObject;

    @BeforeEach
    void beforeEach() {
        testObject = BackupConnector.create(mockedBackupClient);
    }

    @AfterEach
    void afterEach() {
        reset(mockedBackupClient);
    }

    @Test
    void testThatBackupConnectorGetsBackupClientFromServiceProvider() {
        // Given
        try (final var mockedServiceProvider = mockStatic(ServiceProvider.class)) {
            // When
            BackupConnector.create();
            // Then
            mockedServiceProvider.verify(ServiceProvider::getOrBuildBackupClient, times(1));
        }
    }

    @Test
    void testThatListBackupPlansReturnsListOfBackupPlans() {
        // Given
        final var backupPlans = List.of(
                ListBackupPlansResponse.builder()
                        .backupPlansList(
                                BackupPlansListMember.builder()
                                        .backupPlanId("plan-1")
                                        .build(),
                                BackupPlansListMember.builder()
                                        .backupPlanId("plan-2")
                                        .build())
                        .build(),
                ListBackupPlansResponse.builder()
                        .backupPlansList(BackupPlansListMember.builder()
                                .backupPlanId("plan-3")
                                .build())
                        .build());

        final var mockedListBackupPlansIterable = mock(ListBackupPlansIterable.class);

        final var expectedBackupPlanIds = List.of("plan-1", "plan-2", "plan-3");

        // When
        when(mockedListBackupPlansIterable.stream()).thenReturn(backupPlans.stream());
        when(mockedBackupClient.listBackupPlansPaginator()).thenReturn(mockedListBackupPlansIterable);

        final var actualResult = testObject.listAllBackupPlanIds();

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedBackupPlanIds);
    }

    @Test
    void testThatListBackupPlanSelectionsReturnsListOfBackupSelections() {
        // Given
        final var testObjectSpy = Mockito.spy(testObject);

        final var mockedListOfBackupPlanIds = List.of("plan-1", "plan-2");

        final var mockedListOfBackupPlanOneSelectionIds = List.of("selection-1", "selection-2");
        final var mockedListOfBackupPlanTwoSelectionIds = List.of("selection-3");

        final var backupSelectionOneDetails = BackupSelection.builder()
                .selectionName("selection-1")
                .resources("abc")
                .build();
        final var backupSelectionTwoDetails = BackupSelection.builder()
                .selectionName("selection-2")
                .resources("def")
                .build();
        final var backupSelectionThreeDetails = BackupSelection.builder()
                .selectionName("selection-3")
                .resources("abc", "def")
                .build();

        final var expectedBackupPlanSelections =
                List.of(backupSelectionOneDetails, backupSelectionTwoDetails, backupSelectionThreeDetails);
        // When
        doReturn(mockedListOfBackupPlanIds).when(testObjectSpy).listAllBackupPlanIds();

        doReturn(mockedListOfBackupPlanOneSelectionIds).when(testObjectSpy).listAllBackupPlanSelectionIds("plan-1");
        doReturn(mockedListOfBackupPlanTwoSelectionIds).when(testObjectSpy).listAllBackupPlanSelectionIds("plan-2");
        doReturn(Optional.of(backupSelectionOneDetails))
                .when(testObjectSpy)
                .getBackupPlanSelectionDetails("plan-1", "selection-1");
        doReturn(Optional.of(backupSelectionTwoDetails))
                .when(testObjectSpy)
                .getBackupPlanSelectionDetails("plan-1", "selection-2");
        doReturn(Optional.of(backupSelectionThreeDetails))
                .when(testObjectSpy)
                .getBackupPlanSelectionDetails("plan-2", "selection-3");

        final var actualResult = testObjectSpy.listAllBackupPlanSelections();

        // Then
        assertThat(actualResult).usingRecursiveComparison().isEqualTo(expectedBackupPlanSelections);
    }

    @Test
    void testThatListBackupPlanSelectionIdsReturnsListOfSelectionIds() {
        // Given
        final var backupPlanSelections = List.of(
                ListBackupSelectionsResponse.builder()
                        .backupSelectionsList(List.of(
                                BackupSelectionsListMember.builder()
                                        .selectionId("selection-1")
                                        .build(),
                                BackupSelectionsListMember.builder()
                                        .selectionId("selection-2")
                                        .build()))
                        .build(),
                ListBackupSelectionsResponse.builder()
                        .backupSelectionsList(List.of(BackupSelectionsListMember.builder()
                                .selectionId("selection-3")
                                .build()))
                        .build());

        final var expectedBackupSelectionIds = List.of("selection-1", "selection-2", "selection-3");

        final var mockedListBackupSelectionsIterable = mock(ListBackupSelectionsIterable.class);

        // When
        when(mockedListBackupSelectionsIterable.stream()).thenReturn(backupPlanSelections.stream());
        when(mockedBackupClient.listBackupSelectionsPaginator(any(ListBackupSelectionsRequest.class)))
                .thenReturn(mockedListBackupSelectionsIterable);

        final var actualResult = testObject.listAllBackupPlanSelectionIds("plan-1");

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedBackupSelectionIds);
    }

    @Test
    void testThatGetBackupPlanSelectionDetailsReturnsBackupSelectionDetailsWhenFound() {
        // Given
        final var backupSelectionDetails =
                BackupSelection.builder().selectionName("selection-1").build();
        final var backupSelectionDetailsResponse = GetBackupSelectionResponse.builder()
                .backupSelection(backupSelectionDetails)
                .build();
        // When
        when(mockedBackupClient.getBackupSelection(any(GetBackupSelectionRequest.class)))
                .thenReturn(backupSelectionDetailsResponse);
        final var actualResult = testObject.getBackupPlanSelectionDetails("dummy", "dummy");

        // Then
        assertThat(actualResult).isEqualTo(Optional.of(backupSelectionDetails));
    }

    @Test
    void testThatGetBackupPlanSelectionDetailsReturnsBackupSelectionDetailsWhenNotFound() {
        // Given nothing
        // When
        when(mockedBackupClient.getBackupSelection(any(GetBackupSelectionRequest.class)))
                .thenThrow(ResourceNotFoundException.class);
        final var actualResult = testObject.getBackupPlanSelectionDetails("dummy", "dummy");

        // Then
        assertThat(actualResult).isEqualTo(Optional.empty());
    }
}
