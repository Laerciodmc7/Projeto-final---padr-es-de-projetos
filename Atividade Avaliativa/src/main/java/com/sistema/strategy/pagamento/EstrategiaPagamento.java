package com.sistema.strategy.pagamento;

public interface EstrategiaPagamento {

    double calcularValorComPagamento(double valorBase);

    String getNomeFormaPagamento();
}
