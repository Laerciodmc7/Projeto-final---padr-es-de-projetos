package com.sistema.observer;

import com.sistema.model.Pedido;
import com.sistema.model.StatusPedido;

// Interface dos observadores de mudanças no status do pedido.
public interface ObserverStatusPedido {

    void onStatusAlterado(Pedido pedido, StatusPedido statusAnterior, StatusPedido novoStatus);
}
