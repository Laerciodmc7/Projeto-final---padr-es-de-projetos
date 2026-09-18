package com.sistema.decorator;

import com.sistema.strategy.pagamento.EstrategiaPagamento;

public class CupomDescontoDecorator extends PagamentoDecorator {
    private String codigoCupom;
    private double percentualDescontoExtra; // ex: 0.05 para 5%

    public CupomDescontoDecorator(EstrategiaPagamento estrategiaEnvolvida, String codigoCupom, double percentualDescontoExtra) {
        super(estrategiaEnvolvida);
        this.codigoCupom = codigoCupom;
        this.percentualDescontoExtra = percentualDescontoExtra;
    }

    @Override
    public double calcularValorComPagamento(double valorBase) {
        double valorAposPagamento = super.calcularValorComPagamento(valorBase);
        return valorAposPagamento * (1.0 - percentualDescontoExtra);
    }

    @Override
    public String getNomeFormaPagamento() {
        return String.format("%s + Cupom [%s] (%.0f%% Desc Extra)",
                super.getNomeFormaPagamento(),
                codigoCupom,
                percentualDescontoExtra * 100);
    }
}
