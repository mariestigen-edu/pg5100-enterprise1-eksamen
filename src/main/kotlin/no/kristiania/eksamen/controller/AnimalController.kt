package no.kristiania.eksamen.controller

import com.fasterxml.jackson.databind.JsonNode
import no.kristiania.eksamen.dto.AnimalDto
import no.kristiania.eksamen.dto.AnimalDtoForUpdate
import no.kristiania.eksamen.model.AnimalEntity
import no.kristiania.eksamen.service.AnimalService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.security.InvalidParameterException

@RestController
@RequestMapping("/api/shelter")
class AnimalController(@Autowired private val animalService: AnimalService) {

    @GetMapping("")
    fun getAnimals(): ResponseEntity<List<AnimalEntity>> {
        return ResponseEntity.ok().body(animalService.getAnimals())
    }

    @GetMapping("/status/{statusId}")
    fun getAnimalsByStatusCode(@PathVariable statusId: Long?): ResponseEntity<List<AnimalEntity>> {

        statusId?.let {
            animalService.getAnimalsByStatus(statusId)?.let {
                return ResponseEntity.ok().body(it)
            }
        }.run { throw InvalidParameterException() }
    }

    @GetMapping("/breed/{breed}")
    fun getAnimalsByBreed(@PathVariable breed: String?): ResponseEntity<List<AnimalEntity>>? {

        breed?.let {
            animalService.getAnimalsByBreed(breed)?.let {
                return ResponseEntity.ok().body(it)
            }
        }.run { throw InvalidParameterException() }
    }

    @GetMapping("/name/{name}")
    fun getAnimalsByName(@PathVariable name: String?): ResponseEntity<List<AnimalEntity>>? {

        name?.let {
            animalService.getAnimalsByName(name)?.let {
                return ResponseEntity.ok().body(it)
            }
        }.run { throw InvalidParameterException() }
    }

    @GetMapping("/{id}")
    fun getAnimalByID(@PathVariable id: Long?): ResponseEntity<Any>? {
        id?.let {
            animalService.getAnimalById(id)?.let {
                return ResponseEntity.ok().body(it)
            }
        }.run { return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Animal Not Found") }
    }

    @PostMapping("")
    fun registerAnimal(@RequestBody animalDto: AnimalDto?): ResponseEntity<Any>? {

        when (animalDto) {
            null -> throw InvalidParameterException()
            else -> {
                animalService.registerAnimal(animalDto)?.let {
                    val uri = URI.create(
                        ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/shelter/").toUriString()
                    )
                    return ResponseEntity.created(uri).body(it)
                }.run { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request") }
            }
        }
    }

    @PutMapping("/{id}")
    fun updateAnimal(
        @PathVariable id: Long?,
        @RequestBody animalDto: AnimalDtoForUpdate?
    ): ResponseEntity<Any>? {
        when {
            id == null -> throw InvalidParameterException()
            animalDto == null -> throw InvalidParameterException()
            else -> {
                animalService.updateAnimal(id, animalDto)?.let {
                    return ResponseEntity.ok().body(it)
                }.run {  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request") }
            }
        }
    }

    @PatchMapping("/{id}/status")
    fun updateAnimalStatus(@PathVariable id: Long?, @RequestBody statusId: JsonNode): ResponseEntity<Any>? {
        id?.let {
            if (!animalService.updateAnimalStatus(
                    it,
                    statusId.get("statusId").asLong()
                )
            ) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request")
            return ResponseEntity.ok().body(animalService.getAnimalById(id))
        }.run {  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request") }
    }

    @DeleteMapping("/{id}")
    fun deleteAnimal(@PathVariable id: Long?): ResponseEntity<String> {
        id?.let {
            if (!animalService.deleteAnimal(it)) throw InvalidParameterException()
            return ResponseEntity.ok().body("Deleted Successfully")
        }.run { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request") }
    }
}
