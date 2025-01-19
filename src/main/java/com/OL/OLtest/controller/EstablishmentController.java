package com.OL.OLtest.controller;

import com.OL.OLtest.model.Establishment;
import com.OL.OLtest.model.Merchant;
import com.OL.OLtest.repository.EstablishmentRepository;
import com.OL.OLtest.repository.MerchantRepository;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import static com.OL.OLtest.model.Establishment.Status.ACTIVE; 


@RestController
@RequestMapping("/api/establishments")
public class EstablishmentController {

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @GetMapping
    public ResponseEntity<Page<Establishment>> getAllEstablishments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Establishment> establishments = establishmentRepository.findAll(pageable);
        return ResponseEntity.ok(establishments);
    }

    @PostMapping
    public ResponseEntity<?> createEstablishment(@RequestBody Establishment establishment) {
        try {
            if (establishment.getMerchant() == null || establishment.getMerchant().getId() == null) {
                return ResponseEntity.badRequest().body("Merchant is required and must include a valid ID");
            }
    
            Optional<Merchant> merchantOptional = merchantRepository.findById(establishment.getMerchant().getId());
            if (merchantOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Merchant not found");
            }
    
            establishment.setMerchant(merchantOptional.get());
    
            if (establishment.getName() == null || establishment.getName().isEmpty()) {
                return ResponseEntity.badRequest().body("Name is required");
            }
            if (establishment.getAddress() == null || establishment.getAddress().isEmpty()) {
                return ResponseEntity.badRequest().body("Address is required");
            }
            if (establishment.getStatus() == null) {
                return ResponseEntity.badRequest().body("Status is required");
            }
            if (establishment.getEmployeeCount() <= 0) {
                return ResponseEntity.badRequest().body("Employee count must be greater than 0");
            }
    
            establishment.setCreatedOn(new Date());
        
    
            Establishment savedEstablishment = establishmentRepository.save(establishment);
            return ResponseEntity.ok(savedEstablishment);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating establishment: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getEstablishmentById(@PathVariable Long id) {
        Optional<Establishment> establishmentOptional = establishmentRepository.findById(id);
        if (establishmentOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Establishment not found");
        }
        return ResponseEntity.ok(establishmentOptional.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEstablishment(@PathVariable Long id, @RequestBody Establishment updatedEstablishment) {
        Optional<Establishment> establishmentOptional = establishmentRepository.findById(id);
        if (establishmentOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Establishment not found");
        }

        Establishment establishment = establishmentOptional.get();
        establishment.setName(updatedEstablishment.getName());
        establishment.setAddress(updatedEstablishment.getAddress());

        if (updatedEstablishment.getMerchant() != null && updatedEstablishment.getMerchant().getId() != null) {
            Optional<Merchant> merchantOptional = merchantRepository.findById(updatedEstablishment.getMerchant().getId());
            if (merchantOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Merchant not found");
            }
            establishment.setMerchant(merchantOptional.get());
        }

        Establishment savedEstablishment = establishmentRepository.save(establishment);
        return ResponseEntity.ok(savedEstablishment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEstablishment(@PathVariable Long id) {
        if (!establishmentRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Establishment not found");
        }

        establishmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportActiveMerchants() {
        try {
            
            List<Merchant> activeMerchants = merchantRepository.findAll((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), "Active"));

            if (activeMerchants.isEmpty()) {
                return ResponseEntity.status(204).body(null); 
            }

            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream, "UTF-8"));

            
            writer.println("Nombre|Departamento|Municipio|Teléfono|Correo Electrónico|Fecha de Registro|Estado|Cantidad de Establecimientos|Total Activos|Cantidad de Empleados");

            
            for (Merchant merchant : activeMerchants) {
                
                String departmentName = merchant.getDepartment() != null ? merchant.getDepartment().getName() : "N/A";
                String cityName = merchant.getCity() != null ? merchant.getCity().getName() : "N/A";
                String phone = merchant.getPhone() != null ? merchant.getPhone() : "N/A";
                String email = merchant.getEmail() != null ? merchant.getEmail() : "N/A";
                String status = "N/A";
                String createdBy = merchant.getCreatedBy() != null ? merchant.getCreatedBy() : "N/A";

                
                int establishmentCount = establishmentRepository.countByMerchant(merchant);
                int activeEstablishments = establishmentRepository.countByMerchantAndStatus(merchant, Establishment.Status.ACTIVE);
                int totalEmployees = establishmentRepository.sumEmployeesByMerchant(merchant) != null
                ? establishmentRepository.sumEmployeesByMerchant(merchant)
                : 0;

                
                writer.printf("%s|%s|%s|%s|%s|%s|%s|%d|%d|%d%n",
                        merchant.getBusinessName(),
                        departmentName,
                        cityName,
                        phone,
                        email,
                        createdBy,
                        status,
                        establishmentCount,
                        activeEstablishments,
                        totalEmployees);
            }

            writer.flush();
            writer.close();

            
            byte[] content = byteArrayOutputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=merchants.csv");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(content);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(("Error al generar el archivo CSV: " + e.getMessage()).getBytes());
        }
    }


}
