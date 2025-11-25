public class Exercicio2 {
    
    public static void main(String[] args) {
        System.out.println("\n\n" + "=".repeat(80));
        System.out.println("EXERCÍCIO 2");
        System.out.println("=".repeat(80));
        System.out.println("\nSuponha que 30% do tempo de execução é estritamente sequencial.\n");
        
        double s = 0.30;
        
        double sMax = 1.0 / s;
        System.out.println("a) Speedup máximo teórico (Lei de Amdahl):");
        System.out.printf("   S_max = 1 / s = 1 / %.2f = %.2f\n", s, sMax);
        System.out.println("   Interpretação: Mesmo com infinitos processadores, o speedup máximo é ~3.33x");
        System.out.println("   Isso ocorre porque 30% do código sempre será sequencial (gargalo)");
        
        System.out.println("\nb) Cálculo de S(p) para diferentes valores de p:");
        System.out.println("   Fórmula: S(p) = 1 / (s + (1-s)/p)");
        System.out.println();
        System.out.printf("   %-12s %-15s %-15s %-15s\n", "Processadores", "Cálculo", "Speedup", "% do Máximo");
        System.out.println("   " + "-".repeat(65));
        
        int[] valores_p = {2, 4, 8, 16, 32, 64};
        
        for (int p : valores_p) {
            double speedup_p = 1.0 / (s + (1 - s) / p);
            double percentualMax = (speedup_p / sMax) * 100;
            
            System.out.printf("   %-12d %-15s %-15.2f %-15.2f\n", 
                p, 
                String.format("1/(%.2f+%.2f/%d)", s, 1-s, p),
                speedup_p,
                percentualMax);
        }
        
        System.out.println("\nc) Discussão - Por que retornos são cada vez menores:");
        System.out.println("   \n   ANÁLISE DOS RESULTADOS:");
        System.out.println("   • De 2 para 4 processadores: ganho de ~0.57x");
        System.out.println("   • De 4 para 8 processadores: ganho de ~0.47x");
        System.out.println("   • De 8 para 16 processadores: ganho de ~0.26x");
        System.out.println("   • De 32 para 64 processadores: ganho de ~0.06x");
        
        System.out.println("\n   RAZÕES FUNDAMENTAIS:");
        System.out.println("   1. LEI DE AMDAHL:");
        System.out.println("      A parte sequencial (30%) se torna cada vez mais dominante");
        System.out.println("      conforme paralelizamos mais a parte paralela (70%)");
        
        System.out.println("\n   2. LIMITE ASSINTÓTICO:");
        System.out.println("      À medida que p → ∞, o termo (1-s)/p → 0");
        System.out.println("      O speedup se aproxima de 1/s = 3.33, mas nunca ultrapassa");
        
        System.out.println("\n   3. GARGALO SEQUENCIAL:");
        System.out.println("      Com muitos processadores, a maioria fica ociosa esperando");
        System.out.println("      a parte sequencial terminar");
        
        System.out.println("\n   4. OVERHEAD NA PRÁTICA:");
        System.out.println("      Além da limitação teórica, há overhead real:");
        System.out.println("      • Sincronização cresce com mais threads");
        System.out.println("      • Contenção de recursos compartilhados");
        System.out.println("      • Custos de comunicação e coordenação");
        
        System.out.println("\n   LIÇÃO IMPORTANTE:");
        System.out.println("   Adicionar mais processadores tem utilidade limitada!");
        System.out.println("   É mais efetivo reduzir a fração sequencial (otimizar o algoritmo)");
        System.out.println("   do que simplesmente adicionar mais hardware.");
        
        System.out.println("\n   VISUALIZAÇÃO DA LEI DE AMDAHL:");
        System.out.println("   \n   Speedup");
        System.out.println("     3.5 |                    .......................");
        System.out.println("     3.0 |              ......");
        System.out.println("     2.5 |         .....");
        System.out.println("     2.0 |      ...");
        System.out.println("     1.5 |   ...");
        System.out.println("     1.0 | .");
        System.out.println("         +----------------------------------------");
        System.out.println("           1    2    4    8   16   32   64  (processadores)");
        System.out.println("\n   Note como a curva se achata rapidamente!");
    }
}