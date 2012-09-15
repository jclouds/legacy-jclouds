/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.compute.exception;

/**
 * Exception thrown when there are not enough resources in the infrastructure to deploy the desired
 * template.
 * 
 * @author Ignasi Barrera
 */
public class NotEnoughResourcesException extends RuntimeException
{
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    public NotEnoughResourcesException()
    {
        super();
    }

    public NotEnoughResourcesException(final String arg0, final Throwable arg1)
    {
        super(arg0, arg1);
    }

    public NotEnoughResourcesException(final String arg0)
    {
        super(arg0);
    }

    public NotEnoughResourcesException(final Throwable arg0)
    {
        super(arg0);
    }

}
