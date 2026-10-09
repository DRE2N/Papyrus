# Papyrus combat contract

This is the server-side hit policy used by Erethon combat. Class damage, healing,
CC-bar values, telegraphs and PvP/PvE coefficients remain authored by plugins.

## Damage order

1. The caller supplies a damage amount and a `CraftPDamageType`. The untyped
   `hurtServer` overload currently selects `PURE`; new abilities should pass
   their intended type explicitly.
2. A physical basic attack rolls the attacker's critical chance once. A normal
   critical multiplier is `1.5 + clamp(stat_crit_damage, 0, 250) / 100`. A
   vanilla jump critical has already applied its 1.5 multiplier in
   `Player.attack`, so Papyrus adds only the remaining attribute bonus. Spells
   must own their own critical eligibility and avoid a second roll.
3. Resistance applies after critical damage as a flat subtraction:
   `max(0, damageAfterCritical - (targetResistance - attackerPenetration))`.
   Penetration offsets resistance point for point; excess penetration adds
   flat damage. Resistance can fully block a hit.
4. Spellbook `onDamage` modifiers run after mitigation. Bukkit's damage event
   can change or cancel the result. A nonpositive or nonfinite final result is
   rejected and does not trigger contact knockback or last-hurt attribution.
5. Successful hits update last-hurt state and health. A hook that awards a
   confirmed-hit resource should observe the successful result rather than
   grant it in a pre-damage modifier.

`CombatDamageBreakdown` exposes the stages on the damage event. Start the
server with `-Dpapyrus.traceCombat=true` to log accepted and rejected hits,
including immunity, cancellation, critical result, mitigation and whether
contact knockback was attempted. The flag is off by default.

## Hurt cooldown

Accepted basic contact hits from living attackers set the target's
`combat_hurtinvulnerability` timer. A further basic contact hit is rejected
while that timer is active. Players default to 8 ticks (0.4 seconds);
nonplayers default to 3 ticks (0.15 seconds). Aether creature configuration
can override the mob value through `config.noDamageTicks`.

Spell, effect, trait and summon damage bypass this timer and do not reset
it, so authored multi-hit abilities and combos can land. Environmental damage
is not gated by the basic contact timer. The timer uses LivingEntity's damage
cooldown, not Entity's general invulnerability, so it does not block unrelated
damage sources. Other explicit invulnerability states still apply.

## Melee feel

Ordinary basic attacks attempt 0.2-strength contact knockback. Spells and
effects have no automatic contact push. An ability may apply explicit,
advertised displacement at its own call site. Player sprint and weapon bonus
knockback remain separate from the ordinary contact push.

A player with the active `papyrus:rapid_melee` SpellCaster tag uses full base
weapon damage without the modern charge factor and can attempt one
attack every three ticks. The rapid path does not gain vanilla sweep. It keeps
aim, jump criticals and sprint reset behavior. The Swordstorm trait owns this
tag; Vanguard and other lines use the normal charge path.

## Follow-up integration

Spellbook's varied-damage helper and direct critical bonuses must be aligned
with this definition before final ability tuning. Self-heal PvP/PvE context,
boss CC bars and class-specific numbers belong in the gameplay plugins.
