package com.sistema.model;

import com.sistema.observer.ObserverStatusPedido;
import com.sistema.strategy.entrega.EstrategiaEntrega;
import com.sistema.strategy.pagamento.EstrategiaPagamento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Entidade principal que representa o Pedido no sistema.
public class Pedido {
    private final int id;
    private final String cliente;
    private final List<ItemPedido> itens;
    private final EstrategiaPagamento estrategiaPagamento;
    private final EstrategiaEntrega estrategiaEntrega;
    private StatusPedido status;
    private final List<ObserverStatusPedido> observers;

    // Construtor com escopo de pacote PedidoBuilder usa ele.
    Pedido(int id, String cliente, List<ItemPedido> itens, EstrategiaPagamento estrategiaPagamento,
           EstrategiaEntrega estrategiaEntrega, StatusPedido status, List<ObserverStatusPedido> observers) {
        this.id = id;
        this.cliente = cliente;
        this.itens = new ArrayList<>(itens);
        this.estrategiaPagamento = estrategiaPagamento;
        this.estrategiaEntrega = estrategiaEntrega;
        this.status = status != null ? status : StatusPedido.PENDENTE;
        this.observers = new ArrayList<>(observers != null ? observers : Collections.emptyList());
    }

    public int getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public EstrategiaPagamento getEstrategiaPagamento() {
        return estrategiaPagamento;
    }

    public EstrategiaEntrega getEstrategiaEntrega() {
        return estrategiaEntrega;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void adicionarObserver(ObserverStatusPedido observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removerObserver(ObserverStatusPedido observer) {
        observers.remove(observer);
    }

    // Altera o status do pedido e notifica todos os observadores cadastrados
    public void setStatus(StatusPedido novoStatus) {
        if (this.status != novoStatus) {
            StatusPedido statusAnterior = this.status;
            this.status = novoStatus;
            notificarObservers(statusAnterior, novoStatus);
        }
    }

    private void notificarObservers(StatusPedido statusAnterior, StatusPedido novoStatus) {
        for (ObserverStatusPedido observer : observers) {
            observer.onStatusAlterado(this, statusAnterior, novoStatus);
        }
    }

    public double getValorBase() {
        double total = 0.0;
        for (ItemPedido item : itens) {
            total += item.getSubtotal();
        }
        return total;
    }

    public double getValorPagamentoAjustado() {
        if (estrategiaPagamento == null) {
            return getValorBase();
        }
        return estrategiaPagamento.calcularValorComPagamento(getValorBase());
    }

    public double getValorFrete() {
        if (estrategiaEntrega == null) {
            return 0.0;
        }
        return estrategiaEntrega.calcularFrete(getValorBase());
    }

    public double getValorFinal() {
        return getValorPagamentoAjustado() + getValorFrete();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=========================================\n"));
        sb.append(String.format(" PEDIDO #%d | Cliente: %s | Status: %s\n", id, cliente, status.getDescricao()));
        sb.append(String.format("=========================================\n"));
        sb.append(" Itens:\n");
        for (ItemPedido item : itens) {
            sb.append(String.format("   - %s\n", item));
        }
        sb.append(String.format("-----------------------------------------\n"));
        sb.append(String.format(" Valor Base dos Itens : R$ %.2f\n", getValorBase()));
        sb.append(String.format(" Pagamento (%s): R$ %.2f\n",
                estrategiaPagamento != null ? estrategiaPagamento.getNomeFormaPagamento() : "Não especificado",
                getValorPagamentoAjustado()));
        sb.append(String.format(" Frete (%s): R$ %.2f\n",
                estrategiaEntrega != null ? estrategiaEntrega.getNomeModalidade() : "Não especificado",
                getValorFrete()));
        sb.append(String.format("-----------------------------------------\n"));
        sb.append(String.format(" VALOR FINAL DO PEDIDO: R$ %.2f\n", getValorFinal()));
        sb.append(String.format("========================================="));
        return sb.toString();
    }
}
