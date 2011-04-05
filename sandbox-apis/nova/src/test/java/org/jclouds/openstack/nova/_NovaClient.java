package org.jclouds.openstack.nova;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.ssh.jsch.config.JschSshClientModule;

import java.util.Collections;
import java.util.Properties;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;

public class _NovaClient {
    static public void main(String[] args) {
        //curl -v -H "X-Auth-User:admin" -H "X-Auth-Key: d744752f-20d3-4d75-979f-f62f16033b07" http://172.18.34.40:8774/v1.0/
        //curl -v -H "X-Auth-Token: c97b10659008d5a9ce91462f8c6a5c2c80439762" http://172.18.34.40:8774/v1.0/images/detail?format=json

        String identity = "admin";
        String credential = "d744752f-20d3-4d75-979f-f62f16033b07";
        String endpoint = "http://172.18.34.40:8774";

        ComputeServiceContextFactory contextFactory = new ComputeServiceContextFactory();

        Properties overrides = new Properties();
        overrides.setProperty(PROPERTY_ENDPOINT, endpoint);
        ComputeServiceContext context = contextFactory.createContext("nova", identity, credential, Collections.singleton(new JschSshClientModule()), overrides);

        ComputeService cs = context.getComputeService();
        System.out.println(cs.listImages());
        System.out.println(cs.listNodes());
        System.out.println(cs.listAssignableLocations());
        System.out.println(cs.listHardwareProfiles());
    }
}
