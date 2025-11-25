import java.util.Random;

public class Exercicio5_MatrizVetor {
    
    public static double[] multiplicarSequencial(double[][] A, double[] x) {
        int N = A.length;
        double[] y = new double[N];
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                y[i] += A[i][j] * x[j];
            }
        }
        
        return y;
    }
    
    static class ThreadMultiplicacao extends Thread {
        private double[][] A;
        private double[] x;
        private double[] y;
        private int inicio;
        private int fim;
        
        public ThreadMultiplicacao(double[][] A, double[] x, double[] y, int inicio, int fim) {
            this.A = A;
            this.x = x;
            this.y = y;
            this.inicio = inicio;
            this.fim = fim;
        }
        
        @Override
        public void run() {
            int N = x.length;
            for (int i = inicio; i < fim; i++) {
                double soma = 0;
                for (int j = 0; j < N; j++) {
                    soma += A[i][j] * x[j];
                }
                y[i] = soma;
            }
        }
    }
    
    public static double[] multiplicarParalelo(double[][] A, double[] x, int numThreads) throws InterruptedException {
        int N = A.length;
        double[] y = new double[N];
        int linhasPorThread = N / numThreads;
        ThreadMultiplicacao[] threads = new ThreadMultiplicacao[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            int inicio = i * linhasPorThread;
            int fim = (i == numThreads - 1) ? N : (i + 1) * linhasPorThread;
            
            threads[i] = new ThreadMultiplicacao(A, x, y, inicio, fim);
            threads[i].start();
        }
        
        for (ThreadMultiplicacao thread : threads) {
            thread.join();
        }
        
        return y;
    }
    
    public static double erroMaximo(double[] v1, double[] v2) {
        double maxErro = 0;
        for (int i = 0; i < v1.length; i++) {
            double erro = Math.abs(v1[i] - v2[i]);
            if (erro > maxErro) {
                maxErro = erro;
            }
        }
        return maxErro;
    }
    
    public static void main(String[] args) throws InterruptedException {
        int[] tamanhos = {500, 1000, 2000};
        int[] numThreadsList = {2, 4, 8};
        
        System.out.println("=".repeat(80));
        System.out.println("EXERCÍCIO 5 - MULTIPLICAÇÃO MATRIZ × VETOR");
        System.out.println("=".repeat(80));
        
        for (int N : tamanhos) {
            System.out.println("\n" + "─".repeat(80));
            System.out.printf("Tamanho da matriz: %d×%d\n", N, N);
            System.out.println("─".repeat(80));
            
            double[][] A = new double[N][N];
            double[] x = new double[N];
            Random rand = new Random(42);
            
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    A[i][j] = rand.nextDouble();
                }
                x[i] = rand.nextDouble();
            }
            
            long inicio = System.nanoTime();
            double[] ySeq = multiplicarSequencial(A, x);
            long fim = System.nanoTime();
            double ts = (fim - inicio) / 1_000_000_000.0;
            
            System.out.println("\n✓ Sequencial:");
            System.out.printf("  Tempo: %.4f s\n", ts);
            
            System.out.println("\n📊 Análise de Escalabilidade:");
            System.out.printf("  %-10s %-12s %-10s %-12s\n", "Threads", "Tempo (s)", "Speedup", "Eficiência");
            System.out.println("  " + "-".repeat(50));
            
            for (int numThreads : numThreadsList) {
                inicio = System.nanoTime();
                double[] yPar = multiplicarParalelo(A, x, numThreads);
                fim = System.nanoTime();
                double tp = (fim - inicio) / 1_000_000_000.0;
                
                double speedup = ts / tp;
                double eficiencia = speedup / numThreads;
                
                double erro = erroMaximo(ySeq, yPar);
                
                System.out.printf("  %-10d %-12.4f %-10.2f %.2f%%\n", 
                    numThreads, tp, speedup, eficiencia * 100);
                
                if (erro > 1e-6) {
                    System.out.printf("    ⚠ AVISO: Erro de %.10f\n", erro);
                }
            }
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ANÁLISE TEÓRICA vs PRÁTICA");
        System.out.println("=".repeat(80));
        System.out.println("""
        
        SPEEDUP TEÓRICO:
        - Quase todo trabalho é paralelizável (dois loops aninhados)
        - Esperaríamos speedup ≈ N (linear com número de threads)
        
        SPEEDUP PRÁTICO (fatores limitantes):
        
        1. ACESSO À MEMÓRIA:
           - Cada thread lê a matriz A e o vetor x
           - Cache miss pode ser significativo
           - Bandwidth de memória é compartilhado
        
        2. OVERHEAD DE THREADS:
           - Criação e destruição de threads
           - Sincronização (join)
           - Para N pequeno, overhead domina
        
        3. ARQUITETURA DO HARDWARE:
           - Número de cores físicos vs threads
           - Hierarquia de cache (L1, L2, L3)
           - Falso compartilhamento (false sharing)
        
        4. PONTO DE SATURAÇÃO:
           - Adicionar threads além do número de cores físicos não ajuda
           - Pode até piorar devido a troca de contexto
           - Lei de Amdahl: parte sequencial limita ganho
        
        OBSERVAÇÕES:
        - Matrizes maiores → melhor aproveitamento (menos overhead relativo)
        - Divisão por linhas é eficiente (acesso sequencial à memória)
        - Eficiência tende a diminuir com mais threads
        """);
    }
}