# EdgeOS_Movistar_TV
Backup config to make Ubiquiti's EdgeRouter X to work with Movistar FTTH, internet, voice and TV service
# Use
Change the address value of interface on vlan 2 to your own Movistar TV address
```clojure
vif 2 {
            address YOUR_MOVISTAR-TV_IP_ADDRESS
            description MovistarTV
        }
````
# Contribution
Pull requests are welcome, to make major changes, please open an issue and comment what are you going to change and mhy. This project needs to be updated to support future's movistar native iPv6
# Licenses
[MIT](https://choosealicense.com/licenses/mit/)
