package com.sistema.observer;

import com.sistema.model.Pedido;
import com.sistema.model.StatusPedido;

// Observador Concreto que simula a notificação ao cliente (E-mail/SMS/Push) sobre a mudança de status.
public class NotificacaoClienteObserver implements ObserverStatusPedido {

    @Override
    public void onStatusAlterado(Pedido pedido, StatusPedido statusAnterior, StatusPedido novoStatus) {
        System.out.printf("  [NOTIFICAÇÃO AO CLIENTE] Olá %s, seu Pedido #%d teve o status atualizado para: %s!\n",
                pedido.getCliente(),
                pedido.getId(),
                novoStatus.getDescricao());
    }
}
