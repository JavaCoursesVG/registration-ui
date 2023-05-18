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
    private boolean registered;

    public void register() {
        LOG.debug("registering {}", name);
        var reg = new RegistrationDTO();
        reg.setName(name);
        registrationClient.register(reg);
        registered = true;
    }

    public void reset() {
        LOG.debug("resetting");
        name = null;
        registered = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRegistered() {
        return registered;
    }
}
