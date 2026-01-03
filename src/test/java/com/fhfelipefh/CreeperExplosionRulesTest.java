package com.fhfelipefh;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes das Regras de Explosão do Creeper")
class CreeperExplosionRulesTest {

    @Test
    @DisplayName("Deve escalar o poder do creeper corretamente")
    void testScaleCreeperPower() {
        float basePower = 3.0F;
        float scaledPower = CreeperExplosionRules.scaleCreeperPower(basePower);
        
        assertEquals(basePower * CreeperExplosionRules.CREEPER_POWER_MULTIPLIER, scaledPower, 0.001F,
                "Poder do creeper deve ser multiplicado corretamente");
        
        assertTrue(scaledPower > basePower, "Poder escalado deve ser maior que o base");
    }

    @Test
    @DisplayName("Deve validar constantes do mod")
    void testConstants() {
        assertEquals(6.0F, CreeperExplosionRules.RESISTANT_BLAST_RESISTANCE, 
                "Limite de resistência deve ser 6.0");
        assertEquals(3.0F, CreeperExplosionRules.WEAK_RESISTANCE_THRESHOLD,
                "Limite de blocos fracos deve ser 3.0");
        assertEquals(1.2F, CreeperExplosionRules.CREEPER_POWER_MULTIPLIER,
                "Multiplicador de poder deve ser 1.2");
        
        assertTrue(CreeperExplosionRules.GLASS_RESISTANCE_MULTIPLIER < 
                   CreeperExplosionRules.DIRT_RESISTANCE_MULTIPLIER,
                "Vidro deve ser mais frágil que terra");
        
        assertTrue(CreeperExplosionRules.DIRT_RESISTANCE_MULTIPLIER <
                   CreeperExplosionRules.FRAGILE_RESISTANCE_MULTIPLIER,
                "Terra deve ser mais frágil que blocos frágeis gerais");
    }

    @Test
    @DisplayName("Deve ter multiplicadores positivos")
    void testMultipliersArePositive() {
        assertTrue(CreeperExplosionRules.WEAK_RESISTANCE_MULTIPLIER > 0,
                "Multiplicador de resistência fraca deve ser positivo");
        assertTrue(CreeperExplosionRules.DIRT_RESISTANCE_MULTIPLIER > 0,
                "Multiplicador de terra deve ser positivo");
        assertTrue(CreeperExplosionRules.FRAGILE_RESISTANCE_MULTIPLIER > 0,
                "Multiplicador de fragilidade deve ser positivo");
        assertTrue(CreeperExplosionRules.GLASS_RESISTANCE_MULTIPLIER > 0,
                "Multiplicador de vidro deve ser positivo");
        assertTrue(CreeperExplosionRules.CREEPER_POWER_MULTIPLIER > 0,
                "Multiplicador de poder deve ser positivo");
    }

    @Test
    @DisplayName("Deve ter multiplicadores menores que 1 para blocos fracos")
    void testWeakMultipliersAreLessThanOne() {
        assertTrue(CreeperExplosionRules.WEAK_RESISTANCE_MULTIPLIER < 1.0F,
                "Multiplicador de resistência fraca deve reduzir a resistência");
        assertTrue(CreeperExplosionRules.DIRT_RESISTANCE_MULTIPLIER < 1.0F,
                "Multiplicador de terra deve reduzir a resistência");
        assertTrue(CreeperExplosionRules.FRAGILE_RESISTANCE_MULTIPLIER < 1.0F,
                "Multiplicador de fragilidade deve reduzir a resistência");
        assertTrue(CreeperExplosionRules.GLASS_RESISTANCE_MULTIPLIER < 1.0F,
                "Multiplicador de vidro deve reduzir a resistência");
    }

    @Test
    @DisplayName("Deve ter multiplicador de poder maior que 1")
    void testPowerMultiplierIncreasesStrength() {
        assertTrue(CreeperExplosionRules.CREEPER_POWER_MULTIPLIER > 1.0F,
                "Multiplicador de poder deve aumentar o poder da explosão");
    }

    @Test
    @DisplayName("Deve ter ordem correta de fragilidade")
    void testFragilityOrder() {
        // Vidro é o mais frágil
        assertTrue(CreeperExplosionRules.GLASS_RESISTANCE_MULTIPLIER < 
                   CreeperExplosionRules.DIRT_RESISTANCE_MULTIPLIER,
                "Vidro deve ser mais frágil que terra");
        
        // Terra é mais frágil que blocos frágeis gerais
        assertTrue(CreeperExplosionRules.DIRT_RESISTANCE_MULTIPLIER <
                   CreeperExplosionRules.FRAGILE_RESISTANCE_MULTIPLIER,
                "Terra deve ser mais frágil que blocos frágeis gerais");
        
        // Blocos frágeis são mais frágeis que blocos fracos
        assertTrue(CreeperExplosionRules.FRAGILE_RESISTANCE_MULTIPLIER <
                   CreeperExplosionRules.WEAK_RESISTANCE_MULTIPLIER,
                "Blocos frágeis devem ser mais frágeis que blocos fracos");
    }

    @Test
    @DisplayName("Poder do creeper deve ser escalado em 20%")
    void testCreeperPowerScalesBy20Percent() {
        float power1 = 1.0F;
        float power2 = 5.0F;
        float power3 = 10.0F;
        
        assertEquals(1.2F, CreeperExplosionRules.scaleCreeperPower(power1), 0.001F);
        assertEquals(6.0F, CreeperExplosionRules.scaleCreeperPower(power2), 0.001F);
        assertEquals(12.0F, CreeperExplosionRules.scaleCreeperPower(power3), 0.001F);
    }

    @Test
    @DisplayName("Limite de resistência deve ser 6.0")
    void testResistantThreshold() {
        assertEquals(6.0F, CreeperExplosionRules.RESISTANT_BLAST_RESISTANCE,
                "Blocos com resistência >= 6.0 devem ser imunes");
    }

    @Test
    @DisplayName("Limite de fragilidade deve ser 3.0")
    void testWeakThreshold() {
        assertEquals(3.0F, CreeperExplosionRules.WEAK_RESISTANCE_THRESHOLD,
                "Blocos com resistência <= 3.0 devem ser considerados fracos");
    }

    @Test
    @DisplayName("Multiplicadores devem estar em ordem crescente de força")
    void testMultiplierStrengthOrder() {
        // Ordem: vidro < terra < frágil < fraco
        float[] multipliers = {
            CreeperExplosionRules.GLASS_RESISTANCE_MULTIPLIER,
            CreeperExplosionRules.DIRT_RESISTANCE_MULTIPLIER,
            CreeperExplosionRules.FRAGILE_RESISTANCE_MULTIPLIER,
            CreeperExplosionRules.WEAK_RESISTANCE_MULTIPLIER
        };
        
        for (int i = 0; i < multipliers.length - 1; i++) {
            assertTrue(multipliers[i] < multipliers[i + 1],
                    "Multiplicadores devem estar em ordem crescente");
        }
    }
}
