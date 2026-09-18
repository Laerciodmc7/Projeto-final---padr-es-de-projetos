package com.sistema.strategy.pagamento;

public class PagamentoCartaoVista implements EstrategiaPagamento {

    @Override
    public double calcularValorComPagamento(double valorBase) {
        return valorBase; // 0% de desconto (valor integral)
    }

    @Override
    public String getNomeFormaPagamento() {
        return "Cartão à Vista (0% de Desconto)";
    }
}
