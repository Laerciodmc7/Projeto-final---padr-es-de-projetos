O sistema foi desenvolvido em **Java 17/21**

---

## Como executar

### Pré-requisitos
- JDK 17 ou superior instalado.
- Apache Maven.

### Executando via Maven
```bash
mvn compile
mvn exec:java
```

---

## Padrões de projeto adotados

### Singleton (Criacional)

#### Problema
A aplicação opera em console e precisa manter o estado dos pedidos unicamente em memória durante sua execução. Se houvesse múltiplas instâncias do repositório espalhadas pelo código, dados poderiam ser perdidos, inconsistências seriam geradas e o controle da geração de IDs únicos falharia.

#### Justificativa
O Singleton garante que apenas uma única instância da classe de repositório exista em toda a execução do sistema, provendo um ponto de acesso aos dados guardados em memória.

#### Implementação
Foi criada a classe `PedidoRepository` com construtor privado, impedindo instanciação externa. A única instância é obtida através do método estático `getInstance()`.

#### Classes e/ou interfaces envolvidas
- `com.sistema.repository.PedidoRepository`

#### Benefícios obtidos
- Fonte única para persistência em memória.
- Controle centralizado e seguro sobre o gerador de IDs sequenciais.
- Economia de recursos e prevenção de concorrência inconsistente.

#### Trecho do código
```java
// Localização: src/main/java/com/sistema/repository/PedidoRepository.java
public class PedidoRepository {
    private static volatile PedidoRepository instance;
    private final List<Pedido> pedidos;

    private PedidoRepository() {
        this.pedidos = new ArrayList<>();
    }

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
}
```

---

### Builder (Criacional)

#### Problema
A criação de um `Pedido` envolve múltiplos parâmetros opcionais e obrigatórios (ID, nome do cliente, lista de itens, estratégia de pagamento, modalidade de entrega, status inicial e observadores). O uso de um construtor telescópico tradicional torna o código ilegível, propício a erros de ordem de parâmetros e difícil de manter.

#### Justificativa
O padrão Builder separa a construção de um objeto complexo da sua representação, permitindo construir um `Pedido` de forma fluente, clara, controlada e validada passo a passo.

#### Implementação
A classe `PedidoBuilder` disponibiliza métodos encadeados como `comCliente()`, `adicionarItem()`, `comFormaPagamento()`, `comModalidadeEntrega()` e `adicionarObserver()`. Ao final, o método `build()` valida a integridade dos dados e retorna o objeto `Pedido` pronto.

#### Classes e/ou interfaces envolvidas
- `com.sistema.model.PedidoBuilder`
- `com.sistema.model.Pedido`

#### Benefícios obtidos
- Encadeamento fluente e legível durante a instanciação de pedidos.
- Garantia de validação dos campos obrigatórios antes da criação do objeto.
- Imutabilidade parcial e proteção contra objetos em estado inconsistente.

#### Trecho do código
```java
// Localização: src/main/java/com/sistema/model/PedidoBuilder.java
public class PedidoBuilder {
    public PedidoBuilder comCliente(String cliente) { ... }
    public PedidoBuilder adicionarItem(ItemPedido item) { ... }
    public PedidoBuilder comFormaPagamento(EstrategiaPagamento pagamento) { ... }
    public PedidoBuilder comModalidadeEntrega(EstrategiaEntrega entrega) { ... }
    
    public Pedido build(int idGerado) {
        if (this.cliente == null) throw new IllegalStateException("Cliente obrigatório");
        return new Pedido(idFinal, cliente, itens, estrategiaPagamento, estrategiaEntrega, status, observers);
    }
}
```

---

### Facade (Estrutural)

#### Problema
A interface gráfica em console (`Main`) precisava interagir com múltiplos subsistemas complexos: repositório Singleton, validador Builder, instanciação de estratégias de pagamento/frete, envolvimento por decoradores de cupom e cadastro de observadores. Expor toda essa lógica diretamente na classe `Main` poluiria a CLI e acoplaria as camadas.

