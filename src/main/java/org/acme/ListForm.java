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

    private ArrayList<Registration> registrationsList = new ArrayList<Registration>();
    private ArrayList<Registration> selectedList = new ArrayList<>();

    public void loadRecords() {
        registrationsList = registrationClient.getAll();
    }

    public void sendApproval() {
        registrationsList.forEach((n) -> {
            Registration reg = new Registration();
            sendEmail((n.getEmail()));
            reg.setId(n.getId());
            reg.setApproved(true);
            registrationClient.updateRegistrations(reg);
        });
        loadRecords();
    }

    public void sendApprovalToSelected() {

        selectedList.forEach((n) -> {
            Registration reg = new Registration();
            sendEmail((n.getEmail()));
            reg.setId(n.getId());
            reg.setApproved(true);
            registrationClient.updateRegistrations(reg);
        });
        loadRecords();
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

    public ArrayList<Registration> getRegistrationsList() {
        return registrationsList;
    }

    public void setRegistrationsList(ArrayList<Registration> registrationsList) {
        this.registrationsList = registrationsList;
    }

    public ArrayList<Registration> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(ArrayList<Registration> selectedList) {
        this.selectedList = selectedList;
    }
}
