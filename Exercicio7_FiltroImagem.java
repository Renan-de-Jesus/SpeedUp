import java.util.Random;

public class Exercicio7_FiltroImagem {
    
    public static double[][] aplicarBlurSequencial(double[][] imagem) {
        int N = imagem.length;
        double[][] saida = new double[N][N];
        
        for (int i = 1; i < N - 1; i++) {
            for (int j = 1; j < N - 1; j++) {
                double soma = 0;
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        soma += imagem[i + di][j + dj];
                    }
                }
                saida[i][j] = soma / 9.0;
            }
        }
        
        return saida;
    }
    
    static class ThreadBlurLinhas extends Thread {
        private double[][] imagem;
        private double[][] saida;
        private int linhaInicio;
        private int linhaFim;
        
        public ThreadBlurLinhas(double[][] imagem, double[][] saida, int linhaInicio, int linhaFim) {
            this.imagem = imagem;
            this.saida = saida;
            this.linhaInicio = linhaInicio;
            this.linhaFim = linhaFim;
        }
        
        @Override
        public void run() {
            int N = imagem.length;
            
            for (int i = linhaInicio; i < linhaFim; i++) {
                if (i == 0 || i == N - 1) continue;
                
                for (int j = 1; j < N - 1; j++) {
                    double soma = 0;
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            soma += imagem[i + di][j + dj];
                        }
                    }
                    saida[i][j] = soma / 9.0;
                }
            }
        }
    }
    
    public static double[][] aplicarBlurParaleloLinhas(double[][] imagem, int numThreads) throws InterruptedException {
        int N = imagem.length;
        double[][] saida = new double[N][N];
        
        int linhasPorThread = N / numThreads;
        ThreadBlurLinhas[] threads = new ThreadBlurLinhas[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            int linhaInicio = i * linhasPorThread;
            int linhaFim = (i == numThreads - 1) ? N : (i + 1) * linhasPorThread;
            
            threads[i] = new ThreadBlurLinhas(imagem, saida, linhaInicio, linhaFim);
            threads[i].start();
        }
        
        for (ThreadBlurLinhas thread : threads) {
            thread.join();
        }
        
        return saida;
    }
    
    static class ThreadBlurBlocos extends Thread {
        private double[][] imagem;
        private double[][] saida;
        private int iInicio, iFim, jInicio, jFim;
        
        public ThreadBlurBlocos(double[][] imagem, double[][] saida, 
                               int iInicio, int iFim, int jInicio, int jFim) {
            this.imagem = imagem;
            this.saida = saida;
            this.iInicio = iInicio;
            this.iFim = iFim;
            this.jInicio = jInicio;
            this.jFim = jFim;
        }
        
        @Override
        public void run() {
            int N = imagem.length;
            
            for (int i = iInicio; i < iFim; i++) {
                if (i == 0 || i == N - 1) continue;
                
                for (int j = jInicio; j < jFim; j++) {
                    if (j == 0 || j == N - 1) continue;
                    
                    double soma = 0;
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            soma += imagem[i + di][j + dj];
                        }
                    }
                    saida[i][j] = soma / 9.0;
                }
            }
        }
    }
    
    public static double[][] aplicarBlurParaleloBlocos(double[][] imagem, int numThreads) throws InterruptedException {
        int N = imagem.length;
        double[][] saida = new double[N][N];
        
        int lado = (int) Math.sqrt(numThreads);
        while (numThreads % lado != 0) {
            lado--;
        }
        int linhasThreads = lado;
        int colunasThreads = numThreads / lado;
        
        int linhasPorThread = N / linhasThreads;
        int colunasPorThread = N / colunasThreads;
        
        ThreadBlurBlocos[] threads = new ThreadBlurBlocos[numThreads];
        int idx = 0;
        
        for (int i = 0; i < linhasThreads; i++) {
            for (int j = 0; j < colunasThreads; j++) {
                int iInicio = i * linhasPorThread;
                int iFim = (i == linhasThreads - 1) ? N : (i + 1) * linhasPorThread;
                int jInicio = j * colunasPorThread;
                int jFim = (j == colunasThreads - 1) ? N : (j + 1) * colunasPorThread;
                
                threads[idx] = new ThreadBlurBlocos(imagem, saida, iInicio, iFim, jInicio, jFim);
                threads[idx].start();
                idx++;
            }
        }
        
        for (ThreadBlurBlocos thread : threads) {
            thread.join();
        }
        
        return saida;
    }
    
    public static double erroMaximo(double[][] m1, double[][] m2) {
        int N = m1.length;
        double maxErro = 0;
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double erro = Math.abs(m1[i][j] - m2[i][j]);
                if (erro > maxErro) {
                    maxErro = erro;
                }
            }
        }
        
        return maxErro;
    }
    
    public static void main(String[] args) throws InterruptedException {
        int[] tamanhos = {500, 1000, 2000};
        int[] numThreadsList = {2, 4, 8};
        
        System.out.println("=".repeat(80));
        System.out.println("EXERCÍCIO 7 - FILTRO DE IMAGEM (BLUR 3×3)");
        System.out.println("=".repeat(80));
        
        for (int N : tamanhos) {
            System.out.println("\n" + "─".repeat(80));
            System.out.printf("Tamanho da imagem: %d×%d pixels\n", N, N);
            System.out.println("─".repeat(80));
            
            double[][] imagem = new double[N][N];
            Random rand = new Random(42);
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    imagem[i][j] = rand.nextInt(256);
                }
            }
            
            long inicio = System.nanoTime();
            double[][] saidaSeq = aplicarBlurSequencial(imagem);
            long fim = System.nanoTime();
            double ts = (fim - inicio) / 1_000_000_000.0;
            
            System.out.println("\n✓ Sequencial:");
            System.out.printf("  Tempo: %.4f s\n", ts);
            
            System.out.println("\n" + "─".repeat(40));
            System.out.println("DIVISÃO POR LINHAS");
            System.out.println("─".repeat(40));
            
            for (int numThreads : numThreadsList) {
                inicio = System.nanoTime();
                double[][] saidaPar = aplicarBlurParaleloLinhas(imagem, numThreads);
                fim = System.nanoTime();
                double tp = (fim - inicio) / 1_000_000_000.0;
                
                double speedup = ts / tp;
                double eficiencia = speedup / numThreads;
                double erro = erroMaximo(saidaSeq, saidaPar);
                
                System.out.printf("\n  %d threads:\n", numThreads);
                System.out.printf("    Tempo: %.4f s\n", tp);
                System.out.printf("    Speedup: %.2fx\n", speedup);
                System.out.printf("    Eficiência: %.2f%%\n", eficiencia * 100);
                System.out.printf("    Erro máximo: %.10f\n", erro);
            }
            
            System.out.println("\n" + "─".repeat(40));
            System.out.println("DIVISÃO POR BLOCOS (4 threads)");
            System.out.println("─".repeat(40));
            
            inicio = System.nanoTime();
            double[][] saidaBlocos = aplicarBlurParaleloBlocos(imagem, 4);
            fim = System.nanoTime();
            double tpBlocos = (fim - inicio) / 1_000_000_000.0;
            
            double speedup = ts / tpBlocos;
            double eficiencia = speedup / 4;
            double erro = erroMaximo(saidaSeq, saidaBlocos);
            
            System.out.printf("\n  Tempo: %.4f s\n", tpBlocos);
            System.out.printf("  Speedup: %.2fx\n", speedup);
            System.out.printf("  Eficiência: %.2f%%\n", eficiencia * 100);
            System.out.printf("  Erro máximo: %.10f\n", erro);
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ANÁLISE DE LOCALIDADE E PADRÕES DE ACESSO");
        System.out.println("=".repeat(80));
        System.out.println("""
        
        PADRÃO DE ACESSO À MEMÓRIA:
        
        1. DIVISÃO POR LINHAS:
           ✓ Acesso sequencial por linha (boa localidade espacial)
           ✓ Aproveitamento de cache lines
           ✓ Prefetching automático do hardware funciona bem
           - Threads não compartilham cache (menos contenção)
        
        2. DIVISÃO POR BLOCOS:
           ~ Pode ter localidade espacial dentro do bloco
           ~ Mais complexa de implementar
           ~ Pode ter melhor distribuição de cache em alguns casos
           - Para imagens, geralmente não compensa a complexidade
        
        FATORES QUE LIMITAM SPEEDUP:
        
        1. LARGURA DE BANDA DA MEMÓRIA:
           - Operação memory-bound (mais leitura/escrita que computação)
           - Múltiplas threads competem pela mesma memória
           - Saturação do barramento
        
        2. HIERARQUIA DE CACHE:
           - L1/L2 cache são por core
           - L3 cache é compartilhado
           - Working set da imagem pode não caber em cache
        
        3. OVERHEAD vs COMPUTAÇÃO:
           - Filtro 3×3 é relativamente simples
           - Para filtros maiores ou mais complexos, speedup seria melhor
        
        4. SINCRONIZAÇÃO:
           - Join das threads
           - Alocação da matriz de saída
        
        OBSERVAÇÕES:
        - Imagens maiores têm melhor eficiência
        - Ganho não é linear devido a limitações de memória
        - Divisão por linhas é geralmente superior
        - Para máximo desempenho, considere vetorização (SIMD)
        """);
    }
}