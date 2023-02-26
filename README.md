# EdgeOS_Movistar_TV
Backup config to make Ubiquiti's EdgeRouter X to work with Movistar FTTH, internet, voice and TV service, and now with the native iPv6
# Use
Just change the address value of interface on vlan 2 on config.boot to your own Movistar TV address and upload the tar.gz to the restore config section of your router
```clojure
 vif 2 {
            address [YourMovistarTVIPAddress]/[Subnet]
            description MovistarTV
        }
````
# Contribution
Pull requests are welcome, to make major changes, please open an issue and comment what are you going to change and why.
# Licenses
[MIT](https://choosealicense.com/licenses/mit/)
