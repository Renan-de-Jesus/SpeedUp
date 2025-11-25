public class Exercicio6_Primos {
    
    public static boolean ehPrimo(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        
        int limite = (int) Math.sqrt(n);
        for (int i = 3; i <= limite; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
    
    public static int contarPrimosSequencial(int N) {
        int count = 0;
        for (int i = 1; i <= N; i++) {
            if (ehPrimo(i)) count++;
        }
        return count;
    }
    
    static class ThreadPrimosEstatico extends Thread {
        private int inicio;
        private int fim;
        private int resultado;
        
        public ThreadPrimosEstatico(int inicio, int fim) {
            this.inicio = inicio;
            this.fim = fim;
        }
        
        @Override
        public void run() {
            resultado = 0;
            for (int i = inicio; i <= fim; i++) {
                if (ehPrimo(i)) resultado++;
            }
        }
        
        public int getResultado() {
            return resultado;
        }
    }
    
    public static int contarPrimosParaleloEstatico(int N, int numThreads) throws InterruptedException {
        int tamanhoBloco = N / numThreads;
        ThreadPrimosEstatico[] threads = new ThreadPrimosEstatico[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            int inicio = i * tamanhoBloco + 1;
            int fim = (i == numThreads - 1) ? N : (i + 1) * tamanhoBloco;
            
            threads[i] = new ThreadPrimosEstatico(inicio, fim);
            threads[i].start();
        }
        
        int total = 0;
        for (ThreadPrimosEstatico thread : threads) {
            thread.join();
            total += thread.getResultado();
        }
        
        return total;
    }
    
    static class FilaTarefas {
        private int proximo;
        private int N;
        private int tamanhoBloco;
        
        public FilaTarefas(int N, int tamanhoBloco) {
            this.proximo = 1;
            this.N = N;
            this.tamanhoBloco = tamanhoBloco;
        }
        
        public synchronized int[] pegarTarefa() {
            if (proximo > N) return null;
            
            int inicio = proximo;
            int fim = Math.min(proximo + tamanhoBloco - 1, N);
            proximo = fim + 1;
            
            return new int[]{inicio, fim};
        }
    }
    
    static class ThreadPrimosDinamico extends Thread {
        private FilaTarefas fila;
        private int resultado;
        
        public ThreadPrimosDinamico(FilaTarefas fila) {
            this.fila = fila;
        }
        
        @Override
        public void run() {
            resultado = 0;
            while (true) {
                int[] tarefa = fila.pegarTarefa();
                if (tarefa == null) break;
                
                int inicio = tarefa[0];
                int fim = tarefa[1];
                
                for (int i = inicio; i <= fim; i++) {
                    if (ehPrimo(i)) resultado++;
                }
            }
        }
        
        public int getResultado() {
            return resultado;
        }
    }
    
    public static int contarPrimosParaleloDinamico(int N, int numThreads, int tamanhoBloco) throws InterruptedException {
        FilaTarefas fila = new FilaTarefas(N, tamanhoBloco);
        ThreadPrimosDinamico[] threads = new ThreadPrimosDinamico[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new ThreadPrimosDinamico(fila);
            threads[i].start();
        }
        
        int total = 0;
        for (ThreadPrimosDinamico thread : threads) {
            thread.join();
            total += thread.getResultado();
        }
        
        return total;
    }
    
    public static void main(String[] args) throws InterruptedException {
        int[] valoresN = {50_000, 100_000, 200_000};
        int[] numThreadsList = {2, 4, 8};
        
        System.out.println("=".repeat(80));
        System.out.println("EXERCÍCIO 6 - CONTAGEM DE NÚMEROS PRIMOS");
        System.out.println("=".repeat(80));
        
        for (int N : valoresN) {
            System.out.println("\n" + "─".repeat(80));
            System.out.printf("Intervalo: [1, %,d]\n", N);
            System.out.println("─".repeat(80));
            
            long inicio = System.nanoTime();
            int countSeq = contarPrimosSequencial(N);
            long fim = System.nanoTime();
            double ts = (fim - inicio) / 1_000_000_000.0;
            
            System.out.println("\n✓ Sequencial:");
            System.out.printf("  Tempo: %.4f s\n", ts);
            System.out.printf("  Primos encontrados: %,d\n", countSeq);
            
            System.out.println("\n" + "─".repeat(40));
            System.out.println("PARTIÇÃO ESTÁTICA");
            System.out.println("─".repeat(40));
            
            for (int numThreads : numThreadsList) {
                inicio = System.nanoTime();
                int countEst = contarPrimosParaleloEstatico(N, numThreads);
                fim = System.nanoTime();
                double tpEst = (fim - inicio) / 1_000_000_000.0;
                
                double speedup = ts / tpEst;
                double eficiencia = speedup / numThreads;
                
                System.out.printf("\n  %d threads:\n", numThreads);
                System.out.printf("    Tempo: %.4f s\n", tpEst);
                System.out.printf("    Speedup: %.2fx\n", speedup);
                System.out.printf("    Eficiência: %.2f%%\n", eficiencia * 100);
                System.out.printf("    Primos: %,d %s\n", countEst, 
                    (countEst == countSeq) ? "✓" : "✗");
            }
            
            System.out.println("\n" + "─".repeat(40));
            System.out.println("PARTIÇÃO DINÂMICA");
            System.out.println("─".repeat(40));
            
            for (int numThreads : numThreadsList) {
                inicio = System.nanoTime();
                int countDin = contarPrimosParaleloDinamico(N, numThreads, 1000);
                fim = System.nanoTime();
                double tpDin = (fim - inicio) / 1_000_000_000.0;
                
                double speedup = ts / tpDin;
                double eficiencia = speedup / numThreads;
                
                System.out.printf("\n  %d threads:\n", numThreads);
                System.out.printf("    Tempo: %.4f s\n", tpDin);
                System.out.printf("    Speedup: %.2fx\n", speedup);
                System.out.printf("    Eficiência: %.2f%%\n", eficiencia * 100);
                System.out.printf("    Primos: %,d %s\n", countDin, 
                    (countDin == countSeq) ? "✓" : "✗");
            }
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ANÁLISE DO DESEQUILÍBRIO DE CARGA");
        System.out.println("=".repeat(80));
        System.out.println("""
        
        POR QUE O TRABALHO NÃO É BALANCEADO:
        
        1. CUSTO VARIÁVEL:
           - Números pequenos: teste rápido (poucos divisores)
           - Números grandes: teste mais lento (mais divisores até sqrt(n))
           - Primos são mais "caros" (testam todos os divisores)
        
        2. DISTRIBUIÇÃO IRREGULAR:
           - Primos ficam mais raros em números maiores
           - Intervalos altos têm menos primos, mas testes mais caros
        
        PARTIÇÃO ESTÁTICA:
        ✓ Simples, sem sincronização extra
        ✗ Desequilíbrio: threads com intervalos altos terminam por último
        ✗ Algumas threads ficam ociosas esperando outras
        
        PARTIÇÃO DINÂMICA:
        ✓ Melhor balanceamento: threads pegam trabalho conforme terminam
        ✓ Speedup geralmente melhor
        ✗ Overhead de sincronização (synchronized na fila)
        ✗ Mais complexa de implementar
        
        TRADE-OFF:
        - Partição dinâmica vale a pena quando:
          * Desequilíbrio é significativo
          * Tarefas grandes o suficiente para compensar overhead
          * Número de tarefas >> número de threads
        """);
    }
}