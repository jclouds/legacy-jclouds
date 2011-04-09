/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.s3;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseS3AsyncClientTest extends RestClientTest<S3AsyncClient> {

   protected BlobToObject blobToS3Object;
   protected RequestAuthorizeSignature filter;

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), RequestAuthorizeSignature.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<S3AsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<S3AsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      blobToS3Object = injector.getInstance(BlobToObject.class);
      filter = injector.getInstance(RequestAuthorizeSignature.class);
   }

   public BaseS3AsyncClientTest() {
      super();
   }

   protected String provider = "s3";

   @Override
   protected Properties getProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(provider + ".endpoint", "https://s3.amazonaws.com");
      overrides.setProperty(provider + ".propertiesbuilder", S3PropertiesBuilder.class.getName());
      overrides.setProperty(provider + ".contextbuilder", S3ContextBuilder.class.getName());
      return overrides;
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory(getProperties()).createContextSpec(provider, "identity", "credential",
            new Properties());
   }

}
