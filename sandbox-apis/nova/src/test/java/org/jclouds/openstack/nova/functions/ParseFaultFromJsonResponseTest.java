/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.openstack.nova.functions;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.handlers.ParseNovaErrorFromHttpResponse;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Tests behavior of {@code ParseServerListFromJsonResponse}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseFaultFromJsonResponseTest {

    Injector i = Guice.createInjector(new GsonModule());

    @Test
    public void testApplyInputStream() {
        InputStream is = getClass().getResourceAsStream("/test_list_servers.json");


        UnwrapOnlyJsonValue<List<Server>> parser = i.getInstance(Key
                .get(new TypeLiteral<UnwrapOnlyJsonValue<List<Server>>>() {
                }));
        //List<Server> response = parser.apply(new HttpResponse(413, "Over limit", Payloads.newInputStreamPayload(is)));
        new ParseNovaErrorFromHttpResponse().handleError(createHttpCommand(), new HttpResponse(413, "Over limit", Payloads.newInputStreamPayload(is)));

        //assertEquals(response, expects);
    }

    @Test
    public void testHandler() {
        //InputStream is = getClass().getResourceAsStream("/test_error_handler.json");

//
//
//      NovaErrorHandler handler = Guice.createInjector(new GsonModule()).getInstance(GoGridErrorHandler.class);
//
//      HttpCommand command = createHttpCommand();
//      handler.handleError(command, new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is)));
//
//      Exception createdException = command.getException();
//
//      assertNotNull(createdException, "There should've been an exception generated");
//      String message = createdException.getMessage();
//      assertTrue(message.contains("No object found that matches your input criteria."),
//            "Didn't find the expected error cause in the exception message");
//      assertTrue(message.contains("IllegalArgumentException"),
//            "Didn't find the expected error code in the exception message");
//
//      // make sure the InputStream is closed
//      try {
//         is.available();
//         throw new TestException("Stream wasn't closed by the GoGridErrorHandler when it should've");
//      } catch (IOException e) {
//         // this is the excepted output
//      }
    }

    HttpCommand createHttpCommand() {
        return new HttpCommand() {
            private Exception exception;

            @Override
            public int incrementRedirectCount() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public int getRedirectCount() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isReplayable() {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public int incrementFailureCount() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public int getFailureCount() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public HttpRequest getCurrentRequest() {
                try {
                    return new HttpRequest("method", new URI("http://endpoint"));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void setCurrentRequest(HttpRequest request) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void setException(Exception exception) {
                this.exception = exception;
            }

            @Override
            public Exception getException() {
                return exception;
            }
        };
    }


}
