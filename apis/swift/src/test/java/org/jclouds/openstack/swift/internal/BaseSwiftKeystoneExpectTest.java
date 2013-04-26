package org.jclouds.openstack.swift.internal;

import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.internal.KeystoneFixture;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

/**
 * Base class for writing Swift Keystone Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseSwiftKeystoneExpectTest<T> extends BaseRestClientExpectTest<T>  {
   protected HttpRequest keystoneAuthWithUsernameAndPassword;
   protected HttpRequest keystoneAuthWithUsernameAndPasswordAndTenantName;
   protected HttpRequest keystoneAuthWithAccessKeyAndSecretKeyAndTenantName;
   protected String authToken;
   protected HttpResponse responseWithKeystoneAccess;
   protected HttpRequest keystoneAuthWithAccessKeyAndSecretKeyAndTenantId;
   protected String identityWithTenantId;

   public BaseSwiftKeystoneExpectTest() {
      provider = "swift-keystone";
      keystoneAuthWithUsernameAndPassword = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPassword(identity,
            credential);
      keystoneAuthWithUsernameAndPasswordAndTenantName = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPasswordAndTenantName(identity,
            credential);
      keystoneAuthWithAccessKeyAndSecretKeyAndTenantName = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKeyAndTenantName(identity,
            credential);
      keystoneAuthWithAccessKeyAndSecretKeyAndTenantId = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKeyAndTenantId(identity,
              credential);
      
      authToken = KeystoneFixture.INSTANCE.getAuthToken();
      responseWithKeystoneAccess = KeystoneFixture.INSTANCE.responseWithAccess();
      // now, createContext arg will need tenant prefix
      identityWithTenantId = KeystoneFixture.INSTANCE.getTenantId() + ":" + identity;
      identity = KeystoneFixture.INSTANCE.getTenantName() + ":" + identity;
  }
   
   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      // hpcloud or openstack
      overrides.setProperty("jclouds.regions", "region-a.geo-1,RegionOne");
      return overrides;
   }
   
   protected HttpRequest.Builder<?> authenticatedGET() {
      return HttpRequest.builder()
                        .method("GET")
                        .addHeader("Accept", MediaType.APPLICATION_JSON)
                        .addHeader("X-Auth-Token", authToken);
   }
   
   @Override
   protected HttpRequestComparisonType compareHttpRequestAsType(HttpRequest input) {
      return HttpRequestComparisonType.JSON;
   }
}
