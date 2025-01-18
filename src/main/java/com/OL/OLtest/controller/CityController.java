package com.OL.OLtest.controller;

import com.OL.OLtest.model.City;
import com.OL.OLtest.model.Department;
import com.OL.OLtest.repository.CityRepository;
import com.OL.OLtest.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    
    @GetMapping
    public ResponseEntity<Page<City>> getAllCities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<City> cities = cityRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(cities);
    }

    
    @PostMapping
    public ResponseEntity<?> createCity(@RequestBody Map<String, Object> cityData) {
        try {
            
            String name = (String) cityData.get("name");
            Long departmentId = Long.valueOf(cityData.get("department_id").toString());
            String createdBy = (String) cityData.get("createdBy");

            
            Optional<Department> departmentOptional = departmentRepository.findById(departmentId);
            if (departmentOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Department not found");
            }

            
            Department department = departmentOptional.get();
            City city = new City();
            city.setName(name);
            city.setDepartment(department);
            city.setCreatedBy(createdBy);

            
            City savedCity = cityRepository.save(city);
            return ResponseEntity.ok(savedCity);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error creating city: " + e.getMessage());
        }
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable Long id) {
        return cityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Long id, @RequestBody City updatedCity) {
        return cityRepository.findById(id).map(city -> {
            city.setName(updatedCity.getName());
            city.setDepartment(updatedCity.getDepartment());
            return ResponseEntity.ok(cityRepository.save(city));
        }).orElse(ResponseEntity.notFound().build());
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        if (cityRepository.existsById(id)) {
            cityRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
