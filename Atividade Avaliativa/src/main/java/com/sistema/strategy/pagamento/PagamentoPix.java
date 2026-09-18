package com.sistema.strategy.pagamento;

public class PagamentoPix implements EstrategiaPagamento {

    @Override
    public double calcularValorComPagamento(double valorBase) {
        return valorBase * 0.90; // 10% de desconto
    }

    @Override
    public String getNomeFormaPagamento() {
        return "Pix (10% de Desconto)";
    }
}
