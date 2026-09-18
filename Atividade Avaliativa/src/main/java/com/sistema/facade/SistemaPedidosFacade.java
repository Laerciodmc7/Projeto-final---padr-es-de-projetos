package com.sistema.facade;

import com.sistema.decorator.CupomDescontoDecorator;
import com.sistema.model.ItemPedido;
import com.sistema.model.Pedido;
import com.sistema.model.PedidoBuilder;
import com.sistema.model.StatusPedido;
import com.sistema.observer.LogStatusObserver;
import com.sistema.observer.NotificacaoClienteObserver;
import com.sistema.repository.PedidoRepository;
import com.sistema.strategy.entrega.EntregaPAC;
import com.sistema.strategy.entrega.EntregaSedex;
import com.sistema.strategy.entrega.EntregaTransportadora;
import com.sistema.strategy.entrega.EstrategiaEntrega;
import com.sistema.strategy.pagamento.*;

import java.util.List;
import java.util.Optional;

public class SistemaPedidosFacade {

    private final PedidoRepository repository;

    public SistemaPedidosFacade() {
        // Obtém a instância do Singleton repository
        this.repository = PedidoRepository.getInstance();
    }


    // Cria e salva um pedido a partir dos parâmetros fornecidos.
    public Pedido criarPedido(String cliente, List<ItemPedido> itens,
                              EstrategiaPagamento pagamento, EstrategiaEntrega entrega) {
        int proximoId = repository.gerarProximoId();

        PedidoBuilder builder = new PedidoBuilder()
                .comId(proximoId)
                .comCliente(cliente)
                .comFormaPagamento(pagamento)
                .comModalidadeEntrega(entrega)
                .comStatus(StatusPedido.PENDENTE)
                // Registra por padrão os observadores de auditoria e notificação
                .adicionarObserver(new LogStatusObserver())
                .adicionarObserver(new NotificacaoClienteObserver());

        for (ItemPedido item : itens) {
            builder.adicionarItem(item);
        }

        Pedido pedido = builder.build(proximoId);
        repository.salvar(pedido);
        return pedido;
    }

    // Retorna a lista de todos os pedidos salvos no repositório em memória.

    public List<Pedido> listarPedidos() {
        return repository.listarTodos();
    }

    // Busca um pedido por ID
    public Optional<Pedido> buscarPedidoPorId(int id) {
        return repository.buscarPorId(id);
    }

    // Altera o status do pedido especificado e dispara automaticamente os Observers cadastrados.
    public boolean mudarStatusPedido(int id, StatusPedido novoStatus) {
        Optional<Pedido> opt = repository.buscarPorId(id);
        if (opt.isPresent()) {
            Pedido pedido = opt.get();
            System.out.println("\n>>> Alterando status do pedido...");
            pedido.setStatus(novoStatus);
            repository.salvar(pedido);
            return true;
        }
        return false;
    }

    public EstrategiaPagamento criarEstrategiaPagamento(int opcao, String codigoCupom, Double percentualCupom) {
        EstrategiaPagamento base;
        switch (opcao) {
            case 1:
                base = new PagamentoPix();
                break;
            case 2:
                base = new PagamentoBoleto();
                break;
            case 3:
                base = new PagamentoCartaoVista();
                break;
            case 4:
                base = new PagamentoCartaoParcelado();
                break;
            default:
                throw new IllegalArgumentException("Opção de pagamento inválida: " + opcao);
        }

        if (codigoCupom != null && !codigoCupom.trim().isEmpty() && percentualCupom != null && percentualCupom > 0) {
            base = new CupomDescontoDecorator(base, codigoCupom, percentualCupom);
        }

        return base;
    }

    public EstrategiaEntrega criarEstrategiaEntrega(int opcao) {
        switch (opcao) {
            case 1:
                return new EntregaPAC();
            case 2:
                return new EntregaSedex();
            case 3:
                return new EntregaTransportadora();
            default:
                throw new IllegalArgumentException("Opção de entrega inválida: " + opcao);
        }
    }
}
