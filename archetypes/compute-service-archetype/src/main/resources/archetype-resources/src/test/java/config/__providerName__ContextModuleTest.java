#set( $lcaseProviderName = ${providerName.toLowerCase()} )
#set( $ucaseProviderName = ${providerName.toUpperCase()} )
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
package ${package}.config;

import static org.testng.Assert.assertEquals;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;

import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.http.functions.config.ParserModule.DateAdapter;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.handlers.RedirectionRetryHandler;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import ${package}.reference.${providerName}Constants;
import org.jclouds.util.Jsr330;
import org.jclouds.Constants;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author ${author}
 */
@Test(groups = "unit", testName = "${lcaseProviderName}.${providerName}ContextModule")
public class ${providerName}ContextModuleTest {

    Injector createInjector() {
        return Guice.createInjector(new ${providerName}RestClientModule(), new ${providerName}ContextModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Jsr330.named(${providerName}Constants.PROPERTY_${ucaseProviderName}_USER)).to(
                        "user");
                bindConstant().annotatedWith(Jsr330.named(${providerName}Constants.PROPERTY_${ucaseProviderName}_KEY))
                        .to("password");
                bindConstant().annotatedWith(Jsr330.named(${providerName}Constants.PROPERTY_${ucaseProviderName}_ENDPOINT))
                        .to("http://localhost");
                bindConstant().annotatedWith(Jsr330.named(${providerName}Constants.PROPERTY_${ucaseProviderName}_SESSIONINTERVAL))
                        .to("30");
                bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST))
                        .to("1");
                bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT))
                        .to("0");
                bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_IO_WORKER_THREADS))
                        .to("1");
                bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_USER_THREADS))
                        .to("1");
                bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_CONNECTION_TIMEOUT))
                        .to("30");
                bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_SO_TIMEOUT))
                        .to("10");
                bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
                    public Logger getLogger(String category) {
                        return Logger.NULL;
                    }
                });
                super.configure();
            }
        }, new ParserModule(), new JavaUrlHttpCommandExecutorServiceModule(),
                new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()));
    }

    @Test
    void testServerErrorHandler() {
        DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
        assertEquals(handler.getServerErrorHandler().getClass(),
                "TODO: insert expected error handler class");
    }

    @Test
    void testDateTimeAdapter() {
        assertEquals(this.createInjector().getInstance(DateAdapter.class).getClass(),
                ${providerName}ContextModule.DateSecondsAdapter.class);
    }

    @Test
    void testClientErrorHandler() {
        DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
        assertEquals(handler.getClientErrorHandler().getClass(),
                "TODO: insert expected error handler class");
    }

    @Test
    void testClientRetryHandler() {
        DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
        assertEquals(handler.getClientErrorRetryHandler(), HttpRetryHandler.NEVER_RETRY);
    }

    @Test
    void testRedirectionRetryHandler() {
        DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
        assertEquals(handler.getRedirectionRetryHandler().getClass(), RedirectionRetryHandler.class);
    }

}