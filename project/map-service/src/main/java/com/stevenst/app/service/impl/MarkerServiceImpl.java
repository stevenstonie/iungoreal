package com.stevenst.app.service.impl;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.stevenst.app.exception.IgorMarkerException;
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
        try {
            return markerRepository.save(marker);
        } catch (DataIntegrityViolationException ex) {
            throw new IgorMarkerException(ex.getMessage());
        }
    }

    @Override
    public Marker getMarker(Long id) {
        return markerRepository.findById(id).orElse(null);
    }
}
