package org.jclouds.openstack.nova.v1_1.features;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaRestClientExpectTest;
import org.jclouds.openstack.nova.v1_1.parse.ParseFloatingIPListTest;
import org.jclouds.openstack.nova.v1_1.parse.ParseFloatingIPTest;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests annotation parsing of {@code FloatingIPAsyncClient}
 *
 * @author Michael Arnold
 */
@Test(groups = "unit", testName = "FloatingIPClientExpectTest")
public class FloatingIPClientExpectTest extends BaseNovaRestClientExpectTest {

    public void testListFloatingIPsWhenResponseIs2xx() throws Exception {
        HttpRequest listFloatingIPs = HttpRequest
                .builder()
                .method("GET")
                .endpoint(
                        URI.create("https://compute.north.host/v1.1/3456/os-floating-ips"))
                .headers(
                        ImmutableMultimap.<String, String> builder()
                                .put("Accept", "application/json")
                                .put("X-Auth-Token", authToken).build()).build();

        HttpResponse listFloatingIPsResponse = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/floatingip_list.json")).build();

        NovaClient clientWhenFloatingIPsExist = requestsSendResponses(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess,
                listFloatingIPs, listFloatingIPsResponse);

        assertEquals(clientWhenFloatingIPsExist.getConfiguredRegions(),
                ImmutableSet.of("North"));

        assertEquals(clientWhenFloatingIPsExist.getFloatingIPClientForRegion("North")
                .listFloatingIPs().toString(), new ParseFloatingIPListTest().expected()
                .toString());
    }
    
    public void testListFloatingIPsWhenResponseIs404() throws Exception {
        HttpRequest listFloatingIPs = HttpRequest
                .builder()
                .method("GET")
                .endpoint(
                        URI.create("https://compute.north.host/v1.1/3456/os-floating-ips"))
                .headers(
                        ImmutableMultimap.<String, String> builder()
                                .put("Accept", "application/json")
                                .put("X-Auth-Token", authToken).build()).build();

        HttpResponse listFloatingIPsResponse = HttpResponse.builder().statusCode(404)
                .build();

        NovaClient clientWhenNoServersExist = requestsSendResponses(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess,
                listFloatingIPs, listFloatingIPsResponse);

        assertTrue(clientWhenNoServersExist.getFloatingIPClientForRegion("North")
                .listFloatingIPs().isEmpty());
    }
    
    public void testGetFloatingIPWhenResponseIs2xx() throws Exception {
        HttpRequest getFloatingIP = HttpRequest
                .builder()
                .method("GET")
                .endpoint(
                        URI.create("https://compute.north.host/v1.1/3456/os-floating-ips/1"))
                .headers(
                        ImmutableMultimap.<String, String> builder()
                                .put("Accept", "application/json")
                                .put("X-Auth-Token", authToken).build()).build();

        HttpResponse getFloatingIPResponse = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/floatingip_details.json")).build();

        NovaClient clientWhenFloatingIPsExist = requestsSendResponses(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess,
                getFloatingIP, getFloatingIPResponse);

        assertEquals(clientWhenFloatingIPsExist.getFloatingIPClientForRegion("North")
                .getFloatingIP("1").toString(),
                new ParseFloatingIPTest().expected().toString());
    }
    
    public void testGetFloatingIPWhenResponseIs404() throws Exception {
        HttpRequest getFloatingIP = HttpRequest
                .builder()
                .method("GET")
                .endpoint(
                        URI.create("https://compute.north.host/v1.1/3456/os-floating-ips/1"))
                .headers(
                        ImmutableMultimap.<String, String> builder()
                                .put("Accept", "application/json")
                                .put("X-Auth-Token", authToken).build()).build();

        HttpResponse getFloatingIPResponse = HttpResponse.builder().statusCode(404).build();

        NovaClient clientWhenNoServersExist = requestsSendResponses(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess,
                getFloatingIP, getFloatingIPResponse);

        assertNull(clientWhenNoServersExist.getFloatingIPClientForRegion("North")
                .getFloatingIP("1"));
    }

    public void testAllocateWhenResponseIs2xx() throws Exception {
        HttpRequest allocateFloatingIP = HttpRequest
                .builder()
                .method("POST")
                .endpoint(
                        URI.create("https://compute.north.host/v1.1/3456/os-floating-ips"))
                .headers(
                        ImmutableMultimap.<String, String> builder()
                                .put("Accept", "application/json")
                                .put("X-Auth-Token", authToken).build())
                .payload(payloadFromStringWithContentType("{}", "application/json")).build();

        HttpResponse allocateFloatingIPResponse = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/floatingip_details.json")).build();

        NovaClient clientWhenFloatingIPsExist = requestsSendResponses(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess,
                allocateFloatingIP, allocateFloatingIPResponse);

        assertEquals(clientWhenFloatingIPsExist.getFloatingIPClientForRegion("North")
                .allocate().toString(),
                new ParseFloatingIPTest().expected().toString());

    }

    public void testAllocateWhenResponseIs404() throws Exception {
        HttpRequest allocateFloatingIP = HttpRequest
                .builder()
                .method("POST")
                .endpoint(
                        URI.create("https://compute.north.host/v1.1/3456/os-floating-ips"))
                .headers(
                        ImmutableMultimap.<String, String> builder()
                                .put("Accept", "application/json")
                                .put("X-Auth-Token", authToken).build())
                .payload(payloadFromStringWithContentType("{}", "application/json")).build();

        HttpResponse allocateFloatingIPResponse = HttpResponse.builder().statusCode(404).build();

        NovaClient clientWhenNoServersExist = requestsSendResponses(
                keystoneAuthWithAccessKeyAndSecretKey, responseWithKeystoneAccess,
                allocateFloatingIP, allocateFloatingIPResponse);

        assertNull(clientWhenNoServersExist.getFloatingIPClientForRegion("North")
                .allocate());
    }
    
}