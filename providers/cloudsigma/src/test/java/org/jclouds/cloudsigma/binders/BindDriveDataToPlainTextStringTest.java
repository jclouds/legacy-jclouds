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

package org.jclouds.cloudsigma.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jclouds.cloudsigma.domain.ClaimType;
import org.jclouds.cloudsigma.domain.Drive;
import org.jclouds.cloudsigma.domain.DriveData;
import org.jclouds.cloudsigma.functions.BaseDriveToMap;
import org.jclouds.cloudsigma.functions.DriveDataToMap;
import org.jclouds.http.HttpRequest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class BindDriveDataToPlainTextStringTest {

   private static final BindDriveDataToPlainTextString FN = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         bind(new TypeLiteral<Function<Drive, Map<String, String>>>() {
         }).to(BaseDriveToMap.class);
         bind(new TypeLiteral<Function<DriveData, Map<String, String>>>() {
         }).to(DriveDataToMap.class);
      }

   }).getInstance(BindDriveDataToPlainTextString.class);

   public void testSimple() {
      HttpRequest request = new HttpRequest("POST", URI.create("https://host/drives/create"));
      FN.bindToRequest(request, new DriveData.Builder().name("foo").size(100l).build());
      assertEquals(request.getPayload().getContentMetadata().getContentType(), MediaType.TEXT_PLAIN);
      assertEquals(request.getPayload().getRawContent(), "name foo\nsize 100");
   }

   public void testComplete() throws IOException {
      DriveData input = new DriveData.Builder().name("Ubuntu 10.10 Server Edition Linux 64bit Preinstalled System")
      //
            .size(8589934592l)//
            .claimType(ClaimType.SHARED)//
            .readers(ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"))//
            .use(ImmutableSet.of("tag1", "tag2"))//
            .build();

      HttpRequest request = new HttpRequest("POST", URI.create("https://host/drives/create"));
      FN.bindToRequest(request, input);
      assertEquals(request.getPayload().getContentMetadata().getContentType(), MediaType.TEXT_PLAIN);
      assertEquals(request.getPayload().getRawContent(),
            Strings2.toStringAndClose(BindDriveDataToPlainTextStringTest.class.getResourceAsStream("/drive_data.txt")));

   }
}