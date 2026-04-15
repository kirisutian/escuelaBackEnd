package com.christian.escuela.mappers;

import com.christian.escuela.dto.alumnos.AlumnoRequest;
import com.christian.escuela.dto.alumnos.AlumnoResponse;
import com.christian.escuela.dto.datos.DatosCalificacion;
import com.christian.escuela.entities.Alumno;
import com.christian.escuela.utils.StringCustomUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
public class AlumnoMapper implements CommonMapper<AlumnoRequest, AlumnoResponse, Alumno>{

    @Override
    public Alumno requestAEntidad(AlumnoRequest request) {
        if(request == null) return null;

        return Alumno.builder()
                .nombre(request.nombre())
                .apellidoPaterno(request.apellidoPaterno())
                .apellidoMaterno(request.apellidoMaterno())
                .fechaIngreso(LocalDate.now())
                .build();
    }

    public Alumno requestAEntidad(AlumnoRequest request, String matricula, String email) {
        if(request == null) return null;

        Alumno alumno = this.requestAEntidad(request);
        alumno.setMatricula(matricula);
        alumno.setEmail(email);
        return alumno;
    }

    @Override
    public AlumnoResponse entidadAResponse(Alumno entidad) {
        if(entidad == null) return null;

        List<DatosCalificacion> calificaciones = entidadADatosCalificacion(entidad);

        return new AlumnoResponse(
                entidad.getId(),
                String.join(" ",
                        entidad.getNombre(),
                        entidad.getApellidoPaterno(),
                        entidad.getApellidoMaterno()),
                entidad.getEmail(),
                entidad.getMatricula(),
                StringCustomUtils.localDateAString(entidad.getFechaIngreso()),
                calificaciones,
                this.datosCalificacionAPromedio(calificaciones));
    }

    private List<DatosCalificacion> entidadADatosCalificacion(Alumno alumno) {
        if (alumno == null || alumno.getInscripciones() == null)
            return List.of();

        return alumno.getInscripciones().stream()
                .map(inscripcion -> new DatosCalificacion(
                        inscripcion.getGrupo().getCurso().getNombre(),
                        inscripcion.getGrupo().getPeriodo(),
                        inscripcion.getCalificacion() != null
                                ? inscripcion.getCalificacion().getCalificacion()
                                : null
                )).toList();
    }

    private BigDecimal datosCalificacionAPromedio(List<DatosCalificacion> calificaciones) {
        if (calificaciones == null || calificaciones.isEmpty())
            return BigDecimal.ZERO;

        //FILTRAR LAS CALIFICACIONES NO NULAS
        List<BigDecimal> calificacionesValidas = calificaciones.stream()
                .map(DatosCalificacion::calificacion)
                .filter(Objects::nonNull).toList();

        if (calificacionesValidas.isEmpty())
            return BigDecimal.ZERO;

        BigDecimal suma = calificacionesValidas.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return suma.divide(BigDecimal.valueOf(calificacionesValidas.size()), 2, RoundingMode.HALF_UP);
    }
}
