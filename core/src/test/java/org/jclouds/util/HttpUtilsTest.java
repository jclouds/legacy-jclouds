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
package org.jclouds.util;

import static com.google.common.base.Predicates.equalTo;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.jclouds.http.HttpUtils.changeSchemeHostAndPortTo;
import static org.jclouds.http.HttpUtils.parseQueryToMap;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Collections;

import javax.inject.Provider;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jclouds.PerformanceTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * This tests the performance of Digest commands.
 * 
 * @author Adrian Cole
 */
@Test(groups = "performance", sequential = true, testName = "jclouds.HttpUtils")
public class HttpUtilsTest extends PerformanceTest {
   Provider<UriBuilder> uriBuilderProvider = new Provider<UriBuilder>() {

      @Override
      public UriBuilder get() {
         return new UriBuilderImpl();
      }

   };

   public void testParseBase64InForm() {
      Multimap<String, String> expects = LinkedListMultimap.create();
      expects.put("Version", "2010-06-15");
      expects.put("Action", "ModifyInstanceAttribute");
      expects.put("Attribute", "userData");
      expects.put("Value", "dGVzdA==");
      expects.put("InstanceId", "1");
      assertEquals(
            expects,
            parseQueryToMap("Version=2010-06-15&Action=ModifyInstanceAttribute&Attribute=userData&Value=dGVzdA%3D%3D&InstanceId=1"));
   }

   @Test
   public void testParseQueryToMapSingleParam() {
      Multimap<String, String> parsedMap = parseQueryToMap("v=1.3");
      assert parsedMap.keySet().size() == 1 : "Expected 1 key, found: " + parsedMap.keySet().size();
      assert parsedMap.keySet().contains("v") : "Expected v to be a part of the keys";
      String valueForV = Iterables.getOnlyElement(parsedMap.get("v"));
      assert valueForV.equals("1.3") : "Expected the value for 'v' to be '1.3', found: " + valueForV;
   }

   @Test
   public void testParseQueryToMapMultiParam() {
      Multimap<String, String> parsedMap = parseQueryToMap("v=1.3&sig=123");
      assert parsedMap.keySet().size() == 2 : "Expected 2 keys, found: " + parsedMap.keySet().size();
      assert parsedMap.keySet().contains("v") : "Expected v to be a part of the keys";
      assert parsedMap.keySet().contains("sig") : "Expected sig to be a part of the keys";
      String valueForV = Iterables.getOnlyElement(parsedMap.get("v"));
      assert valueForV.equals("1.3") : "Expected the value for 'v' to be '1.3', found: " + valueForV;
      String valueForSig = Iterables.getOnlyElement(parsedMap.get("sig"));
      assert valueForSig.equals("123") : "Expected the value for 'v' to be '123', found: " + valueForSig;
   }

   @Test
   public void testChangeSchemeHostAndPortTo() {
      HttpRequest request = createMock(HttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/mypath"));
      request.setEndpoint(URI.create("https://remotehost:443/mypath"));
      Multimap<String, String> headers = HashMultimap.create();
      expect(request.getHeaders()).andReturn(headers);
      replay(request);
      changeSchemeHostAndPortTo(request, "https", "remotehost", 443, uriBuilderProvider.get());
      assertEquals(headers.get(HttpHeaders.HOST), Collections.singletonList("remotehost"));
   }

   public void testIsEncoded() {
      assert HttpUtils.isUrlEncoded("/read-tests/%73%6f%6d%65%20%66%69%6c%65");
      assert !HttpUtils.isUrlEncoded("/read-tests/ tep");
   }

   public void testNoDoubleEncode() {
      assertEquals(HttpUtils.urlEncode("/read-tests/%73%6f%6d%65%20%66%69%6c%65", '/'),
            "/read-tests/%73%6f%6d%65%20%66%69%6c%65");
      assertEquals(HttpUtils.urlEncode("/read-tests/ tep", '/'), "/read-tests/%20tep");
   }

   public void testIBM() {
      URI ibm = HttpUtils
            .createUri("https://www-180.ibm.com/cloud/enterprise/beta/ram/assetDetail/generalDetails.faces?guid={A31FF849-0E97-431A-0324-097385A46298}&v=1.2");
      assertEquals(ibm.getQuery(), "guid={A31FF849-0E97-431A-0324-097385A46298}&v=1.2");
   }

   public void testAtmos() {
      URI creds = HttpUtils.createUri("compute://domain/user:Base64==@azureblob/container-hyphen/prefix");
      assertEquals(creds, URI.create("compute://domain%2Fuser:Base64%3D%3D@azureblob/container-hyphen/prefix"));
   }

   public void testAzure() {
      URI creds = HttpUtils.createUri("compute://identity:Base64==@azureblob/container-hyphen/prefix");
      assertEquals(creds, URI.create("compute://identity:Base64==@azureblob/container-hyphen/prefix"));
   }

   public void testHosting() {
      URI creds = HttpUtils.createUri("compute://user@domain:pa$sword@hostingdotcom");
      assertEquals(creds.getUserInfo(), "user@domain:pa$sword");
      assertEquals(creds, URI.create("compute://user%40domain:pa%24sword@hostingdotcom"));
   }

   public void testTerremark() {
      URI creds = HttpUtils.createUri("compute://user@domain:password@terremark");
      assertEquals(creds.getUserInfo(), "user@domain:password");
      assertEquals(creds, URI.create("compute://user%40domain:password@terremark"));
   }

   public void testTerremark2() {
      URI creds = HttpUtils.createUri("compute://user@domain:passw@rd@terremark");
      assertEquals(creds.getUserInfo(), "user@domain:passw@rd");
      assertEquals(creds, URI.create("compute://user%40domain:passw%40rd@terremark"));
   }

   public void testTerremark3() {
      URI creds = HttpUtils.createUri("compute://user@domain:AbC!@943!@terremark");
      assertEquals(creds.getUserInfo(), "user@domain:AbC!@943!");
      assertEquals(creds, URI.create("compute://user%40domain:AbC%21%40943%21@terremark"));
   }

   public void testCloudFiles() {
      URI creds = HttpUtils.createUri("compute://identity:h3c@cloudfiles/container-hyphen/prefix");
      assertEquals(creds, URI.create("compute://identity:h3c@cloudfiles/container-hyphen/prefix"));
   }

   public void testS3() {

      URI creds = HttpUtils.createUri("compute://0AB:aA+/0@s3/buck-et/prefix");
      assertEquals(creds, URI.create("compute://0AB:aA%2B%2F0@s3/buck-et/prefix"));
   }

   public void testS3Space() {

      URI creds = HttpUtils.createUri("compute://0AB:aA+/0@s3/buck-et/pre fix");

      assertEquals(creds, URI.create("compute://0AB:aA%2B%2F0@s3/buck-et/pre%20fix"));
   }

   public void testPercent() {
      URI creds = HttpUtils.createUri("https://jclouds.blob.core.windows.net/jclouds-getpath/write-tests/file1%.txt");

      assertEquals(creds, URI.create("https://jclouds.blob.core.windows.net/jclouds-getpath/write-tests/file1%25.txt"));

   }

   public void test404() {
      Exception from = new HttpResponseException("message", null, new HttpResponse(404, "not found", null));
      assertEquals(returnValueOnCodeOrNull(from, true, equalTo(404)), Boolean.TRUE);
   }
   public void testNullResponse() {
      Exception from = new HttpResponseException("message", null, null);
      assertEquals(returnValueOnCodeOrNull(from, true, equalTo(404)), null);
   }
}
