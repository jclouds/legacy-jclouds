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
package org.jclouds.gogrid.binders;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

/**
 * Tests that name bindings are proper for request 
 *
 * @author Oleksiy Yarmula
 */
public class BindNamesToQueryParamsTest {

    @Test
    public void testBinding() {
        GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
        String[] input = {"hello", "world"};

        BindNamesToQueryParams binder = new BindNamesToQueryParams();

        request.addQueryParam("name", "hello");
        request.addQueryParam("name", "world");
        replay(request);

        binder.bindToRequest(request, input);

    }

}
