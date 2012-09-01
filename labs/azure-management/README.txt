 How to create the certificate :
 
 Create the .cer and pfx files using http://www.windowsazure4j.org/learn/labs/Management/index.html or http://msdn.microsoft.com/en-us/library/gg551722
  
 Add it to azure : http://msdn.microsoft.com/en-us/library/gg551726
  
 Generate the keystore  http://stackoverflow.com/questions/4217107/how-to-convert-pfx-file-to-keystore-with-private-key
   openssl pkcs12 -in certificate.pfx -out certificate.pem
