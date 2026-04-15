package com.christian.escuela.mappers;

import com.christian.escuela.dto.cursos.CursoRequest;
import com.christian.escuela.dto.cursos.CursoResponse;
import com.christian.escuela.dto.datos.DatosCurso;
import com.christian.escuela.entities.Curso;
import org.springframework.stereotype.Component;

@Component
public class CursoMapper implements CommonMapper<CursoRequest, CursoResponse, Curso> {

    @Override
    public Curso requestAEntidad(CursoRequest request) {
        if(request == null) return null;

        return Curso.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .creditos(request.creditos())
                .build();
    }

    @Override
    public CursoResponse entidadAResponse(Curso entidad) {
        if(entidad == null) return null;

        String descripcion = entidad.getDescripcion() == null ?
                "Sin descripción" : entidad.getDescripcion();

        return new CursoResponse(
                entidad.getId(),
                entidad.getNombre(),
                descripcion,
                entidad.getCreditos()
        );
    }

    public DatosCurso entidadADatosCurso(Curso entidad) {
        if(entidad == null) return null;

        String descripcion = entidad.getDescripcion() == null ?
                "Sin descripción" : entidad.getDescripcion();

        return new DatosCurso(
                entidad.getNombre(),
                descripcion,
                entidad.getCreditos()
        );
    }
}
