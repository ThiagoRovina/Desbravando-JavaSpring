package com.javaspring.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.awt.*;
import java.net.URI;

@SpringBootApplication
@EnableTransactionManagement
public class JavaSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaSpringApplication.class, args);
		openBrowser("http://localhost:8080/telaLogin");
	}

	private static void openBrowser(String url) {
		if (!GraphicsEnvironment.isHeadless()) {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("java.Aplicattion: " + url);
		}
	}


}
