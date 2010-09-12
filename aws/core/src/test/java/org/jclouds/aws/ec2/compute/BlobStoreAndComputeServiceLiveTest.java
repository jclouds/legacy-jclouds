/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws.ec2.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.get;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class BlobStoreAndComputeServiceLiveTest {

   protected ComputeServiceContext computeContext;
   protected BlobStoreContext blobContext;
   protected String tag = System.getProperty("user.name") + "happy";
   protected String identity;
   protected String credential;
   protected String blobStoreProvider;
   protected String computeServiceProvider;

   protected void setupCredentials() {
      tag = System.getProperty("user.name") + "happy";
      identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      computeContext = new ComputeServiceContextFactory().createContext(computeServiceProvider, identity, credential,
               ImmutableSet.of(new Log4JLoggingModule(), new JschSshClientModule()));
      blobContext = new BlobStoreContextFactory().createContext(blobStoreProvider, identity, credential,
               ImmutableSet.of(new Log4JLoggingModule()));
      blobContext.getAsyncBlobStore().createContainerInLocation(null, tag);
      computeContext.getComputeService().destroyNodesMatching(NodePredicates.withTag(tag));
   }

   protected void assertSshOutputOfCommandContains(Iterable<? extends NodeMetadata> nodes, String cmd, String expects) {
      IPSocket socket = new IPSocket(get(get(nodes, 0).getPublicAddresses(), 0), 22);

      SshClient ssh = computeContext.utils().sshFactory().create(socket, get(nodes, 0).getCredentials().identity,
               get(nodes, 0).getCredentials().credential.getBytes());
      try {
         ssh.connect();
         ;
         ExecResponse exec = ssh.exec(cmd);
         assert exec.getOutput().indexOf(expects) != -1 || exec.getError().indexOf(expects) != -1 : exec;
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   protected void uploadBlob(String container, String name, String script) {
      Blob blob = blobContext.getBlobStore().newBlob(name);
      blob.setPayload(script);
      blob.getPayload().setContentType("text/plain");
      blobContext.getBlobStore().putBlob(container, blob);
   }

   @AfterGroups(groups = { "live" })
   public void teardownCompute() {
      if (computeContext != null) {
         computeContext.getComputeService().destroyNodesMatching(NodePredicates.withTag(tag));
         computeContext.close();
      }
   }

   @AfterGroups(groups = { "live" })
   public void teardownBlobStore() {
      if (blobContext != null) {
         blobContext.getAsyncBlobStore().deleteContainer(tag);
         blobContext.close();
      }
   }

}