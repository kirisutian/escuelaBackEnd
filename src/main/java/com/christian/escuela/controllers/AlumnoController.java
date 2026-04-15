package com.christian.escuela.controllers;

import com.christian.escuela.dto.alumnos.AlumnoRequest;
import com.christian.escuela.dto.alumnos.AlumnoResponse;
import com.christian.escuela.services.alumnos.AlumnoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController extends CommonController<AlumnoRequest, AlumnoResponse, AlumnoService>{

    public AlumnoController(AlumnoService service) {
        super(service);
    }
}
