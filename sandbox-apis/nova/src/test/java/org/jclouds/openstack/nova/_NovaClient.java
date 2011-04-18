package org.jclouds.openstack.nova;

import com.google.common.collect.ImmutableSet;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.ssh.jsch.config.JschSshClientModule;

import java.util.Properties;

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
            ImmutableSet.of(new JschSshClientModule(), new SLF4JLoggingModule()), overrides);

      ComputeService cs = context.getComputeService();

      System.out.println(cs.listImages());
      System.out.println(cs.listHardwareProfiles());
      System.out.println(cs.listAssignableLocations());
      System.out.println(cs.listNodes());

      /*TemplateOptions options = new TemplateOptions().blockUntilRunning(false);
      Template template = cs.templateBuilder().imageId("13").options(options).build();
      try {
         Set<? extends NodeMetadata> metedata = cs.runNodesWithTag("test", 1, template);
         System.out.println(metedata);
      } catch (RunNodesException e) {
         e.printStackTrace();
      }*/

      //System.out.println(cs.getNodeMetadata("64"));

      //cs.destroyNode("64");

      context.close();
   }
}
