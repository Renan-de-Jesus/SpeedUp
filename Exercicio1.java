public class Exercicio1 {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("EXERCÍCIO 1");
        System.out.println("=".repeat(80));
        System.out.println("\nUm programa sequencial leva 20 segundos.");
        System.out.println("A versão paralela usando 5 threads leva 7 segundos.\n");
        
        double ts = 20.0;
        double tp = 7.0;
        int p = 5;
        
        double speedup = ts / tp;
        System.out.println("a) Speedup:");
        System.out.printf("   S = Ts / Tp = %.1f / %.1f = %.2f\n", ts, tp, speedup);
        System.out.println("   Interpretação: O programa ficou ~2.86 vezes mais rápido");

        double eficiencia = speedup / p;
        System.out.println("\nb) Eficiência:");
        System.out.printf("   E = S / p = %.2f / %d = %.2f ou %.2f%%\n", 
            speedup, p, eficiencia, eficiencia * 100);
        System.out.println("   Interpretação: Estamos usando ~57% da capacidade dos processadores");
 
        System.out.println("\nc) Interpretação completa:");
        System.out.println("   ✓ SPEEDUP de 2.86: Houve ganho significativo com a paralelização");
        System.out.println("   ⚠ EFICIÊNCIA de 57%: Não estamos aproveitando totalmente os 5 cores");
        System.out.println("\n   Possíveis causas da eficiência não ideal:");
        System.out.println("   • Overhead de criação e sincronização de threads");
        System.out.println("   • Partes sequenciais do código (Lei de Amdahl)");
        System.out.println("   • Desequilíbrio de carga entre threads");
        System.out.println("   • Contenção de memória/cache");
        System.out.println("   • Troca de contexto do sistema operacional");
        System.out.println("\n   CONCLUSÃO: O paralelismo trouxe benefícios, mas há espaço para otimização.");
    }  
}