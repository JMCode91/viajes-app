package com.app.viajes.service.impl;

import com.app.viajes.dto.BalanceResponseDTO;
import com.app.viajes.dto.DetalleDeudaDTO;
import com.app.viajes.dto.DeudaResumenDTO;
import com.app.viajes.dto.DetalleDivisionDTO;
import com.app.viajes.dto.GastoRequestDTO;
import com.app.viajes.dto.GastoResponseDTO;
import com.app.viajes.exception.AccesoDenegadoException;
import com.app.viajes.exception.DatosInvalidosException;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.exception.ViajeNoEncontradoException;
import com.app.viajes.model.DeudaGasto;
import com.app.viajes.model.Gasto;
import com.app.viajes.model.Usuario;
import com.app.viajes.model.UsuarioViaje;
import com.app.viajes.model.Viaje;
import com.app.viajes.model.enums.EstadoInvitacion;
import com.app.viajes.model.enums.TipoDivision;
import com.app.viajes.model.enums.TipoGasto;
import com.app.viajes.repository.DeudaGastoRepository;
import com.app.viajes.repository.GastoRepository;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.UsuarioViajeRepository;
import com.app.viajes.repository.ViajeRepository;
import com.app.viajes.service.GastoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementacion del servicio de gastos. Valida la pertenencia del
 * usuario autenticado al viaje y aplica el algoritmo de division de
 * gastos compartidos (partes iguales, porcentajes o deuda completa) y
 * el calculo de balances del viaje.
 */
