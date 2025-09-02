package dev.stanislavskyi.feedback_bot_vgr.service.google_docs.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.BatchUpdateDocumentRequest;
import com.google.api.services.docs.v1.model.EndOfSegmentLocation;
import com.google.api.services.docs.v1.model.InsertTextRequest;
import com.google.api.services.docs.v1.model.Request;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GoogleDocsService {

    private Docs docsService;

    @PostConstruct
    public void init() {
        try {
            var resource = new ClassPathResource("service-account.json");
            var credentials = ServiceAccountCredentials.fromStream(resource.getInputStream())
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/documents"));

            docsService = new Docs.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("Feedback Bot")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Google Docs service", e);
        }
    }

    public void appendText(String documentId, String text) {
        try {
            List<Request> requests = Collections.singletonList(
                    new Request().setInsertText(new InsertTextRequest()
                            .setText(text + "\n")
                            .setEndOfSegmentLocation(new EndOfSegmentLocation()))
            );

            BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest().setRequests(requests);
            docsService.documents().batchUpdate(documentId, body).execute();
        } catch (Exception e) {
            throw new RuntimeException("Failed to append text to Google Docs", e);
        }
    }
}
