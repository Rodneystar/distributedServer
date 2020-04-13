package com.example.jdog.distributedServer;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class DistributedServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedServerApplication.class, args);
	}


	public Watcher zkWatcher() {
		return new Watcher() {
			@Override
			synchronized public void process(WatchedEvent event) {
				synchronized (this) {
					System.out.println("in process - notifying: " + Thread.currentThread().getName());
					this.notify();
				}
			}
		};
	}

	public ZooKeeper getZooKeeper( Watcher zkWatcher) throws IOException {
		ZooKeeper zookeeper = new ZooKeeper("192.168.99.100", 3000, zkWatcher);
		return zookeeper;
	}

	@Bean
	ZkBarrier zkBarrier( ) throws IOException {
		String root = "/barrier";
		Watcher watcher = zkWatcher();
		return new ZkBarrier(getZooKeeper(watcher), root, 2, watcher);

	}
}
