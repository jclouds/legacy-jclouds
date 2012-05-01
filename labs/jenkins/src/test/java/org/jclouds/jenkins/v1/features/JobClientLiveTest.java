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
package org.jclouds.jenkins.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;

import org.jclouds.jenkins.v1.domain.JobDetails;
import org.jclouds.jenkins.v1.internal.BaseJenkinsClientLiveTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "JobClientLiveTest")
public class JobClientLiveTest extends BaseJenkinsClientLiveTest {

   public void testCreateJob() throws IOException {
      getClient().delete("blagoo");
      getClient().createFromXML("blagoo", Strings2.toStringAndClose(getClass().getResourceAsStream("/sample_job.xml")));
   }
   
   @Test(dependsOnMethods = "testCreateJob")
   public void testFetchConfigXML() throws IOException {
      String configXML = getClient().fetchConfigXML("blagoo");
      assertNotNull(configXML);
      //TODO enable this assertion
      //assertEquals(configXML, Strings2.toStringAndClose(getClass().getResourceAsStream("/sample_job.xml")));
   }

   @Test(dependsOnMethods = "testFetchConfigXML")
   public void testGetJob() throws IOException {
      JobDetails job = getClient().get("blagoo");
      assertNotNull(job);
      assertEquals(job.getName(), "blagoo");
   }

   @Test(dependsOnMethods = "testGetJob")
   public void testBuildJob() throws IOException {
      getClient().build("blagoo");
   }
   
   @Test(dependsOnMethods = "testBuildJob")
   public void testDeleteJob() {
      getClient().delete("blagoo");
   }
   
   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      getClient().delete("blagoo");
      getClient().delete("blagooCopy");
      super.tearDownContext();
   }

   private JobClient getClient() {
      return context.getApi().getJobClient();
   }
}