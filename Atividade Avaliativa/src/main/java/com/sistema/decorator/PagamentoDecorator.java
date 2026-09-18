package com.sistema.decorator;

import com.sistema.strategy.pagamento.EstrategiaPagamento;

public abstract class PagamentoDecorator implements EstrategiaPagamento {
    protected EstrategiaPagamento estrategiaEnvolvida;

    public PagamentoDecorator(EstrategiaPagamento estrategiaEnvolvida) {
        this.estrategiaEnvolvida = estrategiaEnvolvida;
    }

    @Override
    public double calcularValorComPagamento(double valorBase) {
        return estrategiaEnvolvida.calcularValorComPagamento(valorBase);
    }

    @Override
    public String getNomeFormaPagamento() {
        return estrategiaEnvolvida.getNomeFormaPagamento();
    }
}
