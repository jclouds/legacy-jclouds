package org.jclouds.openstack.nova;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.ssh.jsch.config.JschSshClientModule;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;

public class _NovaClient {
   static public void main(String[] args) {
      //curl -v -H "X-Auth-User:admin" -H "X-Auth-Key: d744752f-20d3-4d75-979f-f62f16033b07" http://dragon004.hw.griddynamics.net:8774/v1.0/
      //curl -v -H "X-Auth-Token: c97b10659008d5a9ce91462f8c6a5c2c80439762" http://dragon004.hw.griddynamics.net:8774/v1.0/images/detail?format=json

      String identity = "admin";
      String credential = "d744752f-20d3-4d75-979f-f62f16033b07";
      String endpoint = "http://dragon004.hw.griddynamics.net:8774";

      ComputeServiceContextFactory contextFactory = new ComputeServiceContextFactory();

      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_ENDPOINT, endpoint);
      ComputeServiceContext context = contextFactory.createContext("nova", identity, credential,
            Arrays.asList(new JschSshClientModule(), new SLF4JLoggingModule()), overrides);

      ComputeService cs = context.getComputeService();

      System.out.println(cs.listImages());
      System.out.println(cs.listHardwareProfiles());
      System.out.println(cs.listAssignableLocations());
      System.out.println(cs.listNodes());

      TemplateOptions options = new TemplateOptions();
      Template template = cs.templateBuilder().imageId("13").options(options).build();
      try {
         Set<? extends NodeMetadata> metedata = cs.runNodesWithTag("test", 1, template);
         System.out.println(metedata);
      } catch (RunNodesException e) {
         e.printStackTrace();
      }

      context.close();
   }
}