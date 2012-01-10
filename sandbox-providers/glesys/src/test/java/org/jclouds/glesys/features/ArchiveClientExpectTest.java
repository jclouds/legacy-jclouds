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
package org.jclouds.glesys.features;

import com.google.common.collect.ImmutableList;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.Archive;
import org.jclouds.glesys.domain.ArchiveAllowedArguments;
import org.jclouds.glesys.domain.ArchiveDetails;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * Tests annotation parsing of {@code ArchiveAsyncClient}
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ArchiveAsyncClientTest")
public class ArchiveClientExpectTest extends BaseGleSYSClientExpectTest<ArchiveClient> {
   public ArchiveClientExpectTest() {
      remoteServicePrefix = "archive";
   }

   private Map.Entry<String, String> userName = entry("username", "xxxxxx_test1");

   public void testListArchives() throws Exception {
      ArchiveClient client = createMock("list", "POST", 200, "/archive_list.json");
      assertEquals(client.listArchives(),
            ImmutableList.<Archive>of(Archive.builder().username("xxxxx_test1").freeSize("20 GB").totalSize("20 GB").locked(false).build()));

      // check not found response
      client = createMock("list", "POST", 404, "Something not found");
      assertTrue(client.listArchives().isEmpty());
   }

   public void testArchiveDetails() throws Exception {
      assertEquals(createMock("details", "POST", 200, "/archive_details.json", userName).getArchiveDetails("xxxxxx_test1"),
            ArchiveDetails.builder().username("xxxxxx_test1").freeSize("30 GB").totalSize("30 GB").locked(false).build());
      assertNull(createMock("details", "POST", 404, "/archive_details.json", userName).getArchiveDetails("xxxxxx_test1"));
   }

   public void testCreateArchive() throws Exception {
      createMock("create", "POST", 200, null, userName, entry("size", 5),
            entry("password", "somepass")).createArchive(userName.getValue(), "somepass", 5);
   }

   public void testDeleteArchive() throws Exception {
      createMock("delete", "POST", 200, null, userName).deleteArchive(userName.getValue());
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testDeleteArchiveNotFound() throws Exception {
      createMock("delete", "POST", 404, null, userName).deleteArchive(userName.getValue());
   }

   public void testResizeArchive() throws Exception {
      createMock("resize", "POST", 200, null, entry("username", "username"),
            entry("size", "5")).resizeArchive("username", 5);
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testResizeArchiveNotFound() throws Exception {
      createMock("resize", "POST", 404, null, entry("username", "username"), entry("size", "5")).resizeArchive("username", 5);
   }
   
   public void testChangeArchivePassword() throws Exception {
      createMock("changepassword", "POST", 200, null, userName,
            entry("password", "newpass")).changeArchivePassword(userName.getValue(), "newpass");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testChangeArchivePasswordNotFound() throws Exception {
      createMock("changepassword", "POST", 404, null, userName,
            entry("password", "newpass")).changeArchivePassword(userName.getValue(), "newpass");
   }
   
   public void testGetArchiveAllowedArguments() throws Exception {
      assertEquals(createMock("allowedarguments", "GET", 200, "/archive_allowed_arguments.json").getArchiveAllowedArguments(),
            ArchiveAllowedArguments.builder().archiveSizes(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375, 400, 425, 450, 475, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000).build());
      assertNull(createMock("allowedarguments", "GET", 404, "/archive_allowed_arguments.json").getArchiveAllowedArguments());
   }

   @Override
   protected ArchiveClient getClient(GleSYSClient gleSYSClient) {
      return gleSYSClient.getArchiveClient();
   }
}