#### Justificativa
O Facade provê uma interface unificada e simplificada para um conjunto de interfaces de um subsistema, facilitando o uso por parte da CLI/Console e promovendo o baixo acoplamento.

#### Implementação
A classe `SistemaPedidosFacade` abstrai a complexidade. Ela oferece métodos de alto nível como `criarPedido()`, `listarPedidos()`, `mudarStatusPedido()`, `criarEstrategiaPagamento()` e `criarEstrategiaEntrega()`.

#### Classes e/ou interfaces envolvidas
- `com.sistema.facade.SistemaPedidosFacade`
- Interage com: `PedidoRepository`, `PedidoBuilder`, `EstrategiaPagamento`, `EstrategiaEntrega`, `ObserverStatusPedido`.

#### Benefícios obtidos
- A classe da CLI (`Main`) foca exclusivamente na interação com o usuário.
- Centralização da orquestração dos componentes do domínio.
- Facilidade de manutenção e desacoplamento do código.

#### Trecho do código
```java
// Localização: src/main/java/com/sistema/facade/SistemaPedidosFacade.java
public class SistemaPedidosFacade {
    private final PedidoRepository repository = PedidoRepository.getInstance();

    public Pedido criarPedido(String cliente, List<ItemPedido> itens, EstrategiaPagamento pagamento, EstrategiaEntrega entrega) {
        // Orquestra a construção com Builder, associa Observers padrão e salva no Repositório
    }
}
```

---

### Decorator (Estrutural)

#### Problema
Necessidade de adicionar um cupom de desconto extra (ex: 5% ou 10% promocional) de forma dinâmica sobre o valor do pagamento, sem alterar as classes originais de estratégia de pagamento (`PagamentoPix`, `PagamentoBoleto`, etc.) nem violar o Princípio do Aberto/Fechado (Open/Closed Principle).

#### Justificativa
O Decorator permite adicionar responsabilidades e comportamentos a um objeto dinamicamente em tempo de execução, envolvendo o objeto original sem necessidade de herança excessiva.

#### Implementação
Foi criada a classe abstrata `PagamentoDecorator` implementando `EstrategiaPagamento` e contendo uma referência para a estratégia envolvida. A classe concreta `CupomDescontoDecorator` estende o decorador e aplica a porcentagem de desconto extra sobre o resultado da estratégia original.

#### Classes e/ou interfaces envolvidas
- `com.sistema.strategy.pagamento.EstrategiaPagamento` (Componente)
- `com.sistema.decorator.PagamentoDecorator` (Decorator Abstrato)
- `com.sistema.decorator.CupomDescontoDecorator` (Decorator Concreto)

#### Benefícios obtidos
- Combinação dinâmica de novos descontos e cupons sem modificar as classes de pagamento existentes.
- Respeito integral ao Princípio do Aberto/Fechado (OCP).
- Flexibilidade para empilhar múltiplos decoradores se desejado no futuro.

#### Trecho do código
```java
// Localização: src/main/java/com/sistema/decorator/CupomDescontoDecorator.java
public class CupomDescontoDecorator extends PagamentoDecorator {
    private String codigoCupom;
    private double percentualDescontoExtra;

    public CupomDescontoDecorator(EstrategiaPagamento estrategiaEnvolvida, String codigoCupom, double percentual) {
        super(estrategiaEnvolvida);
        this.codigoCupom = codigoCupom;
        this.percentualDescontoExtra = percentual;
    }

    @Override
    public double calcularValorComPagamento(double valorBase) {
        double valorAposPagamento = super.calcularValorComPagamento(valorBase);
        return valorAposPagamento * (1.0 - percentualDescontoExtra);
    }
}
```

---

### Strategy (Comportamental)

#### Problema
O cálculo do valor final do pedido depende de duas decisões variáveis:
1. Forma de pagamento escolhida (Pix: 10% desc, Boleto: 5% desc, Cartão Vista: 0%, Cartão Parcelado: +5%).
2. Modalidade de entrega selecionada (PAC: 5% frete, Sedex: 10% frete, Transportadora: 15% frete).
Utilizar condicionais `if/else` ou `switch` espalhados pelo código tornaria a aplicação rígida e difícil de estender.

