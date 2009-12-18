package org.jclouds.compute.domain;

/**
 * Indicates the status of a server
 * 
 * @author Adrian Cole
 */
public enum ServerState {
   /**
    * The server is in transition
    */
   PENDING,
   /**
    * The server is not running
    */
   TERMINATED,
   /**
    * The server is deployed, but suspended
    */
   SUSPENDED,
   /**
    * The server is available for requests
    */
   RUNNING;

}