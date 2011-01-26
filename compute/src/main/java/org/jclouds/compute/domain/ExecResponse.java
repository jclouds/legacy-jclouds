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

package org.jclouds.compute.domain;

import org.jclouds.compute.config.CustomizationResponse;

/**
 * @author Adrian Cole
 */
public class ExecResponse implements CustomizationResponse {

   private final String error;
   private final String output;
   private final int exitCode;

   public ExecResponse(String output, String error, int exitCode) {
      this.output = output;
      this.error = error;
      this.exitCode = exitCode;
   }

   public String getError() {
      return error;
   }

   public String getOutput() {
      return output;
   }

   @Override
   public String toString() {
      return "[output=" + output + ", error=" + error + ", exitCode=" + exitCode + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((error == null) ? 0 : error.hashCode());
      result = prime * result + exitCode;
      result = prime * result + ((output == null) ? 0 : output.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ExecResponse other = (ExecResponse) obj;
      if (error == null) {
         if (other.error != null)
            return false;
      } else if (!error.equals(other.error))
         return false;
      if (exitCode != other.exitCode)
         return false;
      if (output == null) {
         if (other.output != null)
            return false;
      } else if (!output.equals(other.output))
         return false;
      return true;
   }

   public int getExitCode() {
      return exitCode;
   }

}