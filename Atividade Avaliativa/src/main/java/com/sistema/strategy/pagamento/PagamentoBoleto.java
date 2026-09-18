package com.sistema.strategy.pagamento;

public class PagamentoBoleto implements EstrategiaPagamento {

    @Override
    public double calcularValorComPagamento(double valorBase) {
        return valorBase * 0.95; // 5% de desconto
    }

    @Override
    public String getNomeFormaPagamento() {
        return "Boleto Bancário (5% de Desconto)";
    }
}
