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
package org.jclouds.nirvanix.sdn.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.nirvanix.sdn.domain.UploadInfo;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseUploadInfoFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sdn.ParseUploadInfoFromJsonResponse")
public class ParseUploadInfoFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/authtoken.json");

      ParseUploadInfoFromJsonResponse parser = new ParseUploadInfoFromJsonResponse(i
               .getInstance(Gson.class));
      UploadInfo response = parser.apply(is);
      assertEquals(response.getHost(), URI.create("https://node1.nirvanix.com"));
      assertEquals(response.getToken(),
               "siR-ALYd~BEcJ8GR2tE~oX3SEHO8~2WXKT5xjFk~YLS5OvJyHI21TN34rQ");
   }

}
