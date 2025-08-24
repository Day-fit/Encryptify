package pl.dayfit.encryptifyemail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyemail.configuration.EmailConfigurationProperties;
import pl.dayfit.encryptifyemail.event.EmailVerificationCodeEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(EmailConfigurationProperties.class)
public class EmailSenderService {
    private final JavaMailSender mailSender;
    private final EmailConfigurationProperties emailConfigurationProperties;

    @Value("${encryptify.domain}")
    private String encryptifyDomain;

    /**
     * Handles sending an email containing a verification code to the recipient specified in the event.
     * The verification code expires after the duration defined in the configuration properties.
     *
     * @param event the RabbitMQ event containing recipient details and verification data
     * @throws MessagingException if there is an error while constructing or sending the email
     * @throws IOException if an I/O error occurs while processing email templates or attachments
     */
    @RabbitListener(queues = "email.sender")
    public void sendVerificationEmail(EmailVerificationCodeEvent event) throws MessagingException, IOException {
        String username = event.username();
        int code = event.code();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setSubject(emailConfigurationProperties.getSubject());
        helper.setFrom(emailConfigurationProperties.getFrom());
        helper.addTo(event.email());
        helper.setText(loadEmailTemplate()
                        .replace("$ENCRYPTIFY_DOMAIN", encryptifyDomain)
                        .replace("$USERNAME", username)
                        .replace("$CODE", String.valueOf(code)), true);

        mailSender.send(message);
    }

    private String loadEmailTemplate() throws IOException
    {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource emailTemplate = resourceLoader.getResource("classpath:email-template.html");
        return emailTemplate.getContentAsString(StandardCharsets.UTF_8);
    }
}
