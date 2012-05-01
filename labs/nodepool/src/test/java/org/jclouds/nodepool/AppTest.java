/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.nodepool;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.enterprise.config.EnterpriseConfigurationModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.nodepool.internal.BasePooledComputeService;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.java.InstallJDK;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
   private String identity;
   private String credential;
   private String providerName;
   private File privateKey;
   private File publicKey;
   private String endPointUrl;
   public String profile;
   private int retentionTime;
   public int instanceCap;

   private String imageId;
   private String osFamily;
   private String osVersion;
   private String hardwareId;
   private int ram;
   private int cores;
   private String initScript;
   private String name;
   private boolean stopOnTerminate;
   private ComputeService compute;
   private PooledComputeService pooledCompute;
   private Collection<NodeMetadata> nodes = new LinkedList<NodeMetadata>();
   private static Logger LOGGER = Logger.getLogger("AppTest");
   
   private long time;

   @Override
   protected void setUp() throws Exception {
      // setup JCloudsCloud
      identity = "insert-your-identity-here";
      credential = "insert-your-credential-here";
      providerName = "aws-ec2";
      privateKey = new File("private-key");
      publicKey = new File("public-key");
      endPointUrl = "";
      profile = "aws-slave-profile";
      retentionTime = -1;
      instanceCap = 3;

      // cloud instance template
      name = "aws-jenkins-slave";
      // numExecutors = 1;
      // description = ""
      imageId = "us-east-1/ami-4dad7424";
      osFamily = "";
      osVersion = "";
      hardwareId = "t1.micro";
      ram = -1;
      cores = -1;
      // labels = "whii";
      initScript = "touch /tmp/hellothere";
      stopOnTerminate = true;
   }

   /**
    * Rigourous Test :-)
    */
   public void testApp() {
      createCompute();
      assertNotNull(compute);
      createAndStartPool();
      assertNotNull(pooledCompute);
      for (int i = 0; i < 3; i++) {
         startCounter();
         provision("pool-1");
         stopCounter();
      }
      for (int i = 0; i < 3; i++) {
         startCounter();
         provision("pool-2");
         stopCounter();
      }
      for (int i = 0; i < 3; i++) {
         startCounter();
         provision("pool-3");
         stopCounter();
      }
      assertEquals(9, getRunningNodesCount());
      for (NodeMetadata slave : nodes) {
         assertNotNull(slave);
         LOGGER.info(slave.getId() + "-" + slave.getGroup());
         terminate(slave);
      }
      assertEquals(0, getRunningNodesCount());
   }

   private void stopCounter() {
      LOGGER.info("Elapsed time: " + (System.currentTimeMillis() - time));
   }

   private void startCounter() {
      time = System.currentTimeMillis();      
   }

   public AppTest getCloud() {
      return this;
   }

   public ComputeService getCompute() {
      return pooledCompute;
   }
   
   public NodeMetadata provision(String groupName) {
      LOGGER.info("Provisioning new node");
      NodeMetadata nodeMetadata = createNodeWithJdk(groupName);
      nodes.add(nodeMetadata);
      return nodeMetadata;
   }

   public int getRunningNodesCount() {
      int nodeCount = 0;

      for (ComputeMetadata cm : pooledCompute.listNodes()) {
         if (NodeMetadata.class.isInstance(cm)) {
            String nodeGroup = ((NodeMetadata) cm).getGroup();

            if (!((NodeMetadata) cm).getState().equals(NodeState.SUSPENDED)
                  && !((NodeMetadata) cm).getState().equals(NodeState.TERMINATED)) {
               nodeCount++;
            }
         }
      }
      return nodeCount;
   }

   private void createCompute() {
      Properties overrides = new Properties();
      if (!Strings.isNullOrEmpty(this.endPointUrl)) {
         overrides.setProperty(Constants.PROPERTY_ENDPOINT, this.endPointUrl);
      }
      Iterable<Module> modules = ImmutableSet.<Module> of(new SshjSshClientModule(), new SLF4JLoggingModule(),
            new EnterpriseConfigurationModule());
      this.compute = ContextBuilder.newBuilder(providerName)
                                   .credentials(identity, credential)
                                   .modules(modules)
                                   .overrides(overrides).buildView(ComputeServiceContext.class).getComputeService();
   }

   private void createAndStartPool() {
      LOGGER.info("creating jclouds nodepool");
      ImmutableMap<String, String> userMetadata = ImmutableMap.of("Name", name);
      TemplateBuilder templateBuilder = compute.templateBuilder();
      if (!Strings.isNullOrEmpty(imageId)) {
         LOGGER.info("Setting image id to " + imageId);
         templateBuilder.imageId(imageId);
      } else {
         if (!Strings.isNullOrEmpty(osFamily)) {
            LOGGER.info("Setting osFamily to " + osFamily);
            templateBuilder.osFamily(OsFamily.valueOf(osFamily));
         }
         if (!Strings.isNullOrEmpty(osVersion)) {
            LOGGER.info("Setting osVersion to " + osVersion);
            templateBuilder.osVersionMatches(osVersion);
         }
      }
      if (!Strings.isNullOrEmpty((hardwareId))) {
         LOGGER.info("Setting hardware Id to " + hardwareId);
      } else {
         LOGGER.info("Setting minRam " + ram + " and minCores " + cores);
         templateBuilder.minCores(cores).minRam(ram);
      }

      Template template = templateBuilder.build();

      // setup the jcloudTemplate to customize the nodeMetadata with jdk, etc.
      // also opening ports
      AdminAccess adminAccess = AdminAccess.builder().adminUsername("jenkins").installAdminPrivateKey(false) // no
                                                                                                             // need
            .grantSudoToAdminUser(false) // no need
            .adminPrivateKey(getCloud().privateKey) // temporary due to jclouds
                                                    // bug
            .authorizeAdminPublicKey(true).adminPublicKey(getCloud().publicKey).build();

      // Jenkins needs /jenkins dir.
      Statement jenkinsDirStatement = Statements.newStatementList(Statements.exec("mkdir /jenkins"),
            Statements.exec("chown jenkins /jenkins"));

      Statement bootstrap = newStatementList(adminAccess, jenkinsDirStatement, Statements.exec(this.initScript),
            InstallJDK.fromOpenJDK());

      template.getOptions().inboundPorts(22).userMetadata(userMetadata).runScript(bootstrap);

      pooledCompute = new BasePooledComputeService(compute, "jenkins-pool", template, 10);

      try {
         pooledCompute.startPool();
      } catch (RunNodesException e) {
         destroyBadNodesAndPropagate(e);
      }
   }

   private NodeMetadata createNodeWithJdk(String groupName) {
      LOGGER.info("creating jclouds node");

      NodeMetadata nodeMetadata = null;

      try {
         nodeMetadata = getOnlyElement(pooledCompute.createNodesInGroup(groupName, 1));
      } catch (RunNodesException e) {
         throw destroyBadNodesAndPropagate(e);
      }

      // Check if nodeMetadata is null and throw
      return nodeMetadata;
   }

   private RuntimeException destroyBadNodesAndPropagate(RunNodesException e) {
      for (Map.Entry<? extends NodeMetadata, ? extends Throwable> nodeError : e.getNodeErrors().entrySet())
         getCloud().getCompute().destroyNode(nodeError.getKey().getId());
      throw propagate(e);
   }

   public void terminate(NodeMetadata nodeMetaData) {
      if (stopOnTerminate) {
         LOGGER.info("Suspending the Slave : " + nodeMetaData.getName());
         final ComputeService compute = getCloud().getCompute();
         compute.suspendNode(nodeMetaData.getId());
      } else {
         LOGGER.info("Terminating the Slave : " + nodeMetaData.getName());
         final ComputeService compute = getCloud().getCompute();
         compute.destroyNode(nodeMetaData.getId());
      }
   }

}
