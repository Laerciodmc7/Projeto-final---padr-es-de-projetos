package com.sistema;

import com.sistema.facade.SistemaPedidosFacade;
import com.sistema.model.ItemPedido;
import com.sistema.model.Pedido;
import com.sistema.model.StatusPedido;
import com.sistema.strategy.entrega.EstrategiaEntrega;
import com.sistema.strategy.pagamento.EstrategiaPagamento;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final SistemaPedidosFacade facade = new SistemaPedidosFacade();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   SISTEMA DE GERENCIAMENTO DE PEDIDOS  ");
        System.out.println("=================================================");

        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            System.out.print("Escolha uma opção: ");
            String entrada = scanner.nextLine().trim();

            switch (entrada) {
                case "1":
                    criarNovoPedido();
                    break;
                case "2":
                    listarPedidos();
                    break;
                case "3":
                    visualizarPedidoPorId();
                    break;
                case "4":
                    mudarStatusPedido();
                    break;
                case "5":
                    carregarDadosExemplo();
                    break;
                case "0":
                    rodando = false;
                    System.out.println("\nEncerrando a aplicação.");
                    break;
                default:
                    System.out.println("\nOpção inválida. Tente novamente.");
            }
        }
    }

    private static void exibirMenu() {
            System.out.println("\n---------------- MENU PRINCIPAL ----------------");
            System.out.println("1. Criar novo pedido");
            System.out.println("2. Listar todos os pedidos");
            System.out.println("3. Visualizar detalhes de um pedido por ID");
            System.out.println("4. Mudar status de um pedido");
            System.out.println("5. Inserir pedidos de LOREM IPSUM (exemplo, entendeu) ");
            System.out.println("0. Sair");
            System.out.println("------------------------------------------------");
        }


    private static void criarNovoPedido() {
        System.out.println("\n=== NOVO PEDIDO ===");
        System.out.print("Nome do Cliente: ");
        String cliente = scanner.nextLine().trim();
        if (cliente.isEmpty()) {
            System.out.println("O nome do cliente não pode ser vazio.");
            return;
        }

        List<ItemPedido> itens = new ArrayList<>();
        boolean adicionandoItens = true;
        while (adicionandoItens) {
            System.out.println("\n--- Adicionar Item ---");
            System.out.print("Nome do produto: ");
            String nomeItem = scanner.nextLine().trim();

            int qtd = lerInteiro("Quantidade: ");
            double preco = lerDouble("Preço unitário (R$): ");

            itens.add(new ItemPedido(nomeItem, qtd, preco));

            System.out.print("Deseja adicionar mais um item? (S/N): ");
            String resp = scanner.nextLine().trim().toLowerCase();
            if (!resp.equals("s") && !resp.equals("sim")) {
                adicionandoItens = false;
            }
        }

        if (itens.isEmpty()) {
            System.out.println("Nenhum item foi adicionado. Cancelando criação de pedido.");
            return;
        }

        // Seleção da Forma de Pagamento
        System.out.println("\n--- Forma de Pagamento ---");
        System.out.println("1. Pix (10% de Desconto)");
        System.out.println("2. Boleto (5% de Desconto)");
        System.out.println("3. Cartão à Vista (0% de Desconto)");
        System.out.println("4. Cartão Parcelado (5% de Acréscimo)");
        int opcaoPag = lerInteiro("Selecione a opção (1-4): ");

        // Cupom de Desconto Adicional (Demonstração do Padrão Decorator)
        System.out.print("Possui cupom de desconto extra? (S/N): ");
        String temCupom = scanner.nextLine().trim().toLowerCase();
        String codigoCupom = null;
        Double percentualCupom = null;

        if (temCupom.equals("s") || temCupom.equals("sim")) {
            System.out.print("Informe o código do cupom (ex: PROMO10): ");
            codigoCupom = scanner.nextLine().trim();
            double descPorcentagem = lerDouble("Informe a porcentagem de desconto extra (ex: 10 para 10%): ");
            percentualCupom = descPorcentagem / 100.0;
        }

        EstrategiaPagamento pagamento;
        try {
            pagamento = facade.criarEstrategiaPagamento(opcaoPag, codigoCupom, percentualCupom);
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRO] " + e.getMessage());
            return;
        }

        // Seleção da Modalidade de Entrega
        System.out.println("\n--- Modalidade de Entrega ---");
        System.out.println("1. PAC (Frete 5% do pedido)");
        System.out.println("2. Sedex (Frete 10% do pedido)");
        System.out.println("3. Transportadora (Frete 15% do pedido)");
        int opcaoEntrega = lerInteiro("Selecione a opção (1-3): ");

        EstrategiaEntrega entrega;
        try {
            entrega = facade.criarEstrategiaEntrega(opcaoEntrega);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
            return;
        }

        try {
            Pedido pedidoCriado = facade.criarPedido(cliente, itens, pagamento, entrega);
            System.out.println("\nPedido #" + pedidoCriado.getId() + " criado com sucesso!");
            System.out.println(pedidoCriado);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void listarPedidos() {
        System.out.println("\n=== LISTA DE PEDIDOS ===");
        List<Pedido> pedidos = facade.listarPedidos();
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado até o momento.");
            return;
        }

        for (Pedido p : pedidos) {
            System.out.println(p);
            System.out.println();
        }
    }

    private static void visualizarPedidoPorId() {
        System.out.println("\n=== VISUALIZAR PEDIDO ===");
        int id = lerInteiro("Informe o ID do pedido: ");
        Optional<Pedido> opt = facade.buscarPedidoPorId(id);

        if (opt.isPresent()) {
            System.out.println(opt.get());
        } else {
            System.out.println("Pedido com ID #" + id + " não foi encontrado.");
        }
    }

    private static void mudarStatusPedido() {
        System.out.println("\n=== MUDAR STATUS DO PEDIDO ===");
        int id = lerInteiro("Informe o ID do pedido: ");
        Optional<Pedido> opt = facade.buscarPedidoPorId(id);

        if (opt.isEmpty()) {
            System.out.println("Pedido com ID #" + id + " não foi encontrado.");
            return;
        }

        System.out.println("Status atual: " + opt.get().getStatus().getDescricao());
        System.out.println("Escolha o novo status:");
        StatusPedido[] statusValores = StatusPedido.values();
        for (int i = 0; i < statusValores.length; i++) {
            System.out.printf("%d. %s\n", (i + 1), statusValores[i].getDescricao());
        }

        int opcaoStatus = lerInteiro("Selecione a opção (1-" + statusValores.length + "): ");
        if (opcaoStatus < 1 || opcaoStatus > statusValores.length) {
            System.out.println("Opção de status inválida.");
            return;
        }

        StatusPedido novoStatus = statusValores[opcaoStatus - 1];
        boolean sucesso = facade.mudarStatusPedido(id, novoStatus);

        if (sucesso) {
            System.out.println("Status atualizado com sucesso");
        }
    }

    private static void carregarDadosExemplo() {
        System.out.println("\n=== INSERINDO DADOS DE EXEMPLO ===");

        // Pedido 1: Maria - Pix (10% desc) + PAC (5% frete)
        List<ItemPedido> itens1 = List.of(
                new ItemPedido("Notebook Dell", 1, 3500.00),
                new ItemPedido("Mouse Sem Fio", 2, 75.00)
        );
        EstrategiaPagamento pag1 = facade.criarEstrategiaPagamento(1, null, null); // Pix
        EstrategiaEntrega ent1 = facade.criarEstrategiaEntrega(1); // PAC
        Pedido p1 = facade.criarPedido("Maria Silva", itens1, pag1, ent1);

        // Pedido 2: João - Cartão Parcelado (+5%) + Sedex (10% frete) + Cupom (10% extra)
        List<ItemPedido> itens2 = List.of(
                new ItemPedido("Smartphone Galaxy S23", 1, 4000.00),
                new ItemPedido("Capa Protetora", 1, 100.00)
        );
        EstrategiaPagamento pag2 = facade.criarEstrategiaPagamento(4, "BLACK10", 0.10); // Cartão Parcelado + Cupom
        EstrategiaEntrega ent2 = facade.criarEstrategiaEntrega(2); // Sedex
        Pedido p2 = facade.criarPedido("João Souza", itens2, pag2, ent2);

        System.out.println("2 Pedidos de exemplo inseridos em memória!");
        System.out.println(" - Pedido #" + p1.getId() + " criado para " + p1.getCliente());
        System.out.println(" - Pedido #" + p2.getId() + " criado para " + p2.getCliente());
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                String valor = scanner.nextLine().trim();
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private static double lerDouble(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                String valor = scanner.nextLine().trim().replace(",", ".");
                return Double.parseDouble(valor);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número decimal válido (ex: 49.90).");
            }
        }
    }
}
