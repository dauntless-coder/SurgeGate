package com.surgegate.backend.controller;

import com.surgegate.backend.model.Concert;
import com.surgegate.backend.repository.ConcertRepository;
import com.surgegate.backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/organizer")
@CrossOrigin(origins = "http://localhost:3000")
public class OrganizerController {

    @Autowired
    private ConcertRepository concertRepository;
    
    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/concert")
    public ResponseEntity<Concert> createConcert(
            @RequestParam String organizerId,
            @RequestParam String title,
            @RequestParam String venue,
            @RequestParam String description,
            @RequestParam int totalTickets,
            @RequestParam double price) {
        
        Concert concert = new Concert(organizerId, title, venue, totalTickets, price);
        concert.setDescription(description);
        concert = concertRepository.save(concert);
        
        // Initialize stock in Redis and create tickets
        inventoryService.initializeConcertStock(concert.getId(), totalTickets);
        concert.setStatus("ACTIVE");
        concert = concertRepository.save(concert);
        
        return ResponseEntity.ok(concert);
    }

    @GetMapping("/concerts/{organizerId}")
    public ResponseEntity<List<Concert>> getOrganizerConcerts(@PathVariable String organizerId) {
        List<Concert> concerts = concertRepository.findByOrganizerId(organizerId);
        return ResponseEntity.ok(concerts);
    }

    @GetMapping("/concert/{concertId}")
    public ResponseEntity<Concert> getConcertDetails(@PathVariable String concertId) {
        Optional<Concert> concert = concertRepository.findById(concertId);
        if (concert.isPresent()) {
            Concert c = concert.get();
            int available = inventoryService.getAvailableStock(concertId);
            // Ensure available is never negative
            if (available < 0) available = 0;
            c.setAvailableTickets(available);
            return ResponseEntity.ok(c);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/concert/{concertId}/stats")
    public ResponseEntity<Map<String, Object>> getConcertStats(@PathVariable String concertId) {
        Optional<Concert> concert = concertRepository.findById(concertId);
        if (!concert.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Concert c = concert.get();
        int available = inventoryService.getAvailableStock(concertId);
        // Ensure available is never negative
        if (available < 0) available = 0;
        int sold = c.getTotalTickets() - available;
        // Ensure sold doesn't exceed total tickets
        if (sold > c.getTotalTickets()) sold = c.getTotalTickets();

        Map<String, Object> stats = new HashMap<>();
        stats.put("concertId", concertId);
        stats.put("title", c.getTitle());
        stats.put("totalTickets", c.getTotalTickets());
        stats.put("soldTickets", sold);
        stats.put("availableTickets", available);
        stats.put("revenue", sold * c.getPrice());

        return ResponseEntity.ok(stats);
    }

    @PutMapping("/concert/{concertId}")
    public ResponseEntity<?> updateConcert(
            @PathVariable String concertId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String venue,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double price) {
        
        Optional<Concert> concertOpt = concertRepository.findById(concertId);
        if (!concertOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Concert concert = concertOpt.get();
        
        if (title != null) concert.setTitle(title);
        if (venue != null) concert.setVenue(venue);
        if (description != null) concert.setDescription(description);
        if (price != null) concert.setPrice(price);
        
        concert = concertRepository.save(concert);
        return ResponseEntity.ok(concert);
    }

    @DeleteMapping("/concert/{concertId}")
    public ResponseEntity<String> deleteConcert(@PathVariable String concertId) {
        Optional<Concert> concert = concertRepository.findById(concertId);
        if (!concert.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Concert c = concert.get();
        // Only allow deletion if no tickets sold
        int soldTickets = c.getTotalTickets() - inventoryService.getAvailableStock(concertId);
        if (soldTickets > 0) {
            return ResponseEntity.badRequest().body("Cannot delete concert with sold tickets");
        }

        concertRepository.deleteById(concertId);
        return ResponseEntity.ok("Concert deleted successfully");
    }

    @PutMapping("/concert/{concertId}/cancel")
    public ResponseEntity<String> cancelConcert(@PathVariable String concertId) {
        Optional<Concert> concert = concertRepository.findById(concertId);
        if (concert.isPresent()) {
            Concert c = concert.get();
            c.setStatus("CANCELLED");
            concertRepository.save(c);
            return ResponseEntity.ok("Concert cancelled");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/concert/{concertId}/sync-stock")
    public ResponseEntity<Map<String, Object>> syncConcertStock(@PathVariable String concertId) {
        Optional<Concert> concert = concertRepository.findById(concertId);
        if (!concert.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Sync stock from MongoDB
        int actualAvailable = inventoryService.syncStockFromMongoDB(concertId);
        
        Concert c = concert.get();
        int sold = c.getTotalTickets() - actualAvailable;
        
        Map<String, Object> result = new HashMap<>();
        result.put("concertId", concertId);
        result.put("title", c.getTitle());
        result.put("totalTickets", c.getTotalTickets());
        result.put("actualAvailable", actualAvailable);
        result.put("soldCount", sold);
        result.put("message", "✅ Stock synced successfully!");
        
        return ResponseEntity.ok(result);
    }
}
