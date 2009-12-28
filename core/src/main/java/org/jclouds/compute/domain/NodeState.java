package org.jclouds.compute.domain;

/**
 * Indicates the status of a node
 * 
 * @author Adrian Cole
 */
public enum NodeState {
   /**
    * The node is in transition
    */
   PENDING,
   /**
    * The node is not running
    */
   TERMINATED,
   /**
    * The node is deployed, but suspended
    */
   SUSPENDED,
   /**
    * The node is available for requests
    */
   RUNNING;

}