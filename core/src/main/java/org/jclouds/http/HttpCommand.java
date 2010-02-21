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
package org.jclouds.http;

/**
 * Command whose endpoint is an http service.
 * 
 * @author Adrian Cole
 */
public interface HttpCommand {

   /**
    * increments the current number of redirect attempts for this command.
    * 
    * @see #getRedirectCount
    */
   int incrementRedirectCount();

   /**
    * This displays the current number of redirect attempts for this command.
    * 
    * @see org.jclouds.Constants.PROPERTY_MAX_REDIRECTS
    */
   int getRedirectCount();

   /**
    * Commands need to be replayed, if redirected or on a retryable error. Typically, this implies
    * the payload carried is not a streaming type.
    */
   boolean isReplayable();

   /**
    * change the destination of the current http command. typically used in handling redirects.
    * 
    * @param string
    */
   void changeSchemeHostAndPortTo(String scheme, String host, int port);

   /**
    * change method from GET to HEAD. typically used in handling redirects.
    */
   void changeToGETRequest();

   /**
    * change the path of the service. typically used in handling redirects.
    */
   void changePathTo(String newPath);

   /**
    * increment the current failure count.
    * 
    * @see #getFailureCount
    */
   int incrementFailureCount();

   /**
    * This displays the current number of error retries for this command.
    * 
    * @see org.jclouds.Constants.PROPERTY_MAX_RETRIES
    */
   int getFailureCount();

   /**
    * The request associated with this command.
    */
   HttpRequest getRequest();

   /**
    * Used to prevent a command from being re-executed, or having its response parsed.
    */
   void setException(Exception exception);

   /**
    * @see #setException
    */
   Exception getException();

}
