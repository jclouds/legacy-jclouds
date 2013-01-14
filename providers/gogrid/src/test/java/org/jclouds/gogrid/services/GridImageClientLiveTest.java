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
package org.jclouds.gogrid.services;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.gogrid.options.SaveImageOptions;
import org.jclouds.gogrid.predicates.ServerLatestJobCompleted;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "GridImageClientLiveTest")
public class GridImageClientLiveTest extends BaseGoGridClientLiveTest {

   public void testListImages() throws Exception {
      Set<ServerImage> response = restContext.getApi().getImageServices().getImageList();
      assert null != response;
      for (ServerImage image : response) {
         assert image.getId() >= 0 : image;
         checkImage(image);

         ServerImage query = Iterables.getOnlyElement(restContext.getApi().getImageServices()
               .getImagesById(image.getId()));
         assertEquals(query.getId(), image.getId());

         checkImage(query);
      }
   }

   private void checkImage(ServerImage image) {
      assert image.getArchitecture() != null : image;
      assert image.getBillingTokens() != null : image;
      if (image.getCreatedTime() == null)
         Logger.getAnonymousLogger().warning("image " + image.getId() + " is missing the createdTime field");
      assert image.getDescription() != null : image;
      assert image.getFriendlyName() != null : image;
      assert image.getId() >= 0 : image;
      assert image.getLocation() != null : image;
      assert image.getName() != null : image;
      assert image.getOs() != null : image;
      assert image.getOwner() != null : image;
      assert image.getPrice() >= 0 : image;
      assert image.getState() != null : image;
      assert image.getType() != null : image;
      if (image.getUpdatedTime() == null)
         Logger.getAnonymousLogger().warning("image " + image.getId() + " is missing the updatedTime field");
   }

   @Test
   public void testSaveServerToImage() throws IOException {
      Predicate<Server> serverLatestJobCompleted = retry(new ServerLatestJobCompleted(restContext.getApi()
            .getJobServices()), 800, 20, SECONDS);

      final String nameOfServer = "Server" + String.valueOf(new Date().getTime()).substring(6);
      ServerImage image = null;
      try {
         Set<Ip> availableIps = restContext.getApi().getIpServices().getUnassignedPublicIpList();
         Ip availableIp = Iterables.getLast(availableIps);

         Server createdServer = restContext.getApi().getServerServices()
               .addServer(nameOfServer, "5489", "1", availableIp.getIp());
         assertNotNull(createdServer);
         assert serverLatestJobCompleted.apply(createdServer);
         image = restContext
               .getApi()
               .getImageServices()
               .saveImageFromServer("friendlyName", createdServer.getName(),
                     SaveImageOptions.Builder.withDescription("description"));
         
         assertEquals(image.getFriendlyName(), "friendlyName");
         assertEquals(image.getDescription(), "description");
         assertFalse(image.isPublic());

         assertEventuallyImageStateEquals(image, ServerImageState.AVAILABLE);
         
         restContext.getApi().getImageServices().deleteById(image.getId());

         assertEventuallyImageStateEquals(image, ServerImageState.TRASH);
         
         image = null;
      } finally {
         if (image != null)
            try {
               restContext.getApi().getImageServices().deleteById(image.getId());
            } catch (Exception e) {
               // not failing so that we can ensure server below deletes
               e.printStackTrace();
            }
         // delete the server
         restContext.getApi().getServerServices().deleteByName(nameOfServer);
      }

   }

   protected void assertEventuallyImageStateEquals(ServerImage image, final ServerImageState state) {
      assertTrue(retry(new Predicate<ServerImage>() {
         public boolean apply(ServerImage input) {
            return Iterables.getOnlyElement(restContext.getApi().getImageServices().getImagesById(input.getId()))
                  .getState() == state;
         }
      }, 600, 1, SECONDS).apply(image));
   }
}
