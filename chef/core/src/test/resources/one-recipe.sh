if [ ! -f /usr/bin/chef-client ]; then
  apt-get update
  apt-get install -y ruby ruby1.8-dev build-essential wget libruby-extras libruby1.8-extras
  mkdir -p /tmp/bootchef
  (
    cd /tmp/bootchef
    wget http://rubyforge.org/frs/download.php/69365/rubygems-1.3.6.tgz
    tar xvf rubygems-1.3.6.tgz
    cd rubygems-1.3.6
    ruby setup.rb
    cp /usr/bin/gem1.8 /usr/bin/gem
  )
  rm -rf /tmp/bootchef
  gem install chef ohai --no-rdoc --no-ri --verbose
fi
mkdir -p /etc/chef
cat > /etc/chef/client.rb <<'END_OF_FILE'
require 'rubygems'
require 'ohai'
o = Ohai::System.new
o.all_plugins
node_name "foo-" + o[:ipaddress]
log_level :info
log_location STDOUT
validation_client_name "fooclient"
chef_server_url "http://localhost:4000"
END_OF_FILE
cat > /etc/chef/validation.pem <<'END_OF_FILE'
-----BEGIN PRIVATE KEY-----
LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFcFFJQkFBS0NBUUVB
eWIyWkpKcUdtMEtLUis4bmZRSk5zU2QrRjl0WE5NVjdDZk9jVzZqc3FzOEVaZ2lW
ClIwOWhEMUlZT2o0WXFNMHFKT05sZ3lnNHhSV2V3ZFNHN1FUUGoxbEpwVkFpZGE5
c1h5MitrenlhZ1pBMUFtME8KWmNicWI1aG9lSURnY1grZURhNzlzMHUwRG9tamNm
TzlFS2h2SExCeit6TSszUXFQUmtQVjhuWVRiZnMrSGpWegp6T1U2RDFCMFhSMytJ
UFpabDJBbldzMmQwcWhuU3RIY0RVdm5SVlEwUDQ4Mll3TjlWZ2NlT1p0cFB6MERD
S0VKCjVUeDVTVHViOGswL3p0L1ZBTUhRYWZMU3VRTUxkMnM0Wkx1T1pwdE4vL3VB
c1RteGlyZXFkMzd6KzhaVGRCYkoKOExFcEoraUNYdVNmbTVhVWg3aXc2b3h2VG9Z
MkFMNTMraksyVVFJREFRQUJBb0lCQVFEQTg4QjNpL3hXbjB2WApCVnhGYW1DWW9l
Y3VOakd3WFhrU3laZXc2MTZBK0VPQ3U0N2JoNGFUdXJkRmJZTDBZRmFBdGFXdnps
YU4yZUhnCkRiK0hEdVRlZkUyOStXa2NHazZTc2hQbWl6NVQwWE9DQUlDV3c2d1NW
RGtIbUd3UzRqWnZiQUZtN1c4bndHazkKWWh4Z3hGaVJuZ3N3SlpGb3BPTG9GNVdY
czJ0ZDhndUlZTnNsTXBvN3R1NTBpRm5CSHdLTzJac1BBazh0OW5uUwp4bERhdkty
dXltRW1xSENyMytkdGlvNWVhZW5KY3AzZmpvWEJRT0tVazNpcElJMjlYUkI4TnFl
Q1ZWLzdLeHdxCmNrcU9CRWJSd0JjbGNreUliRCtSaUFnS3ZPZWxPUmpFaUU5UjQy
dnVxdnhSQTZrOWtkOW83dXRsWDBBVXRwRW4KM2daYzZMZXBBb0dCQVA5YWVsNVk3
NStzSzJKSlVOT09oTzhhZTQ1Y2RzaWxwMnlJMFgrVUJhU3VRczIrZHlQcAprcEVI
QXhkNHBtbVN2bi84YzlUbEVaaHIrcVliQUJYVlBsRG5jeHBJdXcyQWpiazdzL1M0
WGFTS3NScXBYTDU3CnpqL1FPcUxrUms4K09WVjlxNmxNZVFOcUx0RWoxdTZKUHZp
WDcwUm8rRlF0UnR0Tk9ZYmZkUC9mQW9HQkFNcEEKWGpSNXdvVjVzVWIrUkVnOXZF
dVlvOFJTeU9hcnhxS0ZDSVhWVU5zTE94KzIyK0FLNCtDUXBidWVXTjdqb3RybApZ
RDZ1VDZzdldpM0FBQzdraVkwVUkvZmpWUFJDVWk4dFZvUVVFMFRhVTVWTElUYVlP
QitXL2JCYURFNE05NTYwCjFOdURXTzkwYmFBNWRmVTQ0aXV6dmEwMnJHSlhLOStu
UzNvOG5rL1BBb0dCQUxPTDZkam5EZTRtd0FhRzZKY28KY2Q0eHI4amt5UHpDUlp1
eUJDU0Jid3BoSVVYTGM3aERwclBreTA2NG5jSkQxVURtd0lka1hkL2ZwTWtnMlFt
QQovQ1VrNkxFRmpNaXNxSG9qT2FDTDlnUVpKUGhMTjVRVU4yeDFQSldHanMxdlFo
OFRreDBpVVVDT2E4YlFQWE5SCiszNE9Uc1c2VFVuYTRDU1pBeWNMZmhmZkFvR0JB
SWdnVnNlZkJDdnVRa0YwTmVVaG1EQ1JaZmhuZDh5NTVSSFIKMUhDdnFLSWxwdity
aGNYL3pteUJMdXRlb3BZeVJKUnNPaUUyRlcwMGk4K3JJUFJ1NFozUTVueWJ4N3cz
UHpWOQpvSE41UjViYUU5T3lJNEtwWld6dHBZWWl0WkY2N05jbkF2VlVMSEhPdlZK
UUduS1lmTEhKWW1ySkY3R0Exb2pNCkF1TWRGYmpGQW9HQVB4VWh4d0Z5OGdhcUJh
aEtVRVpuNEY4MUhGUDVpaEdoa1Q0UUw2QUZQTzJlK0poSUdqdVIKMjcrODVoY0Zx
UStISFZ0RnNtODFiL2ErUjdQNFV1Q1JnYzhlQ2p4UU1vSjFYbDRuN1ZialBiSE1u
SU4wUnl2ZApPNFpwV0RXWW5DTzAyMUpUT1VVT0o0Si95MDQxNkJ2a3cwejU5eTdz
Tlg3d0RCQkhIYksvWENjPQotLS0tLUVORCBSU0EgUFJJVkFURSBLRVktLS0tLQo=
-----END PRIVATE KEY-----
END_OF_FILE
cat > /etc/chef/first-boot.json <<'END_OF_FILE'
{"run_list":["recipe[apache2]"]}
END_OF_FILE
chef-client -j /etc/chef/first-boot.json
