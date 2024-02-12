package dk.kea.hurtigigang.controller;


import dk.kea.hurtigigang.model.Person;
import dk.kea.hurtigigang.repository.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/persons")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public ResponseEntity<List<Person>> getPersons() {
        return ResponseEntity.ok(personRepository.findAll());
    }

    @GetMapping("/{personId}")
    public ResponseEntity<Person> getSinglePerson(@PathVariable long personId) {
        Optional<Person> person = personRepository.findById(personId);

        //ResponseEntity.of(Optional<>)
        //Returner OK, hvis person eksisterer. NOT_FOUND hvis fraværende.
        //Virker kun med Optional
        return ResponseEntity.of(person);
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        Person createdPerson = personRepository.save(person);

        //Vi bygger en location til response header
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPerson.getId())
                .toUri();

        //Returnerer http://localhost:8080/persons/5 i Response Header
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{personId}")
    public ResponseEntity<Person> updatePerson(@PathVariable long personId, @RequestBody Person person) {
        Optional<Person> personInDb = personRepository.findById(personId);

        if (personInDb.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        personInDb.get().setFirstName(person.getFirstName());
        personInDb.get().setLastName(person.getLastName());
        personInDb.get().setDateOfBirth(person.getDateOfBirth());

        Person updatedPerson = personRepository.save(personInDb.get());

        return ResponseEntity.ok().body(updatedPerson);
    }

    @DeleteMapping("/{personId}")
    public ResponseEntity<Person> deletePerson(@PathVariable long personId) {
        //Vi returnerer det slettede person til frontend, for at at vise, hvad der er blevet slettet

        Optional<Person> personInDb = personRepository.findById(personId);

        if (personInDb.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        personRepository.delete(personInDb.get());
        return ResponseEntity.ok(personInDb.get());
    }


}
