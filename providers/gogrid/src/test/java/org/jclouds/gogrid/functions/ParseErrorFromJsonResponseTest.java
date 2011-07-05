/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gogrid.functions;

import java.io.InputStream;
import java.net.UnknownHostException;

import org.jclouds.gogrid.config.DateSecondsAdapter;
import org.jclouds.gogrid.domain.internal.ErrorResponse;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Oleksiy Yarmula
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ParseErrorFromJsonResponseTest")
public class ParseErrorFromJsonResponseTest {

   Injector i = Guice.createInjector(new GsonModule() {
      @Override
      protected void configure() {
         bind(DateAdapter.class).to(DateSecondsAdapter.class);
         super.configure();
      }
   });

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_error_handler.json");

      ParseErrorFromJsonResponse parser = i.getInstance(ParseErrorFromJsonResponse.class);
      ErrorResponse response = Iterables.getOnlyElement(parser.apply(new HttpResponse(200, "ok", Payloads
            .newInputStreamPayload(is))));
      assert "No object found that matches your input criteria.".equals(response.getMessage());
      assert "IllegalArgumentException".equals(response.getErrorCode());
   }
}
