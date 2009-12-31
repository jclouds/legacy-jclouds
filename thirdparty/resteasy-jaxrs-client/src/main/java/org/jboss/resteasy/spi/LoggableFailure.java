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
package org.jboss.resteasy.spi;

import javax.ws.rs.core.Response;

/**
 * This exception should only be used by Resteasy integrators.  Applications code should use WebApplicationException
 * <p/>
 * This is thrown by Resteasy runtime when a failure occurs.  It will be logged by the runtime
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LoggableFailure extends Failure
{
   public LoggableFailure(String s, Response response)
   {
      super(s, response);
   }

   public LoggableFailure(String s, Throwable throwable, Response response)
   {
      super(s, throwable, response);
   }

   public LoggableFailure(Throwable throwable, Response response)
   {
      super(throwable, response);
   }

   public LoggableFailure(String s, Throwable throwable)
   {
      super(s, throwable);
      loggable = true;
   }

   public LoggableFailure(Throwable throwable)
   {
      super(throwable);
      loggable = true;
   }

   public LoggableFailure(String s)
   {
      super(s);
      loggable = true;
   }

   public LoggableFailure(int errorCode)
   {
      super(errorCode);
      loggable = true;
   }

   public LoggableFailure(String s, int errorCode)
   {
      super(s, errorCode);
      loggable = true;
   }

   public LoggableFailure(String s, Throwable throwable, int errorCode)
   {
      super(s, throwable, errorCode);
      loggable = true;
   }

   public LoggableFailure(Throwable throwable, int errorCode)
   {
      super(throwable, errorCode);
      loggable = true;
   }
}
