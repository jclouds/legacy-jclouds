Status:

All the private apis are implemented and tested.
Snapshots are disabled because they are also disabled in GCE.
Just missing jcloud.compute glue code (coming soon!)

How to run the live tests:

Pre-requisites:
A Google Api account with Google Compute Engine enabled
The access pk (provided by google in PKCS12 format) in pem format.

running all tests:

mvn clean install -Plive -Dtest.google-compute.identity=<my account>@developer.gserviceaccount.com -Dtest.google-compute.credential=<path to pk.pem>

