package org.jclouds.openstack.nova;

import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.ssh.jsch.config.JschSshClientModule;

import java.util.Collections;
import java.util.Properties;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;

public class _NovaClient {
    static public void main(String[] args) {
        String identity = "admin";
        String credential = "";
        String endpoint = "";

        ComputeServiceContextFactory contextFactory = new ComputeServiceContextFactory();

        Properties overrides = new Properties();
        overrides.setProperty(PROPERTY_ENDPOINT, endpoint);
        ComputeServiceContext context = contextFactory.createContext("nova", identity, credential, Collections.singleton(new JschSshClientModule()), overrides);
    }
}
