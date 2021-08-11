package com.rs.game.player.spells.passive;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utilities.LogUtility;
import com.rs.utilities.ReflectionUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import skills.Skills;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * @author Dennis
 * Since this is Player specific, need to create an instance of this class
 * instead of static so it won't affect other players.
 */
public final class PassiveSpellDispatcher {

    private static final Object2ObjectOpenHashMap<PassiveSpellSignature, PassiveSpell> SPELLS = new Object2ObjectOpenHashMap<>();

    public void execute(Player player, int spellButton) {
        Optional<PassiveSpell> spell = getVerifiedSpell(spellButton);
        spell.filter(caster -> canCast(player, spell, spellButton)).ifPresent(caster -> executeCast(player, spellButton));
    }

    @Getter
    @Setter
    private boolean canCast;

    private boolean canCast(Player player, Optional<PassiveSpell> spell, int spellButton) {
        spell.ifPresent(caster -> {
            Optional<PassiveSpell> verifiedSpell = getVerifiedSpell(spellButton);
            if (!verifiedSpell.get().canExecute(player)) {
                player.getPackets().sendGameMessage("You don't meet the requirements to cast this spell.");
                setCanCast(false);
                return;
            }
            if (player.getSkills().getLevel(Skills.MAGIC) < getSpellLevelRequirement(caster)) {
                player.getPackets().sendGameMessage("You don't have the required Magic level to cast this spell.");
                setCanCast(false);
                return;
            }
            Arrays.stream(caster.runes()).forEach(rune -> setCanCast(player.getInventory().containsItem(rune.getId(), rune.getAmount())));
            if (!isCanCast()) {
                player.getPackets().sendGameMessage("You don't have the required amount of Runes to cast this spell.");
                return;
            }
        });
        return isCanCast();
    }

    private void executeCast(Player player, int spellButton) {
        Optional<PassiveSpell> spell = getVerifiedSpell(spellButton);
        spell.ifPresent(caster -> {
            Arrays.stream(caster.runes()).forEach(rune -> player.getInventory().deleteItem(new Item(rune.getId(), rune.getAmount())));
            spell.get().execute(player);
        });
    }

    private int getSpellLevelRequirement(PassiveSpell spell) {
        Annotation annotation = spell.getClass().getAnnotation(PassiveSpellSignature.class);
        PassiveSpellSignature signature = (PassiveSpellSignature) annotation;
        return signature.spellLevelRequirement();
    }

    private Optional<PassiveSpell> getVerifiedSpell(int id) {
        for (Entry<PassiveSpellSignature, PassiveSpell> spell : SPELLS.entrySet()) {
            if (isSpellButton(spell.getValue(), id)) {
                return Optional.of(spell.getValue());
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("CastCanBeRemovedNarrowingVariableType")
    private boolean isSpellButton(PassiveSpell spell, int spellButton) {
        Annotation annotation = spell.getClass().getAnnotation(PassiveSpellSignature.class);
        PassiveSpellSignature signature = (PassiveSpellSignature) annotation;
        return signature.spellButton() == spellButton;
    }

    public static void load() {
        List<PassiveSpell> passiveSpells = ReflectionUtils.getImplementersOf(PassiveSpell.class);
        passiveSpells.forEach(spell -> SPELLS.put(spell.getClass().getAnnotation(PassiveSpellSignature.class), spell));
        LogUtility.log(LogUtility.LogType.INFO, "Registered " + SPELLS.size() + " combat spells.");
    }

    public void reload() {
        SPELLS.clear();
        load();
    }
}