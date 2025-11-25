import java.util.Random;

public class Exercicio4_MediaDesvio {
    
    public static class Resultado {
        double media;
        double desvio;
        
        public Resultado(double media, double desvio) {
            this.media = media;
            this.desvio = desvio;
        }
    }
    
    public static Resultado calcularSequencial(double[] vetor) {
        int n = vetor.length;
        
        double soma = 0;
        for (double valor : vetor) {
            soma += valor;
        }
        double media = soma / n;
        
        double somaQuadrados = 0;
        for (double valor : vetor) {
            double diff = valor - media;
            somaQuadrados += diff * diff;
        }
        double desvio = Math.sqrt(somaQuadrados / n);
        
        return new Resultado(media, desvio);
    }
    
    static class ThreadSoma extends Thread {
        private double[] vetor;
        private int inicio;
        private int fim;
        private double resultado;
        
        public ThreadSoma(double[] vetor, int inicio, int fim) {
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
    
    static class ThreadDesvio extends Thread {
        private double[] vetor;
        private int inicio;
        private int fim;
        private double media;
        private double resultado;
        
        public ThreadDesvio(double[] vetor, int inicio, int fim, double media) {
            this.vetor = vetor;
            this.inicio = inicio;
            this.fim = fim;
            this.media = media;
        }
        
        @Override
        public void run() {
            resultado = 0;
            for (int i = inicio; i < fim; i++) {
                double diff = vetor[i] - media;
                resultado += diff * diff;
            }
        }
        
        public double getResultado() {
            return resultado;
        }
    }
    
    public static Resultado calcularParalelo(double[] vetor, int numThreads) throws InterruptedException {
        int n = vetor.length;
        int tamanhoBloco = n / numThreads;
        
        ThreadSoma[] threadsSoma = new ThreadSoma[numThreads];
        for (int i = 0; i < numThreads; i++) {
            int inicio = i * tamanhoBloco;
            int fim = (i == numThreads - 1) ? n : (i + 1) * tamanhoBloco;
            threadsSoma[i] = new ThreadSoma(vetor, inicio, fim);
            threadsSoma[i].start();
        }
        
        double somaTotal = 0;
        for (ThreadSoma thread : threadsSoma) {
            thread.join();
            somaTotal += thread.getResultado();
        }
        double media = somaTotal / n;
        
        ThreadDesvio[] threadsDesvio = new ThreadDesvio[numThreads];
        for (int i = 0; i < numThreads; i++) {
            int inicio = i * tamanhoBloco;
            int fim = (i == numThreads - 1) ? n : (i + 1) * tamanhoBloco;
            threadsDesvio[i] = new ThreadDesvio(vetor, inicio, fim, media);
            threadsDesvio[i].start();
        }
        
        double somaQuadrados = 0;
        for (ThreadDesvio thread : threadsDesvio) {
            thread.join();
            somaQuadrados += thread.getResultado();
        }
        double desvio = Math.sqrt(somaQuadrados / n);
        
        return new Resultado(media, desvio);
    }
    
    public static void main(String[] args) throws InterruptedException {
        int[] tamanhos = {1_000_000, 5_000_000, 10_000_000};
        int[] numThreadsList = {2, 4, 8};
        
        System.out.println("=".repeat(80));
        System.out.println("EXERCÍCIO 4 - MÉDIA E DESVIO PADRÃO");
        System.out.println("=".repeat(80));
        
        for (int tamanho : tamanhos) {
            System.out.println("\n" + "─".repeat(80));
            System.out.printf("Tamanho do vetor: %,d elementos\n", tamanho);
            System.out.println("─".repeat(80));
            
            double[] vetor = new double[tamanho];
            Random rand = new Random(42);
            for (int i = 0; i < tamanho; i++) {
                vetor[i] = rand.nextGaussian();
            }
            
            long inicio = System.nanoTime();
            Resultado resultadoSeq = calcularSequencial(vetor);
            long fim = System.nanoTime();
            double ts = (fim - inicio) / 1_000_000_000.0;
            
            System.out.println("\n✓ Sequencial:");
            System.out.printf("  Tempo: %.4f s\n", ts);
            System.out.printf("  Média: %.6f\n", resultadoSeq.media);
            System.out.printf("  Desvio: %.6f\n", resultadoSeq.desvio);
            
            for (int numThreads : numThreadsList) {
                inicio = System.nanoTime();
                Resultado resultadoPar = calcularParalelo(vetor, numThreads);
                fim = System.nanoTime();
                double tp = (fim - inicio) / 1_000_000_000.0;
                
                double speedup = ts / tp;
                double eficiencia = speedup / numThreads;
                
                System.out.printf("\n✓ Paralelo (%d threads):\n", numThreads);
                System.out.printf("  Tempo: %.4f s\n", tp);
                System.out.printf("  Média: %.6f\n", resultadoPar.media);
                System.out.printf("  Desvio: %.6f\n", resultadoPar.desvio);
                System.out.printf("  Speedup: %.2fx\n", speedup);
                System.out.printf("  Eficiência: %.2f%%\n", eficiencia * 100);
                
                double erroMedia = Math.abs(resultadoSeq.media - resultadoPar.media);
                double erroDesvio = Math.abs(resultadoSeq.desvio - resultadoPar.desvio);
                System.out.printf("  Erro média: %.8f\n", erroMedia);
                System.out.printf("  Erro desvio: %.8f\n", erroDesvio);
            }
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ANÁLISE");
        System.out.println("=".repeat(80));
        System.out.println("""
        
        CUSTOS DE SINCRONIZAÇÃO:
        - Duas fases de paralelização (média + desvio)
        - Barreira implícita entre as fases (join das threads)
        - Overhead de criar threads duas vezes
        
        DUAS PASSAGENS vs UMA PASSAGEM:
        - Duas passagens: mais simples, correto, requer sincronização
        - Uma passagem: possível calcular média e soma de quadrados juntos,
          mas desvio ainda precisa da média (problema de dependência)
        
        MÚLTIPLOS REDUCES:
        - Cada reduce (soma para média, soma de quadrados) tem seu overhead
        - Mais variáveis compartilhadas = mais combinações no final
        - Trade-off: simplicidade vs performance
        
        OBSERVAÇÕES:
        - Speedup menor que exercício 3 devido às duas fases
        - Eficiência diminui com mais threads (overhead relativo aumenta)
        - Para vetores grandes, o benefício ainda é significativo
        """);
    }
}