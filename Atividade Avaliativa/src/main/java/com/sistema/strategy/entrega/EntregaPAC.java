package com.sistema.strategy.entrega;

public class EntregaPAC implements EstrategiaEntrega {

    @Override
    public double calcularFrete(double valorBase) {
        return valorBase * 0.05; // 5% do pedido
    }

    @Override
    public String getNomeModalidade() {
        return "PAC (Frete 5%)";
    }
}
