package org.acme;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

@Named
@ViewScoped
public class RegistrationForm implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationForm.class);
    @Inject
    @RestClient
    transient RegistrationClient registrationClient;
    @Inject
    ObjectMapper mapper;
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
        name = "";
        surname = null;
        email = "";
        registered = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRegistered() {
        return registered;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}
