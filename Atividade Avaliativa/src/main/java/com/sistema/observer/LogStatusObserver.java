package com.sistema.observer;

import com.sistema.model.Pedido;
import com.sistema.model.StatusPedido;

// Observador Concreto que gera logs do sistema ao alterar o status de um pedido.
public class LogStatusObserver implements ObserverStatusPedido {

    @Override
    public void onStatusAlterado(Pedido pedido, StatusPedido statusAnterior, StatusPedido novoStatus) {
        System.out.printf("  [LOG DE AUDITORIA] Pedido #%d alterado de '%s' para '%s'.\n",
                pedido.getId(),
                statusAnterior != null ? statusAnterior.getDescricao() : "NENHUM",
                novoStatus.getDescricao());
    }
}
