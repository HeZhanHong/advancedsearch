package iie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring主配置和启动类
 */
@SpringBootApplication
public class HttpflumeApplication {

    //private static final Logger LOG = LoggerFactory.getLogger(HttpflumeApplication.class);
    public static void main(String[] args) {

        SpringApplication.run(HttpflumeApplication.class, args);

    }

}
