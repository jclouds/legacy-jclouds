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

package org.jclouds.aws;

import static org.jclouds.compute.BaseComputeServiceLiveTest.buildScript;
import static org.jclouds.compute.options.TemplateOptions.Builder.runScript;
import static org.jclouds.compute.util.ComputeServiceUtils.buildCurlsh;
import static org.jclouds.io.Payloads.newStringPayload;

import org.jclouds.aws.ec2.compute.BlobStoreAndComputeServiceLiveTest;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "jclouds.ComputeAndBlobStoreTogetherHappilyLiveTest")
public class ComputeAndBlobStoreTogetherHappilyLiveTest extends BlobStoreAndComputeServiceLiveTest {

   protected void setupCredentials() {
      blobStoreProvider = "s3";
      computeServiceProvider = "ec2";
      super.setupCredentials();
   }

   /**
    * This test generates a bootstrap script based on the default operating system of the compute
    * provider.
    * <p/>
    * It then uploads this to a blobstore, in a private location. Now, we want the bootstrap of the
    * server to be able to load from this location without sending credentials to the starting
    * machine. Accordingly, we send a signed url instead.
    * <p/>
    * Using the {@link BlobStore} api, we get a signed url corresponding to the bootstrap script. We
    * next convert this into something that can be invoked via the commandline. Looking around, it
    * seems like alestic runurl is pretty close. However, it is limited as it only works on requests
    * that can be fully specified without headers (ex. Amazon S3). Instead, we use a variant
    * (curlsh).
    * <p/>
    * curlsh simply assembles an http request, headers and all, and passes it to bash
    * <p/>
    * With this script ready, any node or nodes will take instructions from the blobstore when it
    * boots up. we verify this with an assertion.
    * 
    */
   @Test
   public void testWeCanIndirectBootstrapInstructionsToAnArbitraryAndPrivateBlobStore() throws RunNodesException {

      OperatingSystem defaultOperatingSystem = computeContext.getComputeService().templateBuilder().build().getImage()
               .getOperatingSystem();

      // using jclouds ability to detect operating systems before we launch them, we can avoid
      // the bad practice of assuming everything is ubuntu.
      uploadBlob(tag, "openjdk/install", buildScript(defaultOperatingSystem));

      // instead of hard-coding to amazon s3, we can use any blobstore, conceding this test is
      // configured for amz. Note we are getting temporary access to a private blob.
      HttpRequest signedRequestOfInstallScript = blobContext.getBlobStore().signRequestForBlob(tag, "openjdk/install");

      // instruct the bootstrap to pull the script from the blobstore and invoke it directly with bash.
      TemplateOptions runOpenJDKInstallDirectlyFromBlobStore = runScript(newStringPayload(buildCurlsh(signedRequestOfInstallScript)));

      // now that we have the correct instructions, kick-off the provisioner
      Iterable<? extends NodeMetadata> nodes = computeContext.getComputeService().runNodesWithTag(tag, 1,
               runOpenJDKInstallDirectlyFromBlobStore);

      // ensure the bootstrap operated by checking for a known signature of success.
      assertSshOutputOfCommandContains(nodes, "openjdk -version", "OpenJDK");

   }
}
