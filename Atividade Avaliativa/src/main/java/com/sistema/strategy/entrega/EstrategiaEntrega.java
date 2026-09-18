package com.sistema.strategy.entrega;


public interface EstrategiaEntrega {

    double calcularFrete(double valorBase);

    String getNomeModalidade();
}
