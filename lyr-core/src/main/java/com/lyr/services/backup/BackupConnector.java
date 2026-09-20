package com.lyr.services.backup;

import com.lyr.services.ServiceProvider;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.services.backup.BackupClient;
import software.amazon.awssdk.services.backup.model.BackupPlansListMember;
import software.amazon.awssdk.services.backup.model.BackupSelection;
import software.amazon.awssdk.services.backup.model.BackupSelectionsListMember;
import software.amazon.awssdk.services.backup.model.GetBackupSelectionRequest;
import software.amazon.awssdk.services.backup.model.ListBackupPlansResponse;
import software.amazon.awssdk.services.backup.model.ListBackupSelectionsRequest;
import software.amazon.awssdk.services.backup.model.ListBackupSelectionsResponse;
import software.amazon.awssdk.services.backup.model.ResourceNotFoundException;

public final class BackupConnector {

    private final BackupClient client;

    private BackupConnector(final BackupClient backupClient) {
        this.client = backupClient;
    }

    public static BackupConnector create() {
        return new BackupConnector(ServiceProvider.getOrBuildBackupClient());
    }

    public static BackupConnector create(final BackupClient backupClient) {
        return new BackupConnector(backupClient);
    }

    public List<BackupSelection> listAllBackupPlanSelections() {
        return listAllBackupPlanIds().stream()
                .flatMap(backupPlanId -> listAllBackupPlanSelectionIds(backupPlanId).stream()
                        .map(backupSelectionId -> getBackupPlanSelectionDetails(backupPlanId, backupSelectionId)))
                .flatMap(Optional::stream)
                .toList();
    }

    public Optional<BackupSelection> getBackupPlanSelectionDetails(
            final String backupPlanId, final String selectionId) {
        try {
            final var request = GetBackupSelectionRequest.builder()
                    .backupPlanId(backupPlanId)
                    .selectionId(selectionId)
                    .build();
            return Optional.of(client.getBackupSelection(request).backupSelection());
        } catch (final ResourceNotFoundException resourceNotFoundException) {
            return Optional.empty();
        }
    }

    public List<String> listAllBackupPlanSelectionIds(final String backupPlanId) {
        final var request =
                ListBackupSelectionsRequest.builder().backupPlanId(backupPlanId).build();
        return client.listBackupSelectionsPaginator(request).stream()
                .map(ListBackupSelectionsResponse::backupSelectionsList)
                .flatMap(List::stream)
                .map(BackupSelectionsListMember::selectionId)
                .toList();
    }

    public List<String> listAllBackupPlanIds() {
        return client.listBackupPlansPaginator().stream()
                .map(ListBackupPlansResponse::backupPlansList)
                .flatMap(List::stream)
                .map(BackupPlansListMember::backupPlanId)
                .toList();
    }
}
