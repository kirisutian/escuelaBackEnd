package com.christian.escuela.services.alumnos;

import com.christian.escuela.dto.alumnos.AlumnoRequest;
import com.christian.escuela.dto.alumnos.AlumnoResponse;
import com.christian.escuela.entities.Alumno;
import com.christian.escuela.exceptions.EntidadRelacionadaException;
import com.christian.escuela.mappers.AlumnoMapper;
import com.christian.escuela.repositories.AlumnoRepository;
import com.christian.escuela.repositories.InscripcionRepository;
import com.christian.escuela.utils.ServiceUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository alumnoRepository;

    private final InscripcionRepository inscripcionRepository;

    private final AlumnoMapper alumnoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoResponse> listar() {
        log.info("Listado de todos los alumnos solicitado");
        return alumnoRepository.findAll().stream()
                .map(alumnoMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoResponse obtenerPorId(Long id) {
        return alumnoMapper.entidadAResponse(obtenerAlumno(id));
    }

    @Override
    public AlumnoResponse registrar(AlumnoRequest request) {
        log.info("Registrando nuevo Alumno...");

        String matricula = generarMatricula(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno());

        String email = generarEmail(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno());

        Alumno alumno = alumnoRepository.save(alumnoMapper.requestAEntidad(request, matricula, email));
        log.info("Nuevo alumno {} registrado con la matrícula: {}", alumno.getNombre(), alumno.getMatricula());

        return alumnoMapper.entidadAResponse(alumno);
    }

    @Override
    public AlumnoResponse actualizar(AlumnoRequest request, Long id) {
        Alumno alumno = obtenerAlumno(id);
        log.info("Actualizando alumno con id: {}", id);

        if (alumno.cambioEnDatos(request.nombre(), request.apellidoPaterno(), request.apellidoMaterno())) {
            alumno.actualizar(
                    request.nombre(),
                    request.apellidoPaterno(),
                    request.apellidoMaterno(),
                    generarEmail(request.nombre(), request.apellidoPaterno(), request.apellidoMaterno()),
                    generarMatricula(request.nombre(), request.apellidoPaterno(), request.apellidoMaterno())
            );
            log.info("Datos académicos regenerados para el alumno con id: {}", id);
        }
        return alumnoMapper.entidadAResponse(alumno);
    }

    @Override
    public void eliminar(Long id) {
        Alumno alumno = obtenerAlumno(id);
        log.info("Eliminando alumno con id: {}", id);

        if (inscripcionRepository.existsByAlumnoId(id))
            throw new EntidadRelacionadaException("No se puede eliminar al alumno ya que tiene inscripciones asignadas");

        alumnoRepository.delete(alumno);
        log.info("Alumno con id {} eliminado", id);
    }

    public Alumno obtenerAlumno(Long id) {
        return ServiceUtils.obtenerEntidadOException(alumnoRepository, id, Alumno.class);
    }

    private String generarMatricula(String nombre, String apellidoPaterno, String apellidoMaterno) {
        log.info("Generando matrícula...");
        return alumnoRepository.generarMatricula(nombre, apellidoPaterno, apellidoMaterno);
    }

    private String generarEmail(String nombre, String apellidoPaterno, String apellidoMaterno) {
        log.info("Generando email...");
        return alumnoRepository.generarEmail(nombre, apellidoPaterno, apellidoMaterno);
    }
}
