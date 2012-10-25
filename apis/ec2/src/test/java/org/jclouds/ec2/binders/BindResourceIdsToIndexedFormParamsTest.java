package org.jclouds.ec2.binders;

import static org.testng.Assert.assertEquals;

import java.io.File;

import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindResourceIdsToIndexedFormParams}
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "unit")
public class BindResourceIdsToIndexedFormParamsTest {
   Injector injector = Guice.createInjector();
   BindResourceIdsToIndexedFormParams binder = injector.getInstance(BindResourceIdsToIndexedFormParams.class);

   public void test() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost").build();
      request = binder.bindToRequest(request, ImmutableList.builder().add("alpha").add("omega").build());
      assertEquals(request.getPayload().getRawContent(), "ResourceId.1=alpha&ResourceId.2=omega");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeArray() {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("http://localhost").build();;
      binder.bindToRequest(request, new File("foo"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      binder.bindToRequest(request, null);
   }
}