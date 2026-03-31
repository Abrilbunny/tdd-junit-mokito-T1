package edu.pe.cibertec.infracciones.service.impl;

import edu.pe.cibertec.infracciones.dto.PagoResponseDTO;
import edu.pe.cibertec.infracciones.exception.MultaNotFoundException;
import edu.pe.cibertec.infracciones.exception.PagoYaRealizadoException;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Pago;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.PagoRepository;
import edu.pe.cibertec.infracciones.service.IPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements IPagoService {

    private final PagoRepository pagoRepository;
    private final MultaRepository multaRepository;

    @Override
    public PagoResponseDTO procesarPago(Long multaId) {
        Multa multa = multaRepository.findById(multaId)
                .orElseThrow(() -> new RuntimeException("Multa no encontrada"));

        if (multa.getEstado() == EstadoMulta.PAGADA) {
            throw new RuntimeException("Pago ya realizado");
        }

        double montoOriginal = multa.getMonto();
        double descuento = 0;
        double recargo = 0;

        LocalDate hoy = LocalDate.now();
        long dias = ChronoUnit.DAYS.between(multa.getFechaEmision(), hoy);

        if (dias >= 0 && dias <= 5) {
            descuento = montoOriginal * 0.20;
        }

        if (multa.getEstado() == EstadoMulta.VENCIDA) {
            recargo = montoOriginal * 0.15;
        }

        double montoFinal = montoOriginal - descuento + recargo;

        Pago pago = new Pago();
        pago.setMulta(multa);
        pago.setMontoPagado(montoFinal);
        pago.setFechaPago(hoy);

        pagoRepository.save(pago);

        multa.setEstado(EstadoMulta.PAGADA);
        multaRepository.save(multa);

        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setMontoPagado(montoFinal);
        dto.setFechaPago(hoy);

        return dto;
    }

    @Override
    public List<PagoResponseDTO> obtenerPagosPorInfractor(Long infractorId) {
        return pagoRepository.findByMulta_Infractor_Id(infractorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PagoResponseDTO mapToResponse(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setId(pago.getId());
        dto.setMontoPagado(pago.getMontoPagado());
        dto.setFechaPago(pago.getFechaPago());
        dto.setDescuentoAplicado(pago.getDescuentoAplicado());
        dto.setRecargo(pago.getRecargo());
        dto.setMultaId(pago.getMulta().getId());
        return dto;
    }


}