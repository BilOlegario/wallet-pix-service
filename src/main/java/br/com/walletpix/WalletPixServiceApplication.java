package br.com.walletpix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class WalletPixServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletPixServiceApplication.class, args);
    }
}
