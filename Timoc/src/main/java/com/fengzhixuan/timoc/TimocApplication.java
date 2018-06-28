package com.fengzhixuan.timoc;

import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//(basePackages="com.fengzhixuan.timoc.data.repository", entityManagerFactoryRef="entityManagerFactory")
public class TimocApplication
{

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	public static void main(String[] args) {
		SpringApplication.run(TimocApplication.class, args);
	}

	/*@Bean
	public CommandLineRunner demo(PlayerRepository repository)
	{
		return (args) ->
		{
			// save a couple of customers
			repository.save(new Player("Jack"));
			repository.save(new Player("Chloe"));
			repository.save(new Player("Kim"));
			repository.save(new Player("David"));
			repository.save(new Player("Michelle"));

			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Player player : repository.findAll()) {
				log.info(player.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			repository.findById(1L)
					.ifPresent(customer -> {
						log.info("Customer found with findById(1L):");
						log.info("--------------------------------");
						log.info(customer.toString());
						log.info("");
					});

			// fetch customers by last name
			log.info("Customer found with findByLastName('Bauer'):");
			log.info("--------------------------------------------");
			repository.findByName("Bauer").forEach(bauer -> {
				log.info(bauer.toString());
			});
			// for (Customer bauer : repository.findByLastName("Bauer")) {
			// 	log.info(bauer.toString());
			// }
			log.info("");
		};
	}*/
}
