package org.jclouds.openstack.nova.v1_1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaRestClientExpectTest;
import org.jclouds.openstack.nova.v1_1.parse.ParseServerListTest;
import org.jclouds.openstack.nova.v1_1.parse.ParseServerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests annotation parsing of {@code ServerAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ServerAsyncClientTest")
public class ServerClientExpectTest extends BaseNovaRestClientExpectTest {

   public ServerClientExpectTest() {
      provider = "openstack-nova";
   }

   public void testListServersWhenResponseIs2xx() throws Exception {
      HttpRequest listServers = HttpRequest.builder().method("GET")
              .endpoint(URI.create("http://localhost:8774/v1.1/identity/servers"))
              .headers(ImmutableMultimap.<String, String> builder()
                 .put("Accept", "application/json")
                 .put("X-Auth-Token", authToken).build()).build();
      
      HttpResponse listServersResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/server_list.json")).build();

      ServerClient clientWhenServersExist = requestsSendResponses(initialAuth, responseWithUrls, listServers,
               listServersResponse).getServerClient();

      assertEquals(clientWhenServersExist.listServers().toString(), new ParseServerListTest().expected().toString());
   }
   
   public void testListServersWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listServers = HttpRequest.builder().method("GET")
              .endpoint(URI.create("http://localhost:8774/v1.1/identity/servers"))
              .headers(ImmutableMultimap.<String, String> builder()
                 .put("Accept", "application/json")
                 .put("X-Auth-Token", authToken).build()).build();
      
      HttpResponse listServersResponse = HttpResponse.builder().statusCode(404).build();

      ServerClient clientWhenServersExist = requestsSendResponses(initialAuth, responseWithUrls, listServers,
               listServersResponse).getServerClient();

      assertTrue(clientWhenServersExist.listServers().isEmpty());
   }

   //TODO: gson deserializer for Multimap
   @Test
   public void testGetServerWhenResponseIs2xx() throws Exception {
      HttpRequest listServers = HttpRequest.builder().method("GET")
              .endpoint(URI.create("http://localhost:8774/v1.1/identity/servers/foo"))
              .headers(ImmutableMultimap.<String, String> builder()
                 .put("Accept", "application/json")
                 .put("X-Auth-Token", authToken).build()).build();
      
      HttpResponse listServersResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/server_details.json")).build();

      ServerClient clientWhenServersExist = requestsSendResponses(initialAuth, responseWithUrls, listServers,
               listServersResponse).getServerClient();

      assertEquals(clientWhenServersExist.getServer("foo").toString(), new ParseServerTest().expected().toString());
   }

}
