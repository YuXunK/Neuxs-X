package org.yuxun.x.nexusx;

import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("org.yuxun.x.nexusx.Mapper")
public class NexusXApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexusXApplication.class, args);
    }
}
