/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.ibm.smartcloud;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.ibm.smartcloud.domain.Address;
import org.jclouds.ibm.smartcloud.domain.Image;
import org.jclouds.ibm.smartcloud.domain.Instance;
import org.jclouds.ibm.smartcloud.domain.Key;
import org.jclouds.ibm.smartcloud.domain.Location;
import org.jclouds.ibm.smartcloud.domain.Offering;
import org.jclouds.ibm.smartcloud.domain.StorageOffering;
import org.jclouds.ibm.smartcloud.domain.Volume;
import org.jclouds.ibm.smartcloud.options.CreateInstanceOptions;
import org.jclouds.ibm.smartcloud.options.RestartInstanceOptions;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

/**
 * Provides synchronous access to IBMSmartCloud.
 * <p/>
 * 
 * @see IBMSmartCloudAsyncClient
 * @see <a href="http://www-180.ibm.com/cloud/enterprise/beta/support" />
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface IBMSmartCloudClient {
   /**
    * 
    * @return the list of Images available to be provisioned on the IBM
    *         DeveloperCloud.
    */
   Set<? extends Image> listImages();

   /**
    * Returns the available Image identified by the supplied Image ID.
    * 
    * @return null if image is not found
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to view this image to
    *            section
    */
   Image getImage(String id);

   /**
    * Deletes Image identified by the supplied Image ID.
    * 
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to delete this image
    * @throws IllegalStateException
    *            code 412 if the image is in an invalid state to perform this
    *            operation
    */
   void deleteImage(String id);
   
   String getManifestOfImage(String id);

   /**
    * If set to {@code Image.Visibility#PUBLIC}, makes the Image identified by
    * the supplied Image ID publicly available for all users to create Instances
    * of.
    * 
    * @return modified image or null, if image was not found.
    * 
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to change the visibility
    *            of this image
    * @throws IllegalStateException
    *            code 412 if the image is in an invalid state to perform this
    *            operation
    */
   Image setImageVisibility(String id, Image.Visibility visibility);

   /**
    * 
    * @return the list of Instances that the authenticated user manages.
    * @throws AuthorizationException
    *            code 401 if the currently authenticated user is not authorized
    *            to view this information
    */
   Set<? extends Instance> listInstances();

   /**
    * 
    * @return the list of Instances that the authenticated user manages that
    *         were created as part of the request specified by {@code requestId}
    *         , or null if the request was not found
    * @throws AuthorizationException
    *            code 401 if the currently authenticated user is not authorized
    *            to view this request
    */
   Set<? extends Instance> listInstancesFromRequest(String requestId);

   /**
    * Returns the Instance that the authenticated user manages with the
    * specified {@code id}
    * 
    * @return null if instance is not found
    * @throws AuthorizationException
    *            code 401 if the currently authenticated user is not authorized
    *            to view this instance
    */
   Instance getInstance(String id);

   /**
    * Requests a new Instance to be created.
    * 
    * @param location
    *           The id of the Location where this instance will be created
    * @param name
    *           The alias to use to reference this instance
    * @param imageID
    *           The ID of the image to create this instance from
    * @param instanceType
    *           The instance type to use for this instance {SMALL, MEDIUM,
    *           LARGE}
    * @param options
    *           overrides default public key, mounts a volume, or attaches a
    *           static ip
    * @throws AuthorizationException
    *            code 401 if the authenticated user is not authorized to create
    *            instances
    *            <p/>
    *            code 402 if payment is required before more instances may be
    *            created
    * @throws IllegalStateException
    *            code 409 if there are not enough resources in the cloud to
    *            fulfill this request
    *            <p/>
    *            code 412 One or more of the supplied parameters are invalid for
    *            this request
    */
   Instance createInstanceInLocation(String location, String name, String imageID, String instanceType,
         CreateInstanceOptions... options);

   /**
    * Sets the expiration time of the instance to the value specified
    * 
    * @throws ResourceNotFoundException
    *            code 404 The instance specified by {@code name} was not found
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to extend the expiration
    *            time of this instance
    *            <p/>
    *            code 402 if payment is required before more instances may be
    *            created
    * @throws IllegalStateException
    *            <p>
    *            code 406 The provided expiration date is not valid code 409 if
    *            there are not enough resources in the cloud to fulfill this
    *            request
    *            <p/>
    *            code 412 The instance is in an invalid state to perform this
    *            operation
    */
   Date extendReservationForInstance(String id, Date expirationTime);

   /**
    * Restart the instance
    * 
    * @param id
    *           the instance to restart
    * @param options
    *           allows you to specify a new public key for login
    * @throws ResourceNotFoundException
    *            code 404 The instance specified by {@code name} was not found
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to extend the expiration
    *            time of this instance
    *            <p/>
    *            code 402 if payment is required before more instances may be
    *            created
    * @throws IllegalStateException
    *            <p>
    *            code 406 The provided expiration date is not valid code 409 if
    *            there are not enough resources in the cloud to fulfill this
    *            request
    *            <p/>
    *            code 412 The instance is in an invalid state to perform this
    *            operation
    */
   void restartInstance(String id, RestartInstanceOptions... options);

   /**
    * Saves an instance to a private image
    * 
    * @param id
    *           the instance to save
    * @param toImageName
    *           The name to associate with the captured image.
    * @param toImageDescription
    *           The description to associate with the capture image.
    * @return a private image
    * @throws ResourceNotFoundException
    *            code 404 The instance specified by {@code name} was not found
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to extend the expiration
    *            time of this instance
    *            <p/>
    *            code 402 if payment is required before more instances may be
    *            created
    * @throws IllegalStateException
    *            <p>
    *            code 406 The provided expiration date is not valid code 409 if
    *            there are not enough resources in the cloud to fulfill this
    *            request
    *            <p/>
    *            code 412 The instance is in an invalid state to perform this
    *            operation
    */
   Image saveInstanceToImage(String id, String toImageName, String toImageDescription);

   /**
    * Deletes the Instance that the authenticated user manages with the
    * specified {@code id}
    * 
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to delete this instance
    * @throws IllegalStateException
    *            code 412 if the instance is in an invalid state to perform this
    *            operation
    */
   void deleteInstance(String id);

   /**
    * 
    * @return the set of Public Keys stored for the authenticated user.
    */
   Set<? extends Key> listKeys();

   /**
    * Returns the key with the specified key name from the set of Public Keys
    * stored for the authenticated user.
    * 
    * @return null if key is not found
    */
   Key getKey(String name);

   /**
    * Used to generate a new SSH Key Pair for the authenticated user.
    * 
    * @param name
    *           The name to used to identify this key pair.
    * @return private key
    * 
    * @throws IllegalStateException
    *            code 409 A key with the specified {@code name} already exists
    * 
    */
   Key generateKeyPair(String name);

   /**
    * Used to generate a new SSH Key Pair for the authenticated user.
    * 
    * @param name
    *           The name to used to identify this key pair.
    * @param publicKey
    *           The RSA SSH Key to add
    * 
    * @throws IllegalStateException
    *            code 409 A key with the specified {@code name} already exists
    *            <p/>
    *            code 412 The supplied public key is invalid
    */
   void addPublicKey(String name, String publicKey);

   /**
    * Used to update the Public Key specified by the supplied key name stored
    * for the authenticated user.
    * 
    * @param name
    *           The name to used to identify this key pair.
    * @param publicKey
    *           The public key to store
    * @throws ResourceNotFoundException
    *            code 404 The key specified by {@code name} was not found
    * @throws IllegalStateException
    *            code 412 The supplied public key is invalid
    */
   void updatePublicKey(String name, String publicKey);

   /**
    * Used to set the Public Key specified by the supplied key name as the
    * default key.
    * 
    * @param name
    *           The name to used to identify this key pair.
    * @param isDefault
    *           A boolean representing the default state of this key
    * @throws ResourceNotFoundException
    *            code 404 The key specified by {@code name} was not found
    */
   void setDefaultStatusOfKey(String name, boolean isDefault);

   /**
    * Deletes Key identified by the supplied key name.
    * 
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to perform this action
    */
   void deleteKey(String name);

   /**
    * Used to retrieve the offerings of storage for the authenticated user.
    * 
    * @return offerings or empty set if none
    * @throws AuthorizationException
    *            code 401 if the currently authenticated user is not authorized
    *            to view this information
    */
   Set<? extends StorageOffering> listStorageOfferings();

   /**
    * 
    * @return the set of storage volumes for the authenticated user.
    * @throws AuthorizationException
    *            code 401 if the currently authenticated user is not authorized
    *            to view this information
    */
   Set<? extends Volume> listVolumes();

   /**
    * Creates a new storage volume for the authenticated user.
    * 
    * @param location
    *           The id of the Location where the storage volume will be created
    * @param name
    *           The desired name of the newly created storage volume
    * @param format
    *           The filesystem format for the new storage volume. Valid format
    *           is: EXT3
    * @param size
    *           The size of the new storage volume. Valid values may include
    *           SMALL, MEDIUM, and LARGE. Actual values may depend on the
    *           location used and may be discovered via the location service
    * @param offeringID
    *           The offeringID which can be obtained from
    *           {@link #listStorageOfferings}
    * @throws AuthorizationException
    *            code 401 if the currently authenticated user is not authorized
    *            to create a volume
    *            <p/>
    *            code 402 if payment is required before more storage volumes may
    *            be created
    * @throws IllegalStateException
    *            code 409 if there are not enough resources in the cloud to
    *            fulfill this request
    *            <p/>
    *            code 412 One or more of the supplied parameters are invalid for
    *            this request
    */
   Volume createVolumeInLocation(String location, String name, String format, String size, String offeringID);

   /**
    * Used to retrieve the specified storage volume for the authenticated user.
    * 
    * @return null if volume is not found
    * @throws AuthorizationException
    *            code 401 if the currently authenticated user is not authorized
    *            to view this storage volume
    */
   Volume getVolume(String id);

   /**
    * Remove the specified storage volume for the authenticated user.
    * 
    * @throws AuthorizationException
    *            code 401 if the currently authenticated user is not authorized
    *            to remove this storage volume
    * @throws IllegalStateException
    *            code 412 if the storage volume is not in the correct state to
    *            be deleted
    */
   void deleteVolume(String id);

   /**
    * 
    * @return the list of Locations (Data Centers) that the user is entitled to
    *         and their capabilities
    */
   Set<? extends Location> listLocations();

   /**
    * Returns the Location identified by the supplied Location ID
    * 
    * @return null if location is not found
    * 
    * @throws AuthorizationException
    *            code 401 if the user is not authorized
    */
   Location getLocation(String id);

   /**
    * 
    * @return the set of static IP addresses for the authenticated user.
    * @throws AuthorizationException
    *            code 401 if the currently authenticated user is not authorized
    *            to view this information
    */
   Set<? extends Address> listAddresses();

   /**
    * 
    * Allocates a new static IP addresses for the authenticated user.
    * 
    * @param locationId
    *           the id of the Location where this address will be allocated
    * @param offeringID
    *           The offeringID which can be obtained from
    *           {@link #listAddressOfferings}
    * @throws AuthorizationException
    *            code 401 if the currently authenticated user is not authorized
    *            to remove this IP address
    *            <p/>
    *            code 402 if payment is required before more addresses may be
    *            allocated
    * 
    * @throws IllegalStateException
    *            code 409 if there are not enough resources in the cloud to
    *            fulfill this request
    */
   Address allocateAddressInLocation(String locationId, String offeringID);

   /**
    * Used to retrieve the offerings of addresses for the authenticated user.
    * 
    * @return offerings or empty set if none
    * @throws AuthorizationException
    *            code 401 if the currently authenticated user is not authorized
    *            to view this information
    */
   Set<? extends Offering> listAddressOfferings();

   /**
    * Used to release the specified static IP addresses for the authenticated
    * user.
    * 
    * @throws AuthorizationException
    *            code 401 if the user is not authorized to release this address
    * @throws IllegalStateException
    *            code 412 address is in an invalid state to perform this
    *            operation
    */
   void releaseAddress(String id);


}
