package org.jclouds.openstack.cinder.v1.features;

import org.jclouds.openstack.cinder.v1.domain.VolumeType;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Volumes via their REST API.
 * 
 * @see VolumeAsyncApi
 * @see <a href="http://api.openstack.org/">API Doc</a>
 * @author Everett Toews
 */
public interface VolumeTypeApi {
   /**
    * Returns a summary list of VolumeTypes.
    *
    * @return The list of VolumeTypes
    */
   FluentIterable<? extends VolumeType> list();

   /**
    * Return data about the given VolumeType.
    *
    * @param volumeTypeId Id of the VolumeType
    * @return Details of a specific VolumeType
    */
   VolumeType get(String volumeTypeId);
}
