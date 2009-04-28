/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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
package org.jclouds.samples.googleappengine.config; /**
 * // TODO: Adrian: Document this!
 * @author Adrian Cole
 */

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import org.apache.commons.io.IOUtils;
import org.jclouds.http.commands.config.HttpCommandsModule;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;
import org.jclouds.lifecycle.Closer;
import org.jclouds.samples.googleappengine.JCloudsServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GuiceServletConfig extends GuiceServletContextListener {
    @Inject
    Closer closer;

    ServletContext context;


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.context = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);    // TODO: Adrian: Customise this generated block
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new HttpCommandsModule() {
                    @Override
                    protected void configure() {
                        super.configure();
                        Properties props = new Properties();
                        InputStream input = null;
                        try {
                            input = context.getResourceAsStream("/WEB-INF/jclouds.properties");
                            if (input != null)
                                props.load(input);
                            else
                                throw new RuntimeException("not found in classloader");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } finally {
                            IOUtils.closeQuietly(input);
                        }
                        Names.bindProperties(binder(), props);
                        install(new JavaUrlHttpFutureCommandClientModule());
                    }
                }
                , new ServletModule() {
                    @Override
                    protected void configureServlets() {
                        serve("/*").with(JCloudsServlet.class);
                        requestInjection(this);
                    }
                });
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            closer.close();
        } catch (Exception e) {
            e.printStackTrace();  // TODO: Adrian: Customise this generated block
        }
        super.contextDestroyed(servletContextEvent);    // TODO: Adrian: Customise this generated block
    }
}