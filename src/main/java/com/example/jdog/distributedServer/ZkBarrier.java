package com.example.jdog.distributedServer;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ZkBarrier {

    ZooKeeper client;
    String root;
    int groupSize;
    String name;
    Watcher watcher;
    String seqSuffix;

    public ZkBarrier(ZooKeeper client, String root, int groupSize, Watcher watcher) {
        this.client = client;
        this.root = root;
        this.groupSize = groupSize;
        this.watcher = watcher;
        try {
            this.name = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            this.name = "barrier1";
        }

        try {
            Stat stat = client.exists(root, false);
            if(stat == null) {
                client.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT );
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    boolean enter() throws KeeperException, InterruptedException {
        String path = client.create(root + "/" + name, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        seqSuffix = path.substring(path.length() - 10);

        while(true) {
            synchronized (watcher){
                System.out.println("in enter, checking children");
                int nodesAtBarrier = client.getChildren(root, true).size();
                if (nodesAtBarrier < groupSize) {
                    System.out.println("nodes less than group size, waiting thread: " + Thread.currentThread().getName());

                    watcher.wait();
                } else {
                    System.out.println("returning from enter");
                    return true;
                }
            }
        }
    }

    boolean leave() throws KeeperException, InterruptedException {
        client.delete(root + "/" + name + seqSuffix, 0);


        while(true) {
            synchronized (watcher) {
                int nodesAtBarrier = client.getChildren(root, true).size();
                System.out.println("in leave, checking children");

                if(nodesAtBarrier > 0) {
                    System.out.println("nodes greater than zero, waiting thread: " + Thread.currentThread().getName());

                    watcher.wait();
                } else{
                    System.out.println("returning from leave");
                    return true;
                }
            }
        }
    }

}
