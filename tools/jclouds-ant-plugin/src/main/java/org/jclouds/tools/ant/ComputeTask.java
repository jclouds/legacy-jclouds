package org.jclouds.tools.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

/**
 * @author Ivan Meredith
 */
public class ComputeTask extends Task {
   private final String ACTION_CREATE = "create";

   private String action;

   private ServerElement serverElement;
   public void execute() throws BuildException {
      if(ACTION_CREATE.equalsIgnoreCase(action)){
         if(getServerElement() != null){

         }
      }
   }

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }

   public ServerElement getServerElement() {
      return serverElement;
   }

   public void setServerElement(ServerElement serverElement) {
      this.serverElement = serverElement;
   }
}
