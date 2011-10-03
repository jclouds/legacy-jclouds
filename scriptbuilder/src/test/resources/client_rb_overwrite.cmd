copy /y CON c:\etc\chef\client.rb
echo log_level :info >>c:\etc\chef\client.rb
echo log_location STDOUT >>c:\etc\chef\client.rb
echo chef_server_url "http://localhost:4000" >>c:\etc\chef\client.rb
