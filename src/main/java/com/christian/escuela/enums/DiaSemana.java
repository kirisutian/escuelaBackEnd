package com.christian.escuela.enums;

import com.christian.escuela.exceptions.RecursoNoEncontradoException;
import com.christian.escuela.utils.StringCustomUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiaSemana {

    LUNES("Lunes"),
    MARTES("Martes"),
    MIERCOLES("Miércoles"),
    JUEVES("Jueves"),
    VIERNES("Viernes"),
    SABADO("Sábado");

    private final String descripcion;

    public static DiaSemana obtenerDiaSemanaPorDescripcion(String descripcion) {
        String buscado = StringCustomUtils.quitarAcentos(descripcion.trim());
        for (DiaSemana dia : values()) {
            String descDia = StringCustomUtils.quitarAcentos(dia.descripcion);
            if (descDia.equalsIgnoreCase(buscado))
                return dia;
        }
        throw new RecursoNoEncontradoException("No existe día de la semana con la descripción: " + descripcion);
    }
}
