package com.OL.OLtest.controller;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.OL.OLtest.model.Merchant;
import com.OL.OLtest.model.City;
import com.OL.OLtest.model.Department;
import com.OL.OLtest.repository.MerchantRepository;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.UnitValue;
import com.OL.OLtest.model.Establishment;
import com.OL.OLtest.repository.CityRepository;
import com.OL.OLtest.repository.DepartmentRepository;
import com.OL.OLtest.repository.EstablishmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;



import com.OL.OLtest.model.Merchant;
import com.OL.OLtest.repository.MerchantRepository;
import com.OL.OLtest.repository.EstablishmentRepository;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @GetMapping
    public ResponseEntity<?> getAllMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String municipality,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String registrationDate
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Merchant> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("businessName")), "%" + name.toLowerCase() + "%")
            );
        }

        if (municipality != null && !municipality.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("city").get("name"), municipality)
            );
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status)
            );
        }

        if (registrationDate != null && !registrationDate.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("createdAt"), LocalDate.parse(registrationDate))
            );
        }

        Page<Merchant> merchants = merchantRepository.findAll(spec, pageable);

        
        Page<Map<String, Object>> response = merchants.map(merchant -> {
            Map<String, Object> merchantData = new HashMap<>();
            merchantData.put("id", merchant.getId());
            merchantData.put("businessName", merchant.getBusinessName());
            merchantData.put("email", merchant.getEmail());
            merchantData.put("phone", merchant.getPhone());
            merchantData.put("status", merchant.getStatus());
            merchantData.put("numberOfEstablishments", establishmentRepository.countByMerchant(merchant));
            return merchantData;
        });

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMerchantById(@PathVariable Long id) {
        try {
            Optional<Merchant> merchantOptional = merchantRepository.findById(id);
    
            if (merchantOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Merchant not found");
            }
    
            Merchant merchant = merchantOptional.get();
    
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", merchant.getId());
            response.put("businessName", merchant.getBusinessName());
            response.put("email", merchant.getEmail());
            response.put("phone", merchant.getPhone());
            response.put("status", merchant.getStatus());
            response.put("numberOfEstablishments", establishmentRepository.countByMerchant(merchant));

            response.put("createdBy", merchant.getCreatedBy());
            response.put("createdOn", merchant.getCreatedOn());
            response.put("updatedOn", merchant.getUpdatedOn());
            response.put("updatedBy", merchant.getUpdatedBy());
    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving merchant: " + e.getMessage());
        }
    }
    


    @PostMapping
    public ResponseEntity<?> createMerchant(@RequestBody Merchant merchant) {
        try {
            
            if (merchant.getCity() == null || merchant.getCity().getId() == null ||
                !cityRepository.existsById(merchant.getCity().getId())) {
                return ResponseEntity.badRequest().body("Invalid City ID");
            }
    
            
            if (merchant.getDepartment() == null || merchant.getDepartment().getId() == null ||
                !departmentRepository.existsById(merchant.getDepartment().getId())) {
                return ResponseEntity.badRequest().body("Invalid Department ID");
            }
    
            
            merchant.setCreatedOn(new Date()); 
            merchant.setCreatedBy("system"); 
            merchant.setUpdatedOn(new Date());
            merchant.setUpdatedBy("system"); 
    
            
            Merchant savedMerchant = merchantRepository.save(merchant);
            return ResponseEntity.ok(savedMerchant);
    
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error creating merchant: " + e.getMessage());
        }
    }
    
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateMerchantStatus(@PathVariable Long id, @RequestParam String status) {
        if (!status.equalsIgnoreCase("Active") && !status.equalsIgnoreCase("Inactive")) {
            return ResponseEntity.badRequest().body("Invalid status. Allowed values are 'Active' or 'Inactive'.");
        }

        Optional<Merchant> merchantOptional = merchantRepository.findById(id);
        if (merchantOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Merchant not found");
        }

        Merchant merchant = merchantOptional.get();
        merchant.setStatus(status);
        Merchant updatedMerchant = merchantRepository.save(merchant);

        return ResponseEntity.ok(updatedMerchant);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMerchant(@PathVariable Long id, @RequestBody Merchant updatedMerchant) {
        try {
            return merchantRepository.findById(id).map(merchant -> {
                if (updatedMerchant.getCity() != null && updatedMerchant.getCity().getId() != null) {
                    Optional<City> cityOptional = cityRepository.findById(updatedMerchant.getCity().getId());
                    if (cityOptional.isEmpty()) {
                        return ResponseEntity.status(404).body("City not found");
                    }
                    merchant.setCity(cityOptional.get());
                }
    
                if (updatedMerchant.getDepartment() != null && updatedMerchant.getDepartment().getId() != null) {
                    Optional<Department> departmentOptional = departmentRepository.findById(updatedMerchant.getDepartment().getId());
                    if (departmentOptional.isEmpty()) {
                        return ResponseEntity.status(404).body("Department not found");
                    }
                    merchant.setDepartment(departmentOptional.get());
                }
    
                merchant.setBusinessName(updatedMerchant.getBusinessName());
                merchant.setPhone(updatedMerchant.getPhone());
                merchant.setEmail(updatedMerchant.getEmail());
                merchant.setStatus(updatedMerchant.getStatus());
    
                merchant.setUpdatedOn(new Date());
                merchant.setUpdatedBy("system");
    
                Merchant savedMerchant = merchantRepository.save(merchant);
                return ResponseEntity.ok(savedMerchant);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error updating merchant: " + e.getMessage());
        }
    }
    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id) {
        if (merchantRepository.existsById(id)) {
            merchantRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/generate-pdf")
    public ResponseEntity<?> generateMerchantPDF(@PathVariable Long id) {
        try {
            
            Optional<Merchant> merchantOptional = merchantRepository.findById(id);
            if (merchantOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Merchant not found");
            }

            Merchant merchant = merchantOptional.get();

            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));

            
            document.add(new Paragraph("Merchant Information")
                    .setBold()
                    .setFontSize(16)
                    .setMarginBottom(20));

            
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 7}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell(new Paragraph("Field").setBold());
            table.addHeaderCell(new Paragraph("Value").setBold());

            table.addCell(new Paragraph("Name/Business Name:"));
            table.addCell(new Paragraph(merchant.getBusinessName()));

            table.addCell(new Paragraph("Department:"));
            table.addCell(new Paragraph(merchant.getDepartment() != null ? merchant.getDepartment().getName() : "N/A"));

            table.addCell(new Paragraph("Municipality:"));
            table.addCell(new Paragraph(merchant.getCity() != null ? merchant.getCity().getName() : "N/A"));

            table.addCell(new Paragraph("Phone:"));
            table.addCell(new Paragraph(merchant.getPhone() != null ? merchant.getPhone() : "N/A"));

            table.addCell(new Paragraph("Email:"));
            table.addCell(new Paragraph(merchant.getEmail() != null ? merchant.getEmail() : "N/A"));

            
            int establishmentCount = establishmentRepository.countByMerchant(merchant);
            int totalEmployees = Optional.ofNullable(establishmentRepository.sumEmployeesByMerchant(merchant)).orElse(0);

            table.addCell(new Paragraph("Number of Establishments:"));
            table.addCell(new Paragraph(String.valueOf(establishmentCount)));

            table.addCell(new Paragraph("Active Establishments:"));

            table.addCell(new Paragraph("Total Employees:"));
            table.addCell(new Paragraph(String.valueOf(totalEmployees)));

            document.add(table);

            document.close();

            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=merchant_" + id + ".pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(byteArrayOutputStream.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error generating PDF: " + e.getMessage());
        }
    }



}
