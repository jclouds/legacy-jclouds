/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rackspace.cloudfiles.blobstore;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.internal.BlobImpl;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.rackspace.StubRackspaceAuthenticationModule;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesPropertiesBuilder;
import org.jclouds.rackspace.cloudfiles.blobstore.config.CloudFilesBlobStoreContextModule;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesRestClientModule;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesStubClientModule;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.internal.CFObjectImpl;
import org.jclouds.rackspace.cloudfiles.internal.StubCloudFilesAsyncClient;
import org.jclouds.rackspace.reference.RackspaceConstants;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of modules configured in CloudFilesContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.CloudFilesContextBuilderTest")
public class CloudFilesBlobStoreContextBuilderTest {

   public void testNewBuilder() {
      CloudFilesBlobStoreContextBuilder builder = newBuilder();
      assertEquals(builder.getProperties().getProperty(PROPERTY_USER_METADATA_PREFIX),
               "X-Object-Meta-");
      assertEquals(builder.getProperties().getProperty(RackspaceConstants.PROPERTY_RACKSPACE_USER),
               "id");
      assertEquals(builder.getProperties().getProperty(RackspaceConstants.PROPERTY_RACKSPACE_KEY),
               "secret");
   }

   private CloudFilesBlobStoreContextBuilder newBuilder() {
      return new CloudFilesBlobStoreContextBuilder(new CloudFilesPropertiesBuilder("id", "secret")
               .build()).withModules(new CloudFilesStubClientModule(),
               new StubRackspaceAuthenticationModule());
   }

   public void testBuildContext() {
      BlobStoreContext context = newBuilder().buildBlobStoreContext();
      assertEquals(context.getClass(), BlobStoreContextImpl.class);
      assertEquals(context.getProviderSpecificContext().getAsyncApi().getClass(),
               StubCloudFilesAsyncClient.class);
      assertEquals(context.getAsyncBlobStore().getClass(), CloudFilesAsyncBlobStore.class);
      assertEquals(((CloudFilesAsyncClient) context.getProviderSpecificContext().getAsyncApi())
               .newCFObject().getClass(), CFObjectImpl.class);
      assertEquals(context.getAsyncBlobStore().newBlob(null).getClass(), BlobImpl.class);
      assertEquals(context.getProviderSpecificContext().getAccount(), "id");
      assertEquals(context.getProviderSpecificContext().getEndPoint(), URI
               .create("http://localhost/rackspacestub/cloudfiles"));
   }

   public void testBuildInjector() {
      Injector i = newBuilder().buildInjector();
      assert i.getInstance(BlobStoreContext.class) != null;
      assert i.getInstance(CFObject.class) != null;
      assert i.getInstance(Blob.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      CloudFilesBlobStoreContextBuilder builder = newBuilder();
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), CloudFilesBlobStoreContextModule.class);
   }

   protected void addClientModule() {
      List<Module> modules = new ArrayList<Module>();
      CloudFilesBlobStoreContextBuilder builder = newBuilder();
      builder.addClientModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), CloudFilesRestClientModule.class);
   }

}
