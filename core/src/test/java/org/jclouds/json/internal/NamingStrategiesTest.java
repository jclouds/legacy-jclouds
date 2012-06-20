package org.jclouds.json.internal;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.json.internal.NamingStrategies.AnnotationConstructorNamingStrategy;
import org.jclouds.json.internal.NamingStrategies.AnnotationFieldNamingStrategy;
import org.jclouds.json.internal.NamingStrategies.AnnotationOrNameFieldNamingStrategy;
import org.jclouds.json.internal.NamingStrategies.ConstructorFieldNamingStrategy;
import org.jclouds.json.internal.NamingStrategies.ExtractNamed;
import org.jclouds.json.internal.NamingStrategies.ExtractSerializedName;
import org.jclouds.json.internal.NamingStrategies.NameExtractor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adam Lowe
 */
@Test(testName = "NamingStrategiesTest")
public final class NamingStrategiesTest {

   private static class SimpleTest {
      @SerializedName("aardvark")
      private String a;
      private String b;
      @Named("cat")
      private String c;
      @Named("dog")
      private String d;
      
      @ConstructorProperties({"aardvark", "bat", "coyote", "dog"})
      private SimpleTest(String aa, String bb, String cc, @Named("dingo") String dd) {
      }
      
      @Inject
      private SimpleTest(@Named("aa") String aa, @Named("bb") String bb, @Named("cc") String cc, @Named("dd") String dd, boolean nothing) {         
      }
   }

   private static class MixedConstructorTest {
      @Inject
      @ConstructorProperties("thiscanbeoverriddenbyNamed")
      private MixedConstructorTest(@Named("aardvark") String aa, @Named("bat") String bb, @Named("cat") String cc, @Named("dog") String dd) {
      }
   }


   public void testExtractSerializedName() throws Exception {
      NameExtractor extractor = new ExtractSerializedName();
      assertEquals(extractor.extractName(SimpleTest.class.getDeclaredField("a").getAnnotation(SerializedName.class)),
            "aardvark");
      try {
         extractor.extractName(SimpleTest.class.getDeclaredField("b").getAnnotation(SerializedName.class));
         fail();
      } catch (NullPointerException e) {
      }
      try {
         extractor.extractName(SimpleTest.class.getDeclaredField("c").getAnnotation(SerializedName.class));
         fail();
      } catch (NullPointerException e) {
      }
      try {
         extractor.extractName(SimpleTest.class.getDeclaredField("d").getAnnotation(SerializedName.class));
         fail();
      } catch (NullPointerException e) {
      }
   }

   public void testExtractNamed() throws Exception {
      NameExtractor extractor = new ExtractNamed();
      try {
         extractor.extractName(SimpleTest.class.getDeclaredField("a").getAnnotation(Named.class));
      } catch (NullPointerException e) {
      }
      try {
         extractor.extractName(SimpleTest.class.getDeclaredField("b").getAnnotation(Named.class));
         fail();
      } catch (NullPointerException e) {
      }
      assertEquals(extractor.extractName(SimpleTest.class.getDeclaredField("c").getAnnotation(Named.class)),
            "cat");
      assertEquals(extractor.extractName(SimpleTest.class.getDeclaredField("d").getAnnotation(Named.class)),
            "dog");
   }
   
   public void testAnnotationFieldNamingStrategy() throws Exception {
      FieldNamingStrategy strategy = new AnnotationFieldNamingStrategy(ImmutableSet.of(new ExtractNamed()));

      assertNull(strategy.translateName(SimpleTest.class.getDeclaredField("a")));
      assertNull(strategy.translateName(SimpleTest.class.getDeclaredField("b")));
      assertEquals(strategy.translateName(SimpleTest.class.getDeclaredField("c")), "cat");
      assertEquals(strategy.translateName(SimpleTest.class.getDeclaredField("d")), "dog");
   }

   public void testAnnotationOrNameFieldNamingStrategy() throws Exception {
      FieldNamingStrategy strategy = new AnnotationOrNameFieldNamingStrategy(ImmutableSet.of(new ExtractNamed()));

      assertEquals(strategy.translateName(SimpleTest.class.getDeclaredField("a")), "a");
      assertEquals(strategy.translateName(SimpleTest.class.getDeclaredField("b")), "b");
      assertEquals(strategy.translateName(SimpleTest.class.getDeclaredField("c")), "cat");
      assertEquals(strategy.translateName(SimpleTest.class.getDeclaredField("d")), "dog");
   }

