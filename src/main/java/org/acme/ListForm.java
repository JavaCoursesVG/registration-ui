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
    private ArrayList<Registration> selectedRegistrations = new ArrayList<>();

    public void loadRecords() {
        allRegistrations = registrationClient.getAll();
    }

    public void sendApproval() {
        allRegistrations.forEach((n) -> {
            sendEmail((n.getEmail()));
            n.setApproved(true);
        });
    }

    public void sendApprovalToSelected() {

        selectedRegistrations.forEach((n) -> {
            RegistrationDTO reg = new RegistrationDTO();
            sendEmail((n.getEmail()));
            n.setApproved(true);
            allRegistrations.set((int) n.getId() - 1, n);
            reg.setName(n.getName());
            reg.setSurname(n.getSurname());
            reg.setEmail(n.getEmail());
            reg.setApproved(n.getApproved());
            registrationClient.updateRegistrations(reg);
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

    public ArrayList<Registration> getSelectedRegistrations() {
        return selectedRegistrations;
    }

    public void setSelectedRegistrations(ArrayList<Registration> selectedRegistrations) {
        this.selectedRegistrations = selectedRegistrations;
    }
}
