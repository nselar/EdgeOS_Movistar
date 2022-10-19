firewall {
    all-ping enable
    broadcast-ping disable
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians disable
    name INTERNET_LOCAL {
        default-action drop
        description "Trafico de internet a Router"
        rule 1 {
            action accept
            description "Permitir establecidas o relativas"
            log disable
            protocol all
            state {
                established enable
                invalid disable
                new disable
                related enable
            }
        }
        rule 2 {
            action drop
            description "Deniega no validas"
            log disable
            protocol all
            state {
                established disable
                invalid enable
                new disable
                related disable
            }
        }
        rule 3 {
            action accept
            description "permite icmp"
            log disable
            protocol icmp
        }
        rule 4 {
            action accept
            description "permitir gestion remota"
            destination {
                port 22,443
            }
            disable
            log disable
            protocol tcp
        }
    }
    options {
        mss-clamp {
            interface-type pppoe
            mss 1452
        }
    }
    receive-redirects disable
    send-redirects enable
    source-validation disable
    syn-cookies enable
}
interfaces {
    ethernet eth0 {
        duplex auto
        speed auto
        vif 2 {
            address 10.248.248.248/9
            description MovistarTV
        }
        vif 3 {
            address dhcp
            description Voip
            dhcp-options {
                default-route no-update
                default-route-distance 210
            }
        }
        vif 6 {
            description Internet
            pppoe 0 {
                default-route force
                firewall {
                    local {
                        name INTERNET_LOCAL
                    }
                }
                mtu 1492
                name-server auto
                password adslppp
                user-id adslppp@telefonicanetpa
            }
        }
    }
    ethernet eth1 {
        duplex auto
        speed auto
    }
    ethernet eth2 {
        duplex auto
        speed auto
    }
    ethernet eth3 {
        duplex auto
        speed auto
    }
    ethernet eth4 {
        duplex auto
        poe {
            output off
        }
        speed auto
    }
    loopback lo {
    }
    switch switch0 {
        address 192.168.1.1/24
        switch-port {
            interface eth1
            interface eth2
            interface eth3
            interface eth4
        }
    }
}
port-forward {
    auto-firewall disable
    hairpin-nat disable
    lan-interface switch0
    rule 1 {
        description ejemplo
        forward-to {
            address 192.168.1.10
            port 8889
        }
        original-port 8889
        protocol tcp
    }
    wan-interface pppoe0
}
protocols {
    igmp-proxy {
        interface eth0.2 {
            alt-subnet 0.0.0.0/0
            role upstream
            threshold 1
        }
        interface eth0.3 {
            role disabled
            threshold 1
        }
        interface pppoe0 {
            role disabled
            threshold 1
        }
        interface switch0 {
            role downstream
            threshold 1
        }
    }
    rip {
        interface eth0.3
        interface eth0.2
        passive-interface default
    }
}
service {
    dhcp-server {
        disabled false
        global-parameters "option option-deco code 240 = string;"
        global-parameters "class &quot;decos&quot; { match if substring (option vendor-class-identifier , 0, 5) = &quot;[IAL]&quot;; }"
        hostfile-update disable
        shared-network-name dhcp1 {
            authoritative disable
            subnet 192.168.1.0/24 {
                default-router 192.168.1.1
                dns-server 192.168.1.1
                lease 86400
                start 192.168.1.100 {
                    stop 192.168.1.199
                }
                subnet-parameters "option option-deco &quot;:::::239.0.2.10:22222:v6.0:239.0.2.30:22222&quot;;"
                subnet-parameters "pool {"
                subnet-parameters "allow members of &quot;decos&quot;;"
                subnet-parameters "range 192.168.1.200 192.168.1.210;"
                subnet-parameters "option domain-name-servers 172.26.23.3;"
                subnet-parameters " }"
            }
        }
    }
    dns {
        forwarding {
            cache-size 150
            listen-on switch0
        }
    }
    gui {
        https-port 443
    }
    nat {
        rule 1 {
            description VOD_Imagenio
            destination {
                group {
                    address-group ADDRv4_eth0.2
                }
            }
            disable
            inbound-interface eth0.2
            inside-address {
                address 192.168.1.200
            }
            log disable
            protocol tcp_udp
            type destination
        }
        rule 5000 {
            description Masq_Internet
            log disable
            outbound-interface pppoe0
            protocol all
            type masquerade
        }
        rule 5001 {
            description Masq_Voip
            log disable
            outbound-interface eth0.3
            protocol all
            type masquerade
        }
        rule 5002 {
            description Masq_TV
            log disable
            outbound-interface eth0.2
            protocol all
            type masquerade
        }
    }
    ssh {
        port 22
        protocol-version v2
    }
    ubnt-discover {
        disable
    }
    upnp2 {
        listen-on switch0
        nat-pmp enable
        secure-mode disable
        wan pppoe0
    }
}
system {
    config-management {
    }
    conntrack {
        expect-table-size 4096
        hash-size 4096
        modules {
            sip {
                disable
            }
        }
        table-size 32768
        tcp {
            half-open-connections 512
            loose enable
            max-retrans 3
        }
    }
    host-name Router
    login {
        user ubnt {
            authentication {
                encrypted-password $6$PYuiCrphePD3S7$jBrtiG82dkFX23seLsBInYUaI9.S5yIROhLAPHLXpb.azrT2Tdctq/HbpO5vtBtVkuW/WsK3JOIpe6Up1B8KU1
                plaintext-password ""
            }
            full-name ubnt
            level admin
        }
    }
    ntp {
        server 0.ubnt.pool.ntp.org {
        }
        server 1.ubnt.pool.ntp.org {
        }
        server 2.ubnt.pool.ntp.org {
        }
        server 3.ubnt.pool.ntp.org {
        }
    }
    static-host-mapping {
    }
    syslog {
        global {
            facility all {
                level notice
            }
            facility protocols {
                level debug
            }
        }
    }
    time-zone Europe/Madrid
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@4:nat@3:qos@1:quagga@2:system@4:ubnt-pptp@1:ubnt-util@1:vrrp@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v1.6.6.4749363.150224.1217 */
