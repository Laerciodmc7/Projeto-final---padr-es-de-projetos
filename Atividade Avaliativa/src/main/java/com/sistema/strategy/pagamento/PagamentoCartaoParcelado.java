package com.sistema.strategy.pagamento;

public class PagamentoCartaoParcelado implements EstrategiaPagamento {

    @Override
    public double calcularValorComPagamento(double valorBase) {
        return valorBase * 1.05; // 5% de acréscimo
    }

    @Override
    public String getNomeFormaPagamento() {
        return "Cartão Parcelado (5% de Acréscimo)";
    }
}