#### Justificativa
O Strategy define uma família de algoritmos, encapsula cada um deles e os torna intercambiáveis em tempo de execução.

#### Implementação
Foram criadas duas interfaces de estratégia:
- `EstrategiaPagamento` com implementações: `PagamentoPix`, `PagamentoBoleto`, `PagamentoCartaoVista`, `PagamentoCartaoParcelado`.
- `EstrategiaEntrega` com implementações: `EntregaPAC`, `EntregaSedex`, `EntregaTransportadora`.

O `Pedido` armazena referências para estas interfaces e delega a elas o cálculo dos valores.

#### Classes e/ou interfaces envolvidas
- `com.sistema.strategy.pagamento.EstrategiaPagamento`
- `com.sistema.strategy.pagamento.PagamentoPix`, `PagamentoBoleto`, `PagamentoCartaoVista`, `PagamentoCartaoParcelado`
- `com.sistema.strategy.entrega.EstrategiaEntrega`
- `com.sistema.strategy.entrega.EntregaPAC`, `EntregaSedex`, `EntregaTransportadora`

#### Benefícios obtidos
- Eliminação total de blocos `if/else` extensos no cálculo dos valores.
- Facilidade para adicionar novas formas de pagamento ou fretes sem alterar o código do `Pedido`.
- Isolamento e testabilidade independente de cada regra de negócio.

#### Trecho do código
```java
// Localização: src/main/java/com/sistema/strategy/pagamento/PagamentoPix.java
public class PagamentoPix implements EstrategiaPagamento {
    @Override
    public double calcularValorComPagamento(double valorBase) {
        return valorBase * 0.90; // 10% de desconto
    }
}

// Localização: src/main/java/com/sistema/strategy/entrega/EntregaSedex.java
public class EntregaSedex implements EstrategiaEntrega {
    @Override
    public double calcularFrete(double valorBase) {
        return valorBase * 0.10; // 10% de frete
    }
}
```

---

### Observer (Comportamental)

#### Problema
Quando o status de um pedido é alterado (ex: de `PENDENTE` para `PAGO` ou `ENVIADO`), múltiplos serviços interessados precisam reagir a esse evento (ex: registrar um log de auditoria e enviar uma notificação ao cliente). Acoplar esses envios diretamente na classe `Pedido` violaria o Princípio da Responsabilidade Única (SRP).

#### Justificativa
O Observer define uma dependência um-para-muitos entre objetos, de modo que quando um objeto muda de estado, todos os seus dependentes são notificados e atualizados automaticamente.

#### Implementação
Foi definida a interface `ObserverStatusPedido`. A classe `Pedido` atua como o *Subject*, mantendo uma lista de observadores e notificando-os dentro do método `setStatus()`. As classes concretas `LogStatusObserver` e `NotificacaoClienteObserver` reagem ao evento exibindo as informações pertinentes.

#### Classes e/ou interfaces envolvidas
- `com.sistema.observer.ObserverStatusPedido` (Interface Observer)
- `com.sistema.observer.LogStatusObserver` (Observer Concreto)
- `com.sistema.observer.NotificacaoClienteObserver` (Observer Concreto)
- `com.sistema.model.Pedido` (Subject)

#### Benefícios obtidos
- Desacoplamento entre a alteração de status do pedido e os efeitos colaterais (notificações, auditoria, etc.).
- Dinamismo para registrar ou remover observadores a qualquer momento.
- Facilidade de expansão (ex: adicionar integração com WhatsApp sem mexer na entidade `Pedido`).

#### Trecho do código
```java
// Localização: src/main/java/com/sistema/model/Pedido.java
public void setStatus(StatusPedido novoStatus) {
    if (this.status != novoStatus) {
        StatusPedido anterior = this.status;
        this.status = novoStatus;
        for (ObserverStatusPedido obs : observers) {
            obs.onStatusAlterado(this, anterior, novoStatus);
        }
    }
}
```
