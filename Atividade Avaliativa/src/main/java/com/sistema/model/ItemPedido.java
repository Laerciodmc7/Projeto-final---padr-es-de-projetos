package com.sistema.model;

// Item que compõe um pedido.
public class ItemPedido {
    private String nome;
    private int quantidade;
    private double precoUnitario;

    public ItemPedido(String nome, int quantidade, double precoUnitario) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public double getSubtotal() {
        return quantidade * precoUnitario;
    }

    @Override
    public String toString() {
        return String.format("%s (x%d) - R$ %.2f un. | Subtotal: R$ %.2f", nome, quantidade, precoUnitario, getSubtotal());
    }
}
