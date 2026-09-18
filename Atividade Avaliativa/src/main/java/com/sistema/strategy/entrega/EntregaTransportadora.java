package com.sistema.strategy.entrega;

public class EntregaTransportadora implements EstrategiaEntrega {

    @Override
    public double calcularFrete(double valorBase) {
        return valorBase * 0.15; // 15% do pedido
    }

    @Override
    public String getNomeModalidade() {
        return "Transportadora (Frete 15%)";
    }
}
