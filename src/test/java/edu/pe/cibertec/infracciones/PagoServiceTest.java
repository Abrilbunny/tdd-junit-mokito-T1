package edu.pe.cibertec.infracciones;


import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Pago;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.PagoRepository;
import edu.pe.cibertec.infracciones.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PagoServiceTest {

    @ExtendWith(MockitoExtension.class)

        @Mock
        private MultaRepository multaRepository;

        @Mock
        private PagoRepository pagoRepository;

        @InjectMocks
        private PagoServiceImpl pagoService;

        @Test
        void procesarPago_conRecargo_debeGuardarPagoCorrecto() {
            Multa multa = new Multa();
            multa.setId(1L);
            multa.setMonto(500.0);
            multa.setFechaEmision(LocalDate.now().minusDays(12));
            multa.setFechaVencimiento(LocalDate.now().minusDays(2));
            multa.setEstado(EstadoMulta.PENDIENTE);

            when(multaRepository.findById(1L)).thenReturn(Optional.of(multa));

            pagoService.procesarPago(1L);

            ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);

            verify(pagoRepository, times(1)).save(captor.capture());

            Pago pagoGuardado = captor.getValue();

            assertEquals(575.0, pagoGuardado.getMontoPagado());
        }
    }


