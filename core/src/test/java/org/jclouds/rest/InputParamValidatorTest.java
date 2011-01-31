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

package org.jclouds.rest;

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

import org.jclouds.concurrent.Timeout;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.predicates.validators.AllLowerCaseValidator;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.TestException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

@Test(groups = "unit")
public class InputParamValidatorTest {

   @Timeout(duration = 1000, timeUnit = TimeUnit.SECONDS)
   @SkipEncoding('/')
   class InputParamValidatorForm {
      @POST
      @ParamValidators( { AllLowerCaseValidator.class })
      public void allParamsValidated(@PathParam("param1") String param1, @PathParam("param2") String param2) {
      }

      @POST
      public void oneParamValidated(@PathParam("param1") String param1,
               @ParamValidators( { AllLowerCaseValidator.class }) @PathParam("param2") String param2) {
      }
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
      RestAnnotationProcessor<InputParamValidatorForm> restAnnotationProcessor = factory(InputParamValidatorForm.class);
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

   /**
    * Tries to use Validator<String> on Integer parameter. Expected result: ClassCastException
    * 
    * @throws Exception
    *            if method isn't found
    */
   @Test
   public void testWrongPredicateTypeLiteral() throws Exception {
      @Timeout(duration = 1000, timeUnit = TimeUnit.SECONDS)
      @SkipEncoding('/')
      class WrongValidator {
         @SuppressWarnings("unused")
         @POST
         @ParamValidators( { AllLowerCaseValidator.class })
         public void method(@PathParam("param1") Integer param1) {
         }
      }
      WrongValidator validator = new WrongValidator();
      Method method = validator.getClass().getMethod("method", Integer.class);

      try {
         new InputParamValidator(injector).validateMethodParametersOrThrow(method, 55);
         throw new TestException("ClassCastException expected, but wasn't thrown");
      } catch (ClassCastException e) {
         // supposed to happen - continue
      }
   }

   @SuppressWarnings("unchecked")
   private <T> RestAnnotationProcessor<T> factory(Class<T> clazz) {
      return ((RestAnnotationProcessor<T>) injector.getInstance(Key.get(TypeLiteral.get(Types.newParameterizedType(
               RestAnnotationProcessor.class, clazz)))));
   }

   Injector injector;

   @BeforeClass
   void setupFactory() {

      RestContextSpec<IntegrationTestClient, IntegrationTestAsyncClient> contextSpec = contextSpec("test",
               "http://localhost:9999", "1", "", "userFoo", null, IntegrationTestClient.class,
               IntegrationTestAsyncClient.class);

      injector = createContextBuilder(contextSpec).buildInjector();

   }

}
