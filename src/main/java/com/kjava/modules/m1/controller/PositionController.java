package com.kjava.modules.m1.controller;

import com.kjava.modules.m1.model.Position;
import com.kjava.modules.m1.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    public ResponseEntity<Position> createPosition(@RequestBody Position position) {
        return ResponseEntity.ok(positionService.createPosition(position));
    }

    @GetMapping
    public ResponseEntity<List<Position>> getAllPositions() {
        return ResponseEntity.ok(positionService.getAllPositions());
    }
}
