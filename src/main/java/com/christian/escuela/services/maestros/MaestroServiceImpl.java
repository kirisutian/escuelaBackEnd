package com.christian.escuela.services.maestros;

import com.christian.escuela.dto.maestros.MaestroRequest;
import com.christian.escuela.dto.maestros.MaestroResponse;
import com.christian.escuela.entities.Maestro;
import com.christian.escuela.exceptions.EntidadRelacionadaException;
import com.christian.escuela.mappers.MaestroMapper;
import com.christian.escuela.repositories.GrupoRepository;
import com.christian.escuela.repositories.MaestroRepository;
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
public class MaestroServiceImpl implements MaestroService {

    private final MaestroRepository maestroRepository;

    private final GrupoRepository grupoRepository;

    private final MaestroMapper maestroMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MaestroResponse> listar() {
        log.info("Listado de todos los maestros solicitado");
        return maestroRepository.findAll().stream()
                .map(maestroMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MaestroResponse obtenerPorId(Long id) {
        return maestroMapper.entidadAResponse(obtenerMaestro(id));
    }

    @Override
    public MaestroResponse registrar(MaestroRequest request) {
        log.info("Registrando nuevo maestro...");

        validarEmailUnico(request.email());
        validarTelefonoUnico(request.telefono());

        Maestro maestro = maestroRepository.save(maestroMapper.requestAEntidad(request));
        log.info("Nuevo maestro {} registrado", maestro.getNombre());

        return maestroMapper.entidadAResponse(maestro);
    }

    @Override
    public MaestroResponse actualizar(MaestroRequest request, Long id) {
        Maestro maestro = obtenerMaestro(id);
        log.info("Actualizando maestro con id: {}", id);

        validarCambiosUnicos(request, id);

        maestro.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.email().toLowerCase(),
                request.telefono()
        );

        log.info("Maestro {} actualizado correctamente", maestro.getNombre());
        return maestroMapper.entidadAResponse(maestro);
    }

    @Override
    public void eliminar(Long id) {
        Maestro maestro = obtenerMaestro(id);
        log.info("Eliminando maestro con id: {}", id);

        if (grupoRepository.existsByMaestroId(id))
            throw new EntidadRelacionadaException("No se puede eliminar al Maestro ya que tiene grupos asignados");

        maestroRepository.delete(maestro);
        log.info("Maestro con id {} eliminado", id);
    }

    private Maestro obtenerMaestro(Long id) {
        return ServiceUtils.obtenerEntidadOException(maestroRepository, id, Maestro.class);
    }

    private void validarEmailUnico(String email) {
        log.info("Validando email único...");
        if (maestroRepository.existsByEmailIgnoreCase(email))
            throw new IllegalArgumentException("Ya existe un maestro registrado con el email: " + email);
    }

    private void validarTelefonoUnico(String telefono) {
        log.info("Validando teléfono único...");
        if (maestroRepository.existsByTelefono(telefono))
            throw new IllegalArgumentException("Ya existe un maestro registrado con el teléfono: " + telefono);
    }

    private void validarCambiosUnicos(MaestroRequest request, Long id) {

        log.info("Validando email único al actualizar...");
        if (maestroRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id))
            throw new IllegalArgumentException("Ya existe un maestro registrado con el email: " + request.email());

        log.info("Validando teléfono único al actualizar...");
        if (maestroRepository.existsByTelefonoAndIdNot(request.telefono(), id))
            throw new IllegalArgumentException("Ya existe un maestro registrado con el teléfono: " + request.telefono());
    }
}
