package eoeqs.service;

import eoeqs.model.ImportHistory;
import eoeqs.model.OAuthUser;
import eoeqs.repository.ImportHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ImportHistoryService {
    private final ImportHistoryRepository importHistoryRepository;

    public ImportHistoryService(ImportHistoryRepository importHistoryRepository) {
        this.importHistoryRepository = importHistoryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveImportHistory(OAuthUser user, String status, Integer importedCount, String errorMessage) {
        ImportHistory importHistory = new ImportHistory();
        importHistory.setStatus(status);
        importHistory.setUser(user);
        importHistory.setTimestamp(LocalDateTime.now());
        importHistory.setObjectsImported(importedCount);
        importHistory.setErrorMessage(errorMessage);

        importHistoryRepository.save(importHistory);
    }
}