@Service
public class GastoServiceImpl implements GastoService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final GastoRepository gastoRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioViajeRepository usuarioViajeRepository;
    private final DeudaGastoRepository deudaGastoRepository;

    public GastoServiceImpl(GastoRepository gastoRepository,
                            ViajeRepository viajeRepository,
                            UsuarioRepository usuarioRepository,
                            UsuarioViajeRepository usuarioViajeRepository,
                            DeudaGastoRepository deudaGastoRepository) {
        this.gastoRepository = gastoRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioViajeRepository = usuarioViajeRepository;
        this.deudaGastoRepository = deudaGastoRepository;
    }

    @Override
    @Transactional
    public GastoResponseDTO crearGasto(UUID viajeId, GastoRequestDTO dto) {
        verificarPertenencia(viajeId);

        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new ViajeNoEncontradoException("Viaje no encontrado"));

        Usuario pagador = usuarioRepository.findById(dto.pagadorId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "El pagador indicado no existe"));

        // Seguridad por propiedad de recursos: el pagador debe pertenecer
        // al viaje para poder registrar gastos en el.
        if (!usuarioViajeRepository.existsByUsuarioIdAndViajeId(pagador.getId(), viajeId)) {
            throw new AccesoDenegadoException(
                    "El pagador indicado no pertenece a este viaje");
        }

        Gasto gasto = new Gasto();
        gasto.setViaje(viaje);
        gasto.setPagador(pagador);
        gasto.setConcepto(dto.concepto());
        gasto.setImporte(dto.importe());

        TipoGasto tipoGasto = dto.tipoGasto() != null ? dto.tipoGasto() : TipoGasto.COMPARTIDO;
        gasto.setTipoGasto(tipoGasto);

        // REGLA 1: gasto personal. Se guarda asignado al pagador y NO
        // genera registros en DeudaGasto.
        if (tipoGasto == TipoGasto.PERSONAL) {
            gasto.setTipoDivision(null);
            gastoRepository.save(gasto);
            return toResponseDTO(gasto, List.of());
        }

        // REGLA 2: gasto compartido. Se resuelve la division y se crean
        // los registros de deuda correspondientes.
        TipoDivision tipoDivision = dto.tipoDivision() != null
                ? dto.tipoDivision()
                : TipoDivision.PARTES_IGUALES;
        gasto.setTipoDivision(tipoDivision);
        gastoRepository.save(gasto);

        List<UsuarioViaje> participantes = participantesActivos(viajeId);
        List<DeudaGasto> deudas = generarDeudas(gasto, tipoDivision, dto.detalles(), participantes);
        if (!deudas.isEmpty()) {
            deudaGastoRepository.saveAll(deudas);
        }

        return toResponseDTO(gasto, deudas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoResponseDTO> obtenerGastos(UUID viajeId) {
        verificarPertenencia(viajeId);

        List<Gasto> gastos = gastoRepository.findByViajeIdOrderByFechaDesc(viajeId);
        // Se cargan las deudas de todos los gastos del viaje en una sola
        // consulta y se agrupan por gasto para evitar N+1.
        Map<UUID, List<DeudaGasto>> deudasPorGasto = deudaGastoRepository
                .findByGastoViajeId(viajeId)
                .stream()
                .collect(Collectors.groupingBy(deuda -> deuda.getGasto().getId()));

        return gastos.stream()
                .map(gasto -> toResponseDTO(
                        gasto,
                        deudasPorGasto.getOrDefault(gasto.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional
    public GastoResponseDTO actualizarGasto(UUID viajeId, UUID gastoId, GastoRequestDTO dto) {
        verificarPertenencia(viajeId);

        // a) Buscar el gasto original y comprobar que pertenece al viaje.
        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new AccesoDenegadoException("Gasto no encontrado"));

        if (!gasto.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("El gasto no pertenece a este viaje");
        }

        // b) Eliminar fisicamente todas las deudas actuales del gasto para
        // regenerar la division desde cero sin violar la FK.
        deudaGastoRepository.deleteByGastoId(gastoId);

        // c) Reasignar el pagador respetando estrictamente el UUID indicado
        // y actualizar los campos base del gasto.
        Usuario pagador = usuarioRepository.findById(dto.pagadorId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "El pagador indicado no existe"));

        if (!usuarioViajeRepository.existsByUsuarioIdAndViajeId(pagador.getId(), viajeId)) {
            throw new AccesoDenegadoException(
                    "El pagador indicado no pertenece a este viaje");
        }

        gasto.setConcepto(dto.concepto());
        gasto.setImporte(dto.importe());
        gasto.setPagador(pagador);

        TipoGasto tipoGasto = dto.tipoGasto() != null ? dto.tipoGasto() : TipoGasto.COMPARTIDO;
        gasto.setTipoGasto(tipoGasto);

        // REGLA 1: gasto personal. Se guarda asignado al pagador y NO
        // genera registros en DeudaGasto.
        if (tipoGasto == TipoGasto.PERSONAL) {
            gasto.setTipoDivision(null);
            gastoRepository.save(gasto);
            return toResponseDTO(gasto, List.of());
        }

        // REGLA 2: gasto compartido. Se vuelve a ejecutar la logica de
        // division para generar las nuevas deudas.
        TipoDivision tipoDivision = dto.tipoDivision() != null
                ? dto.tipoDivision()
                : TipoDivision.PARTES_IGUALES;
        gasto.setTipoDivision(tipoDivision);
        gastoRepository.save(gasto);

        List<UsuarioViaje> participantes = participantesActivos(viajeId);
        List<DeudaGasto> deudas = generarDeudas(gasto, tipoDivision, dto.detalles(), participantes);
        if (!deudas.isEmpty()) {
            deudaGastoRepository.saveAll(deudas);
        }

        return toResponseDTO(gasto, deudas);
    }

    @Override
    @Transactional
    public void eliminarGasto(UUID viajeId, UUID gastoId) {
        verificarPertenencia(viajeId);

        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new AccesoDenegadoException("Gasto no encontrado"));

        if (!gasto.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("El gasto no pertenece a este viaje");
        }

        // Se purgan primero las deudas asociadas para no violar la FK.
        deudaGastoRepository.deleteByGastoId(gastoId);
        gastoRepository.delete(gasto);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponseDTO obtenerBalance(UUID viajeId) {
        verificarPertenencia(viajeId);

        Usuario usuario = obtenerUsuarioAutenticado();
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new ViajeNoEncontradoException("Viaje no encontrado"));

        // Presupuesto individual del usuario dentro del viaje.
        BigDecimal presupuestoIndividual = usuarioViajeRepository
                .findByUsuarioIdAndViajeId(usuario.getId(), viajeId)
                .map(UsuarioViaje::getPresupuestoIndividual)
                .orElse(BigDecimal.ZERO);

        // Total gastado por el grupo y por el usuario (incluye sus gastos
        // personales, ya que se suman todos los gastos que el pago).
        BigDecimal totalGastadoGrupo = BigDecimal.ZERO;
        BigDecimal totalGastadoUsuario = BigDecimal.ZERO;
        for (Gasto gasto : gastoRepository.findByViajeIdOrderByFechaDesc(viajeId)) {
            BigDecimal importe = gasto.getImporte();
            totalGastadoGrupo = totalGastadoGrupo.add(importe);
            if (gasto.getPagador().getId().equals(usuario.getId())) {
                totalGastadoUsuario = totalGastadoUsuario.add(importe);
            }
        }

        return new BalanceResponseDTO(
                viaje.getPresupuestoBase(),
                totalGastadoGrupo,
                presupuestoIndividual,
                totalGastadoUsuario,
                calcularResumenDeudas(viajeId));
    }

    /**
     * Genera las deudas de un gasto compartido segun su tipo de division.
     */
    private List<DeudaGasto> generarDeudas(Gasto gasto,
                                           TipoDivision tipoDivision,
                                           List<DetalleDivisionDTO> detalles,
                                           List<UsuarioViaje> participantes) {
        Set<UUID> participantesIds = participantes.stream()
                .map(pertenencia -> pertenencia.getUsuario().getId())
                .collect(Collectors.toSet());

        return switch (tipoDivision) {
            case PARTES_IGUALES -> dividirPartesIguales(gasto, participantes);
            case PORCENTAJE -> dividirPorcentaje(gasto, detalles, participantesIds);
            case DEUDA_TERCERO -> deudaCompleta(gasto, detalles, participantesIds);
        };
    }

    /**
     * REGLA 2a: reparte el importe a partes iguales entre los participantes
     * activos del viaje EXCEPTO el pagador (la REGLA DE ORO prohibe que el
     * pagador deba su propio dinero). Cada deudor asume {@code total / n}
     * (con {@code n} = participantes del viaje) y el ultimo deudor absorbe
     * el remanente de centimos para que la suma de las deudas reembolse
     * exactamente la parte adelantada por el pagador.
     */
    private List<DeudaGasto> dividirPartesIguales(Gasto gasto,
                                                  List<UsuarioViaje> participantes) {
        List<UsuarioViaje> deudores = participantes.stream()
                .filter(p -> !p.getUsuario().getId().equals(gasto.getPagador().getId()))
                .toList();
        if (deudores.isEmpty()) {
            return List.of();
        }

        int n = participantes.size();
        BigDecimal importe = gasto.getImporte();
        BigDecimal base = importe.divide(BigDecimal.valueOf(n), 2, RoundingMode.FLOOR);
        BigDecimal remanente = importe.subtract(base.multiply(BigDecimal.valueOf(n)));

        List<DeudaGasto> deudas = new ArrayList<>();
        for (int i = 0; i < deudores.size(); i++) {
            BigDecimal parte = base;
            if (i == deudores.size() - 1 && remanente.signum() > 0) {
                parte = base.add(remanente);
            }
            deudas.add(construirDeuda(gasto, deudores.get(i).getUsuario(), parte, null));
        }
        return deudas;
    }

    /**
     * REGLA 2b: calcula la deuda de cada participante en base a los
     * porcentajes indicados en los detalles (deben sumar 100).
     */
    private List<DeudaGasto> dividirPorcentaje(Gasto gasto,
                                               List<DetalleDivisionDTO> detalles,
                                               Set<UUID> participantesIds) {
        validarDetallesRequeridos(detalles);

        BigDecimal suma = BigDecimal.ZERO;
        for (DetalleDivisionDTO detalle : detalles) {
            verificarParticipante(detalle.usuarioId(), participantesIds);
            suma = suma.add(detalle.valorAsignado());
        }
        if (suma.compareTo(CIEN) != 0) {
            throw new DatosInvalidosException(
                    "Los porcentajes asignados deben sumar 100");
        }

        UUID pagadorId = gasto.getPagador().getId();
        List<DeudaGasto> deudas = new ArrayList<>();
        for (DetalleDivisionDTO detalle : detalles) {
            // REGLA DE ORO: el pagador nunca es deudor de su propio gasto.
            if (detalle.usuarioId().equals(pagadorId)) {
                continue;
            }
            BigDecimal importeDeuda = gasto.getImporte()
                    .multiply(detalle.valorAsignado())
                    .divide(CIEN, 2, RoundingMode.HALF_UP);
            deudas.add(construirDeuda(gasto, usuarioRepository
                    .findById(detalle.usuarioId())
                    .orElseThrow(() -> new UsuarioNoEncontradoException(
                            "Usuario de la division no encontrado")),
                    importeDeuda,
                    detalle.valorAsignado()));
        }
        return deudas;
    }

    /**
     * REGLA 2c: asigna el 100% del importe a la persona indicada en los
     * detalles, creando un unico registro de deuda hacia el pagador.
     * Se aplica a DEUDA_TERCERO.
     */
    private List<DeudaGasto> deudaCompleta(Gasto gasto,
                                           List<DetalleDivisionDTO> detalles,
                                           Set<UUID> participantesIds) {
        validarDetallesRequeridos(detalles);
        if (detalles.size() != 1) {
            throw new DatosInvalidosException(
                    "La division " + gasto.getTipoDivision()
                            + " requiere un unico participante en los detalles");
        }

        DetalleDivisionDTO detalle = detalles.get(0);
        verificarParticipante(detalle.usuarioId(), participantesIds);

        Usuario deudor = usuarioRepository.findById(detalle.usuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario de la division no encontrado"));

        // REGLA DE ORO: si el unico deudor es el propio pagador, no existe
        // deuda real (no puede deber su propio dinero), asi que se ignora.
        if (deudor.getId().equals(gasto.getPagador().getId())) {
            return List.of();
        }

        return List.of(construirDeuda(gasto, deudor, gasto.getImporte(), CIEN));
    }

    private DeudaGasto construirDeuda(Gasto gasto, Usuario deudor,
                                      BigDecimal importeDeuda, BigDecimal porcentaje) {
        DeudaGasto deuda = new DeudaGasto();
        deuda.setGasto(gasto);
        deuda.setDeudor(deudor);
        deuda.setImporteDeuda(importeDeuda);
        deuda.setPorcentajeAsignado(porcentaje);
        return deuda;
    }

    /**
     * Agrupa los DeudaGasto del viaje y devuelve una lista simplificada:
     * se compensan las deudas cruzadas entre cada pareja, quedando un solo
     * resumen "alguien debe algo a alguien".
     */
    private List<DeudaResumenDTO> calcularResumenDeudas(UUID viajeId) {
        // Matriz dirigida: deudor -> (acreedor -> importe acumulado).
        Map<UUID, Map<UUID, BigDecimal>> deudas = new HashMap<>();
        Map<UUID, String> nombres = new HashMap<>();

        for (DeudaGasto deuda : deudaGastoRepository.findByGastoViajeId(viajeId)) {
            UUID deudorId = deuda.getDeudor().getId();
            UUID acreedorId = deuda.getGasto().getPagador().getId();
            if (deudorId.equals(acreedorId)) {
                continue;
            }
            nombres.put(deudorId, deuda.getDeudor().getNombre());
            nombres.put(acreedorId, deuda.getGasto().getPagador().getNombre());
            deudas.computeIfAbsent(deudorId, k -> new HashMap<>())
                    .merge(acreedorId, deuda.getImporteDeuda(), BigDecimal::add);
        }

        // Compensacion de saldos: cada pareja (X, Y) se procesa una sola
        // vez (clave canonica) y se restan las dos direcciones, de modo que
        // si A debe B y B debe A solo sobrevive el saldo neto en un sentido.
        Map<String, BigDecimal> netas = new LinkedHashMap<>();
        Set<String> parejasProcesadas = new HashSet<>();
        for (UUID deudor : deudas.keySet()) {
            for (Map.Entry<UUID, BigDecimal> acreedorEntry
                    : deudas.getOrDefault(deudor, Map.of()).entrySet()) {
                UUID acreedor = acreedorEntry.getKey();
                String pareja = clavePareja(deudor, acreedor);
                if (!parejasProcesadas.add(pareja)) {
                    continue;
                }
                BigDecimal debe = acreedorEntry.getValue();
                BigDecimal leDeben = deudas.getOrDefault(acreedor, Map.of())
                        .getOrDefault(deudor, BigDecimal.ZERO);
                BigDecimal neto = debe.subtract(leDeben);
                if (neto.signum() == 0) {
                    continue;
                }
                if (neto.signum() > 0) {
                    netas.merge(claveDeuda(deudor, acreedor), neto, BigDecimal::add);
                } else {
                    netas.merge(claveDeuda(acreedor, deudor), neto.abs(), BigDecimal::add);
                }
            }
        }

        return netas.entrySet().stream()
                .map(entrada -> {
                    String[] clave = entrada.getKey().split("\\|");
                    UUID deudorId = UUID.fromString(clave[0]);
                    UUID acreedorId = UUID.fromString(clave[1]);
                    return new DeudaResumenDTO(
                            deudorId,
                            nombres.get(deudorId),
                            acreedorId,
                            nombres.get(acreedorId),
                            entrada.getValue());
                })
                .sorted((a, b) -> b.importe().compareTo(a.importe()))
                .toList();
    }

    private String clavePareja(UUID a, UUID b) {
        String sa = a.toString();
        String sb = b.toString();
        return sa.compareTo(sb) <= 0 ? sa + "|" + sb : sb + "|" + sa;
    }

    private String claveDeuda(UUID deudorId, UUID acreedorId) {
        return deudorId + "|" + acreedorId;
    }

    private void validarDetallesRequeridos(List<DetalleDivisionDTO> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new DatosInvalidosException(
                    "La division seleccionada requiere el detalle de participacion");
        }
    }

    private void verificarParticipante(UUID usuarioId, Set<UUID> participantesIds) {
        if (!participantesIds.contains(usuarioId)) {
            throw new AccesoDenegadoException(
                    "El participante indicado no pertenece a este viaje");
        }
    }

    private List<UsuarioViaje> participantesActivos(UUID viajeId) {
        return usuarioViajeRepository
                .findByViajeIdAndEstadoInvitacionAndActivoTrue(viajeId, EstadoInvitacion.ACEPTADA);
    }

    private void verificarPertenencia(UUID viajeId) {
        Usuario usuario = obtenerUsuarioAutenticado();
        if (!usuarioViajeRepository.existsByUsuarioIdAndViajeId(usuario.getId(), viajeId)) {
            throw new AccesoDenegadoException("No tiene acceso a este viaje");
        }
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado"));
    }

    private GastoResponseDTO toResponseDTO(Gasto gasto, List<DeudaGasto> deudas) {
        return new GastoResponseDTO(
                gasto.getId(),
                gasto.getViaje().getId(),
                gasto.getConcepto(),
                gasto.getImporte(),
                gasto.getFecha(),
                gasto.getTipoGasto(),
                gasto.getTipoDivision(),
                gasto.getPagador().getId(),
                gasto.getPagador().getNombre(),
                deudas.stream().map(this::aDetalleDeudaDTO).toList());
    }

    private DetalleDeudaDTO aDetalleDeudaDTO(DeudaGasto deuda) {
        return new DetalleDeudaDTO(
                deuda.getDeudor().getId(),
                deuda.getDeudor().getNombre(),
                deuda.getImporteDeuda());
    }
}