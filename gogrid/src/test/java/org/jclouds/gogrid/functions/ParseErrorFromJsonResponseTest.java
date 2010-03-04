/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.gogrid.functions;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.gogrid.config.GoGridContextModule;
import org.jclouds.gogrid.domain.internal.ErrorResponse;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.net.UnknownHostException;

/**
 * @author Oleksiy Yarmula
 */
public class ParseErrorFromJsonResponseTest {

    @Test
    public void testApplyInputStreamDetails() throws UnknownHostException {
        InputStream is = getClass().getResourceAsStream("/test_error_handler.json");

        ParseErrorFromJsonResponse parser = new ParseErrorFromJsonResponse(i
                .getInstance(Gson.class));
        ErrorResponse response = Iterables.getOnlyElement(parser.apply(is));
        assert "No object found that matches your input criteria.".equals(response.getMessage());
        assert "IllegalArgumentException".equals(response.getErrorCode());
    }


    Injector i = Guice.createInjector(new ParserModule() {
        @Override
        protected void configure() {
            bind(DateAdapter.class).to(GoGridContextModule.DateSecondsAdapter.class);
            super.configure();
        }
    });

}
