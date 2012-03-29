package org.jclouds.openstack.swift.blobstore.strategy;

/*
@author Roman Bogorodskiy
 */

public interface MultipartUpload {

    /* Maximum number of parts per upload */
    public static final int MAX_NUMBER_OF_PARTS = 10000;
    /* Maximum number of parts returned for a list parts request */
    public static final int MAX_LIST_PARTS_RETURNED = 1000;
    /* Maximum number of multipart uploads returned in a list multipart uploads request */
    public static final int MAX_LIST_MPU_RETURNED = 1000;

    /*
    * part size 5 MB to 5 GB, last part can be < 5 MB
    */
    public static final long MIN_PART_SIZE = 5242880L;
    public static final long MAX_PART_SIZE = 5368709120L;
}
