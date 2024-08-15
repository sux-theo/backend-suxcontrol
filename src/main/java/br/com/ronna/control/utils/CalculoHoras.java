package br.com.ronna.control.utils;

import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Log4j2
public class CalculoHoras {
    
    // Retorna o total de tempo em minutos
    public Double diferencaHoras(LocalDateTime inicio, LocalDateTime fim, Double totalAbono) {
        
        log.info("UNTIL: " + Double.valueOf(inicio.until(fim, ChronoUnit.MINUTES)) / 60);
        return (Double.valueOf( inicio.until(fim, ChronoUnit.MINUTES)) - totalAbono)/ 60;
    }

    // Retorna o total de tempo em minutos
    public Double diferencaInicioFim(LocalDateTime inicio, LocalDateTime fim) {

        log.info("UNTIL: " + Double.valueOf(inicio.until(fim, ChronoUnit.MINUTES)) / 60);
        return (Double.valueOf( inicio.until(fim, ChronoUnit.MINUTES)))/ 60;
    }
}