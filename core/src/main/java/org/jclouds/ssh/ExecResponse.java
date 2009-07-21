package org.jclouds.ssh;

/**
 * @author Adrian Cole
 */
public class ExecResponse {
   private final String error;
   private final String output;

   public ExecResponse(String output, String error) {
      this.output = output;
      this.error = error;
   }

   public String getError() {
      return error;
   }

   public String getOutput() {
      return output;
   }

}