import java.util.Random;

public class Exercicio3_SomaVetor {
    
    public static double somaSequencial(double[] vetor) {
        double soma = 0;
        for (double valor : vetor) {
            soma += valor;
        }
        return soma;
    }
    
    static class ThreadSomaParcial extends Thread {
        private double[] vetor;
        private int inicio;
        private int fim;
        private double resultado;
        
        public ThreadSomaParcial(double[] vetor, int inicio, int fim) {
            this.vetor = vetor;
            this.inicio = inicio;
            this.fim = fim;
        }
        
        @Override
        public void run() {
            resultado = 0;
            for (int i = inicio; i < fim; i++) {
                resultado += vetor[i];
            }
        }
        
        public double getResultado() {
            return resultado;
        }
    }
    
    public static double somaParalela(double[] vetor, int numThreads) throws InterruptedException {
        int n = vetor.length;
        int tamanhoBloco = n / numThreads;
        ThreadSomaParcial[] threads = new ThreadSomaParcial[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            int inicio = i * tamanhoBloco;
            int fim = (i == numThreads - 1) ? n : (i + 1) * tamanhoBloco;
            
            threads[i] = new ThreadSomaParcial(vetor, inicio, fim);
            threads[i].start();
        }
        
        double somaTotal = 0;
        for (ThreadSomaParcial thread : threads) {
            thread.join();
            somaTotal += thread.getResultado();
        }
        
        return somaTotal;
    }
    
    public static void main(String[] args) throws InterruptedException {
        int[] tamanhos = {1_000_000, 5_000_000, 10_000_000};
        int[] numThreadsList = {2, 4, 8};
        
        System.out.println("=".repeat(80));
        System.out.println("EXERCÍCIO 3 - SOMA DE VETOR");
        System.out.println("=".repeat(80));
        
        for (int tamanho : tamanhos) {
            System.out.println("\n" + "─".repeat(80));
            System.out.printf("Tamanho do vetor: %,d elementos\n", tamanho);
            System.out.println("─".repeat(80));
            
            double[] vetor = new double[tamanho];
            Random rand = new Random(42);
            for (int i = 0; i < tamanho; i++) {
                vetor[i] = rand.nextDouble();
            }
            
            long inicio = System.nanoTime();
            double resultadoSeq = somaSequencial(vetor);
            long fim = System.nanoTime();
            double ts = (fim - inicio) / 1_000_000_000.0;
            
            System.out.println("\n✓ Sequencial:");
            System.out.printf("  Tempo: %.4f s\n", ts);
            System.out.printf("  Resultado: %.2f\n", resultadoSeq);
            
            for (int numThreads : numThreadsList) {
                inicio = System.nanoTime();
                double resultadoPar = somaParalela(vetor, numThreads);
                fim = System.nanoTime();
                double tp = (fim - inicio) / 1_000_000_000.0;
                
                double speedup = ts / tp;
                double eficiencia = speedup / numThreads;
                
                System.out.printf("\n✓ Paralelo (%d threads):\n", numThreads);
                System.out.printf("  Tempo: %.4f s\n", tp);
                System.out.printf("  Resultado: %.2f\n", resultadoPar);
                System.out.printf("  Speedup: %.2fx\n", speedup);
                System.out.printf("  Eficiência: %.2f%%\n", eficiencia * 100);
                
                double erro = Math.abs(resultadoSeq - resultadoPar);
                if (erro < 0.01) {
                    System.out.printf("  ✓ Resultado correto (erro: %.6f)\n", erro);
                } else {
                    System.out.printf("  ✗ ERRO: diferença de %.6f\n", erro);
                }
            }
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ANÁLISE DOS RESULTADOS");
        System.out.println("=".repeat(80));
        System.out.println("""
        
        DIFERENÇAS TEÓRICAS vs PRÁTICAS:
        
        1. SPEEDUP TEÓRICO:
           - Com N threads, esperaríamos speedup ≈ N
           - Isso assumiria overhead zero e divisão perfeita do trabalho
        
        2. SPEEDUP PRÁTICO (geralmente menor):
           - Criação e sincronização de threads (overhead)
           - Combinação final dos resultados (parte sequencial)
           - Contenção de memória e cache
           - Troca de contexto do SO
        
        3. OBSERVAÇÕES:
           - Vetores maiores tendem a ter melhor eficiência
           - Há um ponto onde mais threads não ajudam
           - Lei de Amdahl limita o ganho máximo
        """);
    }
}