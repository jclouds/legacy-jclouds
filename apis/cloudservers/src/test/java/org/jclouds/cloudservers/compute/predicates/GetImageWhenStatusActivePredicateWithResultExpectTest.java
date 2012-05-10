package org.jclouds.cloudservers.compute.predicates;

import static junit.framework.Assert.assertTrue;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudservers.CloudServersApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v1_1.internal.BaseKeystoneRestClientExpectTest;
import org.jclouds.predicates.PredicateWithResult;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;
import com.google.inject.Injector;
import com.google.inject.Module;

@Test(groups = "unit", testName = "GetImageWhenStatusActivePredicateWithResultExpectTest")
public class GetImageWhenStatusActivePredicateWithResultExpectTest extends BaseKeystoneRestClientExpectTest<Injector>
         implements Function<ComputeServiceContext, Injector> {

   private final HttpRequest listImagesDetail = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images/detail?format=json"))
            .headers(ImmutableMultimap.<String, String> builder().put("X-Auth-Token", authToken)
                     .put(HttpHeaders.ACCEPT, "application/json").build()).build();

   private final HttpResponse listImagesResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/test_list_images_detail_imageextension.json")).build();

   Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .put(initialAuth, responseWithAuth).put(listImagesDetail, listImagesResponse).build();

   public GetImageWhenStatusActivePredicateWithResultExpectTest() {
      provider = "cloudservers";
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new CloudServersApiMetadata();
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_REGIONS, "US");
      overrides.setProperty(provider + ".endpoint", endpoint);
      return overrides;
   }

   @Override
   public Injector createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return apply(createComputeServiceContext(fn, module, props));
   }

   private ComputeServiceContext createComputeServiceContext(Function<HttpRequest, HttpResponse> fn, Module module,
            Properties props) {
      return createInjector(fn, module, props).getInstance(ComputeServiceContext.class);
   }

   @Override
   public Injector apply(ComputeServiceContext input) {
      return input.utils().injector();
   }

   public void testReturnsFalseOnQueuedAndSavingAndTrueOnActive() {
      Injector injector = requestsSendResponses(requestResponseMap);
      PredicateWithResult<Integer, Image> predicate = injector
               .getInstance(GetImageWhenStatusActivePredicateWithResult.class);
      assertTrue(predicate.apply(2));
   }
}
