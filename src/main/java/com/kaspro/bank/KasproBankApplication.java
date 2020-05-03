package com.kaspro.bank;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class KasproBankApplication {
	public static void main(String[] args) throws Exception {
		new SshTunnelStarter().init();{log.info("Ssh Tunnel started please turn off if not needed");};
		SpringApplication.run(KasproBankApplication.class, args);
	}
}

@Slf4j
class SshTunnelStarter {

	@Value("${ssh.tunnel.url}")
	private String url;

	@Value("${ssh.tunnel.username}")
	private String username;

	@Value("${ssh.tunnel.password}")
	private String password;

	@Value("${ssh.tunnel.port:777}")
	private int port;

	private Session session;

	@PostConstruct
	public void init() throws Exception {

		JSch jsch = new JSch();
		log.info("JSch started");
		// Get SSH session
		session = jsch.getSession("devuser", "147.139.169.114", 777);
		session.setPassword("Kokas@jakart4");
		java.util.Properties config = new java.util.Properties();
		// Never automatically add new host keys to the host file
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		// Connect to remote server
		session.connect();
		// Apply the port forwarding
		session.setPortForwardingL(3306, "localhost",3306);
	}

	@PreDestroy
	public void shutdown() throws Exception {
		if (session != null && session.isConnected()) {
			session.disconnect();
		}
	}
}
