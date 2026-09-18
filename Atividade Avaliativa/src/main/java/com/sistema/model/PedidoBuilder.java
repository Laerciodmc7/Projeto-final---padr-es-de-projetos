package com.sistema.model;

import com.sistema.observer.ObserverStatusPedido;
import com.sistema.strategy.entrega.EstrategiaEntrega;
import com.sistema.strategy.pagamento.EstrategiaPagamento;

import java.util.ArrayList;
import java.util.List;

public class PedidoBuilder {
    private Integer id;
    private String cliente;
    private final List<ItemPedido> itens = new ArrayList<>();
    private EstrategiaPagamento estrategiaPagamento;
    private EstrategiaEntrega estrategiaEntrega;
    private StatusPedido status = StatusPedido.PENDENTE;
    private final List<ObserverStatusPedido> observers = new ArrayList<>();

    public PedidoBuilder comId(int id) {
        this.id = id;
        return this;
    }

    public PedidoBuilder comCliente(String cliente) {
        this.cliente = cliente;
        return this;
    }

    public PedidoBuilder adicionarItem(ItemPedido item) {
        if (item != null) {
            this.itens.add(item);
        }
        return this;
    }

    public PedidoBuilder adicionarItem(String nome, int quantidade, double precoUnitario) {
        this.itens.add(new ItemPedido(nome, quantidade, precoUnitario));
        return this;
    }

    public PedidoBuilder comFormaPagamento(EstrategiaPagamento estrategiaPagamento) {
        this.estrategiaPagamento = estrategiaPagamento;
        return this;
    }

    public PedidoBuilder comModalidadeEntrega(EstrategiaEntrega estrategiaEntrega) {
        this.estrategiaEntrega = estrategiaEntrega;
        return this;
    }

    public PedidoBuilder comStatus(StatusPedido status) {
        this.status = status;
        return this;
    }

    public PedidoBuilder adicionarObserver(ObserverStatusPedido observer) {
        if (observer != null) {
            this.observers.add(observer);
        }
        return this;
    }

    public Pedido build(int idGerado) {
        int idFinal = (this.id != null) ? this.id : idGerado;

        if (this.cliente == null || this.cliente.trim().isEmpty()) {
            throw new IllegalStateException("O pedido deve possuir um cliente informado.");
        }
        if (this.itens.isEmpty()) {
            throw new IllegalStateException("O pedido deve possuir ao menos um item cadastrado.");
        }
        if (this.estrategiaPagamento == null) {
            throw new IllegalStateException("O pedido deve possuir uma forma de pagamento definida.");
        }
        if (this.estrategiaEntrega == null) {
            throw new IllegalStateException("O pedido deve possuir uma modalidade de entrega definida.");
        }

        return new Pedido(idFinal, cliente, itens, estrategiaPagamento, estrategiaEntrega, status, observers);
    }
}
