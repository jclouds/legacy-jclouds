package org.jclouds.joyent.cloudapi.v6_5.features;

import java.util.Set;
import org.jclouds.joyent.cloudapi.v6_5.domain.Key;

/**
 * Keys are the means by which you operate on your SSH/signing keys. Currently
 * CloudAPI supports uploads of public keys in the OpenSSH format.
 * 
 * @author Adrian Cole
 * @see KeyAsyncApi
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi/index.html#keys">api doc</a>
 */
public interface KeyApi {

   /**
    * Lists all public keys we have on record for the specified account.
    */
   Set<Key> list();

   /**
    * Retrieves an individual key record.
    */
   Key get(String name);

   /**
    * Uploads a new OpenSSH key to SmartDataCenter for use in SSH and HTTP
    * signing.
    */
   Key create(Key key);

   /**
    * Deletes an SSH key by name.
    */
   void delete(String name);
}
