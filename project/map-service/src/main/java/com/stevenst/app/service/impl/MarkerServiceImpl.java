package com.stevenst.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stevenst.app.model.Marker;
import com.stevenst.app.repository.MarkerRepository;
import com.stevenst.app.service.MarkerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarkerServiceImpl implements MarkerService {
    private final MarkerRepository markerRepository;

    @Override
    public List<Marker> getAllMarkers() {
        return markerRepository.findAll();
    }

    @Override
    public Marker addMarker(Marker marker) {
        return markerRepository.save(marker);
    }

    @Override
    public Marker getMarker(Long id) {
        return markerRepository.findById(id).orElse(null);
    }
}
