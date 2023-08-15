package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

class Coffee {
	private final String id;
	private String name;

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

	public void setName(String name){
		this.name = name;
	}
}

@RestController
@RequestMapping("/")
class RestApiDemoController {
	private List<Coffee> coffees = new ArrayList<>();

	public RestApiDemoController() {
		coffees.addAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Ganador"),
				new Coffee("Cafe Lareno"),
				new Coffee("Cafe Tres Pontas")
		));
	}

	@GetMapping(value="/coffees")
	Iterable<Coffee> getCoffees() {
		return coffees;
	}

	@GetMapping(value="/coffees/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id) {
		for (Coffee c: coffees) {
			if (c.getId().equals(id)) {
				return Optional.of(c);
			}
		}
		return Optional.empty();
	}

	@PostMapping("/coffees")
	Coffee postCoffee(@RequestBody Coffee coffee){
		coffees.add(coffee);
		return coffee;
	}

	@PutMapping("/coffees/{id}")
	Coffee putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
		int coffeeIndex = -1;

		for (Coffee c: coffees) {
			if (c.getId().equals(id)) {
				coffeeIndex = coffees.indexOf(c);
				coffees.set(coffeeIndex, coffee);
			}
		}
		return (coffeeIndex == -1) ? postCoffee(coffee) : coffee;
	}

	@DeleteMapping("/coffees/{id}")
	void deleteCoffee(@PathVariable String id){
		coffees.removeIf(c -> c.getId().equals(id));
	}

}
