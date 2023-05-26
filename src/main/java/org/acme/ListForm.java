package org.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;

@Named
@ViewScoped
public class ListForm implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationForm.class);
    @Inject
    @RestClient
    transient RegistrationClient registrationClient;
    @Inject
    ObjectMapper mapper;
    @Inject
    Mailer mailer;

    private ArrayList<Registration> allRegistrations = new ArrayList<Registration>();

    public void loadRecords() {
        allRegistrations = registrationClient.getAll();
    }

    public void sendApproval() {
        allRegistrations.forEach((n) -> {
            sendEmail((n.getEmail()));
            n.setApproved(true);
        });
    }

    public void sendEmail(String email) {
        LOG.debug("Sending approve to {}", email);
        mailer.send(
                Mail.withText(email,
                        "Your registration is approved",
                        "Congratulations! Your registration is approved."
                )
        );
    }

    public ArrayList<Registration> getAllRegistrations() {
        return allRegistrations;
    }

    public void setAllRegistrations(ArrayList<Registration> allRegistrations) {
        this.allRegistrations = allRegistrations;
    }
}
