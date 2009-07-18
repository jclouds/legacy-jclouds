package org.jclouds.ssh;

/**
 * @author Adrian Cole
 */
public class SshException extends RuntimeException {

   /** The serialVersionUID */
   private static final long serialVersionUID = 7271048517353750433L;

   public SshException() {
      super();
   }

   public SshException(String arg0, Throwable arg1) {
      super(arg0, arg1);
   }

   public SshException(String arg0) {
      super(arg0);
   }

   public SshException(Throwable arg0) {
      super(arg0);
   }

}