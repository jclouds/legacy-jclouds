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
package org.jclouds.rest;

import java.lang.reflect.Method;

import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

import org.jclouds.ContextBuilder;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.predicates.validators.AllLowerCaseValidator;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.TestException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Injector;

@Test(groups = "unit")
public class InputParamValidatorTest {

   private static interface InputParamValidatorForm {
      @POST
      @ParamValidators(AllLowerCaseValidator.class)
      void allParamsValidated(@PathParam("param1") String param1, @PathParam("param2") String param2);

      @POST
      void oneParamValidated(@PathParam("param1") String param1,
            @ParamValidators(AllLowerCaseValidator.class) @PathParam("param2") String param2);
   }

   /**
    * Tests {@link AllLowerCaseValidator} against lowercase and uppercase inputs, both on method
    * level and parameter level.
    * 
    * @throws Exception
    *            if methods aren't found
    */
   @Test
   public void testInputParamsValidation() throws Exception {
      Method allParamsValidatedMethod = InputParamValidatorForm.class.getMethod("allParamsValidated", String.class,
               String.class);
      Method oneParamValidatedMethod = InputParamValidatorForm.class.getMethod("oneParamValidated", String.class,
               String.class);
      RestAnnotationProcessor restAnnotationProcessor = factory(InputParamValidatorForm.class);
      restAnnotationProcessor.createRequest(allParamsValidatedMethod, "blah", "blah");
      restAnnotationProcessor.createRequest(oneParamValidatedMethod, "blah", "blah");

      try {
         restAnnotationProcessor.createRequest(allParamsValidatedMethod, "BLAH", "blah");
         throw new TestException(
                  "AllLowerCaseValidator shouldn't have passed 'BLAH' as a parameter because it's uppercase.");
      } catch (IllegalArgumentException e) {
         // supposed to happen - continue
      }

      restAnnotationProcessor.createRequest(oneParamValidatedMethod, "BLAH", "blah");

      try {
         restAnnotationProcessor.createRequest(oneParamValidatedMethod, "blah", "BLAH");
         throw new TestException(
                  "AllLowerCaseValidator shouldn't have passed 'BLAH' as the second parameter because it's uppercase.");
      } catch (IllegalArgumentException e) {
         // supposed to happen - continue
      }
   }

   @Test
   public void testNullParametersForAllLowerCaseValidator() {
      new AllLowerCaseValidator().validate(null);
   }

   private static interface WrongValidator {
      @POST
      @ParamValidators(AllLowerCaseValidator.class)
      void method(@PathParam("param1") Integer param1);
   }

   @Test(expectedExceptions = ClassCastException.class)
   public void testWrongPredicateTypeLiteral() throws Exception {
      Method method = WrongValidator.class.getMethod("method", Integer.class);
      new InputParamValidator(injector).validateMethodParametersOrThrow(method, 55);
   }

   private RestAnnotationProcessor factory(Class<?> clazz) {
      return injector.getInstance(RestAnnotationProcessor.Factory.class).declaring(clazz);
   }

   Injector injector;

   @BeforeClass
   void setupFactory() {
      injector =  ContextBuilder
            .newBuilder(
                  AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(IntegrationTestClient.class, IntegrationTestAsyncClient.class,
                        "http://localhost:9999")).buildInjector();

   }

}
