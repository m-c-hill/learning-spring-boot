package com.example.demo;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@ConfigurationPropertiesScan  // instructs the application to process config classes and add their properties to the environment
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

@Entity
class Coffee {
	@Id
	private String id;
	private String name;

	public Coffee(){
	}

	public Coffee(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Coffee(String name){
		this(UUID.randomUUID().toString(), name);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(String id){
		this.id = id;
	}

	public void setName(String name){
		this.name = name;
	}
}

@ConfigurationProperties(prefix = "greeting")
class Greeting {
	// Create the configuration variables greeting.name and greeting.coffee
	private String name;
	private String coffee;

	public String getName() {
		return name;
	}

	public String getCoffee() {
		return coffee;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCoffee(String coffee) {
		this.coffee = coffee;
	}
}

// Wraps around the object type to store in the db and the type of its unique ID, in this case string
interface CoffeeRepository extends CrudRepository<Coffee, String> {}

@RestController
@RequestMapping("/coffees")
class RestApiDemoController {
	private final CoffeeRepository coffeeRepository;

	public RestApiDemoController(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	@GetMapping
	Iterable<Coffee> getCoffees() {
		return coffeeRepository.findAll();
	}

	@GetMapping(value="/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id) {
		return coffeeRepository.findById(id);
	}

	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee){
		return coffeeRepository.save(coffee);
	}

	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
		return coffeeRepository.existsById(id) ?
				new ResponseEntity<>(coffee, HttpStatus.OK) :
				new ResponseEntity<>(postCoffee(coffee), HttpStatus.CREATED);
	};

	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id){
		coffeeRepository.deleteById(id);
	}
}

@RestController
@RequestMapping("/greeting")
class GreetingController{
	private final Greeting greeting;

	public GreetingController(Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping
	String getGreeting() {
		return greeting.getName();
	}

	@GetMapping("/coffee")
	String getNameAndCoffee() {
		return greeting.getCoffee();
	}
}

@Component
class DataLoader {
	private final CoffeeRepository coffeeRepository;

	public DataLoader(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	@PostConstruct
	private void loadData() {
		coffeeRepository.saveAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
		));
	}
}

