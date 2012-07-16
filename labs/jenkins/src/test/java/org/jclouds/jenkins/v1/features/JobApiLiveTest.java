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
import java.util.Map;

import org.jclouds.jenkins.v1.domain.JobDetails;
import org.jclouds.jenkins.v1.domain.LastBuild;
import org.jclouds.jenkins.v1.internal.BaseJenkinsApiLiveTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "JobApiLiveTest")
public class JobApiLiveTest extends BaseJenkinsApiLiveTest {

   public void testCreateJob() throws IOException {
      getApi().delete("blagoo");
      getApi().createFromXML("blagoo", Strings2.toStringAndClose(getClass().getResourceAsStream("/sample_job.xml")));
   }
   
   @Test(dependsOnMethods = "testCreateJob")
   public void testFetchConfigXML() throws IOException {
      String configXML = getApi().fetchConfigXML("blagoo");
      assertNotNull(configXML);
      //TODO enable this assertion
      //assertEquals(configXML, Strings2.toStringAndClose(getClass().getResourceAsStream("/sample_job.xml")));
   }

   @Test(dependsOnMethods = "testFetchConfigXML")
   public void testGetJob() throws IOException {
      JobDetails job = getApi().get("blagoo");
      assertNotNull(job);
      assertEquals(job.getName(), "blagoo");
   }

   @Test(dependsOnMethods = "testGetJob")
   public void testBuildJob() throws IOException {
      getApi().build("blagoo");
   }
   
   @Test(dependsOnMethods = "testBuildJob")
   public void testDeleteJob() {
      getApi().delete("blagoo");
   }
   
   @Test(dependsOnMethods = "testDeleteJob")
   public void testCreateJobWithParameters() throws IOException {
      getApi().delete("jobWithParameters");
      getApi().createFromXML("jobWithParameters", Strings2.toStringAndClose(getClass().getResourceAsStream("/sample_job_with_parameters.xml")));
   }
   
   @Test(dependsOnMethods = "testCreateJobWithParameters")
   public void testBuildJobWithParameters() throws IOException {
      Map<String, String> parameters = ImmutableMap.of("name", "test1", "password", "secret");
      getApi().buildWithParameters("jobWithParameters", parameters);
   }
   
   @Test(dependsOnMethods = "testBuildJobWithParameters")
   public void testLastBuild() throws IOException {
      LastBuild lastBuild = getApi().lastBuild("jobWithParameters");
      while(lastBuild == null || lastBuild.getResult() == null) {
         lastBuild = getApi().lastBuild("jobWithParameters");
      }
      assertEquals(lastBuild.getResult(), "SUCCESS");
   }      
   
   @Test(dependsOnMethods = "testLastBuild")
   public void testDeleteJobWithParameters() {
      getApi().delete("jobWithParameters");
   }
   
   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      getApi().delete("blagoo");
      getApi().delete("jobWithParameters");
      super.tearDownContext();
   }

   private JobApi getApi() {
      return context.getApi().getJobApi();
   }
}
