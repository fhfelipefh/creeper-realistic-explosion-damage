package com.fhfelipefh;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes das Tags do Mod")
class ModTagsTest {

    @Test
    @DisplayName("Deve ter ID do mod correto")
    void testModId() {
        assertEquals("creeper-realistic-explosion-damage", ModTags.MOD_ID,
                "ID do mod deve estar correto");
    }

    @Test
    @DisplayName("Deve ter tag de blocos imunes definida")
    void testCreeperImmuneTagExists() {
        assertNotNull(ModTags.CREEPER_IMMUNE, "Tag CREEPER_IMMUNE deve existir");
        assertTrue(ModTags.CREEPER_IMMUNE.toString().contains("creeper_immune"),
                "Tag deve conter o identificador correto");
    }

    @Test
    @DisplayName("Deve ter tag de blocos frágeis definida")
    void testCreeperFragileTagExists() {
        assertNotNull(ModTags.CREEPER_FRAGILE, "Tag CREEPER_FRAGILE deve existir");
        assertTrue(ModTags.CREEPER_FRAGILE.toString().contains("creeper_fragile"),
                "Tag deve conter o identificador correto");
    }

    @Test
    @DisplayName("Tags devem ter identificadores únicos")
    void testTagsAreUnique() {
        assertNotEquals(ModTags.CREEPER_IMMUNE, ModTags.CREEPER_FRAGILE,
                "Tags devem ser diferentes");
    }
}
