package org.acme;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.smallrye.common.annotation.Blocking;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;

@Named
@ViewScoped
public class RegistrationForm implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationForm.class);
    @Inject
    @RestClient
    transient RegistrationClient registrationClient;
    @Inject
    ObjectMapper mapper;
    @Inject
    Mailer mailer;
    @NotNull
    @NotBlank
    @Length(min = 5, max = 20)
    private String name;
    private String surname;
    @NotNull
    @NotBlank
    @Length(min = 5, max = 50)
    private String email;
    private boolean registered;
    private boolean approved;
    private ArrayList<Registration> allRegistrations = new ArrayList<Registration>();



    private ArrayList<Registration> approvedRegistrations = new ArrayList<Registration>();

    public void register() {
        LOG.debug("registering {}", name);
        var reg = new RegistrationDTO();
        reg.setName(name);
        reg.setSurname(surname);
        reg.setEmail(email);
        reg.setApproved(false);
        registrationClient.register(reg);
        registered = true;
        approved = false;
    }

    public void reset() {
        LOG.debug("resetting");
        name = null;
        surname = null;
        email = null;
        registered = false;
    }

    public void loadRecords() {
        allRegistrations = registrationClient.getAll();
    }

    public void sendEmail(String email) {
        mailer.send(
                Mail.withText(email,
                        "Your registration is approved",
                        "Congratulations! Your registration is approved."
                )
        );
    }

    public void sendApproval() {
        allRegistrations.forEach((n) -> sendEmail((n.getEmail())));
        approvedRegistrations = allRegistrations;
        allRegistrations.forEach((n) -> n.setApproved(true));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public boolean isRegistered() {
        return registered;
    }

    public boolean isApproved() { return approved; }

    public void setApproved(Boolean approved) { this.approved = approved; }

    public ArrayList<Registration> getAllRegistrations() {
        return allRegistrations;
    }

    public void setAllRegistrations(ArrayList<Registration> allRegistrations) {
        this.allRegistrations = allRegistrations;
    }

    public ArrayList<Registration> getApprovedRegistrations() {
        return approvedRegistrations;
    }

    public void setApprovedRegistrations(ArrayList<Registration> approvedRegistrations) {
        this.approvedRegistrations = approvedRegistrations;
    }
}
