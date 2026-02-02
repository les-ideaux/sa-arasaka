package fr.lesideaux.saarasaka.backend.controller;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import fr.lesideaux.saarasaka.backend.data.TestEntity;
import fr.lesideaux.saarasaka.backend.data.TestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.to.email}")
    private String resendToEmail;

    private final TestRepository repository;

    public TestController(TestRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/testSqlite")
    public String getValue() {
        return repository.findById(1L)
                .map(TestEntity::getValue)
                .orElse("Not found");
    }

    @GetMapping("/sendTestEmail")
    public Map<String, String> sendTestEmail() {
        Resend resend = new Resend(resendApiKey);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("onboarding@resend.dev")
                .to(resendToEmail)
                .subject("it works!")
                .html("<strong>hello world</strong>")
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }

        return Map.of("message", "Test email sent!");
    }
}
