package com.sistema.repository;

import com.sistema.model.Pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class PedidoRepository {
    private static volatile PedidoRepository instance;

    private final List<Pedido> pedidos;
    private final AtomicInteger idGenerator;

    // Construtor privado para evitar instanciação direta externa
    private PedidoRepository() {
        this.pedidos = new ArrayList<>();
        this.idGenerator = new AtomicInteger(1);
    }

    // retorna a instância única do repositório de pedidos
    public static PedidoRepository getInstance() {
        if (instance == null) {
            synchronized (PedidoRepository.class) {
                if (instance == null) {
                    instance = new PedidoRepository();
                }
            }
        }
        return instance;
    }

    //salva um pedido no repositório em memória.
    public void salvar(Pedido pedido) {
        if (pedido != null) {
            // Se já existe, atualiza; caso contrário, adiciona
            buscarPorId(pedido.getId()).ifPresentOrElse(
                p -> {
                    int index = pedidos.indexOf(p);
                    pedidos.set(index, pedido);
                },
                () -> pedidos.add(pedido)
            );
        }
    }

    public int gerarProximoId() {
        return idGenerator.getAndIncrement();
    }

    public Optional<Pedido> buscarPorId(int id) {
        return pedidos.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    public List<Pedido> listarTodos() {
        return Collections.unmodifiableList(pedidos);
    }
}
