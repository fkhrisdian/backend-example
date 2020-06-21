package com.kaspro.bank;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.kaspro.bank.services.AuditTrailService;
import com.kaspro.bank.services.BniEncryption;
import com.kaspro.bank.util.InitDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Date;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class KasproBankApplication {
	public static void main(String[] args) throws Exception {
		new SshTunnelStarter().init();
		SpringApplication.run(KasproBankApplication.class, args);
//		BniEncryption.TestBniEncryption(); // test encrypt decrypt
		//InitDB config = InitDB.getInstance();
		//log.info(a.toString());


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
	public void init() {

		JSch jsch = new JSch();
		log.info("JSch started");
		// Get SSH session
		try {
			session = jsch.getSession("devuser", "147.139.169.114", 777);
			session.setPassword("Kokas@jakart4");
			java.util.Properties config = new java.util.Properties();
			// Never automatically add new host keys to the host file
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			// Connect to remote server
			session.connect();
			// Apply the port forwarding
			session.setPortForwardingL(3306, "localhost", 3306);
			log.info("Ssh Tunnel started please turn off if not needed");


		} catch (JSchException e)  {
			log.error(String.valueOf(e));

		}
	}

	@PreDestroy
	public void shutdown() {
		if (session != null && session.isConnected()) {
			session.disconnect();
			log.info("Ssh Tunnel disconnected");

		}
	}
}
