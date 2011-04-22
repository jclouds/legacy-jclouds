package org.jclouds.openstack.nova.live.compute;

import com.google.common.collect.ImmutableSet;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import static org.jclouds.openstack.nova.live.PropertyHelper.setupOverrides;
import static org.jclouds.openstack.nova.live.PropertyHelper.setupProperties;

/**
 * Not intended to be run with maven and does not performs a cleanup after tests
 *
 * @author Dmitri Babaev
 */
public class ComputeServiceCheck {
   private ComputeServiceContextFactory contextFactory;
   private ComputeServiceContext context;

   @BeforeTest
   public void setupClient() throws IOException {
      contextFactory = new ComputeServiceContextFactory();
      Properties properties = setupOverrides(setupProperties(this.getClass()));
      context = contextFactory.createContext("nova",
            ImmutableSet.of(new JschSshClientModule(), new SLF4JLoggingModule()), properties);
   }

   @Test
   public void testLists() {
      ComputeService cs = context.getComputeService();

      System.out.println(cs.listImages());
      System.out.println(cs.listHardwareProfiles());
      System.out.println(cs.listAssignableLocations());
      System.out.println(cs.listNodes());
   }

   @Test
   public void testCreateServer() throws RunNodesException {
      ComputeService cs = context.getComputeService();

      TemplateOptions options = new TemplateOptions().blockUntilRunning(false);
      Template template = cs.templateBuilder().imageId("95").hardwareId("2").options(options).build();
      Set<? extends NodeMetadata> metedata = cs.createNodesInGroup("test", 1, template);
      System.out.println(metedata);
   }

   @AfterTest
   public void after() {
      context.close();
   }

   //curl -v -H "X-Auth-User:admin" -H "X-Auth-Key: d744752f-20d3-4d75-979f-f62f16033b07" http://dragon004.hw.griddynamics.net:8774/v1.0/
   //curl -v -H "X-Auth-Token: c97b10659008d5a9ce91462f8c6a5c2c80439762" http://dragon004.hw.griddynamics.net:8774/v1.0/images/detail?format=json

}
