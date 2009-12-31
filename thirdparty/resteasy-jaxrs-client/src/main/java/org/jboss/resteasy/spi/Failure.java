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

import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.Response;


/**
 * This exception should only be used by Resteasy integrators.  Applications code should use WebApplicationException.
 * <p/>
 * This is thrown by Restasy runtime when a failure occurs.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Failure extends RuntimeException
{

   protected int errorCode = -1;
   protected boolean loggable;
   protected Response response;

   public Failure(String s, Response response)
   {
      super(s);
      this.response = response;
   }

   public Failure(String s, Throwable throwable, Response response)
   {
      super(s, throwable);
      this.response = response;
   }

   public Failure(Throwable throwable, Response response)
   {
      super(throwable);
      this.response = response;
   }

   public Failure(String s, Throwable throwable)
   {
      super(s, throwable);
      this.errorCode = HttpResponseCodes.SC_INTERNAL_SERVER_ERROR;
   }

   public Failure(Throwable throwable)
   {
      super(throwable);
      this.errorCode = HttpResponseCodes.SC_INTERNAL_SERVER_ERROR;
   }

   public Failure(String s)
   {
      super(s);
      this.errorCode = HttpResponseCodes.SC_INTERNAL_SERVER_ERROR;
   }

   public Failure(int errorCode)
   {
      this.errorCode = errorCode;
   }

   public Failure(String s, int errorCode)
   {
      super(s);
      this.errorCode = errorCode;
   }

   public Failure(String s, Throwable throwable, int errorCode)
   {
      super(s, throwable);
      this.errorCode = errorCode;
   }

   public Failure(Throwable throwable, int errorCode)
   {
      super(throwable);
      this.errorCode = errorCode;
   }

   public int getErrorCode()
   {
      return errorCode;
   }

   public void setErrorCode(int errorCode)
   {
      this.errorCode = errorCode;
   }

   public boolean isLoggable()
   {
      return loggable;
   }

   public void setLoggable(boolean loggable)
   {
      this.loggable = loggable;
   }

   public Response getResponse()
   {
      return response;
   }
}
