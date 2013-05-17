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
package org.jclouds.lifecycle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public interface LifeCycle {

    /**
     * @return the current state of the component;
     */
    Status getStatus();

    /**
     * @return Exception or null, if there are no fatal Exceptions encountered in the lifecycle of this component.
     */
    Exception getException();

    /**
     * Asynchronously starts the component
     */
    @PostConstruct
    void start();

    /**
     * Requests shutdown of the component.
     */
    @PreDestroy
    void shutdown();

    /**
     * Requests shutdown, but will only wait @link waitms milliseconds
     *
     * @param waitMs maximum time to wait in milliseconds
     */
    void shutdown(long waitMs);

    /**
     * States that are possible for a component.
     */
    public static enum Status {

        /**
         * The component is inactive / has not been started
         */
        INACTIVE,

        /**
         * The component is active / processing I/O events.
         */
        ACTIVE,

        /**
         * Shutdown of the component has been requested.
         */
        SHUTDOWN_REQUEST,

        /**
         * The component is shutting down.
         */
        SHUTTING_DOWN,

        /**
         * The component has shut down.
         */
        SHUT_DOWN;
    }
}
