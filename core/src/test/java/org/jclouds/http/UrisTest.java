/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.http;

import static org.jclouds.http.Uris.uriBuilder;
import static org.jclouds.util.Strings2.urlEncode;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class UrisTest {

   private static final ImmutableMap<String, String> templateParams = ImmutableMap.of("user", "bob");

   public void testScheme() {
      assertEquals(uriBuilder("https://foo.com:8080").scheme("http").toString(), "http://foo.com:8080");
      assertEquals(uriBuilder("https://foo.com:8080").scheme("http").build().toString(), "http://foo.com:8080");
      assertEquals(uriBuilder("https://api.github.com/repos/user?foo=bar&kung=fu").scheme("http").toString(),
            "http://api.github.com/repos/user?foo=bar&kung=fu");
      assertEquals(uriBuilder("https://api.github.com/repos/user?foo=bar&kung=fu").scheme("http").build().toString(),
            "http://api.github.com/repos/user?foo=bar&kung=fu");
      assertEquals(uriBuilder("https://api.github.com/repos/{user}").scheme("http").toString(),
            "http://api.github.com/repos/{user}");
      assertEquals(uriBuilder("https://api.github.com/repos/{user}").scheme("http").build(templateParams)
            .toASCIIString(), "http://api.github.com/repos/bob");

   }

   public void testHost() {
      assertEquals(uriBuilder("https://foo.com:8080").host("robots").toString(), "https://robots:8080");
      assertEquals(uriBuilder("https://foo.com:8080").host("robots").build().toString(), "https://robots:8080");
      assertEquals(uriBuilder("https://api.github.com/repos/user?foo=bar&kung=fu").host("robots").toString(),
            "https://robots/repos/user?foo=bar&kung=fu");
      assertEquals(uriBuilder("https://api.github.com/repos/user?foo=bar&kung=fu").host("robots").build().toString(),
            "https://robots/repos/user?foo=bar&kung=fu");
      assertEquals(uriBuilder("https://api.github.com/repos/{user}").host("robots").toString(),
            "https://robots/repos/{user}");
      assertEquals(uriBuilder("https://api.github.com/repos/{user}").host("robots").build(templateParams)
            .toASCIIString(), "https://robots/repos/bob");
   }

   @DataProvider(name = "strings")
   public Object[][] createData() {
      return new Object[][] { { "normal" }, { "sp ace" }, { "qu?stion" }, { "unic₪de" }, { "path/foo" }, { "colon:" },
            { "asteri*k" }, { "quote\"" }, { "great<r" }, { "lesst>en" }, { "p|pe" } };
   }

   @Test(dataProvider = "strings")
   public void testQuery(String val) {
      assertEquals(uriBuilder("https://foo.com:8080").addQuery("moo", val).toString(), "https://foo.com:8080?moo=" + val);
      assertEquals(uriBuilder("https://foo.com:8080").addQuery("moo", val).build().toString(), "https://foo.com:8080?moo="
            + urlEncode(val, '/', ','));
      assertEquals(uriBuilder("https://api.github.com/repos/user?foo=bar&kung=fu").addQuery("moo", val).toString(),
            "https://api.github.com/repos/user?foo=bar&kung=fu&moo=" + val);
      assertEquals(uriBuilder("https://api.github.com/repos/user?foo=bar&kung=fu").addQuery("moo", val).build().toString(),
            "https://api.github.com/repos/user?foo=bar&kung=fu&moo=" + urlEncode(val, '/', ','));
      assertEquals(uriBuilder("https://api.github.com/repos/{user}").addQuery("moo", val).toString(),
            "https://api.github.com/repos/{user}?moo=" + val);
      assertEquals(
            uriBuilder("https://api.github.com/repos/{user}").addQuery("moo", val).build(templateParams).toASCIIString(),
            "https://api.github.com/repos/bob?moo=" + urlEncode(val, '/', ','));
   }

   @Test(dataProvider = "strings")
   public void testPath(String path) {
      assertEquals(uriBuilder("https://foo.com:8080").path(path).toString(), "https://foo.com:8080/" + path);
      assertEquals(uriBuilder("https://foo.com:8080").path(path).build().toString(), "https://foo.com:8080/"
            + urlEncode(path, '/', ':', ';', '='));
      assertEquals(uriBuilder("https://api.github.com/repos/user?foo=bar&kung=fu").path(path).toString(),
            "https://api.github.com/" + path + "?foo=bar&kung=fu");
      assertEquals(uriBuilder("https://api.github.com/repos/user?foo=bar&kung=fu").path(path).build().toString(),
            "https://api.github.com/" + urlEncode(path, '/', ':', ';', '=') + "?foo=bar&kung=fu");
      assertEquals(uriBuilder("https://api.github.com/repos/{user}").path(path).toString(), "https://api.github.com/"
            + path);
      assertEquals(uriBuilder("https://api.github.com/repos/{user}").path(path).build(templateParams).toASCIIString(),
            "https://api.github.com/" + urlEncode(path, '/', ':', ';', '='));
   }

   @Test(dataProvider = "strings")
   public void testAppendPath(String path) {
      assertEquals(uriBuilder("https://foo.com:8080").appendPath(path).toString(), "https://foo.com:8080/" + path);
      assertEquals(uriBuilder("https://foo.com:8080").appendPath(path).build().toString(), "https://foo.com:8080/"
            + urlEncode(path, '/', ':', ';', '='));
      assertEquals(uriBuilder("https://api.github.com/repos/user?foo=bar&kung=fu").appendPath(path).toString(),
            "https://api.github.com/repos/user/" + path + "?foo=bar&kung=fu");
      assertEquals(uriBuilder("https://api.github.com/repos/user?foo=bar&kung=fu").appendPath(path).build().toString(),
            "https://api.github.com/repos/user/" + urlEncode(path, '/', ':', ';', '=') + "?foo=bar&kung=fu");
      assertEquals(uriBuilder("https://api.github.com/repos/{user}").appendPath(path).toString(),
            "https://api.github.com/repos/{user}/" + path);
      assertEquals(uriBuilder("https://api.github.com/repos/{user}").appendPath(path).build(templateParams)
            .toASCIIString(), "https://api.github.com/repos/bob/" + urlEncode(path, '/', ':', ';', '='));
   }

   @Test
   public void testNoDoubleSlashInPath() {
      assertEquals(uriBuilder("https://vcloud/api/").appendPath("/").build().toASCIIString(), "https://vcloud/api/");
   }

   @Test
   public void testWhenUrnInPath() {
      assertEquals(uriBuilder("https://vcloud/api").appendPath("urn::acme:foo").build(templateParams).toASCIIString(),
            "https://vcloud/api/urn::acme:foo");
   }

   @Test
   public void testWhenMatrixOnPath() {
      assertEquals(
            uriBuilder("https://api.rimuhosting.com/r").appendPath("orders;include_inactive=N").build(templateParams)
                  .toASCIIString(), "https://api.rimuhosting.com/r/orders;include_inactive=N");
   }

   @Test(dataProvider = "strings")
   public void testReplaceQueryIsEncoded(String key) {
      assertEquals(uriBuilder("/redirect").addQuery("foo", key).toString(), "/redirect?foo=" + key);
      assertEquals(uriBuilder("/redirect").addQuery("foo", key).build().toString(),
            "/redirect?foo=" + urlEncode(key, '/', ','));
   }

   public void testAddQuery() {
      assertEquals(uriBuilder("http://localhost:8080/client/api").addQuery("response", "json").toString(),
            "http://localhost:8080/client/api?response=json");

      assertEquals(
            uriBuilder(URI.create("http://localhost:8080/client/api")).addQuery("response", "json")
                  .addQuery("command", "queryAsyncJobResult").build().toString(),
            "http://localhost:8080/client/api?response=json&command=queryAsyncJobResult");
   }
}