   public void testAnnotationConstructorFieldNamingStrategyCPAndNamed() throws Exception {
      ConstructorFieldNamingStrategy strategy = new AnnotationConstructorNamingStrategy(
            ImmutableSet.of(ConstructorProperties.class), ImmutableSet.of(new ExtractNamed()));

      Constructor<? super SimpleTest> constructor = strategy.getDeserializationConstructor(SimpleTest.class);
      assertNotNull(constructor);
      assertEquals(constructor.getParameterTypes().length, 4);

      assertEquals(strategy.translateName(constructor, 0), "aardvark");
      assertEquals(strategy.translateName(constructor, 1), "bat");
      assertEquals(strategy.translateName(constructor, 2), "coyote");
      // Note: @Named overrides the ConstructorProperties setting
      assertEquals(strategy.translateName(constructor, 3), "dingo");

      Constructor<? super MixedConstructorTest> mixedCtor = strategy.getDeserializationConstructor(MixedConstructorTest.class);
      assertNotNull(mixedCtor);
      assertEquals(mixedCtor.getParameterTypes().length, 4);

      assertEquals(strategy.translateName(mixedCtor, 0), "aardvark");
      assertEquals(strategy.translateName(mixedCtor, 1), "bat");
      assertEquals(strategy.translateName(mixedCtor, 2), "cat");
      assertEquals(strategy.translateName(mixedCtor, 3), "dog");
   }

   public void testAnnotationConstructorFieldNamingStrategyCP() throws Exception {
      ConstructorFieldNamingStrategy strategy = new AnnotationConstructorNamingStrategy(
            ImmutableSet.of(ConstructorProperties.class), ImmutableSet.<NameExtractor>of());

      Constructor<? super SimpleTest> constructor = strategy.getDeserializationConstructor(SimpleTest.class);
      assertNotNull(constructor);
      assertEquals(constructor.getParameterTypes().length, 4);

      assertEquals(strategy.translateName(constructor, 0), "aardvark");
      assertEquals(strategy.translateName(constructor, 1), "bat");
      assertEquals(strategy.translateName(constructor, 2), "coyote");
      assertEquals(strategy.translateName(constructor, 3), "dog");

      Constructor<? super MixedConstructorTest> mixedCtor = strategy.getDeserializationConstructor(MixedConstructorTest.class);
      assertNotNull(mixedCtor);
      assertEquals(mixedCtor.getParameterTypes().length, 4);

      assertEquals(strategy.translateName(mixedCtor, 0), "thiscanbeoverriddenbyNamed");
      assertNull(strategy.translateName(mixedCtor, 1));
      assertNull(strategy.translateName(mixedCtor, 2));
      assertNull(strategy.translateName(mixedCtor, 3));
   }
   
   public void testAnnotationConstructorFieldNamingStrategyInject() throws Exception {
      ConstructorFieldNamingStrategy strategy = new AnnotationConstructorNamingStrategy(
            ImmutableSet.of(Inject.class), ImmutableSet.of(new ExtractNamed()));

      Constructor<? super SimpleTest> constructor = strategy.getDeserializationConstructor(SimpleTest.class);
      assertNotNull(constructor);
      assertEquals(constructor.getParameterTypes().length, 5);

      assertEquals(strategy.translateName(constructor, 0), "aa");
      assertEquals(strategy.translateName(constructor, 1), "bb");
      assertEquals(strategy.translateName(constructor, 2), "cc");
      assertEquals(strategy.translateName(constructor, 3), "dd");

      Constructor<? super MixedConstructorTest> mixedCtor = strategy.getDeserializationConstructor(MixedConstructorTest.class);
      assertNotNull(mixedCtor);
      assertEquals(mixedCtor.getParameterTypes().length, 4);

      assertEquals(strategy.translateName(mixedCtor, 0), "aardvark");
      assertEquals(strategy.translateName(mixedCtor, 1), "bat");
      assertEquals(strategy.translateName(mixedCtor, 2), "cat");
      assertEquals(strategy.translateName(mixedCtor, 3), "dog");
   }

}
