firewall {
    all-ping enable
    broadcast-ping disable
    ipv6-name WANv6_IN {
        default-action drop
        description "WAN inbound traffic forwarded to LAN"
        enable-default-log
        rule 10 {
            action accept
            description "Allow established/related sessions"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    ipv6-name WANv6_LOCAL {
        default-action drop
        description "WAN inbound traffic to the router"
        enable-default-log
        rule 10 {
            action accept
            description "Allow established/related sessions"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
        rule 30 {
            action accept
            description "Allow IPv6 icmp"
            protocol ipv6-icmp
        }
        rule 40 {
            action accept
            description "allow dhcpv6"
            destination {
                port 546
            }
            protocol udp
            source {
                port 547
            }
        }
    }
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians disable
    name WAN_IN {
        default-action drop
        description "WAN to internal"
        rule 10 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            state {
                invalid enable
            }
        }
    }
    name WAN_LOCAL {
        default-action drop
        description "WAN to router"
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
            description "Deniega no válidas"
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
            action drop
            description "permite icmp"
            log disable
            protocol icmp
        }
        rule 4 {
            action drop
            description "gestion remota"
            destination {
                port 22,443
            }
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
            address [MovistarTVIPAddress]/[Subnet]
            description MovistarTV
        }
        vif 3 {
            address dhcp
            description Voz
            dhcp-options {
                default-route no-update
                default-route-distance 210
                name-server update
            }
            mtu 1500
        }
        vif 6 {
            description "Internet (PPPoE)"
            pppoe 0 {
                default-route force
                dhcpv6-pd {
                    pd 0 {
                        interface switch0 {
                            host-address ::1
                            service slaac
                        }
                        prefix-length /64
                    }
                    rapid-commit enable
                }
                firewall {
                    in {
                        ipv6-name WANv6_IN
                        name WAN_IN
                    }
                    local {
                        ipv6-name WANv6_LOCAL
                        name WAN_LOCAL
                    }
                }
                ipv6 {
                    dup-addr-detect-transmits 1
                    enable {
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
        description Local
        duplex auto
        speed auto
    }
    ethernet eth2 {
        description Local
        duplex auto
        speed auto
    }
    ethernet eth3 {
        description Local
        duplex auto
        speed auto
    }
    ethernet eth4 {
        description Local
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
        address 2001:db80::2/64
        description Local
        ipv6 {
            dup-addr-detect-transmits 1
            router-advert {
                cur-hop-limit 64
                link-mtu 0
                managed-flag false
                max-interval 600
                other-config-flag false
                prefix ::/64 {
                    autonomous-flag true
                    on-link-flag true
                    valid-lifetime 2592000
                }
                reachable-time 0
                retrans-timer 0
                send-advert true
            }
        }
        mtu 1500
        switch-port {
            interface eth1 {
            }
            interface eth2 {
            }
            interface eth3 {
            }
            interface eth4 {
            }
            vlan-aware disable
        }
    }
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
        shared-network-name LAN {
            authoritative disable
            subnet 192.168.1.0/24 {
                default-router 192.168.1.1
                dns-server 80.58.61.250
                dns-server 80.58.61.254
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
        static-arp disable
        use-dnsmasq disable
    }
    dns {
        forwarding {
            cache-size 150
            listen-on switch0
        }
    }
    gui {
        http-port 80
        https-port 443
        older-ciphers enable
    }
    nat {
        rule 1 {
            description VOD_MovistarTV
            destination {
                group {
                    address-group ADDRv4_eth0.2
                }
            }
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
    analytics-handler {
        send-analytics-report false
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
    crash-handler {
        send-crash-report false
    }
    host-name Edgerouter-X-Movistar
    login {
        user nico {
            authentication {
                encrypted-password $5$9lsAcI.bvg.F2yzI$72R9lhntCZ8mQ1uzj8F8wA8nvVh4wPIhAUscY8n1M4.
                plaintext-password ""
            }
            full-name "Your Nmae"
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
    offload {
        hwnat enable
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
    time-zone UTC
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@5:nat@3:qos@1:quagga@2:suspend@1:system@5:ubnt-l2tp@1:ubnt-pptp@1:ubnt-udapi-server@1:ubnt-unms@2:ubnt-util@1:vrrp@1:vyatta-netflow@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v2.0.9-hotfix.6.5574651.221230.1015 */
