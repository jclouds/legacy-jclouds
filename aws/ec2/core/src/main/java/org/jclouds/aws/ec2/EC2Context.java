package org.jclouds.aws.ec2;

/**
 * Represents an authenticated context to EC2.
 * 
 * <h2>Note</h2> Please issue {@link #close()} when you are finished with this
 * context in order to release resources.
 * 
 * 
 * @see EC2Connection
 * @author Adrian Cole
 * 
 */
public interface EC2Context {

    /**
     * low-level api to EC2. Threadsafe implementations will return a singleton.
     * 
     * @return a connection to EC2
     */
    EC2Connection getConnection();

    /**
     * Closes all connections to EC2.
     */
    void close();

}
