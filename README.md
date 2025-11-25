# Exercícios de Programação Paralela - Java

Este repositório contém as implementações em Java de todos os exercícios de programação paralela (25-11).

## 📋 Lista de Exercícios

### Exercícios Teóricos
- **Exercício 1 e 2**: Cálculos de speedup, eficiência e Lei de Amdahl
  - Arquivo: `Exercicios1.java e Exercicio2.java`

### Exercícios Práticos
- **Exercício 3**: Soma de vetor (sequencial vs paralelo)
  - Arquivo: `Exercicio3_SomaVetor.java`
  
- **Exercício 4**: Cálculo de média e desvio padrão
  - Arquivo: `Exercicio4_MediaDesvio.java`
  
- **Exercício 5**: Multiplicação matriz × vetor
  - Arquivo: `Exercicio5_MatrizVetor.java`
  
- **Exercício 6**: Contagem de números primos (partição estática e dinâmica)
  - Arquivo: `Exercicio6_Primos.java`
  
- **Exercício 7**: Filtro de imagem (blur 3×3)
  - Arquivo: `Exercicio7_FiltroImagem.java`

## 🚀 Como Compilar e Executar

### Pré-requisitos
- Java JDK 11 ou superior
- Terminal/Prompt de comando

### Compilação

Para compilar todos os exercícios:

```bash
# Compilar todos de uma vez
javac Exercicio1.java
javac Exercicio2.java
javac Exercicio3_SomaVetor.java
javac Exercicio4_MediaDesvio.java
javac Exercicio5_MatrizVetor.java
javac Exercicio6_Primos.java
javac Exercicio7_FiltroImagem.java
```

Ou compile individualmente conforme necessário.

### Execução

Execute cada exercício individualmente:

```bash
# Exercícios teóricos (1 e 2)
java Exercicio1
java Exercicio2

# Exercício 3 - Soma de vetor
java Exercicio3_SomaVetor

# Exercício 4 - Média e desvio padrão
java Exercicio4_MediaDesvio

# Exercício 5 - Multiplicação matriz × vetor
java Exercicio5_MatrizVetor

# Exercício 6 - Contagem de primos
java Exercicio6_Primos

# Exercício 7 - Filtro de imagem
java Exercicio7_FiltroImagem
```

## ⏱️ Tempo de Execução

Os tempos variam conforme o hardware, mas estimativas aproximadas:

| Exercício | Tempo Aproximado |
|-----------|------------------|
| 1 e 2 (teóricos) | < 1 segundo |
| 3 (Soma) | 5-15 segundos |
| 4 (Média/Desvio) | 10-30 segundos |
| 5 (Matriz×Vetor) | 10-40 segundos |
| 6 (Primos) | 30-90 segundos |
| 7 (Filtro) | 15-60 segundos |

**Dica**: Você pode ajustar os tamanhos dos testes nos arrays `tamanhos` ou `valoresN` no método `main` de cada arquivo para executar mais rápido durante testes.

## 📊 O Que Cada Exercício Mede

### Exercício 3 - Soma de Vetor
- **Objetivo**: Introdução básica ao paralelismo
- **Métricas**: Speedup e eficiência para diferentes números de threads
- **Conceitos**: Divisão de trabalho, overhead de threads, combinação de resultados

### Exercício 4 - Média e Desvio Padrão
- **Objetivo**: Operações com múltiplas fases
- **Métricas**: Impacto de múltiplos reduces
- **Conceitos**: Sincronização entre fases, dependência de dados

### Exercício 5 - Multiplicação Matriz × Vetor
- **Objetivo**: Operações intensivas em memória
- **Métricas**: Escalabilidade com aumento de dados
- **Conceitos**: Localidade de cache, bandwidth de memória, point of diminishing returns

### Exercício 6 - Contagem de Primos
- **Objetivo**: Carga desequilibrada
- **Métricas**: Comparação partição estática vs dinâmica
- **Conceitos**: Balanceamento de carga, overhead de sincronização, work stealing

### Exercício 7 - Filtro de Imagem
- **Objetivo**: Processamento de imagens 2D
- **Métricas**: Padrões de acesso à memória
- **Conceitos**: Localidade espacial, divisão por linhas vs blocos, memory-bound operations

## 🔧 Personalizando os Testes

Você pode modificar os parâmetros nos arquivos:

```java
// Exemplo: Exercicio3_SomaVetor.java
int[] tamanhos = {1_000_000, 5_000_000, 10_000_000}; // Ajuste aqui
int[] numThreadsList = {2, 4, 8}; // Ou aqui
```

Recomendações:
- **Computador fraco**: Use tamanhos menores (500K, 1M, 2M)
- **Computador potente**: Use tamanhos maiores para ver melhor o speedup
- **Número de threads**: Teste com valores próximos ao número de cores do seu CPU

## 📈 Interpretando os Resultados

### Speedup Ideal vs Real
- **Ideal**: Speedup = número de threads (linear)
- **Real**: Sempre menor devido a overhead, partes sequenciais, contenção

### Eficiência
- **> 80%**: Excelente paralelização
- **60-80%**: Boa paralelização
- **40-60%**: Paralelização moderada (há espaço para otimização)
- **< 40%**: Paralelização pobre (overhead muito alto)

### Fatores que Afetam Performance
1. **Overhead de threads**: Criação, sincronização, destruição
2. **Lei de Amdahl**: Parte sequencial limita ganho máximo
3. **Contenção de memória**: Threads competindo pelo mesmo recurso
4. **Cache**: Falhas de cache degradam performance
5. **Balanceamento**: Threads com trabalho desigual

## 🎯 Objetivos de Aprendizado

Após executar esses exercícios, você deve entender:

1. ✅ Como medir performance paralela (speedup, eficiência)
2. ✅ Limitações teóricas (Lei de Amdahl)
3. ✅ Overhead de paralelização
4. ✅ Importância do balanceamento de carga
5. ✅ Impacto de padrões de acesso à memória
6. ✅ Trade-offs entre diferentes estratégias de paralelização

## 💡 Dicas

- Execute cada programa várias vezes para ter médias mais precisas
- Feche outros programas ao medir performance
- Compare os resultados entre exercícios
- Experimente com diferentes números de threads
- Observe quando adicionar mais threads não ajuda (ou piora!)

## 📚 Conceitos Importantes

### Speedup
```
S = Ts / Tp
```
Onde:
- Ts = tempo sequencial
- Tp = tempo paralelo

### Eficiência
```
E = S / p
```
Onde:
- S = speedup
- p = número de processadores/threads

### Lei de Amdahl
```
S_max = 1 / (s + (1-s)/p)
```
Onde:
- s = fração sequencial do programa
- p = número de processadores

## 🐛 Troubleshooting

### "OutOfMemoryError"
- Reduza os tamanhos dos testes
- Aumente heap: `java -Xmx4g NomeDoExercicio`

### Resultados inconsistentes
- Execute múltiplas vezes
- Verifique se outros programas estão rodando
- Use `Thread.setPriority()` se necessário

### Performance ruim
- Verifique o número de cores disponíveis
- Confirme que o sistema não está em modo economia de energia
- Reduza o número de threads para próximo do número de cores físicos



---
