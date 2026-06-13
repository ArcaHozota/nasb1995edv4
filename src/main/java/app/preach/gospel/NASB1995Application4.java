package app.preach.gospel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import app.preach.gospel.common.ProjectConstants;

/**
 * Projectアプリケーション
 *
 * @author ArkamaHozota
 * @since 1.00beta
 */
@SpringBootApplication
@ServletComponentScan
public class NASB1995Application4 {

	private static final Logger log = LogManager.getLogger(NASB1995Application4.class);

	public static void main(final String[] args) {
		SpringApplication.run(NASB1995Application4.class, args);
		log.info(ProjectConstants.MESSAGE_SPRING_APPLICATION);
	}

}
