package com.sistema.strategy.entrega;

public class EntregaSedex implements EstrategiaEntrega {

    @Override
    public double calcularFrete(double valorBase) {
        return valorBase * 0.10; // 10% do pedido
    }

    @Override
    public String getNomeModalidade() {
        return "Sedex (Frete 10%)";
    }
}
